/* Copyright (c) 2001, 2003 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.vfny.geoserver.servlets.wfs;

import org.vfny.geoserver.*;
import org.vfny.geoserver.requests.readers.*;
import org.vfny.geoserver.requests.readers.wfs.GetFeatureKvpReader;
import org.vfny.geoserver.requests.readers.wfs.GetFeatureXmlReader;
import org.vfny.geoserver.responses.*;
import org.vfny.geoserver.responses.wfs.*;
import org.vfny.geoserver.servlets.*;
import java.util.*;


/**
 * Implements the WFS GetFeatureWithLock interface, which responds to requests
 * for GML, locking the features as well. This servlet accepts a
 * getFeatureWithLock request and returns GML2.1 structured XML docs.
 *
 * @author Chris Holmes, TOPP
 * @version $Id: FeatureWithLock.java,v 1.2 2003/12/16 18:46:10 cholmesny Exp $
 */
public class FeatureWithLock extends WFService {
    /**
     * Gets the response handler.  FeatureResponse handles GetFeatureWithLock.
     *
     * @return A new FeatureResponse object.
     */
    protected Response getResponseHandler() {
        return new FeatureResponse();
    }

    /**
     * Gets a FeatureKvpReader guaranteed to have a GETFEAUTREWITHLOCK request.
     *
     * @param params the kvp pairs to turn into the request object.
     *
     * @return The kvp request reader.
     */
    protected KvpRequestReader getKvpReader(Map params) {
        //if it comes through this servlet then it should be a featurewithlock,
        //add it on if the client forgot it.
        params.put("REQUEST", "GETFEATUREWITHLOCK");

        return new GetFeatureKvpReader(params);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected XmlRequestReader getXmlRequestReader() {
        return new GetFeatureXmlReader();
    }
}
