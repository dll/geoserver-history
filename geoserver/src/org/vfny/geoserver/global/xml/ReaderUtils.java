/* Copyright (c) 2001, 2003 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.vfny.geoserver.global.xml;

import org.vfny.geoserver.global.ConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * ReaderUtils purpose.
 * 
 * <p>
 * This class is intended to be used as a library of XML relevant operation for
 * the XMLConfigReader class.
 * </p>
 * 
 * <p></p>
 *
 * @author dzwiers, Refractions Research, Inc.
 * @version $Id: ReaderUtils.java,v 1.9 2004/04/06 11:32:34 cholmesny Exp $
 *
 * @see XMLConfigReader
 */
public class ReaderUtils {
    /** Used internally to create log information to detect errors. */
    private static final Logger LOGGER = Logger.getLogger(
            "org.vfny.geoserver.global");

    /**
     * ReaderUtils constructor.
     * 
     * <p>
     * Static class, this should never be called.
     * </p>
     */
    private ReaderUtils() {
    }

    /**
     * loadConfig purpose.
     * 
     * <p>
     * Parses the specified file into a DOM tree.
     * </p>
     *
     * @param configFile The file to parse int a DOM tree.
     *
     * @return the resulting DOM tree
     *
     * @throws ConfigurationException
     */
    public static Element loadConfig(Reader configFile)
        throws ConfigurationException {
        try {
            LOGGER.fine("loading configuration file " + configFile);

            InputSource in = new InputSource(configFile);
            DocumentBuilderFactory dfactory = DocumentBuilderFactory
                .newInstance();

            //dfactory.setNamespaceAware(true);
            /*set as optimizations and hacks for geoserver schema config files
             * @HACK should make documents ALL namespace friendly, and validated. Some documents are XML fragments.
             * @TODO change the following config for the parser and modify config files to avoid XML fragmentation.
             */
            dfactory.setNamespaceAware(false);
            dfactory.setValidating(false);
            dfactory.setIgnoringComments(true);
            dfactory.setCoalescing(true);
            dfactory.setIgnoringElementContentWhitespace(true);

            Document serviceDoc = dfactory.newDocumentBuilder().parse(in);
            Element configElem = serviceDoc.getDocumentElement();

            return configElem;
        } catch (IOException ioe) {
            String message = "problem reading file " + configFile + "due to: "
                + ioe.getMessage();
            LOGGER.warning(message);
            throw new ConfigurationException(message, ioe);
        } catch (ParserConfigurationException pce) {
            String message =
                "trouble with parser to read org.vfny.geoserver.global.xml, make sure class"
                + "path is correct, reading file " + configFile;
            LOGGER.warning(message);
            throw new ConfigurationException(message, pce);
        } catch (SAXException saxe) {
            String message = "trouble parsing XML in " + configFile + ": "
                + saxe.getMessage();
            LOGGER.warning(message);
            throw new ConfigurationException(message, saxe);
        }
    }

    /**
     * Checks to ensure the file is valid.
     * 
     * <p>
     * Returns the file passed in to allow this to wrap file creations.
     * </p>
     *
     * @param file A file Handle to test.
     * @param isDir true when the File passed in is expected to be a directory,
     *        false when the handle is expected to be a file.
     *
     * @return the File handle passed in
     *
     * @throws ConfigurationException When the file does not exist or is not
     *         the type specified.
     */
    public static File checkFile(File file, boolean isDir)
        throws ConfigurationException {
        if (!file.exists()) {
            throw new ConfigurationException("File does not exist: " + file);
        }

        if (isDir && !file.isDirectory()) {
            throw new ConfigurationException("File is not a directory:" + file);
        }

        if (!isDir && !file.isFile()) {
            throw new ConfigurationException("File is not valid:" + file);
        }

        LOGGER.fine("File is valid: " + file);

        return file;
    }

    /**
     * getChildElement purpose.
     * 
     * <p>
     * Used to help with XML manipulations. Returns the first child element of
     * the specified name.  An exception occurs when the node is required and
     * not found.
     * </p>
     *
     * @param root The root element to look for children in.
     * @param name The name of the child element to look for.
     * @param mandatory true when an exception should be thrown if the child
     *        element does not exist.
     *
     * @return The child element found, null if not found.
     *
     * @throws ConfigurationException When a child element is required and not
     *         found.
     */
    public static Element getChildElement(Element root, String name,
        boolean mandatory) throws ConfigurationException {
        Node child = root.getFirstChild();

        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (name.equals(child.getNodeName())) {
                    return (Element) child;
                }
            }

