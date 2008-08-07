package org.geoserver.config;

import java.io.File;

import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.util.LegacyCatalogImporter;
import org.geoserver.config.util.LegacyConfigurationImporter;
import org.geoserver.platform.GeoServerResourceLoader;

/**
 * Extension of GeoServerLoader which uses the legacy (1.x) style 
 * data directory to load configuration.
 * 
 * @author Justin Deoliveira, OpenGEO
 *
 */
public class LegacyGeoServerLoader extends GeoServerLoader {

    public LegacyGeoServerLoader(GeoServerResourceLoader resourceLoader) {
        super(resourceLoader);
    }
    
    protected void loadCatalog(Catalog catalog) throws Exception {
        //look for legacy catalog.xml
        File f = resourceLoader.find( "catalog.xml" );
        if ( f != null ) {
            LegacyCatalogImporter catalogImporter = new LegacyCatalogImporter();
            catalogImporter.setResourceLoader(resourceLoader);
            catalogImporter.setCatalog( catalog );
            
            catalogImporter.imprt( resourceLoader.getBaseDirectory() );
        }
        else {
            LOGGER.warning( "No catalog file found.");
        }
    }
    
    protected void loadGeoServer(GeoServer geoServer) throws Exception {
      //look for legacy services.xml
      File f = resourceLoader.find( "services.xml" );
      if ( f != null ) {
          //load configuration
          LegacyConfigurationImporter importer = new LegacyConfigurationImporter();
          importer.setConfiguration(geoServer);
          importer.imprt( resourceLoader.getBaseDirectory() );
      }
      else {
          LOGGER.warning( "No configuration file found.");
      }
    }
}
