/* Copyright (c) 2001, 2003 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.vfny.geoserver.global;

import com.vividsolutions.jts.geom.Envelope;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.jdbc.JDBCDataStore;
import org.geotools.data.jdbc.JDBCFeatureSource;
import org.geotools.data.jdbc.fidmapper.NullFIDMapper;
import org.geotools.factory.FactoryConfigurationError;
import org.geotools.feature.AttributeType;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeFactory;
import org.geotools.feature.SchemaException;
import org.geotools.filter.Filter;
import org.geotools.styling.Style;
import org.vfny.geoserver.global.dto.AttributeTypeInfoDTO;
import org.vfny.geoserver.global.dto.DataTransferObjectFactory;
import org.vfny.geoserver.global.dto.FeatureTypeInfoDTO;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Represents a FeatureTypeInfo, its user config and autodefined information.
 *
 * @author Gabriel Rold�n
 * @author Chris Holmes
 * @author dzwiers
 * @version $Id: FeatureTypeInfo.java,v 1.41 2004/06/26 19:51:24 jive Exp $
 */
public class FeatureTypeInfo extends GlobalLayerSupertype {
	
    private static final Logger LOGGER = Logger.getLogger("org.vfny.geoserver.global");
	
    /** Default constant */
    private static final int DEFAULT_NUM_DECIMALS = 8;
    /**
     * Id used to locate parent DataStoreInfo using Data Catalog.
     */
    private String dataStoreId;
    /**
     * Bounding box in Lat Long of the extent of this FeatureType.
     * <p>
     * Note reprojection may be required to derive this value.
     * </p>
     */
    private Envelope latLongBBox;
    /**
     * SRS number used to locate Coordidate Reference Systems
     * <p>
     * This will be used for reprojection and such like.
     * </p>
     */
    private int SRS;
    
    /**
     * List of AttributeTypeInfo representing the schema.xml information.
     * <p>
     * Used to define the order and manditoryness of FeatureType attributes
     * during query (re)construction.
     * </p> 
     */
    private List schema;
    
    /** Name of elment that is an instance of schemaBase */
    private String schemaName;
    
    /** Base schema (usually NullType) defining manditory attribtues */
    private String schemaBase;
    
    /** typeName as defined by gt2 DataStore */
    private String typeName;
    
    /**
     * Directory where featureType is loaded from.
     * 
     * This may contain metadata files.
     */
    private String dirName;
    /**
     * Abstract used to describe FeatureType
     */
    private String _abstract;
    /**
     * List of keywords for Web Register Services
     */
    private List keywords;
    /**
     * Number of decimals used in GML output.
     */
    private int numDecimals;
    /**
     * Magic query used to limit scope of this FeatureType.
     */
    private Filter definitionQuery = null;
    /**
     * Default style used to render this FeatureType with WMS
     */
    private String defaultStyle;
    /**
     * Title of this FeatureType as presented to End-Users.
     * <p>
     * Think of this as the display name on the off chance that typeName
     * is considered ugly.
     * </p>
     */
    private String title;

    /**
     * ref to parent set of datastores.
     * <p>
     * This backpointer to our Catalog can be used to locate our DataStore
     * using the dataStoreId.
     * </p>
     */
    private Data data;
    /**
     * MetaData used by apps to squirel information away for a rainy day.
     */
    private Map meta;

    /**
     * AttributeTypeInfo by attribute name.
     * 
     * <p>
     * This will be null unless populated by schema or DTO.
     * Even if the DTO provides one this list will be lazily
     * created - so use the accessors.
     * </p>
     */
    private String xmlSchemaFrag;

    /**
     * The real geotools2 featureType cached for sanity checks.
     * <p>
     * This will be lazily created so use the accessors, except when doing
     * pass through SQL and schema.xml is not exmpty
     * </p>
     */
    private FeatureType ft = null;

