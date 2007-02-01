/* Copyright (c) 2001, 2003 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.geoserver.wfs.response;

import net.opengis.wfs.GetCapabilitiesType;
import org.geoserver.ows.Response;
import org.geoserver.ows.util.OwsUtils;
import org.geoserver.platform.Operation;
import org.geotools.xml.transform.TransformerBase;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import javax.xml.transform.TransformerException;


public class GetCapabilitiesResponse extends Response {
    public GetCapabilitiesResponse() {
        super(TransformerBase.class);
    }

    public String getMimeType(Object value, Operation operation) {
        GetCapabilitiesType request = (GetCapabilitiesType) OwsUtils.parameter(operation
                .getParameters(), GetCapabilitiesType.class);

        if ((request != null) && (request.getAcceptFormats() != null)) {
            //look for an accepted format
            List formats = request.getAcceptFormats().getOutputFormat();

            for (Iterator f = formats.iterator(); f.hasNext();) {
                String format = (String) f.next();

                if (format.endsWith("/xml")) {
                    return format;
                }
            }
        }

        //default
        return "application/xml";
    }

    public void write(Object value, OutputStream output, Operation operation)
        throws IOException {
        TransformerBase tx = (TransformerBase) value;

        try {
            tx.transform(this, output);
        } catch (TransformerException e) {
            throw (IOException) new IOException().initCause(e);
        }
    }
}
