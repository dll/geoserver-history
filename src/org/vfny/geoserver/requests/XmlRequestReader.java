/* Copyright (c) 2001, 2003 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.vfny.geoserver.requests;

import org.geotools.filter.Filter;
import org.geotools.filter.FilterFilter;
import org.geotools.gml.GMLFilterDocument;
import org.geotools.gml.GMLFilterGeometry;
import org.vfny.geoserver.responses.WfsException;
import org.vfny.geoserver.responses.WfsTransactionException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.ParserAdapter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * This utility reads in XML requests and returns them as appropriate request
 * objects.
 *
 * @author Rob Hranac, TOPP
 * @author Chris Holmes, TOPP
 * @version $Id: XmlRequestReader.java,v 1.7 2003/09/15 18:27:06 cholmesny Exp $
 */
public class XmlRequestReader {
    /** Class logger */
    private static Logger LOGGER = Logger.getLogger(
            "org.vfny.geoserver.requests");

    /**
     * Private constructor so it cannot be instantiated.
     */
    private XmlRequestReader() {
    }

    /**
     * Reads the GetFeature XML request into a FeatureRequest object.
     *
     * @param rawRequest The plain POST text from the client.
     *
     * @return The FeatureRequest from the xml reader.
     *
     * @throws WfsException For any problems reading the request.
     */
    public static FeatureRequest readGetFeature(Reader rawRequest)
        throws WfsException {
        // translate string into a proper SAX input source
        InputSource requestSource = new InputSource(rawRequest);

        // instantiante parsers and content handlers
        FeatureHandler contentHandler = new FeatureHandler();
        FilterFilter filterParser = new FilterFilter(contentHandler, null);
        GMLFilterGeometry geometryFilter = new GMLFilterGeometry(filterParser);
        GMLFilterDocument documentFilter = new GMLFilterDocument(geometryFilter);

        // read in XML file and parse to content handler
        try {
            LOGGER.finest("about to create parser");

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            ParserAdapter adapter = new ParserAdapter(parser.getParser());
            LOGGER.finest("setting the content handler");
            LOGGER.finest("content handler = " + documentFilter);
            adapter.setContentHandler(documentFilter);
            LOGGER.finest("about to parse");
            LOGGER.finest("calling parse on " + requestSource);
            adapter.parse(requestSource);
            LOGGER.fine("just parsed: " + requestSource);
        } catch (SAXException e) {
            e.printStackTrace(System.out);
            throw new WfsException(e,
                "XML getFeature request SAX parsing error",
                XmlRequestReader.class.getName());
        } catch (IOException e) {
            throw new WfsException(e, "XML get feature request input error",
                XmlRequestReader.class.getName());
        } catch (ParserConfigurationException e) {
            throw new WfsException(e, "Some sort of issue creating parser",
                XmlRequestReader.class.getName());
        }

        return contentHandler.getRequest();
    }

    /**
     * Reads the Filter XML request into a geotools Feature object.
     *
     * @param rawRequest The plain POST text from the client.
     *
     * @return The geotools filter constructed from rawRequest.
     *
     * @throws WfsException For any problems reading the request.
     */
    public static Filter readFilter(Reader rawRequest)
        throws WfsException {
        // translate string into a proper SAX input source
        InputSource requestSource = new InputSource(rawRequest);

        // instantiante parsers and content handlers
        FilterHandlerImpl contentHandler = new FilterHandlerImpl();
        FilterFilter filterParser = new FilterFilter(contentHandler, null);
        GMLFilterGeometry geometryFilter = new GMLFilterGeometry(filterParser);
        GMLFilterDocument documentFilter = new GMLFilterDocument(geometryFilter);

        // read in XML file and parse to content handler
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            ParserAdapter adapter = new ParserAdapter(parser.getParser());

            adapter.setContentHandler(documentFilter);
            adapter.parse(requestSource);
            LOGGER.fine("just parsed: " + requestSource);
        } catch (SAXException e) {
            throw new WfsException(e,
                "XML getFeature request SAX parsing error",
                XmlRequestReader.class.getName());
        } catch (IOException e) {
            throw new WfsException(e, "XML get feature request input error",
                XmlRequestReader.class.getName());
        } catch (ParserConfigurationException e) {
            throw new WfsException(e, "Some sort of issue creating parser",
                XmlRequestReader.class.getName());
        }

        LOGGER.fine("passing filter: " + contentHandler.getFilter());

