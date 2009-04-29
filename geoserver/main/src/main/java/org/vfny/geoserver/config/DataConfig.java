/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.vfny.geoserver.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.geoserver.catalog.FeatureTypeInfo;
import org.geotools.data.DataStore;
import org.geotools.data.DefaultRepository;
import org.geotools.data.Repository;
import org.vfny.geoserver.global.Data;
import org.vfny.geoserver.global.dto.CoverageInfoDTO;
import org.vfny.geoserver.global.dto.CoverageStoreInfoDTO;
import org.vfny.geoserver.global.dto.DataDTO;
import org.vfny.geoserver.global.dto.DataStoreInfoDTO;
import org.vfny.geoserver.global.dto.DataTransferObject;
import org.vfny.geoserver.global.dto.FeatureTypeInfoDTO;
import org.vfny.geoserver.global.dto.NameSpaceInfoDTO;
import org.vfny.geoserver.global.dto.StyleDTO;


/**
 * Data purpose.
 *
 * <p>
 * Represents an instance of the catalog.xml file in the configuration of the
 * server, along with associated configuration files for the feature types.
 * </p>
 *
 * <p></p>
 *
 * @author dzwiers, Refractions Research, Inc.
 * @author $Author: Alessio Fabiani (alessio.fabiani@gmail.com) $ (last modification)
 * @author $Author: Simone Giannecchini (simboss1@gmail.com) $ (last modification)
 * @version $Id$
 *
 * @see DataSource
 * @see FeatureTypeInfo
 * @see StyleConfig
 */
public class DataConfig {
    public static final String CONFIG_KEY = "Config.Data";
    public static final String SEPARATOR = ":::";
    public static final String SELECTED_FEATURE_TYPE = "selectedFeatureType";
    public static final String SELECTED_ATTRIBUTE_TYPE = "selectedAttributeType";
    public static final String SELECTED_COVERAGE = "selectedCoverage";
    private Logger LOGGER = org.geotools.util.logging.Logging.getLogger(this.getClass().toString());

    /**
     * A set of dataFormatConfig by dataFormatId.
     *
     * @see org.vfny.geoserver.config.data.CoverageStoreInfo
     *
     * @uml.property name="dataFormats"
     * @uml.associationEnd qualifier="key:java.lang.String org.vfny.geoserver.config.CoverageStoreConfig"
     * multiplicity="(0 1)"
     */
    private Map<Object, CoverageStoreConfig> dataFormats;

    /**
     * A set of dataStoreConfig by dataStoreId.
     *
     * @see org.vfny.geoserver.config.data.DataStoreInfo
     *
     * @uml.property name="dataStores"
     * @uml.associationEnd qualifier="key:java.lang.String org.vfny.geoserver.config.DataStoreConfig"
     * multiplicity="(0 1)"
     */
    private Map<Object, DataStoreConfig> dataStores;

    /**
     * A set of namespaces and their names.
     *
     * @see org.vfny.geoserver.config.data.NameSpaceConfig
     *
     * @uml.property name="nameSpaces"
     * @uml.associationEnd qualifier="key:java.lang.String org.vfny.geoserver.config.NameSpaceConfig"
     * multiplicity="(0 1)"
     */
    private Map<Object, NameSpaceConfig> nameSpaces;

    /**
     * FeatureTypesInfoConfig referenced by key "<code>dataStoreID + SEPARATOR
     * + typeName</code>"
     *
     * @see org.vfny.geoserver.global.dto.FeatureTypeInfoConfig
     *
     * @uml.property name="featuresTypes"
     * @uml.associationEnd qualifier="key:java.lang.String org.vfny.geoserver.config.FeatureTypeConfig"
     * multiplicity="(0 1)"
     */
    private Map<Object, FeatureTypeConfig> featuresTypes;

    /**
     *
     * @uml.property name="coverages"
     * @uml.associationEnd qualifier="key:java.lang.String org.vfny.geoserver.config.CoverageConfig"
     * multiplicity="(0 1)"
     */
    private Map<Object, CoverageConfig> coverages;

    /**
     * A set of styles and their names.
     *
     * @see org.vfny.geoserver.config.data.StyleConfig
     *
     * @uml.property name="styles"
     * @uml.associationEnd qualifier="key:java.lang.String org.vfny.geoserver.config.StyleConfig"
     * multiplicity="(0 1)"
     */
    private Map<Object, StyleConfig> styles;

    /**
     * the default namespace for the server instance.
     *
     * @see org.vfny.geoserver.config.data.NameSpaceConfig
     *
     * @uml.property name="defaultNameSpace"
     * @uml.associationEnd multiplicity="(1 1)"
     */
    private NameSpaceConfig defaultNameSpace;