            child = child.getNextSibling();
        }

        if (mandatory && (child == null)) {
            throw new ConfigurationException(root.getNodeName()
                + " does not contains a child element named " + name);
        }

        return null;
    }

    /**
     * getChildElement purpose.
     * 
     * <p>
     * Used to help with XML manipulations. Returns the first child element of
     * the specified name.
     * </p>
     *
     * @param root The root element to look for children in.
     * @param name The name of the child element to look for.
     *
     * @return The child element found, null if not found.
     *
     * @see getChildElement(Element,String,boolean)
     */
    public static Element getChildElement(Element root, String name) {
        try {
            return getChildElement(root, name, false);
        } catch (ConfigurationException e) {
            //will never be here.
            return null;
        }
    }

    /**
     * getIntAttribute purpose.
     * 
     * <p>
     * Used to help with XML manipulations. Returns the first child integer
     * attribute of the specified name.  An exception occurs when the node is
     * required and not found.
     * </p>
     *
     * @param elem The root element to look for children in.
     * @param attName The name of the attribute to look for.
     * @param mandatory true when an exception should be thrown if the
     *        attribute element does not exist.
     * @param defaultValue a default value to return incase the attribute was
     *        not found. mutually exclusive with the ConfigurationException
     *        thrown.
     *
     * @return The int value if the attribute was found, the default otherwise.
     *
     * @throws ConfigurationException When a attribute element is required and
     *         not found.
     */
    public static int getIntAttribute(Element elem, String attName,
        boolean mandatory, int defaultValue) throws ConfigurationException {
        String attValue = getAttribute(elem, attName, mandatory);

        if (!mandatory && (attValue == null)) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(attValue);
        } catch (Exception ex) {
            if (mandatory) {
                throw new ConfigurationException(attName
                    + " attribute of element " + elem.getNodeName()
                    + " must be an integer, but it's '" + attValue + "'");
            } else {
                return defaultValue;
            }
        }
    }

    /**
     * getIntAttribute purpose.
     * 
     * <p>
     * Used to help with XML manipulations. Returns the first child integer
     * attribute of the specified name.  An exception occurs when the node is
     * required and not found.
     * </p>
     *
     * @param elem The root element to look for children in.
     * @param attName The name of the attribute to look for.
     * @param mandatory true when an exception should be thrown if the
     *        attribute element does not exist.
     *
     * @return The value if the attribute was found, the null otherwise.
     *
     * @throws ConfigurationException When a child attribute is required and
     *         not found.
     * @throws NullPointerException DOCUMENT ME!
     */
    public static String getAttribute(Element elem, String attName,
        boolean mandatory) throws ConfigurationException {
        if (elem == null) {
            if (mandatory) {
                throw new NullPointerException();
            }

            return "";
        }

        Attr att = elem.getAttributeNode(attName);

        String value = null;

        if (att != null) {
            value = att.getValue();
        }

        if (mandatory) {
            if (att == null) {
                throw new ConfigurationException("element "
                    + elem.getNodeName()
                    + " does not contains an attribute named " + attName);
            } else if ("".equals(value)) {
                throw new ConfigurationException("attribute " + attName
                    + "in element " + elem.getNodeName() + " is empty");
            }
        }

        return value;
    }

    /**
     * getBooleanAttribute purpose.
     * 
     * <p>
     * Used to help with XML manipulations. Returns the first child integer
     * attribute of the specified name.  An exception occurs when the node is
     * required and not found.
     * </p>
     *
     * @param elem The root element to look for children in.
     * @param attName The name of the attribute to look for.
     * @param mandatory true when an exception should be thrown if the
     *        attribute element does not exist.
     * @param defaultValue what to return for a non-mandatory that is not
     *        found.
     *
     * @return The value if the attribute was found, the false otherwise.
     *
     * @throws ConfigurationException When a child attribute is required and
     *         not found.
     */
    public static boolean getBooleanAttribute(Element elem, String attName,
        boolean mandatory, boolean defaultValue) throws ConfigurationException {
        String value = getAttribute(elem, attName, mandatory);

        if ((value == null) || (value == "")) {
            return defaultValue;
        }

        return Boolean.valueOf(value).booleanValue();
    }

    /**
     * getChildText purpose.
     * 
     * <p>
     * Used to help with XML manipulations. Returns the first child text value
     * of the specified element name.
     * </p>
     *
     * @param root The root element to look for children in.
     * @param childName The name of the attribute to look for.
     *
     * @return The value if the child was found, the null otherwise.
     */
    public static String getChildText(Element root, String childName) {
        try {
            return getChildText(root, childName, false);
        } catch (ConfigurationException ex) {
            return null;
        }
    }

    /**
     * getChildText purpose.
     * 
     * <p>
     * Used to help with XML manipulations. Returns the first child text value
     * of the specified element name.  An exception occurs when the node is
     * required and not found.
     * </p>
     *
     * @param root The root element to look for children in.
     * @param childName The name of the attribute to look for.
     * @param mandatory true when an exception should be thrown if the text
     *        does not exist.
     *
     * @return The value if the child was found, the null otherwise.
     *
     * @throws ConfigurationException When a child attribute is required and
     *         not found.
     */
    public static String getChildText(Element root, String childName,
        boolean mandatory) throws ConfigurationException {
        Element elem = getChildElement(root, childName, mandatory);

        if (elem != null) {
            return getElementText(elem, mandatory);
        } else {
            if (mandatory) {
                String msg = "Mandatory child " + childName + "not found in "
                    + " element: " + root;

                throw new ConfigurationException(msg);
            }

            return null;
        }
    }

    /**
     * getChildText purpose.
     * 
     * <p>
     * Used to help with XML manipulations. Returns the text value of the
     * specified element name.
     * </p>
     *
     * @param elem The root element to look for children in.
     *
     * @return The value if the text was found, the null otherwise.
     */
    public static String getElementText(Element elem) {
        try {
            return getElementText(elem, false);
        } catch (ConfigurationException ex) {
            return null;
        }
    }

    /**
     * getChildText purpose.
     * 
     * <p>
     * Used to help with XML manipulations. Returns the text value of the
     * specified element name.  An exception occurs when the node is required
     * and not found.
     * </p>
     *
     * @param elem The root element to look for children in.
     * @param mandatory true when an exception should be thrown if the text
     *        does not exist.
     *
     * @return The value if the text was found, the null otherwise.
     *
     * @throws ConfigurationException When text is required and not found.
     */
    public static String getElementText(Element elem, boolean mandatory)
        throws ConfigurationException {
        String value = null;

        LOGGER.finer("getting element text for " + elem);

        if (elem != null) {
            Node child;

            NodeList childs = elem.getChildNodes();

            int nChilds = childs.getLength();

            for (int i = 0; i < nChilds; i++) {
                child = childs.item(i);

                if (child.getNodeType() == Node.TEXT_NODE) {
                    value = child.getNodeValue();

                    if (mandatory && "".equals(value.trim())) {
                        throw new ConfigurationException(elem.getNodeName()
                            + " text is empty");
                    }

                    break;
                }
            }

            if (mandatory && (value == null)) {
                throw new ConfigurationException(elem.getNodeName()
                    + " element does not contains text");
            }
        } else {
            throw new ConfigurationException("Argument element can't be null");
        }

        LOGGER.finer("got " + value);

        return value;
    }

    /**
     * getKeyWords purpose.
     * 
     * <p>
     * Used to help with XML manipulations. Returns a list of keywords that
     * were found.
     * </p>
     *
     * @param keywordsElem The root element to look for children in.
     *
     * @return The list of keywords that were found.
     */
    public static String[] getKeyWords(Element keywordsElem) {
        NodeList klist = keywordsElem.getElementsByTagName("keyword");
        int kCount = klist.getLength();
        List keywords = new ArrayList(kCount);
        String kword;
        Element kelem;

        for (int i = 0; i < kCount; i++) {
            kelem = (Element) klist.item(i);
            kword = getElementText(kelem);

            if (kword != null) {
                keywords.add(kword);
            }
        }

        Object[] s = (Object[]) keywords.toArray();

        if (s == null) {
            return new String[0];
        }

        String[] ss = new String[s.length];

        for (int i = 0; i < ss.length; i++)
            ss[i] = s[i].toString();

        return ss;
    }

    /**
     * getFirstChildElement purpose.
     * 
     * <p>
     * Used to help with XML manipulations. Returns the element which
     * represents the first child.
     * </p>
     *
     * @param root The root element to look for children in.
     *
     * @return The element if a child was found, the null otherwise.
     */
    public static Element getFirstChildElement(Element root) {
        Node child = root.getFirstChild();

        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) child;
            }

            child = child.getNextSibling();
        }

        return null;
    }

    /**
     * getDoubleAttribute purpose.
     * 
     * <p>
     * Used to help with XML manipulations. Returns the first child integer
     * attribute of the specified name.  An exception occurs when the node is
     * required and not found.
     * </p>
     *
     * @param elem The root element to look for children in.
     * @param attName The name of the attribute to look for.
     * @param mandatory true when an exception should be thrown if the
     *        attribute element does not exist.
     *
     * @return The double value if the attribute was found, the NaN otherwise.
     *
     * @throws ConfigurationException When a attribute element is required and
     *         not found.
     */
    public static double getDoubleAttribute(Element elem, String attName,
        boolean mandatory) throws ConfigurationException {
        String value = getAttribute(elem, attName, mandatory);

        if ((value == null) || (value == "")) {
            return 0.0;
        }

        double d = Double.NaN;

        if (value != null) {
            try {
                d = Double.parseDouble(value);
            } catch (NumberFormatException ex) {
                throw new ConfigurationException("Illegal attribute value for "
                    + attName + " in element " + elem.getNodeName()
                    + ". Expected double, but was " + value);
            }
        }

        return d;
    }
}
