/*
 *    Geotools2 - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2002, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
/* Copyright (c) 2001, 2003 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.vfny.geoserver.responses.wms.map;

import org.vfny.geoserver.responses.wms.GetMapProducerFactorySpi;
import java.util.Collections;
import java.util.Set;


/**
 * DOCUMENT ME!
 *
 * @author Gabriel Roldan, Axios Engineering
 * @version $Id$
 */
public class SvgMapProducerFactory implements GetMapProducerFactorySpi {
    /**
     * this is just to check the requested mime type starts with this string,
     * since the most common error when performing the HTTP request is not to
     * escape the '+' sign in "image/svg+xml", which is decoded as a space
     * character at server side.
     */
    private static final String PRODUCE_TYPE = "image/svg";

    /** DOCUMENT ME! */
    static final String MIME_TYPE = "image/svg+xml";

    /** DOCUMENT ME! */
    private static final Set SUPPORTED_FORMATS = Collections.singleton(MIME_TYPE);

    /**
     * Creates a new SvgMapProducerFactory object.
     */
    public SvgMapProducerFactory() {
        super();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getName() {
        return "Scalable Vector Graphics (SVG) map producer";
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Set getSupportedFormats() {
        return SUPPORTED_FORMATS;
    }

    /**
     * By now SVG map producer does not have external dependencied (such as
     * Batik), so just returns <code>true</code>.
     * 
     * <p>
     * It is most probable that this situation change in the future, like when
     * adding Styling support.
     * </p>
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
     * In this case, true if <code>mapFormat</code> starts with "image/svg", as
     * both <code>"image/svg"</code> and <code>"image/svg+xml"</code> are
     * commonly passed.
     * </p>
     *
     * @param mapFormat the mime type of the output map format requiered
     *
     * @return true if class can produce a map in the passed format.
     */
    public boolean canProduce(String mapFormat) {
        return (mapFormat != null) && mapFormat.startsWith(PRODUCE_TYPE);
    }

    /**
     * DOCUMENT ME!
     *
     * @param mapFormat DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IllegalArgumentException DOCUMENT ME!
     */
    public GetMapProducer createMapProducer(String mapFormat)
        throws IllegalArgumentException {
        return new SVGMapProducer();
    }
}
