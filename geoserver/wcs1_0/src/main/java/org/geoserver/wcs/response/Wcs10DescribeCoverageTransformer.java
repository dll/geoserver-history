/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wcs.response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import net.opengis.wcs10.DescribeCoverageType;

import org.geoserver.ows.util.RequestUtils;
import org.geotools.coverage.io.CoverageResponse;
import org.geotools.coverage.io.CoverageSource;
import org.geotools.coverage.io.CoverageAccess.AccessType;
import org.geotools.coverage.io.CoverageResponse.Status;
import org.geotools.coverage.io.impl.DefaultCoverageReadRequest;
import org.geotools.coverage.io.range.Axis;
import org.geotools.coverage.io.range.FieldType;
import org.geotools.coverage.io.range.RangeType;
import org.geotools.feature.NameImpl;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.LinearTransform;
import org.geotools.util.logging.Logging;
import org.geotools.xml.transform.TransformerBase;
import org.geotools.xml.transform.Translator;
import org.opengis.coverage.Coverage;
import org.opengis.coverage.SampleDimension;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.util.InternationalString;
import org.vfny.geoserver.global.CoverageInfo;
import org.vfny.geoserver.global.Data;
import org.vfny.geoserver.global.MetaDataLink;
import org.vfny.geoserver.global.WCS;
import org.vfny.geoserver.wcs.WcsException;
import org.vfny.geoserver.wcs.WcsException.WcsExceptionCode;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Based on the <code>org.geotools.xml.transform</code> framework, does the
 * job of encoding a WCS 1.0.0 DescribeCoverage document.
 * 
 * @author Andrea Aime, TOPP
 * @author Alessio Fabiani, GeoSolutions
 * 
 */
public class Wcs10DescribeCoverageTransformer extends TransformerBase {
    private static final Logger LOGGER = Logging.getLogger(Wcs10DescribeCoverageTransformer.class
            .getPackage().getName());

    private static final String WCS_URI = "http://www.opengis.net/wcs";

    private static final String XSI_PREFIX = "xsi";

    private static final String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";

    private static final Map<String, String> METHOD_NAME_MAP = new HashMap<String, String>();

    static {
        METHOD_NAME_MAP.put("nearest neighbor", "nearest");
        METHOD_NAME_MAP.put("bilinear", "linear");
        METHOD_NAME_MAP.put("bicubic", "cubic");
    }

    private WCS wcs;

    private Data catalog;

    /**
     * Creates a new WFSCapsTransformer object.
     */
    public Wcs10DescribeCoverageTransformer(WCS wcs, Data catalog) {
        super();
        this.wcs = wcs;
        this.catalog = catalog;
        setNamespaceDeclarationEnabled(false);
    }

    public Translator createTranslator(ContentHandler handler) {
        return new WCS100DescribeCoverageTranslator(handler);
    }

    private class WCS100DescribeCoverageTranslator extends TranslatorSupport {
        private DescribeCoverageType request;

        private String proxifiedBaseUrl;

        /**
         * Creates a new WFSCapsTranslator object.
         * 
         * @param handler
         *            DOCUMENT ME!
         */
        public WCS100DescribeCoverageTranslator(ContentHandler handler) {
            super(handler, null, null);
        }

