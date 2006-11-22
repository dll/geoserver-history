package org.geoserver.wfs.http.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.geoserver.http.util.ResponseUtils;

import org.geoserver.ows.Service;
import org.geoserver.ows.ServiceException;
import org.geoserver.ows.http.DefaultServiceExceptionHandler;
import org.geoserver.ows.http.ServiceExceptionHandler;
import org.geoserver.wfs.WFS;

/**
 * Handles a wfs service exception by producing an exception report.
 * 
 * @author Justin Deoliveira, The Open Planning Project
 *
 */
public class WfsExceptionHandler extends DefaultServiceExceptionHandler {

	/**
	 * WFS configuration
	 */
	WFS wfs;
	
	/**
	 * @param service The wfs service descriptors.
	 */
	public WfsExceptionHandler( List services, WFS wfs ) {
		super( services );
		this.wfs = wfs;
	}
	
	/**
	 * Encodes a ogc:ServiceExceptionReport to output.
	 */
	public void handleServiceException(ServiceException e,
			Service service, HttpServletResponse response ) {

		String version = service.getVersion();
		if ( "1.0.0".equals( version ) ) {
			handle1_0( e, response );
		}
		else {
			super.handleServiceException( e, service, response );
		}
		
	}
	
	public void handle1_0( ServiceException e,
			 HttpServletResponse response ) {
		
		try {
			String tab = "   ";
			
			StringBuffer s = new StringBuffer();
			s.append( "<?xml version=\"1.0\" ?>\n" );
			s.append( "<ServiceExceptionReport\n" );
			s.append( tab + "version=\"1.2.0\"\n" );
			s.append( tab + "xmlns=\"http://www.opengis.net/ogc\"\n" );
			s.append( tab + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" );
			s.append( tab );
			s.append( "xsi:schemaLocation=\"http://www.opengis.net/ogc " );
			s.append( ResponseUtils.appendPath( wfs.getSchemaBaseURL(), "wfs/1.0.0/OGC-exception.xsd") + "\">\n" );

			s.append( tab + "<ServiceException" );
			if ( e.getCode() != null && !e.getCode().equals( "" ) ) {
				s.append( " code=\"" + e.getCode() + "\"" );
			}
			if ( e.getLocator() != null && !e.getLocator().equals( "" ) ) {
	            s.append(" locator=\"" + e.getLocator() + "\"" );
	        }
			s.append( ">" );
			
			if ( e.getMessage() != null && !e.getMessage().equals( "" ) ) {
				s.append( "\n" + tab + tab );
				s.append( ResponseUtils.encodeXML( e.getMessage() ) );
				
				ByteArrayOutputStream stackTrace = new ByteArrayOutputStream();
				e.printStackTrace( new PrintStream( stackTrace ) );
				
				s.append( ResponseUtils.encodeXML( new String( stackTrace.toByteArray() ) ) );
			}
			

			s.append( "\n</ServiceException>" );
			s.append( "</ServiceExceptionReport>" );
			
			response.setContentType( "text/xml" );
			response.setCharacterEncoding( "UTF-8" );
			response.getOutputStream().write( s.toString().getBytes() );
			response.getOutputStream().flush();
			
		} 
		catch (IOException ioe) {
			throw new RuntimeException( ioe );
		}
	

	}

//	public void handle1_1( ServiceException e,
//			 HttpServletResponse response ) {
//		
//		try {
//			String tab = "   ";
//			
//			StringBuffer s = new StringBuffer();
//			s.append( "<?xml version=\"1.0\" ?>\n" );
//			s.append( "<ExceptionReport\n" );
//			s.append( tab + "version=\"1.1.0\"\n" );
//			s.append( tab + "xmlns=\"http://www.opengis.net/ows\"\n" );
//			s.append( tab + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" );
//			s.append( tab );
//			s.append( "xsi:schemaLocation=\"http://www.opengis.net/ows " );
//			//TODO: dont hardcode schmea location
//			s.append( "http://schemas.opengis.net/ows/1.0.0/owsExceptionReport.xsd\">\n" );
//
//			s.append( tab + "<Exception" );
//			s.append( " exceptionCode=\"" + e.getCode() + "\"" );
//			
//			if ( e.getLocator() != null && !e.getLocator().equals( "" ) ) {
//	            s.append(" locator=\"" + e.getLocator() + "\"" );
//	        }
//			s.append( ">");
//			
//			if ( e.getMessage() != null && !e.getMessage().equals( "" ) ) {
//				s.append( "\n" + tab + tab );
//				s.append( "<ExceptionText>\n");
//				s.append( ResponseUtils.encodeXML( e.getMessage() ) );
//				
//				ByteArrayOutputStream stackTrace = new ByteArrayOutputStream();
//				e.printStackTrace( new PrintStream( stackTrace ) );
//				
//				s.append( ResponseUtils.encodeXML( new String( stackTrace.toByteArray() ) ) );
//				s.append( "</ExceptionText>\n");
//			}
//			
//
//			s.append( "\n</Exception>" );
//			s.append( "</ExceptionReport>" );
//			
//			response.setContentType( "text/xml" );
//			response.setCharacterEncoding( "UTF-8" );
//			response.getOutputStream().write( s.toString().getBytes() );
//			response.getOutputStream().flush();
//			
//		} 
//		catch (IOException ioe) {
//			throw new RuntimeException( ioe );
//		}
//	}
}
