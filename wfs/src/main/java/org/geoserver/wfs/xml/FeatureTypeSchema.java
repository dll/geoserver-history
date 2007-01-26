package org.geoserver.wfs.xml;

import java.io.IOException;

import org.eclipse.xsd.XSDSchema;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.wfs.WFS;
import org.vfny.geoserver.global.Data;
import org.vfny.geoserver.global.FeatureTypeInfo;

/**
 * An xml schema describing a wfs feature type.
 *  
 * @author Justin Deoliveira, The Open Planning Project
 *
 */
public abstract class FeatureTypeSchema {

	/**
	 * The feature type metadata object.
	 */
	protected FeatureTypeInfo featureType;
	
	/**
	 * The xsd schema builder.
	 */
	protected FeatureTypeSchemaBuilder builder;
	
	/**
	 * The catalog
	 */
	protected Data catalog;
	
	/**
	 * WFS configuration
	 */
	protected WFS wfs;
	
	/**
	 * resource loader
	 */
	protected GeoServerResourceLoader loader;
	
	protected FeatureTypeSchema ( FeatureTypeInfo featureType, WFS wfs, Data catalog, GeoServerResourceLoader loader ) {
		this.featureType = featureType;
		this.catalog = catalog;
		this.wfs = wfs;
		this.loader = loader;
	}
	
	/**
	 * @return The feautre type info.
	 */
	FeatureTypeInfo getFeatureType() {
		return featureType;
	}
	
	/**
	 * @return The {@link XSDSchema} representation of the schema.
	 */
	public XSDSchema schema() throws IOException {
		return builder.build( new FeatureTypeInfo[] { featureType } );
	}
	
	/**
	 * Converts the schema to a gml2 schema.
	 */
	public FeatureTypeSchema toGML2() {
		if ( this instanceof GML2 ) {
			return this;
		}
		
		return new GML2( featureType, wfs, catalog, loader );
	}
	
	/**
	 * Converts the schema to a gml3 schema.
	 * @return
	 */
	public FeatureTypeSchema toGML3() {
		if ( this instanceof GML3 ) {
			return this;
		}
		
		return new GML3( featureType, wfs, catalog, loader );
	}
	
	/**
	 * GML2 based wfs feature type schema.
	 * 
	 * @author Justin Deoliveira, The Open Planning Project
	 */
	public static final class GML2 extends FeatureTypeSchema {

		public GML2( FeatureTypeInfo featureType, WFS wfs, Data catalog, GeoServerResourceLoader loader ) {
			super(featureType, wfs, catalog, loader);
			builder = new FeatureTypeSchemaBuilder.GML2( wfs, catalog, loader );
		}

	}
	
	/**
	 * GML3 based wfs feature type schema.
	 * 
	 * @author Justin Deoliveira, The Open Planning Project
	 */
	public static final class GML3 extends FeatureTypeSchema {

		protected GML3( FeatureTypeInfo featureType, WFS wfs, Data catalog, GeoServerResourceLoader loader ) {
			super(featureType, wfs, catalog, loader);
			builder = new FeatureTypeSchemaBuilder.GML3( wfs, catalog, loader );
		}
		
		
	}
}