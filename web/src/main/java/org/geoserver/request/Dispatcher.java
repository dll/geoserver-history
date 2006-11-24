package org.geoserver.request;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.geotools.validation.xml.XMLReader;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.vfny.geoserver.ServiceException;
import org.vfny.geoserver.global.GeoServer;
import org.vfny.geoserver.servlets.AbstractService;
import org.vfny.geoserver.util.requests.EncodingInfo;
import org.vfny.geoserver.util.requests.XmlCharsetDetector;
import org.vfny.geoserver.util.requests.readers.DispatcherXmlReader;
import org.vfny.geoserver.util.requests.readers.KvpRequestReader;



/**
 * Root dispatcher which handles all incoming requests.
 * <p>
 * 
 * </p>
 * 
 * @author Justin Deoliveira, The Open Planning Project, jdeolive@openplans.org
 *
 */
public class Dispatcher extends AbstractController {

	GeoServer geoServer;
	
	public Dispatcher ( GeoServer geoServer ) {
		this.geoServer = geoServer;
	}
	
	protected ModelAndView handleRequestInternal(
		HttpServletRequest httpRequest, HttpServletResponse httpResponse
	) throws Exception {
		
		try {
			try {
				dispatch( httpRequest, httpResponse );	
			}
			catch( IOException e ) {
				throw new ServiceException( e );
			}
			catch( ServletException e ) {
				throw new ServiceException( e );
			}
		}
		catch(ServiceException se) {
			String tempResponse = 
				se.getXmlResponse(geoServer.isVerboseExceptions(), httpRequest, geoServer);

            httpResponse.setContentType(geoServer.getCharSet().toString());
            httpResponse.getWriter().write(tempResponse);
		}
		
		return null;
	}
	
	AbstractService find(String service, String request) throws ServiceException {
		//map request parameters to a Request bean to handle it 
		Map requests = 
			//getApplicationContext().getBeansOfType(AbstractService.class);
			getApplicationContext().getParent().getBeansOfType(AbstractService.class);
		
		List matches = new ArrayList();
		for (Iterator itr = requests.entrySet().iterator(); itr.hasNext();) {
			Map.Entry entry = (Entry) itr.next();
			
			AbstractService bean = (AbstractService) entry.getValue();
			
			//we allow for a null service
			if (service == null) {
				if (bean.getRequest().equalsIgnoreCase(request)) {
					//we have a winner
					matches.add(bean);
				}
			}
			else {
				if (bean.getService().equalsIgnoreCase(service) && 
					bean.getRequest().equalsIgnoreCase(request)) {
					
					//we have a winner
					matches.add(bean);
				}	
			}
		}
		
		if (matches.isEmpty())
			return null;
		
		if (matches.size() > 1) {
			String msg = "Multiple requests found capable of handling:" + 
			" (" + service + "," + request + ")"; 
			throw new ServiceException( msg );
		}
		
		return (AbstractService) matches.get(0);
	}
	
