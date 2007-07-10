/* Copyright (c) 2001, 2003 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.vfny.geoserver.wfs.responses;

import com.vividsolutions.jts.geom.Geometry;
import net.sf.json.JSONException;
import org.geotools.data.FeatureLock;
import org.geotools.feature.AttributeType;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureType;
import org.vfny.geoserver.ServiceException;
import org.vfny.geoserver.global.GeoServer;
import org.vfny.geoserver.wfs.requests.FeatureRequest;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.logging.Logger;


/**
 * Handles the encoding the results of a GetFeature or GetFeatureWithLock
 * request's results to JSON
 *
 * <p>
 * The output is an implementation of the 'Draft 2' specification of http://geojson.org
 * As that spec gets finalized we will update
 *
 * <p>
 * I thought about adding a bounds to the featureType as well, but it seems
 * like it might be more useful to have it as a separate call, since it's
 * generally pretty expensive to compute and users often won't need it. But if
 * they want it they could make an explicit getBounds call (perhaps a new
 * 'resultType', like 'hits' in WFS 1.1..
 * </p>
 *
 * @author Chris Holmes
 * @version $Id$
 */
public class JSONFeatureResponseDelegate implements FeatureResponseDelegate {
    /** Standard logging instance for class */
    private static final Logger LOGGER = Logger.getLogger("org.vfny.geoserver.responses");
    public static final String formatName = "JSON";

    /**
     * the results of a getfeature request wich this object will encode as geojson
     */
    private GetFeatureResults results;

    /**
     * empty constructor required to be instantiated through
     * this.class.newInstance()
     */
    public JSONFeatureResponseDelegate() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param outputFormat DOCUMENT ME!
     *
     * @return true if <code>outputFormat</code> is GML2 or GML2-GZIP
     */
    public boolean canProduce(String outputFormat) {
        return formatName.equalsIgnoreCase(outputFormat);
    }

    /**
     * prepares for encoding into GML2 format, optionally compressing its
     * output in gzip, if outputFormat is equal to GML2-GZIP
     *
     * @param outputFormat DOCUMENT ME!
     * @param results DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public void prepare(String outputFormat, GetFeatureResults results)
        throws IOException {
        this.results = results;

        FeatureRequest request = results.getRequest();
        GeoServer config = request.getWFS().getGeoServer();

        int maxFeatures = request.getMaxFeatures();
        int serverMaxFeatures = config.getMaxFeatures();

        if (maxFeatures > serverMaxFeatures) {
            maxFeatures = serverMaxFeatures;
        }

        int numDecimals = config.getNumDecimals();

        //not sure where we specify this in WKT, might be a precision thing
        //with JTS?  We're just using toString on it...
        //transformer.setFeatureBounding(request.getWFS().isFeatureBounding());
        //transformer.setEncoding(request.getWFS().getGeoServer().getCharSet());
        //We're not using this yet, but it could be useful to report in
        //the future?
        FeatureLock featureLock = results.getFeatureLock();

        //if (featureLock != null) {
        //    transformer.setLockId(featureLock.getAuthorization());
        //}
        //transformer.setSrsName(request.getWFS().getSrsPrefix() + meta.getSRS());
    }

    /**
     * DOCUMENT ME!
     *
     * @param gs DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getContentType(GeoServer gs) {
        return "applications/json"; //This seems to be an official mime type, let's see how it works
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getContentEncoding() {
        //        return compressOutput ? "gzip" : null;
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param output DOCUMENT ME!
     *
     * @throws ServiceException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     * @throws IllegalStateException DOCUMENT ME!
     */
    public void encode(OutputStream output) throws ServiceException, IOException {
        if (results == null) {
            throw new IllegalStateException("It seems prepare() has not been called"
                + " or has not succeed");
        }

        //TODO: investigate setting proper charsets in this
        //it's part of the constructor, just need to hook it up.
        Writer outWriter = new BufferedWriter(new OutputStreamWriter(output));

        GeoJSONBuilder jsonWriter = new GeoJSONBuilder(outWriter);

        // execute should of set all the header information
        // including the lockID
        //
        // execute should also fail if all of the locks could not be aquired
        List resultsList = results.getFeatures();

        //FeatureResults[] featureResults = (FeatureResults[]) resultsList
        //    .toArray(new FeatureResults[resultsList.size()]);
        LOGGER.info("about to encode JSON");

        try {
            jsonWriter.object().key("type").value("FeatureCollection");
            //TODO: Add CRS stuff.
            jsonWriter.key("members");
            jsonWriter.array();

            for (int i = 0; i < resultsList.size(); i++) {
                FeatureCollection collection = (FeatureCollection) resultsList.get(i);

                FeatureIterator iterator = collection.features();

                try {
                    FeatureType fType;
                    AttributeType[] types;

                    while (iterator.hasNext()) {
                        Feature feature = iterator.next();
                        jsonWriter.object();
                        jsonWriter.key("type").value("Feature");
                        jsonWriter.key("id");
                        jsonWriter.value(feature.getID());

                        fType = feature.getFeatureType();
                        types = fType.getAttributeTypes();

                        AttributeType defaultGeomType = fType.getDefaultGeometry();
                        jsonWriter.key("geometry");

                        if (feature.getDefaultGeometry() != null) {
                            jsonWriter.writeGeom(feature.getDefaultGeometry());
                        } else {
                            jsonWriter.value("null");
                        }

                        jsonWriter.key("geometry_name").value(defaultGeomType.getName());
                        jsonWriter.key("properties");
                        jsonWriter.object();

                        for (int j = 0; j < types.length; j++) {
                            Object value = feature.getAttribute(j);

                            if (value != null) {
                                if (value instanceof Geometry) {
                                    //This is an area of the spec where they decided to 'let
                                    //convention evolve', that is how to handle multiple 
                                    //geometries.  My take is to print the geometry here if
                                    //it's not the default.  If it's the default that you already
                                    //printed above, so you don't need it here.
                                    if (types[j].equals(defaultGeomType)) {
                                        //Do nothing, we wrote it above
                                        //jsonWriter.value("geometry_name");
                                    } else {
                                        jsonWriter.key(types[j].getName());
                                        jsonWriter.writeGeom((Geometry) value);
                                    }
                                } else {
                                    jsonWriter.key(types[j].getName());
                                    jsonWriter.value(value);
                                }
                            } else {
                                jsonWriter.key(types[j].getName());
                                jsonWriter.value("null");
                            }
                        }

                        jsonWriter.endObject(); //end the properties
                        jsonWriter.endObject(); //end the feature
                    }
                } //catch an exception here?
                finally {
                    collection.close(iterator);
                }

                jsonWriter.endArray();
                jsonWriter.endObject();

                outWriter.flush();
            }
        } catch (JSONException jsonException) {
            ServiceException serviceException = new ServiceException(results.getRequest().getHandle()
                    + " error:" + jsonException.getMessage());
            serviceException.initCause(jsonException);
            throw serviceException;
        }
    }

    public String getContentDisposition(String featureTypeName) {
        return "inline; filename=" + featureTypeName + ".txt";
    }
}
