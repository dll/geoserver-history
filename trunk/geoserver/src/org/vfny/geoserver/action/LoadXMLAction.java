/* Copyright (c) 2001, 2003 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.vfny.geoserver.action;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.geotools.validation.xml.XMLReader;
import org.vfny.geoserver.config.validation.ValidationConfig;
import org.vfny.geoserver.global.ConfigurationException;
import org.vfny.geoserver.global.GeoserverDataDirectory;
import org.vfny.geoserver.global.UserContainer;
import org.vfny.geoserver.global.WFS;
import org.vfny.geoserver.global.dto.DataDTO;
import org.vfny.geoserver.global.dto.GeoServerDTO;
import org.vfny.geoserver.global.dto.WCSDTO;
import org.vfny.geoserver.global.dto.WFSDTO;
import org.vfny.geoserver.global.dto.WMSDTO;
import org.vfny.geoserver.global.xml.XMLConfigReader;


/**
 * Load GeoServer configuration.
 * 
 * <p>
 * The existing getServer instances is updated with a call to load(..) based on
 * the existing XML configuration files.
 * </p>
 * 
 * <p>
 * It seems this class also creates the GeoServer instance in a lazy fashion!
 * That would mean that if this class cannot load, the application cannot
 * load? This could not possibly be the case, because the load action should
 * only appear when logged in.
 * </p>
 * 
 * <p>
 * Load need to remain on the current page, right now it takes us on a wild
 * ride back to the welcome screen.
 * </p>
 * 
 * <p>
 * Q: Does this need to load the Validation Processor as well?
 * </p>
 * @REVISIT: There seems to be quite a bit of code duplication in this class
 *           with GeoServerPlugIn, loading things, especially with the
 *           validation stuff.  Anyway we could cut that down?  Have one call
 *           the other?  Too close to release to do sucha refactoring but
 *           in 1.4 we should. -CH
 */
public class LoadXMLAction extends ConfigAction {
    public ActionForward execute(ActionMapping mapping, ActionForm form,
        UserContainer user, HttpServletRequest request,
        HttpServletResponse response) throws IOException, ServletException {
        ActionForward r1 = loadValidation(mapping, form, request, response);
        ActionForward r2 = loadGeoserver(mapping, form, request, response);

        return mapping.findForward("config");
    }

    private ActionForward loadGeoserver(ActionMapping mapping, ActionForm form,
        
    //UserContainer user,
    HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        ServletContext sc = request.getSession().getServletContext();

        WMSDTO wmsDTO = null;
        WFSDTO wfsDTO = null;
        WCSDTO wcsDTO = null;
        GeoServerDTO geoserverDTO = null;
        DataDTO dataDTO = null;
        //DJB: changed for geoserver_data_dir    
       // File rootDir = new File(sc.getRealPath("/"));
        
        File rootDir =  GeoserverDataDirectory.getGeoserverDataDirectory(sc);

        XMLConfigReader configReader;

        try {
            configReader = new XMLConfigReader(rootDir,sc);
        } catch (ConfigurationException configException) {
            configException.printStackTrace();

            return mapping.findForward("welcome");

            //throw new ServletException( configException );
        }

        if (configReader.isInitialized()) {
            // These are on separate lines so we can tell with the
            // stack trace/debugger where things go wrong
            wmsDTO = configReader.getWms();
            wfsDTO = configReader.getWfs();
            wcsDTO = configReader.getWcs();
            geoserverDTO = configReader.getGeoServer();
            dataDTO = configReader.getData();
        } else {
            System.err.println(
                "Config Reader not initialized for LoadXMLAction.execute().");

            return mapping.findForward("welcome");

            // throw new ServletException( new ConfigurationException( "An error occured loading the initial configuration" ));
        }

        // Update GeoServer
        try {
        	getWCS(request).load(wcsDTO);
            getWFS(request).load(wfsDTO);
            getWMS(request).load(wmsDTO);
            getWCS(request).getGeoServer().load(geoserverDTO,sc);
            getWCS(request).getData().load(dataDTO);
            getWFS(request).getGeoServer().load(geoserverDTO,sc);
            getWFS(request).getData().load(dataDTO);
        } catch (ConfigurationException configException) {
            configException.printStackTrace();

            return mapping.findForward("welcome");

            //			throw new ServletException( configException );			
        }

        // Update Config
        getGlobalConfig().update(geoserverDTO);
        getDataConfig().update(dataDTO);
        getWCSConfig().update(wcsDTO);
        getWFSConfig().update(wfsDTO);
        getWMSConfig().update(wmsDTO);

        getApplicationState(request).notifyLoadXML();

        // We need to stash the current page?
        // or can we use null or something?
        //
        LOGGER.finer("request:" + request.getServletPath());
        LOGGER.finer("forward:" + mapping.getForward());

        return mapping.findForward("config");
    }

    private ActionForward loadValidation(ActionMapping mapping,
        ActionForm form, 
    //UserContainer user,
    HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        ServletContext sc = request.getSession().getServletContext();

        WFS wfs = getWFS(request);

        if (wfs == null) {
            // lazy creation on load?
            loadGeoserver(mapping, form, request, response);
        }

        //CH- fixed for data dir, looks like this got missed first time around.
        File rootDir = GeoserverDataDirectory.getGeoserverDataDirectory(sc);

        try {
            File plugInDir = findConfigDir(rootDir, "plugIns");
            File validationDir = findConfigDir(rootDir, "validation");
            Map plugIns = XMLReader.loadPlugIns(plugInDir);
            Map testSuites = XMLReader.loadValidations(validationDir, plugIns);
            ValidationConfig vc = new ValidationConfig(plugIns, testSuites);
            sc.setAttribute(ValidationConfig.CONFIG_KEY, vc);
        } catch (Exception e) {
            // LOG error
            e.printStackTrace();

            return mapping.findForward("config.validation");
        }

        return mapping.findForward("config.validation");
    }

    private File findConfigDir(File rootDir, String name) throws Exception {
	return GeoserverDataDirectory.findConfigDir(rootDir, name);
    }
}
