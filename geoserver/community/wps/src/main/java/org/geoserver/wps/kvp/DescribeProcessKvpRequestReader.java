/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

/**
    @author lreed@refractions.net
 */

package org.geoserver.wps.kvp;

import java.util.Map;

import net.opengis.wps.DescribeProcessType;

public class DescribeProcessKvpRequestReader extends WPSKvpRequestReader
{
    public DescribeProcessKvpRequestReader()
    {
        super(DescribeProcessType.class);
    }

    public Object read(Object request, Map kvp, Map rawKvp) throws Exception
    {
        request = super.read(request, kvp, rawKvp);

        // Version arbitration could be done at this point

        return request;
    }
}