    /**
     * Data constructor.
     *
     * <p>
     * Creates a Data to represent an instance with default data.
     * </p>
     *
     * @see defaultSettings()
     */
    public DataConfig() {
        dataFormats = new HashMap<Object, CoverageStoreConfig>();
        dataStores = new HashMap<Object, DataStoreConfig>();
        nameSpaces = new HashMap<Object, NameSpaceConfig>();
        styles = new HashMap<Object, StyleConfig>();
        featuresTypes = new HashMap<Object, FeatureTypeConfig>();
        coverages = new HashMap<Object, CoverageConfig>();
        defaultNameSpace = new NameSpaceConfig();
    }

    /**
     * Data constructor.
     *
     * <p>
     * Creates a copy of the DataDTO provided. If the Data provided  is null
     * then default values are used. All the datastructures are cloned.
     * </p>
     *
     * @param data The catalog to copy.
     */
    public DataConfig(DataDTO data) {

        this();
        update(data);
    }

    /**
     * Instantiates the data config from the data module.
     *
     * @param data The data module.
     */
	public DataConfig(Data data) {
        this((DataDTO) data.toDTO());
    }

    /**
     * Implement loadDTO.
     *
     * <p>
     * Populates the object with the param passed.
     * </p>
     *
     * @param data An instance of DataDTO to populate this object
     *
     * @throws NullPointerException DOCUMENT ME!
     *
     * @see org.vfny.geoserver.config.DataStructure#loadDTO(java.lang.Object)
     */
    public synchronized void update(DataDTO data) {
        if (data == null) {
            throw new NullPointerException("Data Data Transfer Object required");
        }

        Iterator i;
        Object key;

        ////
        //
        //
        //
        ////
        i = data.getFormats().keySet().iterator();
        dataFormats = new HashMap<Object, CoverageStoreConfig>();

        while (i.hasNext()) {
            key = i.next();
            dataFormats.put(key,
                new CoverageStoreConfig((CoverageStoreInfoDTO) data.getFormats().get(key)));
        }

        ////
        //
        //
        //
        ////
        i = data.getDataStores().keySet().iterator();
        dataStores = new HashMap<Object, DataStoreConfig>();

        while (i.hasNext()) {
            key = i.next();
            dataStores.put(key,
                new DataStoreConfig((DataStoreInfoDTO) data.getDataStores().get(key)));
        }

        ////
        //
        //
        //
        ////
        i = data.getNameSpaces().keySet().iterator();
        nameSpaces = new HashMap<Object, NameSpaceConfig>();

        while (i.hasNext()) {
            key = i.next();
            nameSpaces.put(key,
                new NameSpaceConfig((NameSpaceInfoDTO) data.getNameSpaces().get(key)));

            if (nameSpaces.get(key).isDefault()) {
                defaultNameSpace = nameSpaces.get(key);
            }
        }

        ////
        //
        //
        //
        ////
        i = data.getFeaturesTypes().keySet().iterator();
        featuresTypes = new HashMap<Object, FeatureTypeConfig>();

        FeatureTypeInfoDTO f;

        while (i.hasNext()) {
            key = i.next();

            f = (FeatureTypeInfoDTO) data.getFeaturesTypes().get(key);
            if(f.getAlias() == null)
                featuresTypes.put(f.getDataStoreId() + ":" + f.getName(), new FeatureTypeConfig(f));
            else
                featuresTypes.put(f.getDataStoreId() + ":" + f.getAlias(), new FeatureTypeConfig(f));
        }

        ////
        //
        //
        //
        ////
        i = data.getCoverages().keySet().iterator();
        coverages = new HashMap<Object, CoverageConfig>();

        CoverageInfoDTO c;

        while (i.hasNext()) {
            key = i.next();
            c = (CoverageInfoDTO) data.getCoverages().get(key);
            coverages.put(c.getFormatId() + ":" + c.getName(), new CoverageConfig(c));
        }

        ////
        //
        //
        //
        ////
        i = data.getStyles().keySet().iterator();
        styles = new HashMap<Object, StyleConfig>();

        while (i.hasNext()) {
            key = i.next();
            StyleDTO style = (StyleDTO) data.getStyles().get(key);
            // Check that the SLD file exists
            if(style.getFilename() != null) {
                styles.put(key, new StyleConfig(style));
            } else {
                LOGGER.log(Level.WARNING, "Missing SLD file for : " + style.getId() + ", skipping.");
            }
        }
    }

