/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.vfny.geoserver.wms.responses.map.tiff;

import com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriter;
import com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriterSpi;
import org.vfny.geoserver.global.WMS;
import org.vfny.geoserver.wms.WmsException;
import org.vfny.geoserver.wms.responses.DefaultRasterMapProducer;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;


/**
 * Map producer for producing Tiff images out of a map.
 *
 * @author Simone Giannecchini
 * @since 1.4.x
 *
 */
public final class TiffMapProducer extends DefaultRasterMapProducer {
    /** A logger for this class. */
    private static final Logger LOGGER = Logger.getLogger("org.vfny.geoserver.responses.wms.map");

    /** DOCUMENT ME! */
    private static final String DEFAULT_MAP_FORMAT = "image/tiff";

    /**
     * Creates a map producer that relies on JAI to encode the BufferedImage
     * generated the default (image/png) image format.
     */
    public TiffMapProducer(WMS wms) {
        this(DEFAULT_MAP_FORMAT, wms);
    }

    /**
     * Creates a map producer that relies on JAI to encode the BufferedImage
     * generated in <code>outputFormat</code> format.
     *
     * @param outputFormat
     *            the output format MIME type.
     */
    public TiffMapProducer(String outputFormat, WMS wms) {
        super(outputFormat, wms);
        setOutputFormat(outputFormat);
    }

    /**
     * Transforms the rendered image into the appropriate format, streaming to
     * the output stream.
     * @param image
     *            The image to be formatted.
     * @param outStream
     *            The stream to write to.
     *
     * @throws WmsException
     *             not really.
     * @throws IOException
     *             if the image writing fails.
     */
    public void formatImageOutputStream(BufferedImage image, OutputStream outStream)
        throws WmsException, IOException {
        //getting a writer
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Getting a writer for tiff");
        }

        final ImageWriter writer = new TIFFImageWriter(new TIFFImageWriterSpi());

        //getting a stream caching in memory
        final ImageOutputStream ioutstream = new MemoryCacheImageOutputStream(outStream);

        // tiff
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Writing for tiff");
        }

        writer.setOutput(ioutstream);
        writer.write(image);
        ioutstream.close();
        writer.dispose();

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Writing tiff image done!");
        }
    }

    public String getContentDisposition() {
        // can be null
        return null;
    }
}
