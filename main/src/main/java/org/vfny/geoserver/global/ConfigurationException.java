/* Copyright (c) 2001, 2003 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.vfny.geoserver.global;


/**
 * Thrown when there is an error in configuration.  Added a third constructor
 * for ease of exception type changing.
 *
 * @author Chris Holmes
 * @author dzwiers
 * @version $Id: ConfigurationException.java,v 1.3 2004/01/31 00:27:23 jive Exp $
 */
public class ConfigurationException extends Exception {
    /**
         *
         */
    private static final long serialVersionUID = 6333673036778693749L;

    /**
    * Constructs a new instance of ConfigurationException
    *
    * @param msg A message explaining the exception
    */
    public ConfigurationException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new instance of ConfigurationException
     *
     * @param msg A message explaining the exception
     * @param exp the throwable object which caused this exception
     */
    public ConfigurationException(String msg, Throwable exp) {
        super(msg, exp);
    }

    /**
     * Constructs a new instance of ConfigurationException
     *
     * @param exp the throwable object which caused this exception
     */
    public ConfigurationException(Throwable exp) {
        super(exp);
    }
}