        /**
         * Encode the object.
         * 
         * @param o
         *            The Object to encode.
         * 
         * @throws IllegalArgumentException
         *             if the Object is not encodeable.
         */
        public void encode(Object o) throws IllegalArgumentException {
            // try {
            if (!(o instanceof DescribeCoverageType)) {
                throw new IllegalArgumentException(new StringBuffer("Not a GetCapabilitiesType: ")
                        .append(o).toString());
            }

            this.request = (DescribeCoverageType) o;

            final AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute("", "xmlns:wcs", "xmlns:wcs", "", WCS_URI);
            attributes.addAttribute("", "xmlns:xlink", "xmlns:xlink", "", "http://www.w3.org/1999/xlink");
            attributes.addAttribute("", "xmlns:ogc", "xmlns:ogc", "", "http://www.opengis.net/ogc");
            attributes.addAttribute("", "xmlns:ows", "xmlns:ows", "", "http://www.opengis.net/ows/1.1");
            attributes.addAttribute("", "xmlns:gml", "xmlns:gml", "", "http://www.opengis.net/gml");

            final String prefixDef = new StringBuffer("xmlns:").append(XSI_PREFIX).toString();
            attributes.addAttribute("", prefixDef, prefixDef, "", XSI_URI);

            final String locationAtt = new StringBuffer(XSI_PREFIX).append(":schemaLocation").toString();

            proxifiedBaseUrl = RequestUtils.proxifiedBaseURL(request.getBaseUrl(), wcs.getGeoServer().getProxyBaseUrl());
            final String locationDef = WCS_URI + " " + proxifiedBaseUrl + "schemas/wcs/1.0.0/describeCoverage.xsd";
            attributes.addAttribute("", locationAtt, locationAtt, "", locationDef);

            attributes.addAttribute("", "version", "version", "", "1.0.0");
            
            start("wcs:CoverageDescription", attributes);
            for (Iterator it = request.getCoverage().iterator(); it.hasNext();) {
                String coverageName = (String) it.next();
                String coverageId = null;
                String fieldId = null;
                
                coverageId = coverageName.indexOf("/") > 0 ? coverageName.substring(0, coverageName.indexOf("/")) : coverageName;
                fieldId = coverageName.indexOf("/") > 0 ? coverageName.substring(coverageName.indexOf("/")+1) : null;

                // check the coverage is known
                if (!Data.TYPE_RASTER.equals(catalog.getLayerType(coverageId))) {
                    throw new WcsException("Could not find the specified coverage: " + coverageId, WcsExceptionCode.InvalidParameterValue, "coverage");
                }

                CoverageInfo ci = catalog.getCoverageInfo(coverageId);
                try {
                    handleCoverageOffering(ci, fieldId);
                } catch (Exception e) {
                    throw new RuntimeException("Unexpected error occurred during describe coverage xml encoding", e);
                }

            }
            end("wcs:CoverageDescription");
        }

        void handleCoverageOffering(CoverageInfo ci, String fieldId) throws Exception {
            if (fieldId != null) {
                FieldType field = ci.getFields().getFieldType(fieldId);
                start("wcs:CoverageOffering");
                    handleMetadataLink(ci.getMetadataLink());
                element("wcs:description", field.getDescription().toString());
                element("wcs:name", ci.getName() + "/" + fieldId);
                element("wcs:label", ci.getLabel());
                    handleLonLatEnvelope(ci.getWGS84LonLatEnvelope());
                    handleKeywords(ci.getKeywords());
                    
                    handleDomain(ci);
                    handleRange(ci, field);
                    
                    handleSupportedCRSs(ci);
                    handleSupportedFormats(ci);
                    handleSupportedInterpolations(ci);
                end("wcs:CoverageOffering");
            } else {
                start("wcs:CoverageOffering");
                    handleMetadataLink(ci.getMetadataLink());
                element("wcs:description", ci.getDescription());
                element("wcs:name", ci.getName());
                element("wcs:label", ci.getLabel());
                    handleLonLatEnvelope(ci.getWGS84LonLatEnvelope());
                    handleKeywords(ci.getKeywords());
                    
                    handleDomain(ci);
                    handleRange(ci, null);
                    
                    handleSupportedCRSs(ci);
                    handleSupportedFormats(ci);
                    handleSupportedInterpolations(ci);
                end("wcs:CoverageOffering");
            }
        }

        /**
         * DOCUMENT ME!
         * 
         * @param metadataLink
         */
        private void handleMetadataLink(MetaDataLink mdl) {
            if (mdl != null) {
                AttributesImpl attributes = new AttributesImpl();

                if ((mdl.getAbout() != null) && (mdl.getAbout() != "")) {
                    attributes.addAttribute("", "about", "about", "", mdl.getAbout());
                }

                // if( mdl.getType() != null && mdl.getType() != "" ) {
                // attributes.addAttribute("", "type", "type", "",
                // mdl.getType());
                // }
                if ((mdl.getMetadataType() != null)
                        && (mdl.getMetadataType() != "")) {
                    attributes.addAttribute("", "metadataType", "metadataType", "", mdl.getMetadataType());
                }

                if (attributes.getLength() > 0) {
                    start("wcs:metadataLink", attributes);
                    // characters(mdl.getContent());
                    end("wcs:metadataLink");
                }
            }
        }

