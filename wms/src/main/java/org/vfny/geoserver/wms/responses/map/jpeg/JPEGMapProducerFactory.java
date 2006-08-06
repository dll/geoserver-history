package org.vfny.geoserver.wms.responses.map.jpeg;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.vfny.geoserver.global.WMS;
import org.vfny.geoserver.wms.GetMapProducer;
import org.vfny.geoserver.wms.GetMapProducerFactorySpi;

/**
 * Factory for a JPEG writer.
 * 
 * @author Simone Giannecchini
 * @since 1.4.x
 */
public class JPEGMapProducerFactory implements GetMapProducerFactorySpi {
	/** the only MIME type this map producer supports */
	static final String MIME_TYPE = "image/jpeg";

	public boolean canProduce(String mapFormat) {
		return MIME_TYPE.equalsIgnoreCase(mapFormat);
	}

	public GetMapProducer createMapProducer(String mapFormat, WMS wms)
			throws IllegalArgumentException {
		if (!canProduce(mapFormat)) {
			throw new IllegalArgumentException(new StringBuffer(mapFormat)
					.append(" not supported by this map producer").toString());
		}

		return new JPEGMapProducer(MIME_TYPE);
	}

	public JPEGMapProducerFactory() {
		super();
	}

	public String getName() {
		return "Joint Photographic Experts Group";
	}

	public Set getSupportedFormats() {
		return Collections.singleton(MIME_TYPE);
	}

	public boolean isAvailable() {

		try {
			return (Class
					.forName("com.sun.media.imageioimpl.plugins.jpeg.CLibJPEGImageWriter") != null)
					|| (Class
							.forName("com.sun.imageio.plugins.jpeg.JPEGImageWriter") != null);
		} catch (ClassNotFoundException e) {
			return false;
		}
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
