/* Copyright (c) 2001 - 2008 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.config.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.RecyclingTileFactory;

import org.geoserver.catalog.util.LegacyCatalogImporter;
import org.geoserver.config.ContactInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerFactory;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.config.ServiceInfo;
import org.geoserver.config.ServiceLoader;
import org.geoserver.jai.JAIInfo;
import org.geoserver.platform.GeoServerExtensions;
import org.geotools.util.logging.Logging;
import org.vfny.geoserver.global.dto.GeoServerDTO;

import com.sun.media.jai.util.SunTileCache;

/**
 * Imports configuration from a legacy "services.xml" file into a geoserver
 * configuration instance.
 * 
 * @author Justin Deoliveira, The Open Planning Project
 * 
 */
public class LegacyConfigurationImporter {

    /** logger */
    static Logger LOGGER = Logging.getLogger( "org.geoserver.confg" );

    /**
     * configuration
     */
    GeoServer geoServer;

    /**
     * Creates the importer.
     * 
     * @param geoServer
     *                The configuration to import into.
     */
    public LegacyConfigurationImporter(GeoServer geoServer) {
        this.geoServer = geoServer;
    }

    /**
     * No argument constructor.
     * <p>
     * Calling code should use {@link #setConfiguration(GeoServer)} when using
     * this constructor.
     * </p>
     * 
     */
    public LegacyConfigurationImporter() {

    }

    /**
     * Sets teh configuration to import into.
     */
    public void setConfiguration(GeoServer geoServer) {
        this.geoServer = geoServer;
    }

    /**
     * The configuration being imported into.
     */
    public GeoServer getConfiguration() {
        return geoServer;
    }