        return contentHandler.getFilter();
    }

    /**
     * Reads the Capabilities XML request into a CapabilitiesRequest object.
     *
     * @param rawRequest The plain POST text from the client.
     *
     * @return The read CapabilitiesRequest object.
     *
     * @throws WfsException For any problems reading the request.
     */
    public static CapabilitiesRequest readGetCapabilities(
        BufferedReader rawRequest) throws WfsException {
        //InputSource requestSource = new InputSource((Reader) tempReader);
        InputSource requestSource = new InputSource(rawRequest);

        // instantiante parsers and content handlers
        CapabilitiesHandler currentRequest = new CapabilitiesHandler();

        // read in XML file and parse to content handler
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            ParserAdapter adapter = new ParserAdapter(parser.getParser());

            adapter.setContentHandler(currentRequest);
            adapter.parse(requestSource);
            LOGGER.fine("just parsed: " + requestSource);
        } catch (SAXException e) {
            throw new WfsException(e, "XML capabilities request parsing error",
                XmlRequestReader.class.getName());
        } catch (IOException e) {
            throw new WfsException(e, "XML capabilities request input error",
                XmlRequestReader.class.getName());
        } catch (ParserConfigurationException e) {
            throw new WfsException(e, "Some sort of issue creating parser",
                XmlRequestReader.class.getName());
        }

        return currentRequest.getRequest();
    }

    /**
     * Reads the Describe XML request into a DescribeRequest object.
     *
     * @param rawRequest The plain POST text from the client.
     *
     * @return The read DescribeRequest object.
     *
     * @throws WfsException For any problems reading the request.
     */
    public static DescribeRequest readDescribeFeatureType(
        BufferedReader rawRequest) throws WfsException {
        /** create a describe feature type request class to return */
        InputSource requestSource = new InputSource(rawRequest);

        // instantiante parsers and content handlers
        DescribeHandler contentHandler = new DescribeHandler();

        // read in XML file and parse to content handler
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            ParserAdapter adapter = new ParserAdapter(parser.getParser());

            adapter.setContentHandler(contentHandler);
            adapter.parse(requestSource);
            LOGGER.finer("just parsed: " + requestSource);
        } catch (SAXException e) {
            throw new WfsException(e, "XML describe request parsing error",
                XmlRequestReader.class.getName());
        } catch (IOException e) {
            throw new WfsException(e, "XML describe request input error",
                XmlRequestReader.class.getName());
        } catch (ParserConfigurationException e) {
            throw new WfsException(e, "Some sort of issue creating parser",
                XmlRequestReader.class.getName());
        }

        LOGGER.finer("about to return ");
        LOGGER.finer("returning " + contentHandler.getRequest());

        return contentHandler.getRequest();
    }

    /**
     * Reads the Transaction XML request into a TransactionRequest object.
     *
     * @param rawRequest The plain POST text from the client.
     *
     * @return The read TransactionRequest object.
     *
     * @throws WfsTransactionException For any problems reading the request.
     */
    public static TransactionRequest readTransaction(Reader rawRequest)
        throws WfsTransactionException {
        // translate string into a proper SAX input source
        InputSource requestSource = new InputSource(rawRequest);

        // instantiante parsers and content handlers
        TransactionHandler contentHandler = new TransactionHandler();
        TransactionFilterHandler filterParser = new TransactionFilterHandler(contentHandler,
                null);
        TransactionFeatureHandler featureParser = new TransactionFeatureHandler(filterParser);
        GMLFilterGeometry geometryFilter = new GMLFilterGeometry(featureParser);
        GMLFilterDocument documentFilter = new GMLFilterDocument(geometryFilter);

        // read in XML file and parse to content handler
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            ParserAdapter adapter = new ParserAdapter(parser.getParser());

            adapter.setContentHandler(documentFilter);
            LOGGER.finest("about to start parsing");
            adapter.parse(requestSource);
            LOGGER.finer("just parsed: " + requestSource);
        } catch (SAXException e) {
            //e.getCause().printStackTrace(System.out);
            //e.printStackTrace(System.out);
            throw new WfsTransactionException(e,
                "XML transaction request SAX parsing error",
                XmlRequestReader.class.getName());
        } catch (IOException e) {
            throw new WfsTransactionException(e,
                "XML transaction request input error",
                XmlRequestReader.class.getName());
        } catch (ParserConfigurationException e) {
            throw new WfsTransactionException(e,
                "Some sort of issue creating parser",
                XmlRequestReader.class.getName());
        }

        return contentHandler.getRequest();
    }

     /**
     * Reads the Lock XML request into a LockRequest object.
     *
     * @param rawRequest The plain POST text from the client.
     *
     * @return The read LockRequest object.
     *
     * @throws WfsException For any problems reading the request.
     */
    public static LockRequest readLockRequest(Reader rawRequest)
        throws WfsException {
        // translate string into a proper SAX input source
        InputSource requestSource = new InputSource(rawRequest);

        // instantiante parsers and content handlers
        LockHandler contentHandler = new LockHandler();
        FilterFilter filterParser = new FilterFilter(contentHandler, null);
        GMLFilterGeometry geometryFilter = new GMLFilterGeometry(filterParser);
        GMLFilterDocument documentFilter = new GMLFilterDocument(geometryFilter);

        // read in XML file and parse to content handler
        try {
            LOGGER.finest("about to create parser");

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            ParserAdapter adapter = new ParserAdapter(parser.getParser());
            LOGGER.finest("setting the content handler");
            LOGGER.finest("content handler = " + documentFilter);
            adapter.setContentHandler(documentFilter);
            LOGGER.finest("about to parse");
            LOGGER.finest("calling parse on " + requestSource);
            adapter.parse(requestSource);
            LOGGER.fine("just parsed: " + requestSource);
        } catch (SAXException e) {
            e.printStackTrace(System.out);
            throw new WfsException(e,
                "XML getFeature request SAX parsing error",
                XmlRequestReader.class.getName());
        } catch (IOException e) {
            throw new WfsException(e, "XML get feature request input error",
                XmlRequestReader.class.getName());
        } catch (ParserConfigurationException e) {
            throw new WfsException(e, "Some sort of issue creating parser",
                XmlRequestReader.class.getName());
        }

        return contentHandler.getRequest();
    }
}
