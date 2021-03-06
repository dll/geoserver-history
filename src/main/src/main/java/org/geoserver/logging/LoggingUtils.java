package org.geoserver.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.geoserver.platform.GeoServerResourceLoader;
import org.vfny.geoserver.global.ConfigurationException;

public class LoggingUtils {
    
    public static final String RELINQUISH_LOG4J_CONTROL = "RELINQUISH_LOG4J_CONTROL";  
    public static final String GT2_LOGGING_REDIRECTION = "GT2_LOGGING_REDIRECTION";
    public static final String GEOSERVER_LOG_LOCATION = "GEOSERVER_LOG_LOCATION";
    public static enum GeoToolsLoggingRedirection {
        JavaLogging, CommonsLogging, Log4J;
        
        /**
         * Returns the enum value corresponding to the name (using case insensitive comparison)
         * or Log4j if no match is found 
         * @param name
         * @return
         */
        public static GeoToolsLoggingRedirection findValue(String name) {
            for (GeoToolsLoggingRedirection value : values()) {
                if(value.name().equalsIgnoreCase(name))
                    return value;
            }
            return Log4J;
        }
    }

    public static void configureGeoServerLogging(GeoServerResourceLoader loader, InputStream loggingConfigStream, boolean suppressStdOutLogging, boolean suppressFileLogging, String logFileName) throws FileNotFoundException, IOException,
                            ConfigurationException {
            //JD: before we wipe out the logging configuration, save any appenders that are not 
            // console or file based. This allows for other types of appenders to remain in tact
            // when geoserver is reloaded.
            List<Appender> appenders = new ArrayList();
            Enumeration a = LogManager.getRootLogger().getAllAppenders();
            while( a.hasMoreElements() ) {
                Appender appender = (Appender) a.nextElement();
                if ( !( appender instanceof ConsoleAppender || appender instanceof FileAppender )  ){ 
                    //save it 
                    appenders.add( appender );
                }
            }
    
            Properties lprops = new Properties();
            lprops.load(loggingConfigStream);
            LogManager.resetConfiguration();
    //        LogLog.setQuietMode(true);
            PropertyConfigurator.configure(lprops);
    //        LogLog.setQuietMode(false);
            
            // configuring the log4j file logger
            if(!suppressFileLogging) {
                    Appender gslf = org.apache.log4j.Logger.getRootLogger().getAppender("geoserverlogfile");
                    if (gslf instanceof org.apache.log4j.RollingFileAppender) {
                        if (logFileName == null ) {
                            logFileName = new File(loader.findOrCreateDirectory("logs"),  "geoserver.log").getAbsolutePath();
                        } else { 
                            if (!new File(logFileName).isAbsolute()) {
                                logFileName = new File(loader.getBaseDirectory(), logFileName).getAbsolutePath();
                                LoggingInitializer.LOGGER.fine("Non-absolute pathname detected for logfile.  Setting logfile relative to data dir.");
                            }
                        }
                        lprops.setProperty("log4j.appender.geoserverlogfile.File", logFileName);
                        PropertyConfigurator.configure(lprops);
                        LoggingInitializer.LOGGER.fine("Logging output to file '" + logFileName + "'");
                    } else if (gslf != null) {
                        LoggingInitializer.LOGGER.warning("'log4j.appender.geoserverlogfile' appender is defined, but isn't a RollingFileAppender.  GeoServer won't control the file-based logging.");
                    } else {
                        LoggingInitializer.LOGGER.warning("'log4j.appender.geoserverlogfile' appender isn't defined.  GeoServer won't control the file-based logging.");
                    }
            }
            
            // ... and the std output logging too
            if (suppressStdOutLogging) {
                LoggingInitializer.LOGGER.warning("Suppressing StdOut logging.  If you want to see GeoServer logs, be sure to look in '" + logFileName + "'");
                Enumeration allAppenders = org.apache.log4j.Logger.getRootLogger().getAllAppenders();
                Appender curApp;
                while (allAppenders.hasMoreElements()) {
                    curApp = (Appender)allAppenders.nextElement();
                    if (curApp instanceof org.apache.log4j.ConsoleAppender) {
                        org.apache.log4j.Logger.getRootLogger().removeAppender(curApp);
                    }
                }
            } 
            
            //add the appenders we saved above
            for ( Appender appender : appenders ) {
                LogManager.getRootLogger().addAppender( appender );
            }
            LoggingInitializer.LOGGER.fine("FINISHED CONFIGURING GEOSERVER LOGGING -------------------------");
        }