        /**
         * DOCUMENT ME!
         * 
         * @param lonLatEnvelope
         */
        private void handleLonLatEnvelope(GeneralEnvelope lonLatEnvelope) {
            if (lonLatEnvelope != null) {
                AttributesImpl attributes = new AttributesImpl();
                attributes.addAttribute("", "srsName", "srsName", "", /* "WGS84(DD)" */ "urn:ogc:def:crs:OGC:1.3:CRS84" );
                
                start("wcs:lonLatEnvelope", attributes);
                    element("gml:pos", lonLatEnvelope.getLowerCorner().getOrdinate(0) + " " + lonLatEnvelope.getLowerCorner().getOrdinate(1));
                    element("gml:pos", lonLatEnvelope.getUpperCorner().getOrdinate(0) + " " + lonLatEnvelope.getUpperCorner().getOrdinate(1));
                end("wcs:lonLatEnvelope");
            }
        }

        /**
         * DOCUMENT ME!
         * 
         * @param kwords
         *                DOCUMENT ME!
         * 
         * @throws SAXException
         *                 DOCUMENT ME!
         */
        private void handleKeywords(List kwords) {
            start("wcs:keywords");

            if (kwords != null) {
                for (Iterator it = kwords.iterator(); it.hasNext();) {
                    element("wcs:keyword", it.next().toString());
                }
            }

            end("wcs:keywords");
        }

        private void handleDomain(CoverageInfo ci) throws Exception {
            start("wcs:domainSet");
                start("wcs:spatialDomain");
                    handleBoundingBox(ci.getSrsName(), ci.getEnvelope(), ci.getVerticalExtent(), ci.getTemporalExtent());
                    handleGrid(ci);
                end("wcs:spatialDomain");
                start("wcs:temporalDomain");
                    handleTemporalDomain(ci);
                end("wcs:temporalDomain");
            end("wcs:domainSet");
        }

        /**
         * DOCUMENT ME!
         * 
         * @param ci
         */
        private void handleTemporalDomain(CoverageInfo ci) {
            Set<TemporalGeometricPrimitive> temporalExtent = ci.getTemporalExtent();
            if (temporalExtent != null && !temporalExtent.isEmpty()) {
                for (Iterator<TemporalGeometricPrimitive> i=temporalExtent.iterator(); i.hasNext();) {
                    TemporalGeometricPrimitive temporalObject = i.next();
                    
                    if (temporalObject instanceof Period) {
                        start("gml:timePeriod");
                            element("gml:beginPosition", ((Period) temporalObject).getBeginning().getPosition().getDateTime().toString());
                            element("gml:endPosition", ((Period) temporalObject).getEnding().getPosition().getDateTime().toString());
                        end("gml:timePeriod");
                    } else if (temporalObject instanceof Instant) {
                        element("gml:timePosition", ((Instant) temporalObject).getPosition().getDateTime().toString());
                    }
                }
            }
        }

