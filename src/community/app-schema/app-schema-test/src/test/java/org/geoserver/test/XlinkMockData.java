/* 
 * Copyright (c) 2001 - 2008 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.test;

import org.geoserver.data.test.MockData;
import org.geotools.data.complex.AppSchemaDataAccess;

/**
 * Mock data for testing integration of {@link AppSchemaDataAccess} with GeoServer.
 * 
 * Inspired by {@link MockData}.
 * 
 * @author Ben Caradoc-Davies, CSIRO Exploration and Mining
 */
public class XlinkMockData extends AbstractAppSchemaMockData {

    public static final String GSML_PREFIX = AbstractAppSchemaMockData.GSML_NAMESPACE_PREFIX;

    @Override
    public void addContent() {
        addFeatureType(GSML_PREFIX, "MappedFeature", "MappedFeatureXlink.xml",
                "MappedFeaturePropertyfile.properties");
    }

}