    /**
     * Imports configuration from a geoserver data directory into the
     * configuration.
     * 
     * @param dir
     *                The root of the data directory.
     * 
     */
    public void imprt(File dir) throws Exception {

        //TODO: this routine needs to be safer about accessing parameters, 
        // wrapping in null checks
        
        GeoServerFactory factory = geoServer.getFactory();

        // services.xml
        File servicesFile = new File(dir, "services.xml");
        if (!servicesFile.exists()) {
            throw new FileNotFoundException(
                    "Could not find services.xml under:"
                            + dir.getAbsolutePath());
        }

        LegacyServicesReader reader = new LegacyServicesReader();
        reader.read(servicesFile);

        //
        //global
        //
        GeoServerInfo info = factory.createGlobal();
        Map<String,Object> global = reader.global(); 
        info.setLoggingLevel( (String) global.get( "log4jConfigFile") );
        info.setLoggingLocation( (String) global.get( "logLocation") );
        
        if ( global.get( "suppressStdOutLogging" ) != null ) {
            info.setStdOutLogging( ! get( global, "suppressStdOutLogging", Boolean.class) );    
        }
        else {
            info.setStdOutLogging(true);
        }

        //info.setMaxFeatures( get( global, "maxFeatures", Integer.class ) );
        info.setVerbose( get( global, "verbose", boolean.class ) );
        info.setVerboseExceptions( get( global, "verboseExceptions", boolean.class ) );
        info.setNumDecimals( get( global, "numDecimals", int.class, 4 ) );
        info.setCharset( (String) global.get( "charSet" ) );
        info.setUpdateSequence( get( global, "updateSequence", int.class ) );
        info.setOnlineResource( get( global, "onlineResource", String.class ) );
        info.setProxyBaseUrl( get( global, "ProxyBaseUrl", String.class ) );
        
        //contact
        Map<String,Object> contact = reader.contact();
        ContactInfo contactInfo = factory.createContact();
       
        contactInfo.setContactPerson( (String) contact.get( "ContactPerson") );
        contactInfo.setContactOrganization( (String) contact.get( "ContactOrganization") );
        contactInfo.setContactVoice( (String) contact.get( "ContactVoiceTelephone" ) );
        contactInfo.setContactFacsimile( (String) contact.get( "ContactFacsimileTelephone" ) );
        contactInfo.setContactPosition( (String) contact.get( "ContactPosition" ) );
        contactInfo.setContactEmail( (String) contact.get( "ContactElectronicMailAddress" ) );
        
        contactInfo.setAddress( (String) contact.get( "Address") );
        contactInfo.setAddressType( (String) contact.get( "AddressType") );
        contactInfo.setAddressCity( (String) contact.get( "City") );
        contactInfo.setAddressCountry( (String) contact.get( "Country") );
        contactInfo.setAddressState( (String) contact.get( "StateOrProvince") );
        contactInfo.setAddressPostalCode( (String) contact.get( "PostCode") );
        info.setContact( contactInfo );
        
        //jai
        JAIInfo jai = new JAIInfo();
        jai.setMemoryCapacity( (Double) value( global.get( "JaiMemoryCapacity"),GeoServerDTO.Defaults.JaiMemoryCapacity ) );
        jai.setMemoryThreshold( (Double) value( global.get( "JaiMemoryThreshold"), GeoServerDTO.Defaults.JaiMemoryThreshold) );
        jai.setTileThreads( (Integer) value( global.get( "JaiTileThreads"), GeoServerDTO.Defaults.JaiTileThreads ) );
        jai.setTilePriority( (Integer) value( global.get( "JaiTilePriority"), GeoServerDTO.Defaults.JaiTilePriority ) );
        jai.setImageIOCache( (Boolean) value( global.get( "ImageIOCache" ), GeoServerDTO.Defaults.ImageIOCache) );
        jai.setJpegAcceleration( (Boolean) value( global.get( "JaiJPEGNative" ),GeoServerDTO.Defaults.JaiJPEGNative ) );
        jai.setPngAcceleration( (Boolean) value( global.get( "JaiPNGNative" ), GeoServerDTO.Defaults.JaiPNGNative)  );
        jai.setRecycling( (Boolean) value( global.get( "JaiRecycling" ), GeoServerDTO.Defaults.JaiRecycling)  );
        jai.setAllowNativeMosaic((Boolean) value( global.get( "JaiMosaicNative" ), GeoServerDTO.Defaults.JaiMosaicNative) );
        
        info.getMetadata().put( JAIInfo.KEY, jai );
        
        geoServer.setGlobal( info );
        
        // read services
        for ( ServiceLoader sl : GeoServerExtensions.extensions( ServiceLoader.class ) ) {
            try {
                //special case for legacy stuff
                if ( sl instanceof LegacyServiceLoader ) {
                    ((LegacyServiceLoader)sl).setReader(reader);
                }
                
                ServiceInfo service = sl.load( geoServer );
                if ( service != null ) {
                    LOGGER.info( "Loading service '" + service.getId()  + "'");
                    geoServer.add( service );
                }
            }
            catch( Exception e ) {
                String msg = "Error occured loading service: " + sl.getServiceClass().getSimpleName();
                LOGGER.warning( msg );
                LOGGER.log( Level.INFO, "", e );
            }
        }
    }
    
    Object value( Object value, Object def ) {
        return value != null ? value : def;
    }
    
    protected <T extends Object> T get(Map map, String key, Class<T> clazz, T def ) {
        Object o = map.get( key );
        if ( o == null ) {
            if ( def != null ) {
                return def;
            }
            
            //check for primitive type
            if ( clazz.isPrimitive() ) {
                if ( clazz == int.class ) {
                    return (T) Integer.valueOf( 0 );
                }
                if ( clazz == double.class ) {
                    return (T) Double.valueOf( 0d ); 
                }
                if ( clazz == boolean.class ) {
                    return (T) Boolean.FALSE;
                }
            }
            return null;
        }
        
        return (T) o;
    }
    
    protected <T extends Object> T get(Map map, String key,
        Class<T> clazz) {
        return get( map, key, clazz, null );
    }
    
    
}