    public static void initLogging(GeoServerResourceLoader resourceLoader, String configFileName, boolean suppressStdOutLogging, String logFileName) throws Exception {
        //to initialize logging we need to do a couple of things:
        // 1)  Figure out whether the user has 'overridden' some configuration settings
        // in the logging system (not using log4j in commons-logging.properties or perhaps
        // has set up their own 'custom' log4j.properties file.
        // 2)  If they *have*, then we don't worry about configuring logging
        // 3)  If they haven't, then we configure logging to use the log4j config file
        // specified, and remove console appenders if the suppressstdoutlogging is true.
        LoggingInitializer.LOGGER.fine("CONFIGURING GEOSERVER LOGGING -------------------------");
        
        if (configFileName == null) {
            configFileName = "DEFAULT_LOGGING.properties";
            LoggingInitializer.LOGGER.warning("No log4jConfigFile defined in services.xml:  using 'DEFAULT_LOGGING.properties'");
        }
        
        File log4jConfigFile = resourceLoader.find( "logs", configFileName );
        if (log4jConfigFile == null) {
            //hmm, well, we don't have a log4j config file and this could be due to the fact
            //that this is a data-dir upgrade.  We can count on the DEFAULT_LOGGING.properties file
            //being present on the classpath, so we'll upgrade their data_dir and then use the
            //default DEFAULT_LOGGING.properties configuration.
            LoggingInitializer.LOGGER.warning("log4jConfigFile '" + configFileName + "' couldn't be found in the data dir, so GeoServer will " +
            "install the various logging config file into the data dir, and then try to find it again.");
            
            //this forces the data_dir/logs directory to be present (if it wasn't already)
            File lcdir = resourceLoader.findOrCreateDirectory( "logs" );
            
            //now we copy in the various logging config files from the base repo location on the classpath
            final String[] lcfiles = new String[] { "DEFAULT_LOGGING.properties",
                    "VERBOSE_LOGGING.properties",
                    "PRODUCTION_LOGGING.properties",
                    "GEOTOOLS_DEVELOPER_LOGGING.properties",
                    "GEOSERVER_DEVELOPER_LOGGING.properties" };
            
            for (int i = 0; i < lcfiles.length; i++) {
                File target = new File(lcdir.getAbsolutePath(), lcfiles[i]);
                if (!target.exists()) {
                    resourceLoader.copyFromClassPath(lcfiles[i], target);
                }
            }
            
            //ok, the possibly-new 'logs' directory is in-place, with all the various configs there.
            // Is the originally configured log4jconfigfile there now?
            log4jConfigFile = resourceLoader.find( "logs", configFileName );
            if (log4jConfigFile == null) {
                LoggingInitializer.LOGGER.warning("Still couldn't find log4jConfigFile '" + configFileName + "'.  Using DEFAULT_LOGGING.properties instead.");
            }
            
            log4jConfigFile = resourceLoader.find( "logs", "DEFAULT_LOGGING.properties" );
        }
    
        if (log4jConfigFile == null || !log4jConfigFile.exists()) {
            throw new ConfigurationException("Unable to load logging configuration '" + configFileName + "'.  In addition, an attempt " +
                    "was made to create the 'logs' directory in your data dir, and to use the DEFAULT_LOGGING configuration, but" +
                    "this failed as well.  Is your data dir writeable?");
        }
        
                // reconfiguring log4j logger levels by resetting and loading a new set of configuration properties
        InputStream loggingConfigStream = new FileInputStream(log4jConfigFile);
        if (loggingConfigStream == null) {
            LoggingInitializer.LOGGER.warning("Couldn't open Log4J configuration file '" + log4jConfigFile.getAbsolutePath());
            return;
        } else {
            LoggingInitializer.LOGGER.fine("GeoServer logging profile '" + log4jConfigFile.getName() + "' enabled.");
        }
    
        configureGeoServerLogging(resourceLoader, loggingConfigStream, suppressStdOutLogging, false, 
                                logFileName);
        
        
    }

}