    public DataDTO toDTO() {
        DataDTO dt = new DataDTO();
        HashMap<Object, DataTransferObject> tmp = null;
        Iterator<Object> i = null;

        tmp = new HashMap<Object, DataTransferObject>();
        dt.setFormats(tmp);
        i = dataFormats.keySet().iterator();

        while (i.hasNext()) {
            Object key = i.next();
            tmp.put(key, dataFormats.get(key).toDTO());
        }

        tmp = new HashMap<Object, DataTransferObject>();
        dt.setDataStores(tmp);
        i = dataStores.keySet().iterator();

        while (i.hasNext()) {
            Object key = i.next();
            tmp.put(key, dataStores.get(key).toDTO());
        }

        tmp = new HashMap<Object, DataTransferObject>();
        dt.setFeaturesTypes(tmp);
        i = featuresTypes.keySet().iterator();

        while (i.hasNext()) {
            Object key = i.next();
            tmp.put(key, featuresTypes.get(key).toDTO());
        }

        tmp = new HashMap<Object, DataTransferObject>();
        dt.setCoverages(tmp);
        i = coverages.keySet().iterator();

        while (i.hasNext()) {
            Object key = i.next();
            tmp.put(key, coverages.get(key).toDTO());
        }

        tmp = new HashMap<Object, DataTransferObject>();
        dt.setStyles(tmp);
        i = styles.keySet().iterator();

        while (i.hasNext()) {
            Object key = i.next();
            tmp.put(key, styles.get(key).toDTO());
        }

        tmp = new HashMap<Object, DataTransferObject>();
        dt.setNameSpaces(tmp);
        i = nameSpaces.keySet().iterator();

        while (i.hasNext()) {
            Object key = i.next();
            tmp.put(key, nameSpaces.get(key).toDTO());

            if (((NameSpaceInfoDTO) tmp.get(key)).isDefault()) {
                dt.setDefaultNameSpacePrefix(((NameSpaceInfoDTO) tmp.get(key)).getPrefix());
            }
        }

        return dt;
    }

    public List<Object> getFeatureTypeConfigKeys() {
        return new ArrayList<Object>(featuresTypes.keySet());
    }

    /**
     * Lookup FeatureTypeConfig for things like WMS.
     *
     * @param key Key based on <code>dataStoreID.typeName</code>
     *
     * @return FeatureTypeInfo or null if not found
     *
     * @throws NoSuchElementException DOCUMENT ME!
     */
    public FeatureTypeConfig lookupFeatureTypeConfig(String key) {
        if (featuresTypes.containsKey(key)) {
            return featuresTypes.get(key);
        } else {
            throw new NoSuchElementException("Could not find FeatureTypeConfig '" + key + "'.");
        }
    }

    /**
     * getDataFormats purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @return
     *
     * @uml.property name="dataFormats"
     */
    public Map<Object, CoverageStoreConfig> getDataFormats() {
        return dataFormats;
    }

    /**
     * List of DataFormatIds
     *
     * @return DOCUMENT ME!
     */
    public List<Object> listDataFormatIds() {
        return new ArrayList<Object>(dataFormats.keySet());
    }

    public List<Object> getDataFormatIds() {
        return listDataFormatIds();
    }

    /**
     * getDataFormats purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @param key DOCUMENT ME!
     *
     * @return
     */
    public CoverageStoreConfig getDataFormat(String key) {
        return dataFormats.get(key);
    }

    /**
     * getDataStores purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @return
     *
     * @uml.property name="dataStores"
     */
    public Map<Object, DataStoreConfig> getDataStores() {
        return dataStores;
    }

    /**
     * List of DataStoreIds
     *
     * @return DOCUMENT ME!
     */
    public List<Object> listDataStoreIds() {
        return new ArrayList<Object>(dataStores.keySet());
    }

    public List<Object> getDataStoreIds() {
        return listDataStoreIds();
    }

    /**
     * getDataStores purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @param key DOCUMENT ME!
     *
     * @return
     */
    public DataStoreConfig getDataStore(String key) {
        return dataStores.get(key);
    }

    /**
     * getDefaultNameSpace purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @return
     *
     * @uml.property name="defaultNameSpace"
     */
    public NameSpaceConfig getDefaultNameSpace() {
        return defaultNameSpace;
    }

    /**
     * getFeatures purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @return
     *
     * @uml.property name="featuresTypes"
     */
    public Map<Object, FeatureTypeConfig> getFeaturesTypes() {
        return featuresTypes;
    }

    /**
     * getFeatures purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @param key DOCUMENT ME!
     *
     * @return
     */
    public FeatureTypeConfig getFeatureTypeConfig(String key) {
        return featuresTypes.get(key);
    }

    public CoverageConfig getCoverageConfig(String key) {
        return coverages.get(key);
    }

