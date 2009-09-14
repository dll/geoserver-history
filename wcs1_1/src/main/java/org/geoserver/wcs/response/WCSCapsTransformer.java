/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.geoserver.wcs.response;

import static org.geoserver.ows.util.ResponseUtils.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import net.opengis.wcs11.GetCapabilitiesType;

import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.MetadataLinkInfo;
import org.geoserver.config.ContactInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.ows.URLMangler.URLType;
import org.geoserver.ows.util.RequestUtils;
import org.geoserver.wcs.WCSInfo;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.util.logging.Logging;
import org.geotools.xml.transform.TransformerBase;
import org.geotools.xml.transform.Translator;
import org.vfny.geoserver.global.CoverageInfoLabelComparator;
import org.vfny.geoserver.wcs.WcsException;
import org.vfny.geoserver.wcs.WcsException.WcsExceptionCode;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Based on the <code>org.geotools.xml.transform</code> framework, does the
 * job of encoding a WCS 1.1 Capabilities document.
 * 
 * @author Alessio Fabiani (alessio.fabiani@gmail.com)
 * @author Simone Giannecchini (simboss1@gmail.com)
 * @author Andrea Aime, TOPP
 */
public class WCSCapsTransformer extends TransformerBase {
    private static final Logger LOGGER = Logging.getLogger(WCSCapsTransformer.class.getPackage()
            .getName());

    protected static final String WCS_URI = "http://www.opengis.net/wcs/1.1.1";

    protected static final String CUR_VERSION = "1.1.1";

    protected static final String XSI_PREFIX = "xsi";

    protected static final String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";

    private WCSInfo wcs;

    private Catalog catalog;

    /**
     * Creates a new WFSCapsTransformer object.
     */
    public WCSCapsTransformer(GeoServer gs) {
        super();
        this.wcs = gs.getService(WCSInfo.class);
        this.catalog = gs.getCatalog();
        setNamespaceDeclarationEnabled(false);
    }

    public Translator createTranslator(ContentHandler handler) {
        return new WCS111CapsTranslator(handler);
    }

    private class WCS111CapsTranslator extends TranslatorSupport {
        /**
         * DOCUMENT ME!
         * 
         * @uml.property name="request"
         * @uml.associationEnd multiplicity="(0 1)"
         */
        private GetCapabilitiesType request;

        private String proxifiedBaseUrl;

        /**
         * Creates a new WFSCapsTranslator object.
         * 
         * @param handler
         *            DOCUMENT ME!
         */
        public WCS111CapsTranslator(ContentHandler handler) {
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
            if (!(o instanceof GetCapabilitiesType)) {
                throw new IllegalArgumentException(new StringBuffer("Not a GetCapabilitiesType: ")
                        .append(o).toString());
            }

            this.request = (GetCapabilitiesType) o;

            // check the update sequence
            final int updateSequence = wcs.getGeoServer().getGlobal().getUpdateSequence();
            int requestedUpdateSequence = -1;
            if (request.getUpdateSequence() != null) {
                try {
                    requestedUpdateSequence = Integer.parseInt(request.getUpdateSequence());
                } catch (NumberFormatException e) {
                    throw new WcsException("Invalid update sequence number format, "
                            + "should be an integer", WcsExceptionCode.InvalidUpdateSequence,
                            "updateSequence");
                }
                if (requestedUpdateSequence > updateSequence) {
                    throw new WcsException("Invalid update sequence value, it's higher "
                            + "than the current value, " + updateSequence,
                            WcsExceptionCode.InvalidUpdateSequence, "updateSequence");
                }
            }

            final AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute("", "version", "version", "", CUR_VERSION);
            attributes.addAttribute("", "xmlns:wcs", "xmlns:wcs", "", WCS_URI);

            attributes.addAttribute("", "xmlns:xlink", "xmlns:xlink", "",
                    "http://www.w3.org/1999/xlink");
            attributes.addAttribute("", "xmlns:ogc", "xmlns:ogc", "", "http://www.opengis.net/ogc");
            attributes.addAttribute("", "xmlns:ows", "xmlns:ows", "",
                    "http://www.opengis.net/ows/1.1");
            attributes.addAttribute("", "xmlns:gml", "xmlns:gml", "", "http://www.opengis.net/gml");

            final String prefixDef = new StringBuffer("xmlns:").append(XSI_PREFIX).toString();
            attributes.addAttribute("", prefixDef, prefixDef, "", XSI_URI);

            final String locationAtt = new StringBuffer(XSI_PREFIX).append(":schemaLocation")
                    .toString();

            final String locationDef = buildSchemaURL(request.getBaseUrl(), "wcs/1.1.1/wcsGetCapabilities.xsd");
            
            attributes.addAttribute("", locationAtt, locationAtt, "", locationDef);
            attributes.addAttribute("", "updateSequence", "updateSequence", "", String
                    .valueOf(updateSequence));
            start("wcs:Capabilities", attributes);

            // handle the sections directive
            boolean allSections;
            List<String> sections;
            if (request.getSections() == null) {
                allSections = true;
                sections = Collections.emptyList();
            } else {
                sections = request.getSections().getSection();
                allSections = sections.contains("All");
            }
            final Set<String> knownSections = new HashSet<String>(Arrays.asList(
                    "ServiceIdentification", "ServiceProvider", "OperationsMetadata", "Contents",
                    "All"));
            for (String section : sections) {
                if(!knownSections.contains(section)) 
                    throw new WcsException("Unknown section " + section, 
                            WcsExceptionCode.InvalidParameterValue, "Sections");
            }

            // encode the actual capabilities contents taking into consideration
            // the sections
            if (requestedUpdateSequence < updateSequence) {
                if (allSections || sections.contains("ServiceIdentification"))
                    handleServiceIdentification();
                if (allSections || sections.contains("ServiceProvider"))
                    handleServiceProvider();
                if (allSections || sections.contains("OperationsMetadata"))
                    handleOperationsMetadata();
                if (allSections || sections.contains("Contents"))
                    handleContents();
            }

            end("wcs:Capabilities");
        }

