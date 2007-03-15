/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.vfny.geoserver.wms.responses;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.vfny.geoserver.config.WMSConfig;
import org.vfny.geoserver.global.WMS;
import org.vfny.geoserver.wms.GetMapProducer;
import org.vfny.geoserver.wms.WMSMapContext;
import org.vfny.geoserver.wms.WmsException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;


/**
 * Abstract base class for GetMapProducers that relies in LiteRenderer for
 * creating the raster map and then outputs it in the format they specializes
 * in.<p>This class does the job of producing a BufferedImage using
 * geotools LiteRenderer, so it should be enough for a subclass to implement
 * {@linkPlain #formatImageOutputStream(String, BufferedImage, OutputStream)}</p>
 *  <p>Generates a map using the geotools jai rendering classes. Uses the
 * Lite renderer, loading the data on the fly, which is quite nice. Thanks
 * Andrea and Gabriel. The word is that we should eventually switch over to
 * StyledMapRenderer and do some fancy stuff with caching layers, but I think
 * we are a ways off with its maturity to try that yet. So Lite treats us
 * quite well, as it is stateless and therefor loads up nice and fast.</p>
 *  <p></p>
 *
 * @author Chris Holmes, TOPP
 * @author Simone Giannecchini, GeoSolutions
 * @version $Id: JAIMapResponse.java,v 1.29 2004/09/16 21:44:28 cholmesny Exp $
 */
public abstract class DefaultRasterMapProducer implements GetMapProducer {
    private final static Interpolation NN_INTERPOLATION = new InterpolationNearest();
    private final static Interpolation BIL_INTERPOLATION = new InterpolationBilinear();
    private final static Interpolation BIC_INTERPOLATION = new InterpolationBicubic2(0);

    /** A logger for this class. */
    private static final Logger LOGGER = Logger.getLogger("org.vfny.geoserver.responses.wms.map");

    /** Which format to encode the image in if one is not supplied */
    private static final String DEFAULT_MAP_FORMAT = "image/png";

    /** WMS Service configuration */
    private WMS wms;

    /** The image generated by the execute method. */
    private BufferedImage image;

    /** The one to do the magic of rendering a map */
    private GTRenderer renderer;

    /**
     * Set in produceMap(...) from the requested output format, it's
     * holded just to be sure that method has been called before
     * getContentType() thus supporting the workflow contract of the request
     * processing
     */
    private String format = null;

    /**
     * set to <code>true</code> on <code>abort()</code> so
     * <code>produceMap</code> leaves the image being worked on to the garbage
     * collector.
     */
    private boolean abortRequested;

    /**
     * Holds the map context passed to produceMap, so subclasses can
     * use it if they need it from inside {@linkPlain
     * #formatImageOutputStream(String, BufferedImage, OutputStream)}
     */
    private WMSMapContext mapContext;

    /**
             *
             */
    public DefaultRasterMapProducer() {
        this(DEFAULT_MAP_FORMAT, null);
    }

    /**
             *
             */
    public DefaultRasterMapProducer(WMS wms) {
        this(DEFAULT_MAP_FORMAT, wms);
    }

    /**
             *
             */
    public DefaultRasterMapProducer(String outputFormat, WMS wms) {
        setOutputFormat(outputFormat);
        this.wms = wms;
    }

    /**
     * Sets the MIME type of the output image.
     *
     * @param format the desired output map format.
     */
    public void setOutputFormat(String format) {
        this.format = format;
    }

    /**
     * Writes the image to the client.
     *
     * @param out The output stream to write to.
     */
    public void writeTo(OutputStream out)
        throws org.vfny.geoserver.ServiceException, java.io.IOException {
        formatImageOutputStream(this.format, this.image, out);
    }

    /**
     * Halts the loading. Right now just calls renderer.stopRendering.
     */
    public void abort() {
        this.abortRequested = true;

        if (this.renderer != null) {
            this.renderer.stopRendering();
        }
    }

    /**
     * Gets the content type. This is set by the request, should only
     * be called after execute. GetMapResponse should handle this though.
     *
     * @return The mime type that this response will generate.
     *
     * @throws IllegalStateException DOCUMENT ME!
     */
    public String getContentType() throws java.lang.IllegalStateException {
        if (this.format == null) {
            throw new IllegalStateException("the output map format was not yet specified");
        }

        return this.format;
    }

