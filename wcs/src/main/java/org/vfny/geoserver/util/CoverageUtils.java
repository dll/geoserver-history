/* Copyright (c) 2001, 2003 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.vfny.geoserver.util;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import javax.media.jai.Interpolation;

import org.geotools.coverage.grid.GeneralGridRange;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.processing.Operations;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.resources.CRSUtilities;
import org.opengis.coverage.Coverage;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.parameter.ParameterValue;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.vfny.geoserver.global.CoverageInfo;
import org.vfny.geoserver.wcs.WcsException;
import org.vfny.geoserver.wcs.requests.CoverageRequest;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: Alessio Fabiani (alessio.fabiani@gmail.com) $ (last
 *         modification)
 * @author $Author: Simone Giannecchini (simboss1@gmail.com) $ (last
 *         modification)
 */
public class CoverageUtils {

	private final static Logger LOGGER = Logger.getLogger(CoverageUtils.class
			.toString());

	public static final int TRANSPARENT = 0;

	public static final int OPAQUE = 1;

	public static URL getResource(String path, String baseDir)
			throws MalformedURLException {
		URL url = null;
		if (path.startsWith("file:data/")) {
			path = path.substring(5); // remove 'file:' prefix

			File file = new File(baseDir, path);
			url = file.toURL();
		} else {
			url = new URL(path);
		}

		return url;
	}

	/**
	 * @param paramValues
	 * @param key
	 * @param param
	 * @return
	 */
	public static Object getCvParamValue(final String key,
			ParameterValue param, final List paramValues, final int index) {
		Object value = null;
		try {
			if (key.equalsIgnoreCase("crs")) {
				if (getParamValue(paramValues, index) != null
						&& ((String) getParamValue(paramValues, index))
								.length() > 0) {
					if (paramValues.get(index) != null
							&& ((String) paramValues.get(index)).length() > 0) 

						value = CRS.parseWKT((String) paramValues.get(index));
					} else {

						LOGGER
								.info("Unable to find a crs for the coverage param, using EPSG:4326");
						value = CRS.decode("EPSG:4326");
					}
			} else if (key.equalsIgnoreCase("envelope")) {
				if (getParamValue(paramValues, index) != null
						&& ((String) getParamValue(paramValues, index))
								.length() > 0) {
					String tmp = (String) getParamValue(paramValues, index);
					if (tmp.indexOf("[") > 0
							&& tmp.indexOf("]") > tmp.indexOf("[")) {
						tmp = tmp.substring(tmp.indexOf("[") + 1,
								tmp.indexOf("]")).trim();
						tmp = tmp.replaceAll(",", "");
						String[] strCoords = tmp.split(" ");
						double[] coords = new double[strCoords.length];
						if (strCoords.length == 4) {
							for (int iT = 0; iT < 4; iT++) {
								coords[iT] = Double.parseDouble(strCoords[iT]
										.trim());
							}

							value = (org.opengis.spatialschema.geometry.Envelope) new GeneralEnvelope(
									new double[] { coords[0], coords[1] },
									new double[] { coords[2], coords[3] });
						}
					}
				}
			} else if (key.equalsIgnoreCase("values_palette")) {
				if (getParamValue(paramValues, index) != null
						&& ((String) getParamValue(paramValues, index))
								.length() > 0) {
					String tmp = (String) getParamValue(paramValues, index);
					String[] strColors = tmp.split(";");
					Vector colors = new Vector();
					for (int col = 0; col < strColors.length; col++) {
						if (Color.decode(strColors[col]) != null) {
							colors.add(Color.decode(strColors[col]));
						}
					}

					value = colors.toArray(new Color[colors.size()]);
				} else {
					value = "#000000;#3C3C3C;#FFFFFF";
				}
			} else {
				Class[] clArray = { getParamValue(paramValues, index)
						.getClass() };
				Object[] inArray = { getParamValue(paramValues, index) };
				value = param.getValue().getClass().getConstructor(clArray)
						.newInstance(inArray);
			}

			// Intentionally generic exception catched
		} catch (Exception e) {
			value = null;
			// errors.add("paramValue[" + i + "]",
			// new ActionError("error.dataFormatEditor.param.parse", key,
			// getParamValue(i).getClass(), e));
		}

		return value;
	}