    /**
     * getNameSpaces purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @return
     *
     * @uml.property name="nameSpaces"
     */
    public Map<Object, NameSpaceConfig> getNameSpaces() {
        return nameSpaces;
    }

    /**
     * getNameSpaces purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @param key DOCUMENT ME!
     *
     * @return
     */
    public NameSpaceConfig getNameSpace(String key) {
        return nameSpaces.get(key);
    }

    /**
     * getStyles purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @return
     *
     * @uml.property name="styles"
     */
    public Map<Object, StyleConfig> getStyles() {
        return styles;
    }

    /**
     * getStyles purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @param key DOCUMENT ME!
     *
     * @return
     */
    public StyleConfig getStyle(String key) {
        return styles.get(key);
    }

    /**
     * setFormats purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @param map
     */
    public void setFormats(Map<Object, CoverageStoreConfig> map) {
        if (map != null) {
            dataFormats = map;
        }
    }

    /**
     * Add a new CoverageStoreConfig for the user to edit
     *
     * <p>
     * The DataFormatCondig will be added under its id name
     * </p>
     *
     * @param dataFormatConfig
     */
    public void addDataFormat(CoverageStoreConfig dataFormatConfig) {
        if (dataFormats == null) {
            dataFormats = new HashMap<Object, CoverageStoreConfig>();
        }

        dataFormats.put(dataFormatConfig.getId(), dataFormatConfig);
    }

    /**
     * setDataFormats purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @param key
     *
     * @return DOCUMENT ME!
     */
    public CoverageStoreConfig removeDataFormat(String key) {
        if (dataFormats == null) {
            dataFormats = new HashMap<Object, CoverageStoreConfig>();
        }

        return dataFormats.remove(key);
    }

    /**
     * setDataStores purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @param map
     *
     * @uml.property name="dataStores"
     */
    public void setDataStores(Map<Object, DataStoreConfig> map) {
        if (map != null) {
            dataStores = map;
        }
    }

    /**
     * Add a new DataStoreConfig for the user to edit
     *
     * <p>
     * The DataStoreCondig will be added under its id name
     * </p>
     *
     * @param dataStoreConfig
     */
    public void addDataStore(DataStoreConfig dataStoreConfig) {
        if (dataStores == null) {
            dataStores = new HashMap<Object, DataStoreConfig>();
        }

        dataStores.put(dataStoreConfig.getId(), dataStoreConfig);
    }

    /**
     * setDataStores purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @param key
     *
     * @return DOCUMENT ME!
     */
    public DataStoreConfig removeDataStore(String key) {
        if (dataStores == null) {
            dataStores = new HashMap<Object, DataStoreConfig>();
        }

        return dataStores.remove(key);
    }

    /**
     * setDefaultNameSpace purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @param support
     *
     * @uml.property name="defaultNameSpace"
     */
    public void setDefaultNameSpace(NameSpaceConfig support) {
        if (support != null) {
            //first unset the old as default
            if (defaultNameSpace != null) {
                defaultNameSpace.setDefault(false);
            }

            defaultNameSpace = support;

            //set the new as default
            if (defaultNameSpace != null) {
                defaultNameSpace.setDefault(true);
            }
        }
    }

    /**
     * setFeatures purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @param map
     *
     * @uml.property name="featuresTypes"
     */
    public void setFeaturesTypes(Map<Object, FeatureTypeConfig> map) {
        if (map != null) {
            featuresTypes = map;
        }
    }

    /**
     * setFeatures purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @param key
     * @param ft DOCUMENT ME!
     */
    public void addFeatureType(String key, FeatureTypeConfig ft) {
        if (featuresTypes == null) {
            featuresTypes = new HashMap<Object, FeatureTypeConfig>();
        }

        if ((key != null) && (ft != null)) {
            featuresTypes.put(key, ft);
        }
    }

    public void addCoverage(String key, CoverageConfig cv) {
        if (coverages == null) {
            coverages = new HashMap<Object, CoverageConfig>();
        }

        if ((key != null) && (cv != null)) {
            coverages.put(key, cv);
        }
    }

    /**
     * setFeatures purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @param key
     *
     * @return DOCUMENT ME!
     */
    public FeatureTypeConfig removeFeatureType(String key) {
        if (featuresTypes == null) {
            featuresTypes = new HashMap<Object, FeatureTypeConfig>();
        }

        return featuresTypes.remove(key);
    }

    public CoverageConfig removeCoverage(String key) {
        if (coverages == null) {
            coverages = new HashMap<Object, CoverageConfig>();
        }

        return coverages.remove(key);
    }