    /**
     * pass through SQL - use this SQL, instead of, or augmented by SQL
     * generation from a WFS filter (the filter as parameter passing mechanism)
     */
    private String bypassSQL = null; 
    
    
    /**
     * Tree representation of XML elements given by xpath attributes in schema.xml
     */
    private XMLelementStructure elementTree = null; 

    
    /**
     * FeatureTypeInfo constructor.
     * 
     * <p>
     * Generates a new object from the data provided.
     * </p>
     *
     * @param dto FeatureTypeInfoDTO The data to populate this class with.
     * @param data Data a reference for future use to get at DataStoreInfo
     *        instances
     *
     * @throws ConfigurationException
     */
    public FeatureTypeInfo(FeatureTypeInfoDTO dto, Data data)
        throws ConfigurationException {
        this.data = data;
        _abstract = dto.getAbstract();
        dataStoreId = dto.getDataStoreId();
        defaultStyle = dto.getDefaultStyle();
        definitionQuery = dto.getDefinitionQuery();
        dirName = dto.getDirName();
        keywords = dto.getKeywords();
        latLongBBox = dto.getLatLongBBox();
        typeName = dto.getName();
        numDecimals = dto.getNumDecimals();

        bypassSQL = dto.getBypassSQL();
        List tmp = dto.getSchemaAttributes();
        schema = new LinkedList();
        
        if ((tmp != null) && !tmp.isEmpty()) {
            Iterator i = tmp.iterator();

            while (i.hasNext())
                schema.add(new AttributeTypeInfo(
                        (AttributeTypeInfoDTO) i.next()));
            
            if (bypassSQL != null && !schema.isEmpty()) {
            	
            	// need to build a geotools feature type corresponding to the
            	// bypassSQL, as described by schema. 
            	ft = buildGeotoolsFeatureType(schema);
            	
            	// then need to stick this and a fid mapper into the cache
            	// maintained by Geotools FeatureTypeHamdler
            	DataStore datastore = data.getDataStoreInfo(dataStoreId).getDataStore();
            	
            	if ( ft != null && datastore instanceof JDBCDataStore ) {
            	
            		// the NullFIDMapper is just placeholder, FID mapping happens
            		// in the schema.xml (xpath attribute)
            		try {
	            		org.geotools.data.jdbc.FeatureTypeInfo fti = 
	            			new org.geotools.data.jdbc.FeatureTypeInfo(
	            				typeName, ft, new NullFIDMapper(), false);
	            				((JDBCDataStore) datastore).getFeatureTypeHandler().setFeatureTypeInfo(fti);
            		} catch (IOException e) {
            			LOGGER.severe("Could not set FeatureType schema " + typeName + 
            				" in data store");
            		}
            	} else if ( ft == null && datastore instanceof JDBCDataStore ) {
        			LOGGER.severe("Failed making FeatureType for " + typeName);
            	}	
            }
            
            // build a tree of XML elements if xpath atts can be found in schema
            elementTree = new XMLelementStructure(this, this.data); 
        }

        schemaBase = dto.getSchemaBase();
        schemaName = dto.getSchemaName();
        SRS = dto.getSRS();
        title = dto.getTitle();
    }