        /**
         * DOCUMENT ME!
         *  
         * @param envelope
         * @param set2 
         * @param set 
         */
        private void handleBoundingBox(String srsName, GeneralEnvelope envelope, Set<Envelope> verticalExtent, Set<TemporalGeometricPrimitive> temporalExtent) {
            if (envelope != null) {
                AttributesImpl attributes = new AttributesImpl();
                attributes.addAttribute("", "srsName", "srsName", "", srsName);
                
                start("gml:Envelope", attributes);
                    element("gml:pos", Double.toString(envelope.getLowerCorner().getOrdinate(0)) + " " + Double.toString(envelope.getLowerCorner().getOrdinate(1)));
                    element("gml:pos", Double.toString(envelope.getUpperCorner().getOrdinate(0)) + " " + Double.toString(envelope.getUpperCorner().getOrdinate(1)));

                    if (temporalExtent != null && !temporalExtent.isEmpty()) {
                        String temporalExtentBegin = null;
                        String temporalExtentEnd = null;
        
                        for (Iterator<TemporalGeometricPrimitive> i=temporalExtent.iterator(); i.hasNext();) {
                            TemporalGeometricPrimitive temporalObject = i.next();
                            
                            if (temporalObject instanceof Period) {
                                temporalExtentBegin = ((Period) temporalObject).getBeginning().getPosition().getDateTime().toString();
                                temporalExtentEnd = ((Period) temporalObject).getEnding().getPosition().getDateTime().toString();
                            } else if (temporalObject instanceof Instant) {
                                if (temporalExtentBegin == null || temporalExtentBegin.length() == 0)
                                    temporalExtentBegin = ((Instant) temporalObject).getPosition().getDateTime().toString();
                                if (!i.hasNext())
                                    temporalExtentEnd = ((Instant) temporalObject).getPosition().getDateTime().toString();
                            }
                        }
                        element("gml:timePosition", temporalExtentBegin);
                        element("gml:timePosition", temporalExtentEnd);
                    }
                    
                end("gml:Envelope");
            }
        }

        private void handleGrid(CoverageInfo ci) throws Exception {
            GridGeometry  grid      = ci.getGrid();
            MathTransform gridToCRS = grid.getGridToCRS();
            final int gridDimension = gridToCRS != null ? gridToCRS.getSourceDimensions() : 0;
            
            AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute("", "dimension", "dimension", "", String.valueOf(gridDimension));

            start("gml:RectifiedGrid", attributes);
                // Grid Envelope
                String lowers = "";
                String uppers= "";
    
                for (int r = 0; r < gridDimension; r++) {
                        lowers += (grid.getGridRange().getLower(r) + " ");
                        uppers += (grid.getGridRange().getUpper(r) + " ");
                }
                start("gml:limits");
                    start("gml:GridEnvelope");
                        element("gml:low", lowers.trim());
                        element("gml:high", uppers.trim());
                    end("gml:GridEnvelope");
                end("gml:limits");
                
                // Grid Axes
                for (int dn = 0; dn < ci.getCrs().getCoordinateSystem().getDimension(); dn++)
                    element("gml:axisName", ci.getCrs().getCoordinateSystem().getAxis(dn).getAbbreviation());

                final LinearTransform tx = (LinearTransform) ci.getGrid().getGridToCRS();
                final Matrix matrix = tx.getMatrix();
                // Grid Origins
                StringBuffer origins = new StringBuffer();
                for (int i = 0; i < matrix.getNumRow() - 1; i++) {
                    origins.append(matrix.getElement(i, matrix.getNumCol() - 1));
                    if (i < matrix.getNumRow() - 2)
                        origins.append(" ");
                }
                start("gml:origin");
                    element("gml:pos", origins.toString());
                end("gml:origin");
                // Grid Offsets
                StringBuffer offsets = new StringBuffer();
                for (int i = 0; i < matrix.getNumRow() - 1; i++) {
                    for (int j = 0; j < matrix.getNumCol() - 1; j++) {
                        offsets.append(matrix.getElement(i, j));
                        if (j < matrix.getNumCol() - 2)
                            offsets.append(" ");
                    }

                    element("gml:offsetVector", offsets.toString());
                    offsets = new StringBuffer();
                }

            end("gml:RectifiedGrid");
        }