	private static String getParamValue(final List paramValues, final int index) {
		return (String) paramValues.get(index);
	}

	/**
	 * @param params
	 * @param key
	 * @param param
	 * @return
	 */
	public static Object getCvParamValue(final String key,
			ParameterValue param, final Map params) {
		Object value = null;
		try {
			if (key.equalsIgnoreCase("crs")) {
				if (params.get(key) != null
						&& ((String) params.get(key)).length() > 0) {

					value = CRS.parseWKT((String) params.get(key));
				} else {

					LOGGER
							.info("Unable to find a crs for the coverage param, using EPSG:4326");
					value = CRS.decode("EPSG:4326");
				}
			} else if (key.equalsIgnoreCase("envelope")) {
				if (params.get(key) != null
						&& ((String) params.get(key)).length() > 0) {
					String tmp = (String) params.get(key);
					if (tmp.indexOf("[") > 0
							&& tmp.indexOf("]") > tmp.indexOf("[")) {
						tmp = tmp.substring(tmp.indexOf("[") + 1,
								tmp.indexOf("]")).trim();
						tmp = tmp.replaceAll(",", "");
						String[] strCoords = tmp.split(" ");
						double[] coords = new double[strCoords.length];
						if (strCoords.length == 4) {
							for (int iT = 0; iT < 4; iT++) {
								coords[iT] = Double.parseDouble(strCoords[iT]
										.trim());
							}

							value = (org.opengis.spatialschema.geometry.Envelope) new GeneralEnvelope(
									new double[] { coords[0], coords[1] },
									new double[] { coords[2], coords[3] });
						}
					}
				}
			} else {
				Class[] clArray = { String.class };
				Object[] inArray = { params.get(key) };
				value = param.getValue().getClass().getConstructor(clArray)
						.newInstance(inArray);
			}
		} catch (Exception e) {
			value = param.getValue();
		}

		return value;
	}

