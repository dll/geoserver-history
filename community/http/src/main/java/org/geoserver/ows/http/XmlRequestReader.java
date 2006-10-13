package org.geoserver.ows.http;

import java.io.InputStream;

/**
 * Creates a request bean from xml.
 * <p>
 *  
 * </p>
 * 
 * @author Justin Deoliveira, The Open Planning Project, jdeolive@openplans.org
 *
 */
public abstract class XmlRequestReader {
	
	/**
	 * Name of element
	 */
	String namespace;
	/**
	 * Name of element
	 */
	String element;
	/**
	 * Appliction specific version number.
	 */
	String version;
	
	public XmlRequestReader( String namespace, String element ) {
		this( namespace, element, null );
	}
	
	public XmlRequestReader( String namespace, String element, String version ) {
		this.namespace = namespace;
		this.element = element;
		this.version = version;
		
		if ( element == null )
			throw new NullPointerException( "element" );
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public String getElement() {
		return element;
	}
	
	public String getVersion() {
		return version;
	}
	
	public abstract Object read( InputStream input ) throws Exception;
	
	/**
	 * Two XmlReaders considered equal if namespace,element, and version properties
	 * are the same.
	 */
	public boolean equals( Object obj ) {
		if ( !( obj instanceof XmlRequestReader ) )
			return false;
		
		XmlRequestReader other = (XmlRequestReader) obj;
		
		if ( !element.equalsIgnoreCase( other.element ) ) {
			return false;
		}
		
		if ( namespace != null ) { 
			if ( !namespace.equalsIgnoreCase( other.namespace ) ) {
				return false;
			}
		}
			
		if ( version != null ) {
			return version.equals( other.version );
		}
		
		return other.version == null;
		
	}
	
	public int hashCode() {
		int result = element.hashCode();
		
		if ( namespace != null ) {
			result = result*17 + element.hashCode();	
		}
		if ( version != null ) {
			result = result*17 + version.hashCode();
		}
		return result;
	}
	
}