    /**
     * toDTO purpose.
     * 
     * <p>
     * This method is package visible only, and returns a reference to the
     * GeoServerDTO. This method is unsafe, and should only be used with
     * extreme caution.
     * </p>
     *
     * @return FeatureTypeInfoDTO the generated object
     */
    Object toDTO() {
        FeatureTypeInfoDTO dto = new FeatureTypeInfoDTO();
        dto.setAbstract(_abstract);
        dto.setDataStoreId(dataStoreId);
        dto.setDefaultStyle(defaultStyle);
        dto.setDefinitionQuery(definitionQuery);
        dto.setDirName(dirName);
        dto.setKeywords(keywords);
        dto.setLatLongBBox(latLongBBox);
        dto.setName(typeName);
        dto.setNumDecimals(numDecimals);

        List tmp = new LinkedList();
        Iterator i = schema.iterator();

        while (i.hasNext()) {
            tmp.add(((AttributeTypeInfo) i.next()).toDTO());
        }

        dto.setSchemaAttributes(tmp);
        dto.setSchemaBase(schemaBase);
        dto.setSchemaName( getSchemaName() );        
        dto.setSRS(SRS);
        dto.setTitle(title);
        dto.setBypassSQL(bypassSQL);        

        return dto;
    }

    
    /**
     * buildFeatureType purpose.
     * 
     * <p>
     * Create a Geotools FeatureType for this feature; this should only be called
     * if the AttributeTypeInfo constructor was able to create Geotools a 
     * AttributeType(s), typically when a schema.xml with extended attributes 
     * (dbJavaType) is associated with non null bypassSQL (feature type uses pass 
     * through SQL operation).  
     * </p>
     *
     * @param schema - a List of Geoserver AttributeTypeInfo 
     * @return the FeatureType or null if failed
     */
    public FeatureType buildGeotoolsFeatureType(List schema) {

    	List attributeTypes = new ArrayList();
    	
        Iterator i = schema.iterator();
        while (i.hasNext()) {
        	attributeTypes.add( ((AttributeTypeInfo) i.next()).getAttributeType() );
        } 
        AttributeType[] types = (AttributeType[]) attributeTypes.toArray(new AttributeType[0]);

    	// should check datastore is assignable to JDBCDataStore

        FeatureType featuretype = null;
    	if (types.length > 0) {
    		try {
    			featuretype = FeatureTypeFactory.newFeatureType(types, typeName, 
	        			((JDBCDataStore)(data.getDataStoreInfo(dataStoreId).getDataStore())).getNameSpaceURI());
    		} catch (SchemaException e) {}
    	}
    	
    	return featuretype;
    }
    
    
    /**
     * getNumDecimals purpose.
     * 
     * <p>
     * The default number of decimals allowed in the data.
     * </p>
     *
     * @return int the default number of decimals allowed in the data.
     */
    public int getNumDecimals() {
        return numDecimals;
    }

    /**
     * getDataStore purpose.
     * 
     * <p>
     * gets the string of the path to the schema file.  This is set during
     * feature reading, the schema file should be in the same folder as the
     * feature type info, with the name schema.xml.  This function does not
     * guarantee that the schema file actually exists, it just gives the
     * location where it _should_ be located.
     * </p>
     *
     * @return DataStoreInfo the requested DataStoreInfo if it was found.
     *
     * @see Data#getDataStoreInfo(String)
     */
    public DataStoreInfo getDataStoreInfo() {
        return data.getDataStoreInfo(dataStoreId);
    }

    /**
     * By now just return the default style to be able to declare it in
     * WMS capabilities, but all this stuff needs to be revisited since it seems
     * currently there is no way of retrieving all the styles declared for
     * a given FeatureType.
     * 
     * @return the default Style for the FeatureType
     */
    public Style getDefaultStyle(){
    	return data.getStyle(defaultStyle);
    }
    
    /**
     * Indicates if this FeatureTypeInfo is enabled.  For now just gets whether
     * the backing datastore is enabled.
     *
     * @return <tt>true</tt> if this FeatureTypeInfo is enabled.
     *
     * @task REVISIT: Consider adding more fine grained control to config
     *       files, so users can indicate specifically if they want the
     *       featureTypes enabled, instead of just relying on if the datastore
     *       is. Jody here - this should be done on a service by service basis
     *       WMS and WFS will need to decide for themselves on this one
     */
    public boolean isEnabled() {
        return (getDataStoreInfo() != null) && (getDataStoreInfo().isEnabled());
    }