        private void handleRange(CoverageInfo ci, FieldType field) {
            // rangeSet
            if (field != null) {
                final List<Axis<?, ?>> fieldAxes = field.getAxes();
                for (Axis<?, ?> axis : fieldAxes) {
                    start("wcs:rangeSet");
                        start("wcs:RangeSet");
                            element("wcs:name", field.getName().getLocalPart());
                            element("wcs:label", field.getDescription().toString());
                            start("wcs:axisDescription");
                                start("wcs:AxisDescription");
                                    element("wcs:name", axis.getName().toString());
                                    element("wcs:label", axis.getDescription().toString());
                                    start("wcs:values");
                                    if (axis.getNumKeys() == 1) {
                                        element("wcs:singleValue", axis.getKey(0).toString());
                                    } else {
                                        start("wcs:interval");
                                            element("wcs:min", axis.getKey(0).toString());
                                            element("wcs:max", axis.getKey(axis.getNumKeys()-1).toString());
                                        end("wcs:interval");
                                    }
                                    end("wcs:values");
                                 end("wcs:AxisDescription");
                             end("wcs:axisDescription");
    
                            // null values
                            TreeSet nodataValues = new TreeSet();
                            final Set<SampleDimension> sampleDimensions = field.getSampleDimensions();
                            for (SampleDimension dim : sampleDimensions) {
                                // TODO: FIX THIS!!!
                                double[] nodata = dim.getNoDataValues();

                                if (nodata != null) {
                                    for (int nd = 0; nd < nodata.length; nd++) {
                                        if (!nodataValues.contains(nodata[nd])) {
                                            nodataValues.add(nodata[nd]);
                                        }
                                    }
                                        }
                            }
                    
                            if (nodataValues.size() > 0) {
                                start("wcs:nullValues");
                                if (nodataValues.size() == 1) {
                                    element("wcs:singleValue", ((Double) nodataValues.first()).toString());
                                } else {
                                    start("wcs:interval");
                                    element("wcs:min", ((Double) nodataValues.first()).toString());
                                    element("wcs:max", ((Double) nodataValues.last()).toString());
                                    end("wcs:interval");
                                }
                                end("wcs:nullValues");
                            }
                        end("wcs:RangeSet");
                    end("wcs:rangeSet");
                }
            } else {
                RangeType fields = ci.getFields();
                if (fields != null && fields.getNumFieldTypes() <= 0) {
                    try {
                        CoverageSource cvSource = ci.getCoverageAccess().access(new NameImpl(ci.getCoverageName()), null, AccessType.READ_ONLY, null, null);
                        DefaultCoverageReadRequest cvReadRequest = new DefaultCoverageReadRequest();
                        cvReadRequest.setName(new NameImpl(ci.getCoverageName()));
                        CoverageResponse cvResponse = cvSource.read(cvReadRequest, null);
                        if (cvResponse.getStatus().equals(Status.SUCCESS)) {
                            Coverage coverage = cvResponse.getResults(null).iterator().next();
                            int numSampleDimensions = coverage.getNumSampleDimensions();
                            start("wcs:rangeSet");
                                start("wcs:RangeSet");
                                    element("wcs:name", ci.getName());
                                    element("wcs:label", ci.getLabel());
                                    start("wcs:axisDescription");
                                        start("wcs:AxisDescription");
                                            element("wcs:name", "Band");
                                            element("wcs:label", "Band");
                                            start("wcs:values");
                                            if (numSampleDimensions == 1) {
                                                element("wcs:singleValue", "1");
                                            } else {
                                                start("wcs:interval");
                                                    element("wcs:min", "1");
                                                    element("wcs:max", String.valueOf(numSampleDimensions));
                                                end("wcs:interval");
                                            }
                                            end("wcs:values");
                                         end("wcs:AxisDescription");
                                     end("wcs:axisDescription");

                                    // null values
                                    TreeSet nodataValues = new TreeSet();
                                    for (int sample = 0; sample < numSampleDimensions; sample++) {
                                        double[] nodata = coverage.getSampleDimension(sample).getNoDataValues();
        
                                        if (nodata != null) {
                                            for (int nd = 0; nd < nodata.length; nd++) {
                                                if (!nodataValues.contains(nodata[nd])) {
                                                    nodataValues.add(nodata[nd]);
                                                }
                                            }
                                        }
                                    }
                            
                                    if (nodataValues.size() > 0) {
                                        start("wcs:nullValues");
                                        if (nodataValues.size() == 1) {
                                            element("wcs:singleValue",
                                                    ((Double) nodataValues.first())
                                                    .toString());
                                        } else {
                                            start("wcs:interval");
                                            element("wcs:min", ((Double) nodataValues.first()).toString());
                                            element("wcs:max", ((Double) nodataValues.last()).toString());
                                            end("wcs:interval");
                                        }
                                        end("wcs:nullValues");
                                    }
                                end("wcs:RangeSet");
                            end("wcs:rangeSet");
                        }
                    } catch (IOException e) {
                        // TODO: FIX THIS!!!
                        e.printStackTrace();
                    }
                } else {
                    start("wcs:rangeSet");
                        start("wcs:RangeSet");
                            element("wcs:name", ci.getName());
                            element("wcs:label", ci.getLabel());
                            start("wcs:axisDescription");
                                start("wcs:AxisDescription");
                                end("wcs:AxisDescription");
                            end("wcs:axisDescription");
                        end("wcs:RangeSet");
                    end("wcs:rangeSet");
                }
            }
        }

