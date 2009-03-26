/* Copyright (c) 2001 - 2008 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.geoserver.catalog.AttributeTypeInfo;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogFactory;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.Wrapper;
import org.geoserver.catalog.event.CatalogListener;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.catalog.util.LegacyCatalogImporter;
import org.geoserver.catalog.util.LegacyCatalogReader;
import org.geoserver.catalog.util.LegacyFeatureTypeInfoReader;
import org.geoserver.config.util.LegacyConfigurationImporter;
import org.geoserver.config.util.XStreamPersister;
import org.geoserver.config.util.XStreamServiceLoader;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geotools.gml2.GML;
import org.geotools.util.logging.Logging;
import org.geotools.xml.Schemas;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.vfny.geoserver.global.GeoserverDataDirectory;

/**
 * Initializes GeoServer configuration and catalog on startup.
 * <p>
 * This class is registered in a spring context and post processes the 
 * singleton beans {@link Catalog} and {@link GeoServer}, populating them 
 * with data from the GeoServer data directory. 
 * </p>
 * @author Justin Deoliveira, The Open Planning Project
 *
 */
public class GeoServerLoader implements BeanPostProcessor, DisposableBean, 
    ApplicationContextAware {

    static Logger LOGGER = Logging.getLogger( "org.geoserver" );
    
    GeoServerResourceLoader resourceLoader;
    GeoServer geoserver;
    
    //JD: this is a hack for the moment, it is used only to maintain tests since the test setup relies
    // on the old data directory structure, once the tests have been ported to the new structure
    // this ugly hack can die
    static boolean legacy = false;
    
    public GeoServerLoader( GeoServerResourceLoader resourceLoader ) {
        this.resourceLoader = resourceLoader;
    }
    
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        GeoserverDataDirectory.init((WebApplicationContext)applicationContext);
    }
    
    public static void setLegacy(boolean legacy) {
        GeoServerLoader.legacy = legacy;
    }
    
    public final Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    public final Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        if ( bean instanceof Catalog ) {
            //ensure this is not a wrapper but the real deal
            if ( bean instanceof Wrapper && ((Wrapper) bean).isWrapperFor(Catalog.class) ) {
                return bean;
            }
            
            //load
            try {
                Catalog catalog = (Catalog) bean;
                XStreamPersister xp = new XStreamPersister.XML();
                xp.setCatalog( catalog );
                loadCatalog( catalog, xp );
            } 
            catch (Exception e) {
                throw new RuntimeException( e );
            }
        }
        
        if ( bean instanceof GeoServer ) {
            geoserver = (GeoServer) bean;
            try {
                loadGeoServer( geoserver,  new XStreamPersister.XML() );
            } 
            catch (Exception e) {
                throw new RuntimeException( e );
            }
            //initialize();
        }
        
        return bean;
    }
    
    protected void loadCatalog(Catalog catalog, XStreamPersister xp) throws Exception {
        catalog.setResourceLoader(resourceLoader);

        //look for catalog.xml, if it exists assume we are dealing with 
        // an old data directory
        File f = resourceLoader.find( "catalog.xml" );
        if ( f == null ) {
            //assume 2.x style data directory
            CatalogImpl catalog2 = (CatalogImpl) readCatalog( xp );
            ((CatalogImpl)catalog).sync( catalog2 );
        } else {
            // import old style catalog, register the persister now so that we start 
            // with a new version of the catalog
            CatalogImpl catalog2 = (CatalogImpl) readLegacyCatalog( f, xp );
            ((CatalogImpl)catalog).sync( catalog2 );
        }
        
        //initialize styles
        initializeStyles(catalog);
        
        if ( !legacy ) {
            //add the listener which will persist changes
            catalog.addListener( new GeoServerPersister( resourceLoader, xp ) );
        }
    }
    
    protected void loadGeoServer(final GeoServer geoServer, XStreamPersister xp) throws Exception {
        //add event listener which persists services
        final List<XStreamServiceLoader> loaders = 
            GeoServerExtensions.extensions( XStreamServiceLoader.class );
        geoServer.addListener( 
            new ConfigurationListenerAdapter() {
                @Override
                public void handlePostServiceChange(ServiceInfo service) {
                    for ( XStreamServiceLoader<ServiceInfo> l : loaders  ) {
                        if ( l.getServiceClass().isInstance( service ) ) {
                            try {
                                l.save( service, geoServer );
                            } catch (Throwable t) {
                                //TODO: log this
                                t.printStackTrace();
                            }
                        }
                    }
                }
            }
        );
        
        //look for services.xml, if it exists assume we are dealing with 
        // an old data directory
        File f = resourceLoader.find( "services.xml" );
        if ( f == null ) {
            //assume 2.x style
            f = resourceLoader.find( "global.xml");
            if ( f != null ) {
                BufferedInputStream in = new BufferedInputStream( new FileInputStream( f ) );
                GeoServerInfo global = new XStreamPersister.XML().load( in, GeoServerInfo.class );
                geoServer.setGlobal( global );    
            }
            
            //load logging
            f = resourceLoader.find( "logging.xml" );
            if ( f != null ) {
                BufferedInputStream in = new BufferedInputStream( new FileInputStream( f ) );
                LoggingInfo logging = new XStreamPersister.XML().load( in, LoggingInfo.class );
                geoServer.setLogging( logging );
            }
            //load services
            for ( XStreamServiceLoader<ServiceInfo> l : loaders ) {
                try {
                    ServiceInfo s = l.load( geoServer );
                    geoServer.add( s );
                    
                    LOGGER.info( "Loaded service '" +  s.getId() + "', " + (s.isEnabled()?"enabled":"disabled") );
                }
                catch( Throwable t ) {
                    //TODO: log this
                    t.printStackTrace();
                }
            }
        } else {
            //add listener now as a converter which will convert from the old style 
            // data directory to the new
            GeoServerPersister p = new GeoServerPersister( resourceLoader, xp );
            geoServer.addListener( p );
            
            //import old style services.xml
            new LegacyConfigurationImporter(geoServer).imprt(resourceLoader.getBaseDirectory());
            
            geoServer.removeListener( p );
            
            //rename the services.xml file
            f.renameTo( new File( f.getParentFile(), "services.xml.old" ) );
        }
        
        //load initializer extensions
        List<GeoServerInitializer> initializers = GeoServerExtensions.extensions( GeoServerInitializer.class );
        for ( GeoServerInitializer initer : initializers ) {
            try {
                initer.initialize( geoServer );
            }
            catch( Throwable t ) {
                //TODO: log this
                t.printStackTrace();
            }
        }
        
        geoServer.addListener( new GeoServerPersister( resourceLoader, xp ) );
    }
    
    //JD: NOTE! This method is no longer used on trunk
    protected void initialize() {
        //load catalog
        LegacyCatalogImporter catalogImporter = new LegacyCatalogImporter();
        catalogImporter.setResourceLoader(resourceLoader);
        Catalog catalog = geoserver.getCatalog();
        if(catalog instanceof Wrapper && ((Wrapper) catalog).isWrapperFor(Catalog.class)) {
            catalog = ((Wrapper) catalog).unwrap(Catalog.class);
        }
        catalogImporter.setCatalog(catalog);
        
        try {
            catalogImporter.imprt( resourceLoader.getBaseDirectory() );
        }
        catch(Exception e) {
            throw new RuntimeException( e );
        }
        
        //load configuration
        LegacyConfigurationImporter importer = new LegacyConfigurationImporter();
        importer.setConfiguration(geoserver);
        
        try {
            importer.imprt( resourceLoader.getBaseDirectory() );
        } 
        catch (Exception e) {
            throw new RuntimeException( e );
        }
        
        //load initializer extensions
        List<GeoServerInitializer> initializers = GeoServerExtensions.extensions( GeoServerInitializer.class );
        for ( GeoServerInitializer initer : initializers ) {
            try {
                initer.initialize( geoserver );
            }
            catch( Throwable t ) {
                //TODO: log this
                t.printStackTrace();
            }
        }
        
        //load listeners
        List<CatalogListener> catalogListeners = GeoServerExtensions.extensions( CatalogListener.class );
        for ( CatalogListener l : catalogListeners ) {
            catalog.addListener( l );
        }
        List<ConfigurationListener> configListeners = GeoServerExtensions.extensions( ConfigurationListener.class );
        for ( ConfigurationListener l : configListeners ) {
            geoserver.addListener( l );
        }
    }
    
    /**
     * Does some post processing on the catalog to ensure that the "well-known" styles
     * are always around.
     */
    void initializeStyles( Catalog catalog ) throws IOException {
        if ( catalog.getStyleByName( StyleInfo.DEFAULT_POINT ) == null ) {
            initializeStyle( catalog, StyleInfo.DEFAULT_POINT, "default_point.sld" );
        }
        if ( catalog.getStyleByName( StyleInfo.DEFAULT_LINE ) == null ) {
            initializeStyle( catalog, StyleInfo.DEFAULT_LINE, "default_line.sld" );
        }
        if ( catalog.getStyleByName( StyleInfo.DEFAULT_POLYGON ) == null ) {
            initializeStyle( catalog, StyleInfo.DEFAULT_POLYGON, "default_line.sld" );
        }
        if ( catalog.getStyleByName( StyleInfo.DEFAULT_RASTER ) == null ) {
            initializeStyle( catalog, StyleInfo.DEFAULT_RASTER, "default_raster.sld" );
        }
    }
    
    /**
     * Copies a well known style out to the data directory and adds a catalog entry for it.
     */
    void initializeStyle( Catalog catalog, String styleName, String sld ) throws IOException {
        
        //copy the file out to the data directory if necessary
        if ( resourceLoader.find( "styles", sld ) == null ) {
            FileUtils.copyURLToFile(getClass().getResource(sld), 
                new File( resourceLoader.find( "styles" ), sld) );
        }
        
        //create a style for it
        StyleInfo s = catalog.getFactory().createStyle();
        s.setName( styleName );
        s.setFilename( sld );
        catalog.add( s );
    }
    
    public void reload() throws Exception {
        destroy();
        
        //reload catalog, make sure we reload the underlying catalog, not any wrappers
        Catalog catalog = geoserver.getCatalog();
        if ( catalog instanceof Wrapper ) {
            catalog = ((Wrapper)geoserver.getCatalog()).unwrap(Catalog.class);
        }
        
        XStreamPersister xp = new XStreamPersister.XML();
        xp.setCatalog( catalog );
        
        loadCatalog( catalog, xp );
        loadGeoServer( geoserver, xp);
    }
    
    //TODO: kill this method, it is not longer needed since persistance is event based
    public void persist() throws Exception {
        //TODO: make the configuration backend pluggable... or loadable
        // from application context, or web.xml, or env variable, etc...
        XStreamPersister p = new XStreamPersister.XML();
        BufferedOutputStream out = new BufferedOutputStream( 
            new FileOutputStream( resourceLoader.createFile( "catalog2.xml" ) )
        );
        
        //persist catalog
        Catalog catalog = geoserver.getCatalog();
        if( catalog instanceof Wrapper ) {
            catalog = ((Wrapper)catalog).unwrap( Catalog.class );
        }
        p.save( catalog, out );
        out.flush();
        out.close();
     
        //persist resources
        File workspaces = resourceLoader.findOrCreateDirectory( "workspaces" );
        for ( ResourceInfo r : catalog.getResources( ResourceInfo.class ) ) {
            WorkspaceInfo ws = r.getStore().getWorkspace();
            File workspace = new File( workspaces, ws.getName() );
            if ( !workspace.exists() ) {
                workspace.mkdir();
            }
            
            String dirName = r.getStore().getName() + "_" + r.getNativeName();
            //dirName = URLEncoder.encode( dirName, "UTF-8" );
            
            File dir = new File( workspace, dirName );
            if ( !dir.exists() ) {
                dir.mkdir();
            }
            
            File info = new File( dir, "resource.xml" );
            try {
                persist( p, r, info );
            }
            catch( Exception e ) {
                LOGGER.log( Level.WARNING, "Error persisting '" + r.getName() + "'", e );
            }
            
            //persist layers publishing the resource
            LayerInfo l = catalog.getLayers( r ).get( 0 );
            try {
                persist( p, l, new File( dir, "layer.xml" ) );
            }
            catch( Exception e ) {
                LOGGER.log( Level.WARNING, "Error persisting layer '" + l.getName() + "'", e );
            }
        }
        
        
        //persist global
        try {
            persist( p, geoserver.getGlobal(), resourceLoader.createFile( "global.xml" ) );
        }
        catch( Exception e ) {
            LOGGER.log( Level.WARNING, "Error persisting global configuration.", e );
        }
        
        //persist services
        Collection services = geoserver.getServices();
        List<ServiceLoader> loaders = GeoServerExtensions.extensions( ServiceLoader.class );
        
        for ( Iterator s = services.iterator(); s.hasNext(); ) {
            ServiceInfo service = (ServiceInfo) s.next();
            for ( ServiceLoader loader : loaders ) {
                if (loader.getServiceClass().isInstance( service ) ) {
                    try {
                        loader.save( service, geoserver );
                        break;
                    }
                    catch( Throwable t ) {
                        LOGGER.warning( "Error persisting service: " + service.getId() );
                        LOGGER.log( Level.INFO, "", t );
                    }
                }
            }
        }
    }
    
    /**
     * Reads the catalog from disk.
     */
    Catalog readCatalog( XStreamPersister xp ) throws Exception {
        Catalog catalog = new CatalogImpl();
        xp.setCatalog( catalog );
        
        CatalogFactory factory = catalog.getFactory();
       
        //styles
        File styles = resourceLoader.find( "styles" );
        for ( File sf : list(styles,new SuffixFileFilter(".xml") ) ) {
            try {
                StyleInfo s = depersist( xp, sf, StyleInfo.class );
                catalog.add( s );
                
                LOGGER.info( "Loaded style '" + s.getName() + "'" );
            }
            catch( Exception e ) {
                LOGGER.log( Level.WARNING, "Failed to load style from file '" + sf.getName() + "'" , e );
            }
        }
        
        //workspaces, stores, and resources
        File workspaces = resourceLoader.find( "workspaces" );
        if ( workspaces != null ) {
            for ( File wsd : list(workspaces, DirectoryFileFilter.INSTANCE ) ) {
                
                WorkspaceInfo ws = factory.createWorkspace();
                ws.setName( wsd.getName() );
                catalog.add( ws );
                
                LOGGER.info( "Loaded workspace '" + ws.getName() +"'");
                
                //load the namespace
                File nsf = new File( wsd, "namespace.xml" );
                if ( nsf.exists() ) {
                    try {
                        NamespaceInfo ns = depersist( xp, nsf, NamespaceInfo.class );
                        catalog.add( ns );
                    }
                    catch( Exception e ) {
                        LOGGER.log( Level.WARNING, "Failed to load namespace for '" + wsd.getName() + "'" , e );
                    }
                }
                
                //load the stores for this workspace
                for ( File sd : list(wsd, DirectoryFileFilter.INSTANCE) ) {
                    File f = new File( sd, "datastore.xml");
                    if ( f.exists() ) {
                        //load as a datastore
                        DataStoreInfo ds = null;
                        try {    
                            ds = depersist( xp, f, DataStoreInfo.class );
                            catalog.add( ds );
                            
                            LOGGER.info( "Loaded data store '" + ds.getName() +"'");
                        }
                        catch( Exception e ) {
                            LOGGER.log( Level.WARNING, "Failed to load data store '" + sd.getName() +"'", e);
                            continue;
                        }
                        
                        //load feature types
                        for ( File ftd : list(sd,DirectoryFileFilter.INSTANCE) ) {
                            f = new File( ftd, "featuretype.xml" );
                            if( f.exists() ) {
                                FeatureTypeInfo ft = null;
                                try {
                                    ft = depersist(xp,f,FeatureTypeInfo.class);
                                }
                                catch( Exception e ) {
                                    LOGGER.log( Level.WARNING, "Failed to load feature type '" + ftd.getName() +"'", e);
                                    continue;
                                }
                                
                                //check for a schema override
                                try {
                                    handleSchemaOverride( ft, ftd );
                                }
                                catch( Exception e ) {
                                    LOGGER.log( Level.WARNING, "Schema override failed for feature type '" + ft.getName() +"'", e);
                                }
                                
                                catalog.add( ft );
                                
                                LOGGER.info( "Loaded feature type '" + ds.getName() +"'");
                                
                                f = new File( ftd, "layer.xml" );
                                if ( f.exists() ) {
                                    try {
                                        LayerInfo l = depersist(xp, f, LayerInfo.class );
                                        catalog.add( l );
                                        
                                        LOGGER.info( "Loaded layer '" + l.getName() + "'" );
                                    }
                                    catch( Exception e ) {
                                        LOGGER.log( Level.WARNING, "Failed to load layer for feature type '" + ft.getName() +"'", e);
                                    }
                                }
                            }
                            else {
                                LOGGER.warning( "Ignoring feature type directory " + ftd.getAbsolutePath() );
                            }
                        }
                    }
                    else {
                        //look for a coverage store
                        f = new File( sd, "coveragestore.xml" );
                        if ( f.exists() ) {
                            CoverageStoreInfo cs = null;
                            try {
                                cs = depersist( xp, f, CoverageStoreInfo.class );
                                catalog.add( cs );
                            
                                LOGGER.info( "Loaded coverage store '" + cs.getName() +"'");
                            }
                            catch( Exception e ) {
                                LOGGER.log( Level.WARNING, "Failed to load coverage store '" + sd.getName() +"'", e);
                                continue;
                            }
                            
                            //load coverages
                            for ( File cd : list(sd,DirectoryFileFilter.INSTANCE) ) {
                                f = new File( cd, "coverage.xml" );
                                if( f.exists() ) {
                                    CoverageInfo c = null;
                                    try {
                                        c = depersist(xp,f,CoverageInfo.class);
                                        catalog.add( c );
                                        
                                        LOGGER.info( "Loaded coverage '" + cs.getName() +"'");
                                    }
                                    catch( Exception e ) {
                                        LOGGER.log( Level.WARNING, "Failed to load coverage '" + cd.getName() +"'", e);
                                        continue;
                                    }
                                    
                                    f = new File( cd, "layer.xml" );
                                    if ( f.exists() ) {
                                        try {
                                            LayerInfo l = depersist(xp, f, LayerInfo.class );
                                            catalog.add( l );
                                            
                                            LOGGER.info( "Loaded layer '" + l.getName() + "'" );
                                        }
                                        catch( Exception e ) {
                                            LOGGER.log( Level.WARNING, "Failed to load layer coverage '" + c.getName() +"'", e);
                                        }
                                    }
                                }
                                else {
                                    LOGGER.warning( "Ignoring coverage directory " + cd.getAbsolutePath() );
                                }
                            }
                        }
                        else {
                            LOGGER.warning( "Ignoring store directory '" + sd.getName() +  "'");
                            continue;
                        }
                    }
                }
            }
        }
        else {
            LOGGER.warning( "No 'workspaces' directory found, unable to load any stores." );
        }

        //namespaces
        
        //layergroups
        File layergroups = resourceLoader.find( "layergroups" );
        if ( layergroups != null ) {
            for ( File lgf : list( layergroups, new SuffixFileFilter( ".xml" ) ) ) {
                try {
                    LayerGroupInfo lg = depersist( xp, lgf, LayerGroupInfo.class );
                    catalog.add( lg );
                    
                    LOGGER.info( "Loaded layer group '" + lg.getName() + "'" );    
                }
                catch( Exception e ) {
                    LOGGER.log( Level.WARNING, "Failed to load layer group '" + lgf.getName() + "'", e );
                }
            }
        }
                
        return catalog;
    }
    
    /**
     * Reads the legacy (1.x) catalog from disk.
     */
    Catalog readLegacyCatalog(File f, XStreamPersister xp) throws Exception {
        Catalog catalog2 = new CatalogImpl();
        
        //add listener now as a converter which will convert from the old style 
        // data directory to the new
        GeoServerPersister p = new GeoServerPersister( resourceLoader, xp );
        if ( !legacy ) {
            catalog2.addListener( p );
        }
        
        LegacyCatalogImporter importer = new LegacyCatalogImporter(catalog2);
        importer.setResourceLoader(resourceLoader);
        importer.imprt(resourceLoader.getBaseDirectory());
        
        if ( !legacy ) {
            catalog2.removeListener( p );
        }
        
        if ( !legacy ) {
            //copy files from old feature type directories to new
            File featureTypesDir = resourceLoader.find( "featureTypes" );
            if ( featureTypesDir != null ) {
                LegacyCatalogReader creader = new LegacyCatalogReader();
                creader.read( f );
                Map<String,Map<String,Object>> dataStores = creader.dataStores();
                
                for ( File featureTypeDir : featureTypesDir.listFiles() ) {
                    if ( !featureTypeDir.isDirectory() ) {
                        continue;
                    }
                    
                    File featureTypeInfo = new File( featureTypeDir, "info.xml" );
                    if ( !featureTypeInfo.exists() )  {
                        continue;
                    }
                    
                    LegacyFeatureTypeInfoReader reader = new LegacyFeatureTypeInfoReader();
                    reader.read( featureTypeInfo );
                    
                    Map<String,Object> dataStore = dataStores.get( reader.dataStore() );
                    if ( dataStore == null ) {
                        continue;
                    }
                    
                    String namespace = (String) dataStore.get( "namespace" );
                    File destFeatureTypeDir = 
                        resourceLoader.find( "workspaces", namespace, reader.dataStore(), reader.name() );
                    if ( destFeatureTypeDir != null ) {
                        //copy all the files over
                        for ( File file : featureTypeDir.listFiles() ) {
                            if ( file.isFile() && !featureTypeInfo.equals( file ) ) {
                                FileUtils.copyFile( file, new File( destFeatureTypeDir, file.getName() ) ) ; 
                            }
                        }
                    }
                }
            }
            
            //rename catalog.xml
            f.renameTo( new File( f.getParentFile(), "catalog.xml.old" ) );
        }
        
        return catalog2;
    }
    
    /**
     * Helper method which uses xstream to persist an object as xml on disk.
     */
    void persist( XStreamPersister xp, Object obj, File f ) throws Exception {
        BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( f  ) );
        xp.save( obj, out );    
        out.flush();
        out.close();
    }
    
    /**
     * Helper method which uses xstream to depersist an object as xml from disk.
     */
    <T> T depersist( XStreamPersister xp, File f , Class<T> clazz ) throws IOException {
        BufferedInputStream in = new BufferedInputStream( new FileInputStream( f ) );
        T obj = xp.load( in, clazz );

        in.close();
        return obj;
    }
    
    /**
     * Handles a schema override of a feature type.
     * <p>
     * Reads schema.xml or schema.xsd, and culls the attributes of the resulting feature type.
     * 
     * </p>
     */
    //JD: this seems a bit out of place here... possibly move somewhere else 
    void handleSchemaOverride( FeatureTypeInfo ft, File ftd ) throws IOException {
        //TODO: create a file that abstracts file system access to data directory
        
        File schemaFile = new File( ftd, "schema.xsd" );
        if ( !schemaFile.exists() ) {
            //check for the old style schema.xml
            File oldSchemaFile = new File(ftd, "schema.xml");
            if ( oldSchemaFile.exists() ) {
                schemaFile = new File( oldSchemaFile.getParentFile(), "schema.xsd");
                BufferedWriter out = 
                    new BufferedWriter(new OutputStreamWriter( new FileOutputStream( schemaFile ) ) );
                out.write( "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>");
                IOUtils.copy( new FileInputStream( oldSchemaFile ), out );
                out.write( "</xs:schema>" );
                out.flush();
                out.close();
            }
        }
        
        if ( schemaFile.exists()) {
            //TODO: farm this schema loading stuff to some utility class
            //parse the schema + generate attributes from that
            List locators = Arrays.asList( GML.getInstance().createSchemaLocator() );
            XSDSchema schema = null;
            try {
                schema = Schemas.parse( schemaFile.getAbsolutePath(), locators, null );
            }
            catch( Exception e ) {
                LOGGER.warning( "Unable to parse " + schemaFile.getAbsolutePath() + "." +
                    " Falling back on native feature type");
            }
            if ( schema != null ) {
                XSDTypeDefinition type = null;
                for ( Iterator e = schema.getElementDeclarations().iterator(); e.hasNext(); ) {
                    XSDElementDeclaration element = (XSDElementDeclaration) e.next();
                    if ( ft.getName().equals( element.getName() ) ) {
                        type = element.getTypeDefinition();
                        break;
                    }
                }
                if ( type == null ) {
                    for ( Iterator t = schema.getTypeDefinitions().iterator(); t.hasNext(); ) {
                        XSDTypeDefinition typedef = (XSDTypeDefinition) t.next();
                        if ( (ft.getName() + "_Type").equals( typedef.getName() ) ) {
                            type = typedef;
                            break;
                        }
                    }
                }
                
                if ( type != null ) {
                    List children = Schemas.getChildElementDeclarations(type,true);
                    for ( Iterator<AttributeTypeInfo> i = ft.getAttributes().iterator(); i.hasNext(); ) {
                        AttributeTypeInfo at = i.next();
                        boolean found = false;
                        for ( Iterator c = children.iterator(); c.hasNext(); ) {
                            XSDElementDeclaration ce = (XSDElementDeclaration) c.next();
                            if ( at.getName().equals( ce.getName() ) ) {
                                found = true;
                                break;
                            }
                        }
                        
                        if ( !found ) {
                            i.remove();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Helper method for listing files in a directory.
     */
    Collection<File> list( File d, IOFileFilter filter ) {
        ArrayList<File> files = new ArrayList(); 
        for ( File f : d.listFiles() ) {
            if ( filter.accept( f ) ) {
                files.add( f );
            }
        }
        return files;
    }
    
    public void destroy() throws Exception {
        //dispose
        geoserver.dispose();
    }
}
