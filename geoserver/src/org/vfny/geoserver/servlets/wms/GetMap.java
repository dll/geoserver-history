/* Copyright (c) 2001, 2003 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.vfny.geoserver.servlets.wms;

import org.vfny.geoserver.requests.readers.*;
import org.vfny.geoserver.requests.readers.wms.*;
import org.vfny.geoserver.responses.Response;
import org.vfny.geoserver.responses.wms.GetMapResponse;
import org.vfny.geoserver.servlets.*;
import java.util.*;


/**
 * WMS service wich returns request and response handlers to manage a GetMap
 * request
 *
 * @author Gabriel Rold�n
 * @version $Id: GetMap.java,v 1.1.2.1 2003/11/14 20:39:15 groldan Exp $
 */
public class GetMap extends WMService {
    /**
     * Creates a new GetMap object.
     */
    public GetMap() {
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected Response getResponseHandler() {
        return new GetMapResponse();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected XmlRequestReader getXmlRequestReader() {
        /**
         * @todo Implement this org.vfny.geoserver.servlets.AbstractService
         *       abstract method
         */
        throw new java.lang.UnsupportedOperationException(
            "Method getXmlRequestReader() not yet implemented.");
    }

    /**
     * DOCUMENT ME!
     *
     * @param params DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected KvpRequestReader getKvpReader(Map params) {
        return new GetMapKvpReader(params);
    }
}
