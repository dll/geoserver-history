/* Copyright (c) 2010 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.map;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.geoserver.wms.Map;
import org.geoserver.wms.WMSMapContext;

/**
 * An already encoded {@link Map} that holds the raw response content in a byte array.
 * 
 * @author Gabriel Roldan
 */
public class RawMap extends Map {

    private WMSMapContext mapContext;

    private byte[] mapContents;

    private ByteArrayOutputStream buffer;

    public RawMap(final WMSMapContext mapContext, final byte[] mapContents, final String mimeType) {
        this.mapContext = mapContext;
        this.mapContents = mapContents;
        setMimeType(mimeType);
    }

    public RawMap(final WMSMapContext mapContext, final ByteArrayOutputStream buff,
            final String mimeType) {
        this.mapContext = mapContext;
        this.buffer = buff;
        setMimeType(mimeType);
    }

    public void writeTo(OutputStream out) throws IOException {
        if (mapContents != null) {
            out.write(mapContents);
        } else if (buffer != null) {
            buffer.writeTo(out);
        } else {
            throw new IllegalStateException();
        }
    }

    public WMSMapContext getMapContext() {
        return mapContext;
    }

}