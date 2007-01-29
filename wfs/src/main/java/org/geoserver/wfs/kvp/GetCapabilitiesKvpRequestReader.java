/* Copyright (c) 2001, 2003 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.geoserver.wfs.kvp;

import net.opengis.wfs.GetCapabilitiesType;
import java.util.Map;


public class GetCapabilitiesKvpRequestReader extends WFSKvpRequestReader {
    public GetCapabilitiesKvpRequestReader() {
        super(GetCapabilitiesType.class);
    }

    public Object read(Object request, Map kvp) throws Exception {
        request = super.read(request, kvp);

        //TODO: this is a cite thing, make configurable
        //TODO: remove this class
        //		//version
        //		if ( kvp.c7ontainsKey( "version") ) {
        //				
        //			AcceptVersionsType acceptVersions = OWSFactory.eINSTANCE.createAcceptVersionsType();
        //			acceptVersions.getVersion().add( kvp.get( "version") );
        //			
        //			GetCapabilitiesType getCapabilities = (GetCapabilitiesType) request;
        //			getCapabilities.setAcceptVersions( acceptVersions );
        //			
        //		}
        //		
        return request;
    }
}
