/* Copyright (c) 2001 - 2008 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog;

import java.io.Serializable;
import java.util.Map;

/**
 * Application schema namespace.
 * 
 * @author Justin Deoliveira, The Open Planning Project
 */
public interface NamespaceInfo extends Info {

    /**
     * Identifier.
     */
    String getId();

    /**
     * The prefix of the namespace.
     * <p>
     * This prefix is unique among all namespace instances and can be used to
     * identify a particular namespace.
     * </p>
     * 
     * @uml.property name="prefix"
     */
    String getPrefix();

    /**
     * Sets the prefix of the namespace.
     * 
     * @uml.property name="prefix"
     */
    void setPrefix(String prefix);

    /**
     * The uri of the namespace.
     * <p>
     * This uri is unique among all namespace instances and can be used to
     * identify a particular namespace.
     * </p>
     * 
     * @uml.property name="uRI"
     */
    String getURI();

    /**
     * Sets the uri of the namespace.
     * 
     * @uml.property name="uRI"
     */
    void setURI(String uri);
    
    /**
     * A persistent map of metadata.
     * <p>
     * Data in this map is intended to be persisted. Common case of use is to
     * have services associate various bits of data with a particular namespace.
     * </p>
     * 
     */
    Map<String,Serializable> getMetadata();
    
    /**
     * Two namespace objects are considred equal if they have the same "prefix"
     * and "uri".
     */
    boolean equals(Object obj);
    
    // /**
    // * The resources which fall into this namespace.
    // * @uml.property name="resources"
    // * @uml.associationEnd multiplicity="(0 -1)"
    // container="java.util.Iterator" aggregation="composite"
    // inverse="namespace:org.geoserver.catalog.ResourceInfo"
    // */
    // Iterator/*<ResourceInfo>*/ resources();
    //	
    // /**
    // * Adds a resource to the namespace.
    // */
    // void add( ResourceInfo resource );
    //	
    // /**
    // * Removes a resource from the namespace.
    // */
    // void remove( ResourceInfo resource );
}