	void dispatch(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
		throws ServiceException, IOException, ServletException {
		
		//mark as get request if it is indeed a get request, or a post request with content type
		// == 'application/x-www-form-urlencoded'
		boolean isGet = "GET".equalsIgnoreCase( httpRequest.getMethod() ) || 
			( httpRequest.getContentType() != null && 
			httpRequest.getContentType().startsWith( "application/x-www-form-urlencoded" ) );
	
		String service = null;
		String request = null;
		
		//look up service / request in key value pairs
		for ( Enumeration e = httpRequest.getParameterNames(); e.hasMoreElements(); ) {
			String key = (String) e.nextElement();
			if ( "service".equalsIgnoreCase( key ) ) {
				service = httpRequest.getParameter( key );
			}
			if ( "request".equalsIgnoreCase( key ) ) {
				request = httpRequest.getParameter( key );
			}
		}
		
		if (service == null || request == null) {
			//lookup in context path, (http://.../geoserver/service/request?)
			
			String path = httpRequest.getContextPath();
			String uri = httpRequest.getRequestURI();
			if (uri.length() > path.length()) {
				uri = uri.substring(path.length()+1);	
			}
			
			int index = uri.indexOf('/'); 
			if ( index != -1 ) {
				//take everything before the slash to be the service and 
				// everything after to be the request
				if (service == null)
					service = uri.substring(0,index);
				
				if (request == null)
					request = uri.substring(index+1);
			}
			else {
				//just take the whole string to be the service
				if (service == null) {
					service = uri;
				}
			}
		}	
		
		
		if (service == null || request == null) {
			//check for a POST request specifying the request as the 
			if ( !isGet ) {
				post(httpRequest,httpResponse);
				return;
			}
		}
		
		AbstractService target = find(service,request);
		
		if (target != null) {
			//we have a servlet, do it
			if ( isGet ) {
				target.doGet(httpRequest,httpResponse);
			}
			else  {
				target.doPost(httpRequest,httpResponse);
			}
			
		}
		else {
			String msg = "Could not locate service mapping to: (" 
				+ service + "," + request + ")";
			throw new ServiceException( msg );
		}
		
	}
	
	static int sequence = 0;
	void post(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
		throws ServiceException, IOException, ServletException {
		File temp;
    
        InputStream is = 
        		new BufferedInputStream(httpRequest.getInputStream());

        // REVISIT: Should do more than sequence here
        // (In case we are running two GeoServers at once)
        // - Could we use response.getHandle() in the filename?
        // - ProcessID is traditional, I don't know how to find that in Java
        sequence++;
        // test to see if we have permission to write, if not, throw an appropirate error
        try {
            	temp = File.createTempFile("wfsdispatch" + sequence, "tmp");
            	if (!temp.canRead() || !temp.canWrite())
            	{
            		String errorMsg = "Temporary-file permission problem for location: " + temp.getPath();
                	throw new IOException(errorMsg);
            	}
        } 
        catch (IOException e) {
            	String errorMsg = "Possible file permission problem. Root cause: \n" + e.toString();
            	IOException newE = new IOException(errorMsg);
            	throw newE;
        }
        
        BufferedReader disReader = null;
        BufferedReader requestReader = null;
        try {
            FileOutputStream fos = new FileOutputStream(temp);
            BufferedOutputStream out = new BufferedOutputStream(fos);
    
            int c;
    
            while (-1 != (c = is.read())) {
                out.write(c);
            }
    
            is.close();
            out.flush();
            out.close();
    
            //JD: GEOS-323, Adding char encoding support
            EncodingInfo encInfo = new EncodingInfo();
    
            try {
                disReader = new BufferedReader(
                        XmlCharsetDetector.getCharsetAwareReader(
                                new FileInputStream(temp), encInfo));
    
                requestReader = new BufferedReader(
                        XmlCharsetDetector.createReader(
                                new FileInputStream(temp), encInfo));
            } catch (Exception e) {
                /*
                 * Any exception other than WfsException will "hang up" the
                 * process - no client output, no log entries, only "Internal
                 * server error". So this is a little trick to make detector's
                 * exceptions "visible".
                 */
                throw new ServiceException(e);
            }
    
            AbstractService target = null;
            //JD: GEOS-323, Adding char encoding support
            if (disReader != null) {
                DispatcherXmlReader requestTypeAnalyzer = new DispatcherXmlReader();
            		requestTypeAnalyzer.read(disReader, httpRequest);
            		
            		target = find(requestTypeAnalyzer.getService(),requestTypeAnalyzer.getRequest());
    		} 
           
            if (target == null) {
            		String msg = "Could not locate service for request";
            		throw new ServiceException( msg );
            }
            
            if (requestReader != null) {
            		target.doPost(httpRequest,httpResponse,requestReader);
            }
            else {
            		target.doPost(httpRequest,httpResponse);
            }
        } finally {
            if(temp != null)
                temp.delete();
            if(disReader != null)
                try {disReader.close(); } catch(IOException e) {e.printStackTrace();}
            if(requestReader != null)
                try {requestReader.close(); } catch(IOException e) {e.printStackTrace();}
        }
        	
    }
}