    /**
     * Returns the XML prefix used for GML output of this FeatureType.
     * 
     * <p>
     * Returns the namespace prefix for this FeatureTypeInfo.
     * </p>
     *
     * @return String the namespace prefix.
     */
    public String getPrefix() {
        return getDataStoreInfo().getNameSpace().getPrefix();
    }

    /**
     * Gets the namespace for this featureType.
     * <p>
     * This isn't _really_ necessary,
     * but I'm putting it in in case we change namespaces,  letting
     * FeatureTypes set their own namespaces instead of being dependant on
     * datasources.  This method will allow us to make that change more easily
     * in the future.
     *
     * @return NameSpaceInfo the namespace specified for the specified
     *         DataStoreInfo (by ID)
     *
     * @throws IllegalStateException THrown when disabled.
     */
    public NameSpaceInfo getNameSpace() {
        if (!isEnabled()) {
            throw new IllegalStateException("This featureType is not "
                + "enabled");
        }
        return getDataStoreInfo().getNameSpace();
    }
    
    /**
     * Complete xml name (namespace:element> for this FeatureType.
     * 
     * This is the full type name with namespace prefix.
     *
     * @return String the FeatureTypeInfo name - should be unique for the
     *         parent Data instance.
     */
    public String getName() {        
        return getPrefix() + ":" + typeName;
    }    

    /**
     * GT2 Feature Type name
     * 
     * type name withOUT the namespace prefix
     *
     * @return String the FeatureTypeInfo name - should be unique for the
     *         parent Data instance.
     */
    public String getGT2FeatureTypeName() {        
        return typeName;
    }    
   
    
    /**
     * getFeatureSource purpose.
     * 
     * <p>
     * Returns a real FeatureSource.
     * </p>
     *
     * @return FeatureSource the feature source represented by this info class
     *
     * @throws IOException when an error occurs.
     */
    public FeatureSource getFeatureSource() throws IOException {
        if (!isEnabled() || (getDataStoreInfo().getDataStore() == null)) {
            throw new IOException("featureType: " + getName()
                + " does not have a properly configured " + "datastore");
        }

        DataStore dataStore = data.getDataStoreInfo(dataStoreId).getDataStore();
        if (dataStore instanceof JDBCDataStore && bypassSQL != null &&
        		!schema.isEmpty() ) {
        	// for pass through SQL, use the FetaureType built here 
        	return new JDBCFeatureSource((JDBCDataStore)dataStore, ft);
        }
        
        FeatureSource realSource = dataStore.getFeatureSource(typeName);

        if (((schema == null) || schema.isEmpty())) { // &&

            //(ftc.getDefinitionQuery() == null || ftc.getDefinitionQuery().equals( Query.ALL ))){
            return realSource;
        } else {
            return GeoServerFeatureLocking.create(realSource,
                getFeatureType(realSource), getDefinitionQuery());
        }
    }
    
