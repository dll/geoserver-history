/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.geoserver.restconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.vfny.geoserver.config.DataConfig;
import org.vfny.geoserver.config.DataStoreConfig;
import org.vfny.geoserver.config.FeatureTypeConfig;
import org.vfny.geoserver.util.DataStoreUtils;
import org.geotools.data.DataStore;

import javax.servlet.ServletContext;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;

import org.geoserver.rest.MapResource;
import org.geoserver.rest.format.DataFormat;
import org.geoserver.rest.format.FreemarkerFormat;
import org.geoserver.rest.format.MapJSONFormat;
import org.geoserver.rest.format.MapXMLFormat;

/**
 * Restlet for DataStore resources
 *
 * @author Arne Kepp <ak@openplans.org> , The Open Planning Project
 */
public class FeatureTypeListResource extends MapResource {
    private DataConfig myDC;

    public FeatureTypeListResource(DataConfig dc, Context context, Request request, Response response){
        super( context, request, response );
        setDataConfig(dc);
    }

    public void setDataConfig(DataConfig dc){
        myDC = dc;
    }

    public DataConfig getDataConfig(){
        return myDC;
    }

    @Override
    protected Map<String, DataFormat> createSupportedFormats(Request request,
            Response response) {
        Map m = new HashMap();

        m.put("html", new FreemarkerFormat("HTMLTemplates/featuretypes.ftl", getClass(), MediaType.TEXT_HTML));
        m.put("json", new MapJSONFormat());
        m.put("xml", new MapXMLFormat("FeatureTypes"));
        m.put(null, m.get("html"));

        return m;
    }

    public Map getMap() {
    	String dataStoreName = (String)getRequest().getAttributes().get("folder");
        DataStoreConfig dsc = (DataStoreConfig)myDC.getDataStores().get(dataStoreName);
    	
    	if (dsc != null){
            try{
            Map m = new HashMap();
            List configured = new ArrayList();
            List available = new ArrayList();
            DataStore store = DataStoreUtils.acquireDataStore(dsc.getConnectionParams(), (ServletContext)null);

            String[] featureTypes = store.getTypeNames();
            for (int i = 0; i < featureTypes.length; i++){
                if (myDC.getFeaturesTypes().containsKey(dataStoreName + ":" + featureTypes[i])){
                    configured.add(featureTypes[i]);
                } else {
                    available.add(featureTypes[i]);
                }
            }
            
            m.put("Configured", configured);
            m.put("Available", available);
            
            return m;    		
            } catch (Exception e){
                LOG.severe("Failure while retrieving " + dataStoreName + "; " + e);
                e.printStackTrace();
                return null;
            }
    	} else return null;    	
    }

    public boolean allowGet() {
        return true;
    }
}