        /**
         * Handles the service identification of the capabilities document.
         * 
         * @param config
         *            The OGC service to transform.
         * 
         * @throws SAXException
         *             For any errors.
         */
        private void handleServiceIdentification() {
            start("ows:ServiceIdentification");
            element("ows:Title", wcs.getTitle());
            element("ows:Abstract", wcs.getAbstract());
            handleKeywords(wcs.getKeywords());
            element("ows:ServiceType", "WCS");
            element("ows:ServiceTypeVersion", "1.1.0");
            element("ows:ServiceTypeVersion", "1.1.1");

            String fees = wcs.getFees();
            if ((fees == null) || "".equals(fees)) {
                fees = "NONE";
            }
            element("ows:Fees", fees);

            String accessConstraints = wcs.getAccessConstraints();
            if ((accessConstraints == null) || "".equals(accessConstraints)) {
                accessConstraints = "NONE";
            }
            element("ows:AccessConstraints", accessConstraints);
            end("ows:ServiceIdentification");
        }

        /**
         * Handles the service provider of the capabilities document.
         * 
         * @param config
         *            The OGC service to transform.
         * 
         * @throws SAXException
         *             For any errors.
         */
        private void handleServiceProvider() {
            start("ows:ServiceProvider");
            GeoServerInfo gs = wcs.getGeoServer().getGlobal();
			element("ows:ProviderName", gs.getContact().getContactOrganization());
            AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute("", "xlink:href", "xlink:href", "", gs.getOnlineResource());
            element("ows:ProviderSite", null, attributes);

            handleContact();

            end("ows:ServiceProvider");
        }

        /**
         * Handles the OperationMetadata portion of the document, printing out
         * the operations and where to bind to them.
         * 
         * @param config
         *            The global wms.
         * 
         * @throws SAXException
         *             For any problems.
         */
        private void handleOperationsMetadata() {
            start("ows:OperationsMetadata");
            handleOperation("GetCapabilities", null);
            handleOperation("DescribeCoverage", null);
            handleOperation("GetCoverage", new HashMap<String, List<String>>() {
                {
                    put("store", Arrays.asList("True", "False"));
                }
            });

            // specify that we do support xml post encoding, clause 8.3.2.2 of
            // the WCS 1.1.1 spec
            AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute(null, "name", "name", null, "PostEncoding");
            start("ows:Constraint", attributes);
            start("ows:AllowedValues");
            element("ows:Value", "XML");
            end("ows:AllowedValues");
            end("ows:Constraint");

            end("ows:OperationsMetadata");
        }

        private void handleOperation(String capabilityName, Map<String, List<String>> parameters) {
            AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute(null, "name", "name", null, capabilityName);
            start("ows:Operation", attributes);

            final String url = proxifiedBaseUrl + "wcs?";

            start("ows:DCP");
            start("ows:HTTP");
            attributes = new AttributesImpl();
            attributes.addAttribute("", "xlink:href", "xlink:href", "", url);
            element("ows:Get", null, attributes);
            end("ows:HTTP");
            end("ows:DCP");

            attributes = new AttributesImpl();
            attributes.addAttribute("", "xlink:href", "xlink:href", "", url);
            start("ows:DCP");
            start("ows:HTTP");
            element("ows:Post", null, attributes);
            end("ows:HTTP");
            end("ows:DCP");

            if (parameters != null && !parameters.isEmpty()) {
                for (Map.Entry<String, List<String>> param : parameters.entrySet()) {
                    attributes = new AttributesImpl();
                    attributes.addAttribute("", "name", "name", "", param.getKey());
                    start("ows:Parameter", attributes);
                    start("ows:AllowedValues");
                    for (String value : param.getValue()) {
                        element("ows:Value", value);
                    }
                    end("ows:AllowedValues");
                    end("ows:Parameter");
                }
            }

            end("ows:Operation");
        }