    /**
     * getFeatureSource purpose.
     * 
     * <p>
     * Returns a real FeatureSource.
     * </p>
     *
     * @return FeatureSource the feature source represented by this info class
     *
     * @throws IOException when an error occurs.
     */
    public FeatureSource getRealFeatureSource() throws IOException {
    	
        if (!isEnabled() || (getDataStoreInfo().getDataStore() == null)) {
            throw new IOException("featureType: " + getName()
                + " does not have a properly configured " + "datastore");
        }

        DataStore dataStore = data.getDataStoreInfo(dataStoreId).getDataStore();
//        if (dataStore instanceof JDBCDataStore && bypassSQL != null &&
//        		!schema.isEmpty() ) {
        if ( dataStore instanceof JDBCDataStore && (bypassSQL != null ||
        		!schema.isEmpty()) ) {        	
        	// for pass through SQL, use the FetaureType built here
        	// which may have been supplied by a schema.xml or generated from
        	// resultset metadata
        	return new JDBCFeatureSource((JDBCDataStore)dataStore, ft);
        } else {
        	return dataStore.getFeatureSource(typeName);
        }
    }    
    
    
    /*public static FeatureSource reTypeSource(FeatureSource source,
       FeatureTypeInfoDTO ftc) throws SchemaException {
       AttributeType[] attributes = new AttributeType[ftc.getSchemaAttributes()
                                                         .size()];
       List attributeDefinitions = ftc.getSchemaAttributes();
       int index = 0;
       FeatureType ft = source.getSchema();
       for (int i = 0; i < attributes.length; i++) {
           AttributeTypeInfoDTO attributeDTO = (AttributeTypeInfoDTO) ftc.getSchemaAttributes()
                                                                         .get(i);
           String xpath = attributeDTO.getName();
           attributes[i] = ft.getAttributeType(xpath);
           if (attributes[i] == null) {
               throw new NullPointerException("Error finding " + xpath
                   + " specified in you schema.xml file for " + ftc.getName()
                   + "FeatureType.");
           }
       }
       FeatureType myType = FeatureTypeFactory.newFeatureType(attributes,
               ftc.getName());
       return GeoServerFeatureLocking.create(source, myType,
           ftc.getDefinitionQuery());
       }*/

    /**
     * getBoundingBox purpose.
     * 
     * <p>
     * The feature source bounds.
     * </p>
     *
     * @return Envelope the feature source bounds.
     *
     * @throws IOException when an error occurs
     */
    public Envelope getBoundingBox() throws IOException {
        DataStore dataStore = data.getDataStoreInfo(dataStoreId).getDataStore();
        FeatureSource realSource = dataStore.getFeatureSource(typeName);

        return realSource.getBounds();
    }

    /**
     * getDefinitionQuery purpose.
     * 
     * <p>
     * Returns the definition query for this feature source
     * </p>
     *
     * @return Filter the definition query
     */
    public Filter getDefinitionQuery() {
        return definitionQuery;
    }

    /**
     * getLatLongBoundingBox purpose.
     * 
     * <p>
     * The feature source lat/long bounds.
     * </p>
     *
     * @return Envelope the feature source lat/long bounds.
     *
     * @throws IOException when an error occurs
     */
    public Envelope getLatLongBoundingBox() throws IOException {
        if (latLongBBox == null) {
            return getBoundingBox();
        }

        return latLongBBox;
    }

    /**
     * getSRS purpose.
     * 
     * <p>
     * Proprietary identifier number
     * </p>
     *
     * @return int the SRS number.
     */
    public String getSRS() {
        return SRS + "";
    }

    /**
     * Get XMLSchema for this FeatureType.
     * 
     * <p>
     * Note this may require connection to the real geotools2 DataStore and as
     * such is subject to IOExceptions.
     * </p>
     * 
     * <p>
     * You have been warned.
     * </p>
     *
     * @return XMLFragment
     *
     * @throws IOException DOCUMENT ME!
     */

    /* public synchronized String getXMLSchema(){
       if (xmlSchemaFrag == null) {
           StringWriter sw = new StringWriter();
           try {
               FeatureTypeInfoDTO dto = getGeneratedDTO();
               XMLConfigWriter.storeFeatureSchema(dto, sw);
           } catch (ConfigurationException e) {
                   e.printStackTrace();
           } catch (IOException e) {
                   e.printStackTrace();
                   xmlSchemaFrag = null;
           }
           xmlSchemaFrag = sw.toString();
           try{
           sw.close();
           }catch(IOException e){}
       }
       return xmlSchemaFrag;
       }*/

    /**
     * Will return our delegate with all information filled out
     * 
     * <p>
     * This is a hack because we cache our DTO delegate, this method combines
     * or ftc delegate with possibly generated schema information for use by
     * XMLConfigWriter among others.
     * </p>
     * 
     * <p>
     * Call this method to receive a complete featureTypeInfoDTO that incldues
     * all schema information.
     * </p>
     *
     * @return
     *
     * @throws IOException DOCUMENT ME!
     */
    private synchronized FeatureTypeInfoDTO getGeneratedDTO()
        throws IOException {
        return DataTransferObjectFactory.create(dataStoreId, getFeatureType());
    }