	/**
	 * @param request
	 * @param outputFormat
	 * @param meta
	 * @param coverage
	 * @return
	 * @throws WcsException
	 * @throws IOException
	 * @throws FactoryException
	 * @throws TransformException
	 * @throws IndexOutOfBoundsException
	 */
	public static GridCoverage2D getCroppedCoverage(CoverageRequest request,
			CoverageInfo meta, GridCoverage coverage) throws WcsException,
			IOException, IndexOutOfBoundsException, FactoryException,
			TransformException {

		// This is the final Response CRS
		final String responseCRS = request.getResponseCRS();
		// - first check if the responseCRS is present on the Coverage
		// ResponseCRSs list
		if (!meta.getResponseCRSs().contains(responseCRS)) {
			throw new WcsException(
					"This Coverage does not support the Response CRS requested.");
		}
		// - then create the Coordinate Reference System
		final CoordinateReferenceSystem targetCRS =CRS.decode(responseCRS,true);
		// This is the CRS of the requested Envelope
		final String requestCRS = request.getCRS();
		// - first check if the requestCRS is present on the Coverage
		// RequestCRSs list
		if (!meta.getResponseCRSs().contains(requestCRS)) {
			throw new WcsException(
					"This Coverage does not support the CRS requested.");
		}
		// - then create the Coordinate Reference System
		final CoordinateReferenceSystem sourceCRS =CRS.decode(requestCRS);
		// This is the CRS of the Coverage Envelope
		final CoordinateReferenceSystem cvCRS = ((GeneralEnvelope) coverage
				.getEnvelope()).getCoordinateReferenceSystem();
		final MathTransform GCCRSTodeviceCRSTransformdeviceCRSToGCCRSTransform = CRS
				.transform(cvCRS, sourceCRS, true);
		final MathTransform deviceCRSToGCCRSTransform = GCCRSTodeviceCRSTransformdeviceCRSToGCCRSTransform
				.inverse();

		com.vividsolutions.jts.geom.Envelope envelope = request.getEnvelope();
		GeneralEnvelope destinationEnvelope;
		final boolean lonFirst = sourceCRS.getCoordinateSystem().getAxis(0)
				.getDirection().absolute().equals(AxisDirection.EAST);
		// the envelope we are provided with is lon,lat always
		if (!lonFirst)
			destinationEnvelope = new GeneralEnvelope(new double[] {
					envelope.getMinY(), envelope.getMinX() }, new double[] {
					envelope.getMaxY(), envelope.getMaxX() });
		else
			destinationEnvelope = new GeneralEnvelope(new double[] {
					envelope.getMinX(), envelope.getMinY() }, new double[] {
					envelope.getMaxX(), envelope.getMaxY() });
		destinationEnvelope.setCoordinateReferenceSystem(sourceCRS);

		// this is the destination envelope in the coverage crs
		final GeneralEnvelope destinationEnvelopeInSourceCRS = (!deviceCRSToGCCRSTransform
				.isIdentity()) ? CRSUtilities.transform(
				deviceCRSToGCCRSTransform, destinationEnvelope)
				: new GeneralEnvelope(destinationEnvelope);
		destinationEnvelopeInSourceCRS.setCoordinateReferenceSystem(cvCRS);

		// ///////////////////////////////////////////////////////////////////
		//
		// BAND SELECT
		//
		//
		// ///////////////////////////////////////////////////////////////////
		final Set params = request.getParameters().keySet();
		final int numDimensions = coverage.getNumSampleDimensions();
		final Map dims = new HashMap();
		final ArrayList selectedBands = new ArrayList();

		for (int d = 0; d < numDimensions; d++) {
			dims.put(coverage.getSampleDimension(d).getDescription().toString(
					Locale.getDefault()).toUpperCase(), new Integer(d));
		}

		if (!params.isEmpty()) {
			for (Iterator p = params.iterator(); p.hasNext();) {
				final String param = (String) p.next();
				if (dims.containsKey(param)) {
					selectedBands.add(dims.get(param));
				}
			}
		}

		final int length = selectedBands.size();
		final int[] bands = new int[length];
		for (int b = 0; b < length; b++) {
			bands[b] = ((Integer) selectedBands.get(b)).intValue();
		}

		Coverage bandSelectedCoverage;
		if (bands != null && bands.length > 0)
			bandSelectedCoverage = Operations.DEFAULT.selectSampleDimension(
					coverage, bands);
		else
			bandSelectedCoverage = coverage;

		// ///////////////////////////////////////////////////////////////////
		//
		// CROP
		//
		//
		// ///////////////////////////////////////////////////////////////////
		final GridCoverage2D croppedGridCoverage;
		final GeneralEnvelope oldEnvelope = (GeneralEnvelope) coverage
				.getEnvelope();
		// intersect the envelopes
		final GeneralEnvelope intersectionEnvelope = new GeneralEnvelope(
				destinationEnvelopeInSourceCRS);
		intersectionEnvelope.setCoordinateReferenceSystem(cvCRS);
		intersectionEnvelope.intersect((GeneralEnvelope) oldEnvelope);
		// dow we have something to show?
		if (intersectionEnvelope.isEmpty())
			throw new WcsException(
					"The Intersection is null. Check the requested BBOX!");
		if (!intersectionEnvelope.equals((GeneralEnvelope) oldEnvelope)) {
			// get the cropped grid geometry
			// final GridGeometry2D cropGridGeometry = getCroppedGridGeometry(
			// intersectionEnvelope, gridCoverage);
			croppedGridCoverage = (GridCoverage2D) Operations.DEFAULT.crop(
					bandSelectedCoverage, intersectionEnvelope);
		} else
			croppedGridCoverage = (GridCoverage2D) bandSelectedCoverage;

		// prefetch to be faster afterwards.
		// This step is important since at this stage we might be loading tiles
		// from disk
		croppedGridCoverage.prefetch(intersectionEnvelope.toRectangle2D());

		// ///////////////////////////////////////////////////////////////////
		//
		// SCALE to the needed resolution
		// Let me now scale down to the EXACT needed resolution. This step does
		// not prevent from having loaded an overview of the original image
		// based on the requested scale.
		//
		// ///////////////////////////////////////////////////////////////////
		GridCoverage2D subCoverage = croppedGridCoverage;

		GridGeometry2D scaledGridGeometry = (GridGeometry2D) coverage
				.getGridGeometry();
		if (request.getGridLow() != null && request.getGridHigh() != null) {
			final int[] lowers = new int[] {
					request.getGridLow()[0].intValue(),
					request.getGridLow()[1].intValue() };
			final int[] highers = new int[] {
					request.getGridHigh()[0].intValue(),
					request.getGridHigh()[1].intValue() };
			// new grid range
			final GeneralGridRange newGridrange = new GeneralGridRange(lowers,
					highers);
			scaledGridGeometry = new GridGeometry2D(newGridrange, coverage
					.getEnvelope());
			final GridCoverage2D scaledGridCoverage = (GridCoverage2D) Operations.DEFAULT
					.resample(croppedGridCoverage, cvCRS, scaledGridGeometry,
							Interpolation
									.getInstance(Interpolation.INTERP_NEAREST));

			subCoverage = scaledGridCoverage;
		}

		// ///////////////////////////////////////////////////////////////////
		//
		// REPROJECT
		//
		//
		// ///////////////////////////////////////////////////////////////////
		if (!sourceCRS.equals(targetCRS)) {
			final GridCoverage2D reprojectedGridCoverage;
			reprojectedGridCoverage = (GridCoverage2D) Operations.DEFAULT
					.resample(subCoverage, targetCRS, null, Interpolation
							.getInstance(Interpolation.INTERP_NEAREST));

			subCoverage = reprojectedGridCoverage;
		}

		final String interp_requested = request.getInterpolation();
		if (interp_requested != null) {
			int interp_type = -1;

			if (interp_requested.equalsIgnoreCase("nearest_neighbor"))
				interp_type = Interpolation.INTERP_NEAREST;
			else if (interp_requested.equalsIgnoreCase("bilinear"))
				interp_type = Interpolation.INTERP_BILINEAR;
			else if (interp_requested.equalsIgnoreCase("bicubic"))
				interp_type = Interpolation.INTERP_BICUBIC;
			else if (interp_requested.equalsIgnoreCase("bicubic_2"))
				interp_type = Interpolation.INTERP_BICUBIC_2;
			else
				throw new WcsException(
						"Unrecognized interpolation type. Allowed values are: nearest_neighbor, bilinear, bicubic, bicubic_2");

			subCoverage = (GridCoverage2D) Operations.DEFAULT.interpolate(
					subCoverage, Interpolation.getInstance(interp_type));
		}

		return subCoverage;
	}

	/**
	 * Convert a JTS envelope to GeneralEnvelope.
	 * 
	 * @param envelope
	 * @param mapcrs
	 * @return
	 */
	public static GeneralEnvelope convertEnvelope(
			com.vividsolutions.jts.geom.Envelope envelope,
			CoordinateReferenceSystem mapcrs) {
		final CoordinateSystem cs = mapcrs.getCoordinateSystem();
		final boolean lonfirst = !GridGeometry2D.swapXY(cs);
		final GeneralEnvelope env;
		if (lonfirst) {
			env = new GeneralEnvelope(new double[] { envelope.getMinX(),
					envelope.getMinY() }, new double[] { envelope.getMaxX(),
					envelope.getMaxY() });
		} else
			env = new GeneralEnvelope(new double[] { envelope.getMinY(),
					envelope.getMinX() }, new double[] { envelope.getMaxY(),
					envelope.getMaxX() });

		env.setCoordinateReferenceSystem(mapcrs);
		return env;
	}
}