        /**
         * DOCUMENT ME!
         * 
         * @param kwords
         *            DOCUMENT ME!
         * 
         * @throws SAXException
         *             DOCUMENT ME!
         */
        private void handleKeywords(List kwords) {
            start("ows:Keywords");

            if (kwords != null) {
                for (Iterator it = kwords.iterator(); it.hasNext();) {
                    element("ows:Keyword", it.next().toString());
                }
            }

            end("ows:Keywords");
        }

        /**
         * Handles contacts.
         * 
         * @param wcs
         *            the service.
         */
        private void handleContact() {
            final GeoServer gs = wcs.getGeoServer();
            start("ows:ServiceContact");

            ContactInfo contact = gs.getGlobal().getContact();
            elementIfNotEmpty("ows:IndividualName", contact.getContactPerson());
            elementIfNotEmpty("ows:PositionName", contact.getContactPosition());

            start("ows:ContactInfo");
            start("ows:Phone");
            elementIfNotEmpty("ows:Voice", contact.getContactVoice());
            elementIfNotEmpty("ows:Facsimile", contact.getContactFacsimile());
            end("ows:Phone");
            start("ows:Address");
            elementIfNotEmpty("ows:DeliveryPoint", contact.getAddress());
            elementIfNotEmpty("ows:City", contact.getAddressCity());
            elementIfNotEmpty("ows:AdministrativeArea", contact.getAddressState());
            elementIfNotEmpty("ows:PostalCode", contact.getAddressPostalCode());
            elementIfNotEmpty("ows:Country", contact.getAddressCountry());
            elementIfNotEmpty("ows:ElectronicMailAddress", contact.getContactEmail());
            end("ows:Address");

            String or = gs.getGlobal().getOnlineResource();
            if ((or != null) && !"".equals(or.trim())) {
                AttributesImpl attributes = new AttributesImpl();
                attributes.addAttribute("", "xlink:href", "xlink:href", "", or);
                start("ows:OnlineResource", attributes);
                end("OnlineResource");
            }

            end("ows:ContactInfo");
            end("ows:ServiceContact");
        }

        private void handleEnvelope(ReferencedEnvelope envelope) {
            start("ows:WGS84BoundingBox");
            element("ows:LowerCorner", new StringBuffer(Double.toString(envelope.getLowerCorner()
                    .getOrdinate(0))).append(" ").append(envelope.getLowerCorner().getOrdinate(1))
                    .toString());
            element("ows:UpperCorner", new StringBuffer(Double.toString(envelope.getUpperCorner()
                    .getOrdinate(0))).append(" ").append(envelope.getUpperCorner().getOrdinate(1))
                    .toString());
            end("ows:WGS84BoundingBox");
        }

        private void handleContents() {
            start("wcs:Contents");

            List<CoverageInfo> coverages = wcs.getGeoServer().getCatalog().getCoverages();
            
            // filter out disabled coverages
            for (Iterator it = coverages.iterator(); it.hasNext();) {
                CoverageInfo cv = (CoverageInfo) it.next();
                if(!cv.isEnabled())
                    it.remove();
            }
            
            // filter out coverages that are not in the requested namespace
            if(request.getNamespace() != null) {
                String namespace = request.getNamespace();
                for (Iterator it = coverages.iterator(); it.hasNext();) {
                    CoverageInfo cv = (CoverageInfo) it.next();
                    if(!namespace.equals(cv.getStore().getWorkspace().getName()))
                        it.remove();
                }
            }
            
            Collections.sort(coverages, new CoverageInfoLabelComparator());
            for (Iterator i = coverages.iterator(); i.hasNext();) {
                CoverageInfo cv = (CoverageInfo) i.next();
                if (cv.isEnabled())
                    handleCoverageSummary(cv);
            }

            end("wcs:Contents");
        }

        private void handleCoverageSummary(CoverageInfo cv) {
            start("wcs:CoverageSummary");
            elementIfNotEmpty("ows:Title", cv.getTitle());
            elementIfNotEmpty("ows:Abstract", cv.getDescription());
            handleKeywords(cv.getKeywords());
            handleMetadataLinks(cv.getMetadataLinks(), "simple");
            handleEnvelope(cv.getLatLonBoundingBox());
            element("wcs:Identifier", cv.getName());

            end("wcs:CoverageSummary");
        }

        private void handleMetadataLinks(List<MetadataLinkInfo> links, String linkType) {
        	for (MetadataLinkInfo  mdl : links) {
        		if (mdl != null) {
                    AttributesImpl attributes = new AttributesImpl();

                    if ((mdl.getAbout() != null) && (mdl.getAbout() != "")) {
                        attributes.addAttribute("", "about", "about", "", mdl.getAbout());
                    }

                    if ((mdl.getMetadataType() != null) && (mdl.getMetadataType() != "")) {
                        attributes.addAttribute("", "xlink:type", "xlink:type", "", linkType);
                    }

                    if (attributes.getLength() > 0) {
                        element("ows:Metadata", null, attributes);
                    }
                }
			}
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