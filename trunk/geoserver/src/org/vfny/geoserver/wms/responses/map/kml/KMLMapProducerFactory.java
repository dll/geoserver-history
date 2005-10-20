/* Copyright (c) 2001, 2003 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.vfny.geoserver.wms.responses.map.kml;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.vfny.geoserver.config.WMSConfig;
import org.vfny.geoserver.wms.GetMapProducer;
import org.vfny.geoserver.wms.GetMapProducerFactorySpi;


/**
 * KMLMapProducerFactory
 * This class is used as part of the SPI auto discovery process which enables
 * new format producers to be plugged in.
 *
 * @version $Id$
 */
public class KMLMapProducerFactory implements GetMapProducerFactorySpi {
    /**
     * this is just to check the requested mime type starts with this string,
     * since the most common error when performing the HTTP request is not to
     * escape the '+' sign in "kml+xml", which is decoded as a space
     * character at server side.
     */
    private static final String PRODUCE_TYPE = "kml";
    
    /** Official KML mime type
     * @TODO add KMZ support
     */
    static final String MIME_TYPE = "application/vnd.google-earth.kml+xml";
    
    /** Set of supported mime types for the producers made by this Factory
     */
    private static final Set SUPPORTED_FORMATS = Collections.singleton(MIME_TYPE);
    
    /**
     * Creates a new KMLMapProducerFactory object.
     */
    public KMLMapProducerFactory() {
        super();
    }
    
    /**
     * Human readable description of output format.
     */
    public String getName() {
        return "Keyhole markup language producer";
    }
    
    /**
     * Discover what output formats are supported by the producers made by this factory.
     *
     * @return Set of supported mime types
     */
    public Set getSupportedFormats() {
        return SUPPORTED_FORMATS;
    }
    
    /**
     * Reports on the availability of this factory.  As no external libraries are
     * required for KML this should always be true.
     *
     * @return <code>true</code>
     */
    public boolean isAvailable() {
        return true;
    }
    
    /**
     * evaluates if this Map producer can generate the map format specified by
     * <code>mapFormat</code>
     *
     * <p>
     * In this case, true if <code>mapFormat</code> starts with "kml", as
     * both <code>"kml"</code> and <code>"kml+xml"</code> are
     * commonly passed.
     * </p>
     *
     * @TODO should this be image/kml and image/kml+xml?
     * @param mapFormat the mime type of the output map format requiered
     *
     * @return true if class can produce a map in the passed format.
     */
    public boolean canProduce(String mapFormat) {
        return (mapFormat != null) && mapFormat.startsWith(PRODUCE_TYPE);
    }
    
    /**
     * Create an actual instance of a KMLMapProducer.
     *
     * @param mapFormat String which MUST match the supported formats.  Call
     * canProcess fisrt if you are unsure.
     *
     * @return GetMapProducer instance.
     *
     * @throws IllegalArgumentException DOCUMENT ME!
     */
    public GetMapProducer createMapProducer(String mapFormat, WMSConfig config )
    throws IllegalArgumentException {
        return new KMLMapProducer();
    }
    
    /* (non-Javadoc)
     * @see org.geotools.factory.Factory#getImplementationHints()
     * This just returns java.util.Collections.EMPTY_MAP
     */
    public Map getImplementationHints() {
        return java.util.Collections.EMPTY_MAP;
    }
    
}
