/* Copyright (c) 2001, 2003 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.vfny.geoserver.responses;

import org.vfny.geoserver.config.ConfigInfo;
import org.vfny.geoserver.config.TypeInfo;
import org.vfny.geoserver.config.TypeRepository;
import org.vfny.geoserver.requests.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;


/**
 * Handles a DescribeFeatureType request and creates a DescribeFeatureType
 * response GML string.
 *
 * @author Rob Hranac, TOPP
 * @author Chris Holmes, TOPP
 * @version $Id: DescribeResponse.java,v 1.18 2003/09/15 22:48:06 cholmesny Exp $
 */
public class DescribeResponse {
    /** Standard logging instance for class */
    private static final Logger LOGGER = Logger.getLogger(
            "org.vfny.geoserver.responses");

    /** Bean that holds global server configuration information. */
    private static ConfigInfo config = ConfigInfo.getInstance();

    /** Bean that holds global featureType information */
    private static TypeRepository typeRepo = TypeRepository.getInstance();

    // Initialize some generic GML information
    // ABSTRACT OUTSIDE CLASS, IF POSSIBLE
    private static final String SCHEMA_URI = "\"http://www.w3.org/2001/XMLSchema\"";
    private static final String XS_NAMESPACE = "\n  xmlns:xs=" + SCHEMA_URI;
    private static final String GML_NAMESPACE = "\n  xmlns:gml=\"http://www.opengis.net/gml\"";
    private static final String HEADER = config.getXmlHeader()
        + "\n<xs:schema ";
    private static final String ELEMENT_FORM_DEFAULT = "\n  elementFormDefault=\"qualified\"";
    private static final String ATTR_FORM_DEFAULT = "\n  attributeFormDefault=\"unqualified\" version=\"1.0\">";
    private static final String TARGETNS_PREFIX = "\n  targetNamespace=\"";
    private static final String TARGETNS_SUFFIX = "\" ";
    private static final String GML_IMPORT =
        "\n\n<xs:import namespace=\"http://www.opengis.net/gml\""
        + " schemaLocation=\"http://schemas.opengis.net/gml/2.1.2/feature.xsd\"/>\n\n";

    /** Fixed return footer information */
    private static final String FOOTER = "\n</xs:schema>";

    /** Main XML class for interpretation and response. */
    private String xmlResponse = new String();

    /**
     * Constructor with request.
     *
     * @param wfsRequest The DescribeFeatureType request object.
     *
     * @throws WfsException For any problems making the xml response.
     */
    public DescribeResponse(DescribeRequest wfsRequest)
        throws WfsException {
        LOGGER.finer("processing describe request" + wfsRequest);

        String outputFormat = wfsRequest.getOutputFormat();

        if (!outputFormat.equalsIgnoreCase("XMLSCHEMA")) {
            throw new WfsException("output format: " + outputFormat + " not "
                + "supported by geoserver");
        }

        // generates response, using general function 
        xmlResponse = generateTypes(wfsRequest);

        if (!config.formatOutput()) {
            //strip out the formatting.  This is pretty much the only way we
            //can do this, as the user files are going to have newline
            //characters and whatnot, unless we can get rid of formatting
            //when we read the file, which could be worth looking into if
            //this slows things down.
            xmlResponse = xmlResponse.replaceAll(">\n[ \\t\\n]*", ">");
            xmlResponse = xmlResponse.replaceAll("\n[ \\t\\n]*", " ");
        }
    }

    /**
     * Returns the xml response constructed.
     *
     * @return The describe response from the request constructed with.
     */
    public String getXmlResponse() {
        return xmlResponse;
    }

    /**
     * Internal method to generate the XML response object, using feature
     * types.
     *
     * @param wfsRequest The request object.
     *
     * @return The XMLSchema describing the features requested.
     *
     * @throws WfsException For any problems.
     */
    private final String generateTypes(DescribeRequest wfsRequest)
        throws WfsException {
        List requestedTypes = wfsRequest.getFeatureTypes();

        // Initialize return information and intermediate return objects
        StringBuffer tempResponse = new StringBuffer();

        //ComplexType table = new ComplexType();
        if (requestedTypes.size() == 0) {
            //if there are no specific requested types then get all.
            requestedTypes = typeRepo.getAllTypeNames();
        }

        tempResponse.append(HEADER);

        //allSameType will throw WfsException if there are types that are not found. 
        if (typeRepo.allSameType(requestedTypes)) {
            //all the requested have the same namespace prefix, so return their
            //schemas.
            TypeInfo nsInfoType = typeRepo.getType((String) requestedTypes.get(
                        0));

            //all types have same prefix, so just use the first.
            String nsPrefix = nsInfoType.getPrefix();
            String targetNs = nsInfoType.getXmlns();
            tempResponse.append(TARGETNS_PREFIX + targetNs + TARGETNS_SUFFIX);
            tempResponse.append("\n  xmlns:" + nsPrefix + "=\"" + targetNs
                + "\"");
            tempResponse.append(GML_NAMESPACE);
            tempResponse.append(XS_NAMESPACE);
            tempResponse.append(ELEMENT_FORM_DEFAULT + ATTR_FORM_DEFAULT);

            //this is not always necessary, but it doesn't seem to hurt...
            tempResponse.append(GML_IMPORT);
            tempResponse.append(generateSpecifiedTypes(requestedTypes));
        } else {
            //the featureTypes do not have all the same prefixes.
            tempResponse.append(XS_NAMESPACE);
            tempResponse.append(ELEMENT_FORM_DEFAULT + ATTR_FORM_DEFAULT);

            Set prefixes = new HashSet();
            Iterator nameIter = requestedTypes.iterator();

            //iterate through the types, and make a set of their prefixes.
            while (nameIter.hasNext()) {
                String typeName = nameIter.next().toString();
                String typePrefix = typeRepo.getType(typeName).getPrefix();
                prefixes.add(typePrefix);
            }

            Iterator prefixIter = prefixes.iterator();

            while (prefixIter.hasNext()) {
                //iterate through prefixes, and add the types that have that prefix.
                String prefix = prefixIter.next().toString();
                tempResponse.append(getNSImport(prefix, requestedTypes));
            }
        }

        tempResponse.append(FOOTER);

        return tempResponse.toString();
    }