        /**
         * DOCUMENT ME!
         * 
         * @param ci
         * @throws Exception
         */
        private void handleSupportedCRSs(CoverageInfo ci) throws Exception {
            Set supportedCRSs = new LinkedHashSet();
            if (ci.getRequestCRSs() != null)
                supportedCRSs.addAll(ci.getRequestCRSs());
            if (ci.getResponseCRSs() != null)
                supportedCRSs.addAll(ci.getResponseCRSs());
            start("wcs:supportedCRSs");
                for (Iterator it = supportedCRSs.iterator(); it.hasNext();) {
                    String crsName = (String) it.next();
                    CoordinateReferenceSystem crs = CRS.decode(crsName, true);
    //                element("requestResponseCRSs", urnIdentifier(crs));
                    element("wcs:requestResponseCRSs", CRS.lookupIdentifier(crs, false));
                }
            end("wcs:supportedCRSs");
        }

        private String urnIdentifier(final CoordinateReferenceSystem crs) throws FactoryException {
            String authorityAndCode = CRS.lookupIdentifier(crs, false);
            String code = authorityAndCode.substring(authorityAndCode.lastIndexOf(":") + 1);
            // we don't specify the version, but we still need to put a space
            // for it in the urn form, that's why we have :: before the code
            return "urn:ogc:def:crs:EPSG::" + code;
        }

        /**
         * DOCUMENT ME!
         * 
         * @param ci
         * @throws Exception
         */
        private void handleSupportedFormats(CoverageInfo ci) throws Exception {
            final String nativeFormat = (((ci.getNativeFormat() != null) && 
                    ci.getNativeFormat().equalsIgnoreCase("GEOTIFF")) ? "GeoTIFF" : ci.getNativeFormat());
            
            final AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute("", "nativeFormat", "nativeFormat", "", nativeFormat);
            
            // gather all the formats for this coverage 
            Set<String> formats = new HashSet<String>();
            for (Iterator it = ci.getSupportedFormats().iterator(); it.hasNext();) {
                String format = (String) it.next();
                formats.add(format);
            }
            // sort them
            start("wcs:supportedFormats", attributes);
                List<String> sortedFormats = new ArrayList<String>(formats);
                Collections.sort(sortedFormats);
                for (String format : sortedFormats) {
                    element("wcs:formats", format.equalsIgnoreCase("GEOTIFF") ? "GeoTIFF" : format);
                }
            end("wcs:supportedFormats");            
        }
        
        /**
         * DOCUMENT ME!
         * 
         * @param ci
         */
        private void handleSupportedInterpolations(CoverageInfo ci) {
            final AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute("", "default", "default", "", ci.getDefaultInterpolationMethod());

            start("wcs:supportedInterpolations", attributes);
            for (Iterator it = ci.getInterpolationMethods().iterator(); it.hasNext();) {
                String method = (String) it.next();
                String converted = METHOD_NAME_MAP.get(method);
                if (converted != null)
                    element("wcs:interpolationMethod", /* converted */ method);

            }
            end("wcs:supportedInterpolations");
        }

        /**
         * Writes the element if and only if the content is not null and not
         * empty
         * 
         * @param elementName
         * @param content
         */
        private void elementIfNotEmpty(String elementName, String content) {
            if (content != null && !"".equals(content.trim()))
                element(elementName, content);
        }
    }

}