    /**
     * getAttribute purpose.
     * 
     * <p>
     * XLM helper method.
     * </p>
     *
     * @param elem The element to work on.
     * @param attName The attribute name to find
     * @param mandatory true is an exception is be thrown when the attr is not
     *        found.
     *
     * @return String the Attr value
     *
     * @throws ConfigurationException thrown when an error occurs.
     */
    protected String getAttribute(Element elem, String attName,
        boolean mandatory) throws ConfigurationException {
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

    /*private FeatureType getSchema(String schema) throws ConfigurationException{
       try{
               return getSchema(loadConfig(new StringReader(schema)));
       }catch(IOException e){
               throw new ConfigurationException("",e);
       }
       }*/

    /**
     * loadConfig purpose.
     * 
     * <p>
     * Parses the specified file into a DOM tree.
     * </p>
     *
     * @param fromSrId The file to parse int a DOM tree.
     * @param bbox DOCUMENT ME!
     *
     * @return the resulting DOM tree
     */

    /*public static Element loadConfig(Reader fis) throws ConfigurationException {
       try {
           InputSource in = new InputSource(fis);
           DocumentBuilderFactory dfactory = DocumentBuilderFactory
               .newInstance();
           /*set as optimizations and hacks for geoserver schema config files
     * @HACK should make documents ALL namespace friendly, and validated. Some documents are XML fragments.
     * @TODO change the following config for the parser and modify config files to avoid XML fragmentation.
     */
    /*       dfactory.setNamespaceAware(false);
       dfactory.setValidating(false);
       dfactory.setIgnoringComments(true);
       dfactory.setCoalescing(true);
       dfactory.setIgnoringElementContentWhitespace(true);
       Document serviceDoc = dfactory.newDocumentBuilder().parse(in);
       Element configElem = serviceDoc.getDocumentElement();
       return configElem;
       } catch (IOException ioe) {
           String message = "problem reading file " + "due to: "
               + ioe.getMessage();
           LOGGER.warning(message);
           throw new ConfigurationException(message, ioe);
       } catch (ParserConfigurationException pce) {
           String message =
               "trouble with parser to read org.vfny.geoserver.config.org.vfny.geoserver.config.xml, make sure class"
               + "path is correct, reading file ";
           LOGGER.warning(message);
           throw new ConfigurationException(message, pce);
       } catch (SAXException saxe) {
           String message = "trouble parsing XML " + ": " + saxe.getMessage();
           LOGGER.warning(message);
           throw new ConfigurationException(message, saxe);
       }
       }*/

    /**
     * here we must make the transformation. Crhis: do you know how to do it? I
     * don't know.  Ask martin or geotools devel.  This will be better when
     * our geometries actually have their srs objects.  And I think that we
     * may need some MS Access database, not sure, but I saw some stuff about
     * that on the list.  Hopefully they'll do it all in java soon.  I'm sorta
     * tempted to just have users define for now.
     *
     * @param fromSrId
     * @param bbox Envelope
     *
     * @return Envelope
     */
    private static Envelope getLatLongBBox(String fromSrId, Envelope bbox) {
        return bbox;
    }

    /**
     * Get abstract (description) of FeatureType.
     *
     * @return Short description of FeatureType
     */
    public String getAbstract() {
        return _abstract;
    }

    /**
     * Keywords describing content of FeatureType.
     * 
     * <p>
     * Keywords are often used by Search engines or Catalog services.
     * </p>
     *
     * @return List the FeatureTypeInfo keywords
     */
    public List getKeywords() {
        return keywords;
    }

    /**
     * getTitle purpose.
     * 
     * <p>
     * returns the FeatureTypeInfo title
     * </p>
     *
     * @return String the FeatureTypeInfo title
     */
    public String getTitle() {
        return title;
    }

    /**
     * A valid schema name for this FeatureType.
     * 
     * @return schemaName if provided or typeName+"_Type"
     */
    public String getSchemaName() {
        if( schemaName == null ){
            return typeName + "_Type";
        }
        return schemaName;
    }

    /**
     * setSchemaName purpose.
     * 
     * <p>
     * Description ...
     * </p>
     *
     * @param string
     */
    public void setSchemaName(String string) {
        schemaName = string;
    }

    /**
     * getSchemaName purpose.
     * 
     * <p>
     * Description ...
     * </p>
     *
     * @return
     */
    public String getSchemaBase() {
        return schemaBase;
    }

    /**
     * setSchemaName purpose.
     * 
     * <p>
     * Description ...
     * </p>
     *
     * @param string
     */
    public void setSchemaBase(String string) {
        schemaBase = string;
    }

    //
    // FeatureTypeMetaData Interface
    //
    /**
     * Access the name of this FeatureType.
     * <p>
     * This is the typeName as provided by the real gt2 DataStore.
     * </p>
     *
     * @return String getName()
     * @see org.geotools.data.FeatureTypeMetaData#getTypeName()
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Access real geotools2 FeatureType.
     *
     * @return Schema information.
     *
     * @throws IOException
     *
     * @see org.geotools.data.FeatureTypeMetaData#getFeatureType()
     */
    public FeatureType getFeatureType() throws IOException {
        return getFeatureType(getFeatureSource());
    }

    /**
     *
     */
    private FeatureType getFeatureType(FeatureSource fs)
        throws IOException {
        if (ft == null) {
            int count = 0;
            ft = fs.getSchema();

            String[] baseNames = DataTransferObjectFactory
                .getRequiredBaseAttributes(schemaBase);
            AttributeType[] attributes = new AttributeType[schema.size()
                + baseNames.length];

            if (attributes.length > 0) {
                int errors = 0;

                for (; count < baseNames.length; count++) {
                    attributes[count - errors] = ft.getAttributeType(baseNames[count]);

                    if (attributes[count - errors] == null) {
                        // desired base attr is not availiable
                        errors++;
                    }
                }

                if (errors != 0) {
                    //resize array;
                    AttributeType[] tmp = new AttributeType[attributes.length
                        - errors];
                    count = count - errors;

                    for (int i = 0; i < count; i++) {
                        tmp[i] = attributes[i];
                    }

                    attributes = tmp;
                }

                for (Iterator i = schema.iterator(); i.hasNext();) {
                    AttributeTypeInfo ati = (AttributeTypeInfo) i.next();
                    String attName = ati.getName();
                    attributes[count] = ft.getAttributeType(attName);

                    if (attributes[count] == null) {
                        throw new IOException("the FeatureType " + getName()
                            + " does not contains the configured attribute "
                            + attName + ". Check your schema configuration");
                    }

                    count++;
                }

                try {
                    ft = FeatureTypeFactory.newFeatureType(attributes, typeName);
                } catch (SchemaException ex) {
                } catch (FactoryConfigurationError ex) {
                }
            }
        }

        return ft;
    }

    /**
     * Implement getDataStoreMetaData.
     *
     * @return
     *
     * @see org.geotools.data.FeatureTypeMetaData#getDataStoreMetaData()
     */
    public DataStoreInfo getDataStoreMetaData() {
        return data.getDataStoreInfo(dataStoreId);
    }

    /**
     * FeatureType attributes names as a List.
     * 
     * <p>
     * Convience method for accessing attribute names as a Collection. You may
     * use the names for AttributeTypeMetaData lookup or with the schema for
     * XPATH queries.
     * </p>
     *
     * @return List of attribute names
     *
     * @task REVISIT: This method sucks.  It didn't do the same thing as
     *       getAttributes, which it should have.  I fixed the root problem of
     *       why attribs.size() would equal 0.  So the second half of this
     *       method should probably be eliminated, as it should never be
     *       called. But I don't want to break code right before a release -
     *       ch.
     *
     * @see org.geotools.data.FeatureTypeMetaData#getAttributeNames()
     */
    public List getAttributeNames() {
        List attribs = schema;

        if (attribs.size() != 0) {
            List list = new ArrayList(attribs.size());

            for (Iterator i = attribs.iterator(); i.hasNext();) {
                AttributeTypeInfo at = (AttributeTypeInfo) i.next();
                list.add(at.getName());
            }

            return list;
        }

        List list = new ArrayList();

        try {
            FeatureType schema = getFeatureType();
            AttributeType[] types = schema.getAttributeTypes();
            list = new ArrayList(types.length);

            for (int i = 0; i < types.length; i++) {
                list.add(types[i].getName());
            }
        } catch (IOException e) {
        }

        return list;
    }

    /**
     * Returns a list of the attributeTypeInfo objects that make up this
     * FeatureType.
     *
     * @return list of attributeTypeInfo objects.
     */
    public List getAttributes() {
        return schema;
    }

    /**
     * Implement AttributeTypeMetaData.
     * 
     * <p>
     * Description ...
     * </p>
     *
     * @param attributeName
     *
     * @return
     *
     * @see org.geotools.data.FeatureTypeMetaData#AttributeTypeMetaData(java.lang.String)
     */
    public synchronized AttributeTypeInfo AttributeTypeMetaData(
        String attributeName) {
        AttributeTypeInfo info = null;

        if (schema != null) {
            for (Iterator i = schema.iterator(); i.hasNext();) {
                AttributeTypeInfoDTO dto = (AttributeTypeInfoDTO) i.next();
                info = new AttributeTypeInfo(dto);
            }

            DataStore dataStore = data.getDataStoreInfo(dataStoreId)
                                      .getDataStore();

            try {
                FeatureType schema = dataStore.getSchema(typeName);
                info.sync(schema.getAttributeType(attributeName));
            } catch (IOException e) {
            }
        } else {
            // will need to generate from Schema
            DataStore dataStore = data.getDataStoreInfo(dataStoreId)
                                      .getDataStore();

            try {
                FeatureType schema = dataStore.getSchema(typeName);
                info = new AttributeTypeInfo(schema.getAttributeType(
                            attributeName));
            } catch (IOException e) {
            }
        }

        return info;
    }

    /**
     * Implement containsMetaData.
     *
     * @param key
     *
     * @return
     *
     * @see org.geotools.data.MetaData#containsMetaData(java.lang.String)
     */
    public boolean containsMetaData(String key) {
        return meta.containsKey(key);
    }

    /**
     * Implement putMetaData.
     *
     * @param key
     * @param value
     *
     * @see org.geotools.data.MetaData#putMetaData(java.lang.String,
     *      java.lang.Object)
     */
    public void putMetaData(String key, Object value) {
        meta.put(key, value);
    }

    /**
     * Implement getMetaData.
     *
     * @param key
     *
     * @return
     *
     * @see org.geotools.data.MetaData#getMetaData(java.lang.String)
     */
    public Object getMetaData(String key) {
        return meta.get(key);
    }
    
    /**
     * pass through SQL - use this SQL, instead of, or augmented by SQL
     * generation from a WFS filter (the filter as parameter passing mechanism)
     *
     * <p>
     * @return String adhocSQL
     */
    public String getBypassSQL() {
        return bypassSQL;
    }    

    /**
     * Retrieve a value indicating an override (null otherwise) for WFS getFeature processing
     *
     * <p>
     * @return String executionPattern
     */
    public XMLelementStructure getXMLelementStructure() {
        return elementTree;
    }
    
}
