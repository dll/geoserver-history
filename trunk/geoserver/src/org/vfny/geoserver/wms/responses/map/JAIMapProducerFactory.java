/* Copyright (c) 2001, 2003 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.vfny.geoserver.wms.responses.map;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.vfny.geoserver.config.WMSConfig;
import org.vfny.geoserver.wms.GetMapProducer;
import org.vfny.geoserver.wms.GetMapProducerFactorySpi;
import org.vfny.geoserver.wms.responses.helpers.JAISupport;

/**
 * DOCUMENT ME!
 * 
 * @author Gabriel Roldan, Axios Engineering
 * @version $Id$
 */
public class JAIMapProducerFactory implements GetMapProducerFactorySpi {
	/** DOCUMENT ME! */
	private static final Logger LOGGER = Logger
			.getLogger(JAIMapProducerFactory.class.getPackage().getName());

	/**
	 * 
	 */
	public JAIMapProducerFactory() {

	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getName() {
		return "Tiff backed raster maps producer";
	}

	/**
	 * The formats this delegate supports. Includes those formats supported by
	 * the Java ImageIO extension, mostly: <i>png, x-portable-graymap, jpeg,
	 * jpeg2000, x-png, tiff, vnd.wap.wbmp, x-portable-pixmap,
	 * x-portable-bitmap, bmp and x-portable-anymap</i>, but the specific ones
	 * will depend on the platform and JAI version. At leas JPEG and PNG will
	 * generally work.
	 * 
	 * @return The list of the supported formats, as returned by the Java
	 *         ImageIO extension.
	 */
	public Set getSupportedFormats() {
		return JAISupport.getSupportedFormats();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public boolean isAvailable() {
		try {
			Class.forName("javax.imageio.ImageIO");
			Class.forName("javax.media.jai.JAI");
			return true;

		} catch (ClassNotFoundException e) {
			return false;
		}

	}

	/**
	 * Evaluates if this Map producer can generate the map format specified by
	 * <code>mapFormat</code>
	 * 
	 * @param mapFormat
	 *            the mime type of the output map format requiered
	 * 
	 * @return true if class can produce a map in the passed format
	 */
	public boolean canProduce(String mapFormat) {
		return JAISupport.getSupportedFormats().contains(mapFormat);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param mapFormat
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws IllegalArgumentException
	 *             DOCUMENT ME!
	 */
	public GetMapProducer createMapProducer(String mapFormat, WMSConfig config)
			throws IllegalArgumentException {
		if (!canProduce(mapFormat))
			throw new IllegalArgumentException("Can't produce " + mapFormat
					+ " format");
		return new JAIMapProducer(mapFormat);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geotools.factory.Factory#getImplementationHints() This just
	 *      returns java.util.Collections.EMPTY_MAP
	 */
	public Map getImplementationHints() {
		return java.util.Collections.EMPTY_MAP;
	}

}