    /**
     * Creates a import namespace element, for cases when requests contain
     * multiple namespaces, as you can not have more than one target
     * namespace.  See wfs spec. 8.3.1.  All the typeNames that have the
     * correct prefix are added to the import statement.
     *
     * @param prefix the namespace prefix, which must be mapped in the main
     *        ConfigInfo, for this import statement.
     * @param typeNames a list of all requested typeNames, only those that
     *        match the prefix will be a part of this import statement.
     *
     * @return The namespace element.
     */
    private StringBuffer getNSImport(String prefix, List typeNames) {
        LOGGER.finer("prefix is " + prefix);

        StringBuffer retBuffer = new StringBuffer("\n  <xs:import namespace=\"");
        String namespace = config.getNSUri(prefix);
        retBuffer.append(namespace + "\"");
        retBuffer.append("\n        schemaLocation=\"" + config.getUrl()
            + "/DescribeFeatureType?" //HACK: bad hard code here.
            + "typeName=");

        Iterator nameIter = typeNames.iterator();

        //boolean first = true;
        while (nameIter.hasNext()) {
            String typeName = nameIter.next().toString();

            if (typeName.startsWith(prefix)
                    || ((typeName.indexOf(':') == -1)
                    && prefix.equals(config.getDefaultNSPrefix()))) {
                retBuffer.append(typeName + ",");
            }
        }

        retBuffer.deleteCharAt(retBuffer.length() - 1);
        retBuffer.append("\"/>");

        return retBuffer;
    }

    /**
     * Internal method to print just the requested types.
     *
     * @param requestedTypes The requested table names.
     *
     * @return DOCUMENT ME!
     *
     * @throws WfsException DOCUMENT ME!
     *
     * @task REVISIT: We need a way to make sure the extension bases are
     *       correct. should likely add a field to the info.xml in the
     *       featureTypes folder, that optionally references an extension base
     *       (should it be same namespace? we could also probably just do an
     *       import on the extension base).  This function then would see if
     *       the typeInfo has an extension base, and would add or import the
     *       file appropriately, and put the correct substitution group in
     *       this function.
     */
    private String generateSpecifiedTypes(List requestedTypes)
        throws WfsException {
        TypeRepository repository = TypeRepository.getInstance();
        String tempResponse = new String();
        String currentFile = new String();
        String curTypeName = new String();
        String generatedType = new String();
        Set validTypes = new HashSet();

        // Loop through requested tables to add element types
        for (int i = 0; i < requestedTypes.size(); i++) {
            // set the current file
            // print type data for the table object
            curTypeName = requestedTypes.get(i).toString();

            TypeInfo meta = repository.getType(curTypeName);
            curTypeName = meta.getFullName();

            if (meta == null) {
                throw new WfsException("Feature Type " + curTypeName + " does "
                    + "not exist on this server");
            }

            if (!validTypes.contains(meta)) {
                currentFile = meta.getSchemaFile();
                generatedType = writeFile(currentFile);

                if (!generatedType.equals("")) {
                    tempResponse = tempResponse + writeFile(currentFile);
                    validTypes.add(meta);
                }
            }
        }

        // Loop through requested tables again to add elements
        // NOT VERY EFFICIENT - PERHAPS THE MYSQL ABSTRACTION CAN FIX THIS; 
        //  STORE IN HASH?
        for (Iterator i = validTypes.iterator(); i.hasNext();) {
            // Print element representation of table
            tempResponse = tempResponse + printElement((TypeInfo) i.next());
        }

        tempResponse = tempResponse + "\n\n";

        return tempResponse;
    }

    /**
     * Internal method to print XML element information for table.
     *
     * @param type The table name.
     *
     * @return The element part of the response.
     */
    private static String printElement(TypeInfo type) {
        //int prefixDelimPos = typeName.indexOf(":");
        //String tablename = typeName;
        //if (prefixDelimPos > 0) {
        //String tableName = typeName.substring(prefixDelimPos + 1);
        //  } 
        return "\n  <xs:element name='" + type.getName() + "' type='"
        + type.getFullName() + "_Type' substitutionGroup='gml:_Feature'/>";
    }

    /**
     * Adds a feature type object to the final output buffer
     *
     * @param inputFileName The name of the feature type.
     *
     * @return The string representation of the file containing the schema.
     *
     * @throws WfsException For io problems reading the file.
     */
    public String writeFile(String inputFileName) throws WfsException {
        LOGGER.finest("writing file " + inputFileName);

        String finalOutput = new String();

        try {
            File inputFile = new File(inputFileName);
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] fileBuffer = new byte[inputStream.available()];
            int bytesRead;

            while ((bytesRead = inputStream.read(fileBuffer)) != -1) {
                String tempOutput = new String(fileBuffer);
                finalOutput = finalOutput + tempOutput;
            }
        } catch (IOException e) {
            //REVISIT: should things fail if there are featureTypes that 
            //don't have schemas in the right place?  Because as it is now
            //a describe all will choke if there is one ft with no schema.xml
            throw new WfsException("problem writing featureType information "
                + " from " + inputFileName);
        }

        return finalOutput;
    }
}