    /**
     * setNameSpaces purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @param map
     *
     * @uml.property name="nameSpaces"
     */
    public void setNameSpaces(Map<Object, NameSpaceConfig> map) {
        if (map != null) {
            nameSpaces = map;
        }
    }

    /**
     * setNameSpaces purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @param key
     * @param ns DOCUMENT ME!
     */
    public void addNameSpace(String key, NameSpaceConfig ns) {
        if (nameSpaces == null) {
            nameSpaces = new HashMap<Object, NameSpaceConfig>();
        }

        if ((key != null) && (ns != null)) {
            nameSpaces.put(key, ns);
        }
    }

    /**
     * setNameSpaces purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @param key
     *
     * @return DOCUMENT ME!
     */
    public NameSpaceConfig removeNameSpace(String key) {
        if (nameSpaces == null) {
            nameSpaces = new HashMap<Object, NameSpaceConfig>();
        }

        return nameSpaces.remove(key);
    }

    /**
     * setStyles purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @param map
     *
     * @uml.property name="styles"
     */
    public void setStyles(Map<Object, StyleConfig> map) {
        if (map != null) {
            styles = map;
        }
    }

    /**
     * setStyles purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @param key
     * @param s DOCUMENT ME!
     */
    public void addStyle(String key, StyleConfig s) {
        if (styles == null) {
            styles = new HashMap<Object, StyleConfig>();
        }

        if ((key != null) && (s != null)) {
            styles.put(key, s);
        }
    }

    /**
     * setStyles purpose.
     *
     * <p>
     * Description ...
     * </p>
     *
     * @param key
     *
     * @return DOCUMENT ME!
     */
    public StyleConfig removeStyle(String key) {
        if (styles == null) {
            styles = new HashMap<Object, StyleConfig>();
        }

        return styles.remove(key);
    }

    /**
     * This is the list of all feature types
     *
     * @return a set of all "DataStoreId.TypeName"
     */
    public SortedSet<String> getFeatureTypeIdentifiers(ServletContext sc) {
        TreeSet<String> set = new TreeSet<String>();

        for (Iterator<DataStoreConfig> iter = dataStores.values().iterator(); iter.hasNext();) {
            DataStoreConfig dataStoreConfig = iter.next();

            DataStore dataStore = null;
            try {
                dataStore = dataStoreConfig.findDataStore(sc);

                String[] typeNames = dataStore.getTypeNames();

                for (int i = 0; i < typeNames.length; i++) {
                    typeNames[i] = dataStoreConfig.getId() + SEPARATOR + typeNames[i];
                }

                List<String> typeNamesList = Arrays.asList(typeNames);

                set.addAll(typeNamesList);
            } catch (Throwable ignore) {
                LOGGER.warning("Could not use " + dataStoreConfig.getId()
                    + " datastore was unavailable!");
                LOGGER.log(Level.WARNING, "", ignore);

                continue;
            } finally {
                if(dataStore != null) dataStore.dispose();
            }
        }

        return Collections.unmodifiableSortedSet(set);
    }

    public SortedSet<String> getCoverageIdentifiers(ServletContext sc) {
        TreeSet<String> set = new TreeSet<String>();

        for (Iterator<CoverageStoreConfig> iter = dataFormats.values().iterator(); iter.hasNext();) {
            CoverageStoreConfig dataFormatConfig = iter.next();

            set.add(dataFormatConfig.getId());
        }

        return Collections.unmodifiableSortedSet(set);
    }

    /**
     * To DataRepository for ValidationProcessor.
     * <p>
     * This repository is limited to the FeatureTypes currently defined.
     * @throws IOException
     */
    public Repository toRepository(ServletContext context)
        throws IOException {
        DefaultRepository repository = new DefaultRepository();

        for (Iterator i = dataStores.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            String dataStoreId = (String) entry.getKey();
            DataStoreConfig dataStoreConfig = (DataStoreConfig) entry.getValue();
            repository.register(dataStoreId, dataStoreConfig.findDataStore(context));
        }

        return repository;
    }

    /**
     * @return Returns the coverages.
     *
     * @uml.property name="coverages"
     */
    public Map<Object, CoverageConfig> getCoverages() {
        return coverages;
    }

    /**
     * @param coverages The coverages to set.
     *
     * @uml.property name="coverages"
     */
    public void setCoverages(Map<Object, CoverageConfig> coverages) {
        this.coverages = coverages;
    }

    /**
     * @param dataFormats The dataFormats to set.
     *
     * @uml.property name="dataFormats"
     */
    public void setDataFormats(Map<Object, CoverageStoreConfig> dataFormats) {
        this.dataFormats = dataFormats;
    }
}