    /**
     * returns the content encoding for the output data (null for this
     * class)
     *
     * @return <code>null</code> since no special encoding is performed while
     *         wrtting to the output stream. Do not confuse this with
     *         getMimeType().
     */
    public String getContentEncoding() {
        return null;
    }

    /**
     * Performs the execute request using geotools rendering.
     *
     * @param map The information on the types requested.
     *
     * @throws WmsException For any problems.
     */
    public void produceMap(WMSMapContext map) throws WmsException {
        this.mapContext = map;

        final int width = map.getMapWidth();
        final int height = map.getMapHeight();

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine(new StringBuffer("setting up ").append(width).append("x").append(height)
                                                       .append(" image").toString());
        }

        BufferedImage curImage = prepareImage(width, height);
        final Graphics2D graphic = curImage.createGraphics();

        if (!map.isTransparent()) {
            graphic.setColor(map.getBgColor());
            graphic.fillRect(0, 0, width, height);
        } else {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("setting to transparent");
            }

            int type = AlphaComposite.SRC;
            graphic.setComposite(AlphaComposite.getInstance(type));

            Color c = new Color(map.getBgColor().getRed(), map.getBgColor().getGreen(),
                    map.getBgColor().getBlue(), 0);
            graphic.setBackground(map.getBgColor());
            graphic.setColor(c);
            graphic.fillRect(0, 0, width, height);
            type = AlphaComposite.SRC_OVER;
            graphic.setComposite(AlphaComposite.getInstance(type));
        }

        Rectangle paintArea = new Rectangle(width, height);

        renderer = new StreamingRenderer();
        renderer.setContext(map);

        Map hintsMap = new HashMap();
        hintsMap.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // turn off/on interpolation rendering hint
        if ((wms != null) && WMSConfig.INT_NEAREST.equals(wms.getAllowInterpolation())) {
            hintsMap.put(JAI.KEY_INTERPOLATION, NN_INTERPOLATION);
        } else if ((wms != null) && WMSConfig.INT_BIlINEAR.equals(wms.getAllowInterpolation())) {
            hintsMap.put(JAI.KEY_INTERPOLATION, BIL_INTERPOLATION);
        } else if ((wms != null) && WMSConfig.INT_BICUBIC.equals(wms.getAllowInterpolation())) {
            hintsMap.put(JAI.KEY_INTERPOLATION, BIC_INTERPOLATION);
        }

        RenderingHints hints = new RenderingHints(hintsMap);
        renderer.setJava2DHints(hints);

        // we already do everything that the optimized data loading does...
        // if we set it to true then it does it all twice...
        Map rendererParams = new HashMap();
        rendererParams.put("optimizedDataLoadingEnabled", new Boolean(true));
        rendererParams.put("renderingBuffer", new Integer(map.getBuffer()));
        renderer.setRendererHints(rendererParams);

        final ReferencedEnvelope dataArea = map.getAreaOfInterest();

        if (this.abortRequested) {
            graphic.dispose();

            return;
        }

        renderer.paint(graphic, paintArea, dataArea);

        map = null;
        graphic.dispose();

        if (!this.abortRequested) {
            this.image = curImage;
        }
    }

    /**
     * This is the method subclases must implement to transform the
     * rendered image into the appropriate format, streaming to the output
     * stream.
     *
     * @param format The name of the format
     * @param image The image to be formatted.
     * @param outStream The stream to write to.
     *
     * @throws WmsException
     * @throws IOException DOCUMENT ME!
     */
    protected abstract void formatImageOutputStream(String format, BufferedImage image,
        OutputStream outStream) throws WmsException, IOException;

    /**
     * This is a package protected method with the sole purpose of
     * facilitate unit testing. Do not use by any means for oher purposes.
     *
     * @return DOCUMENT ME!
     */
    public BufferedImage getImage() {
        return this.image;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected WMSMapContext getMapContext() {
        return this.mapContext;
    }

    protected abstract BufferedImage prepareImage(int width, int height);
}
