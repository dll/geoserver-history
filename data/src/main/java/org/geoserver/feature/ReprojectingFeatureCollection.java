package org.geoserver.feature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureReader;
import org.geotools.factory.FactoryRegistryException;
import org.geotools.factory.Hints;
import org.geotools.feature.AttributeType;
import org.geotools.feature.CollectionListener;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureList;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypes;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.feature.SchemaException;
import org.geotools.feature.visitor.FeatureVisitor;
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotools.referencing.FactoryFinder;
import org.geotools.util.ProgressListener;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.OperationNotFoundException;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Decorating feature collection which reprojects feature geometries to a particular coordinate
 * reference system on the fly.
 * <p>
 * The coordinate reference system of feature geometries is looked up using 
 * {@link com.vividsolutions.jts.geom.Geometry#getUserData()}.
 * </p>
 * <p>
 * The {@link #defaultSource} attribute can be set to specify a coordinate refernence system 
 * to transform from when one is not specified by teh geometry itself. Leaving the property 
 * null specifies that the geometry will not be transformed.
 * </p>
 * @author Justin Deoliveira, The Open Planning Project
 *
 */
public class ReprojectingFeatureCollection implements FeatureCollection {

	/**
	 * The decorated collection
	 */
	FeatureCollection delegate;
	/**
	 * The schema of reprojected features
	 */
	FeatureType schema;
	/**
	 * The target coordinate reference system
	 */
	CoordinateReferenceSystem target;
	
	/**
	 * Coordinate reference system to use when one is not 
	 * specified on an encountered geometry.
	 */
	CoordinateReferenceSystem defaultSource;
	
	/**
	 * MathTransform cache, keyed by source CRS
	 */
	HashMap/*<CoordinateReferenceSystem,GeometryCoordinateSequenceTransformer>*/ transformers;
	
	/**
	 * Transformation hints
	 */
	Hints hints = new Hints(  Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE );
	
	public ReprojectingFeatureCollection( FeatureCollection delegate, CoordinateReferenceSystem target ) 
		throws SchemaException, OperationNotFoundException, FactoryRegistryException, FactoryException {
		
		this.delegate = delegate;
		this.target = target;
		this.schema = FeatureTypes.transform( delegate.getFeatureType(), target );
		
		//create transform cache
		transformers = new HashMap();
		
		//cache "default" transform
		CoordinateReferenceSystem source = 
			delegate.getFeatureType().getDefaultGeometry().getCoordinateSystem();
		if ( source != null ) {
			MathTransform2D tx = (MathTransform2D) FactoryFinder.getCoordinateOperationFactory(hints)
        		.createOperation(source,target).getMathTransform();
			
			GeometryCoordinateSequenceTransformer transformer = 
				new GeometryCoordinateSequenceTransformer();
			transformer.setMathTransform( tx );
			transformers.put( source, transformer );
		}
		else {
			//throw exception?
		}
	}
	
	public void setDefaultSource( CoordinateReferenceSystem defaultSource ) {
		this.defaultSource = defaultSource;
	}
	
	public FeatureIterator features() {
		return new ReprojectingFeatureIterator( delegate.features() );
	}

	public Iterator iterator() {
		return new ReprojectingIterator( delegate.iterator() );
	}

	public void purge() {
		delegate.purge();
	}
	
	public void close(FeatureIterator iterator) {
		if ( iterator instanceof ReprojectingFeatureIterator ) {
			delegate.close( ( (ReprojectingFeatureIterator) iterator ).getDelegate() );
		}
		
		iterator.close();
	}

	public void close(Iterator iterator) {
		if ( iterator instanceof ReprojectingIterator ) {
			delegate.close( ( (ReprojectingIterator) iterator ).getDelegate() );
		}
		
	}

	public void addListener(CollectionListener listenter) throws NullPointerException {
		delegate.addListener( listenter );
	}

	public void removeListener(CollectionListener listener) throws NullPointerException {
		delegate.removeListener( listener );
	}

	public FeatureType getFeatureType() {
		return schema;
	}

	public FeatureType getSchema() {
		return schema;
	}

	public void accepts(FeatureVisitor visitor, ProgressListener listener) throws IOException {
		delegate.accepts( visitor, listener );
	}

	public FeatureCollection subCollection(Filter filter) {
		FeatureCollection sub = delegate.subCollection( filter );
		if ( sub != null ) {
			try {
				ReprojectingFeatureCollection wrapper = 
					new ReprojectingFeatureCollection( sub, target );
				wrapper.setDefaultSource( defaultSource );
				
				return wrapper;
			} 
			catch( Exception e ) {
				throw new RuntimeException( e );
			}
		}
		
		return null;
	}

	public FeatureList sort(SortBy sortBy) {
		throw new UnsupportedOperationException();
	}


	public int size() {
		return delegate.size();
	}

	public void clear() {
		delegate.clear();
	}

	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	public Object[] toArray() {
		Object[] array = delegate.toArray();

		for ( int i = 0; i < array.length; i++ ) {
			try {
				array[ i ] = reproject( (Feature) array[ i ] );
			} 
			catch (IOException e) {
				throw new RuntimeException( e );
			}
		}
		
		return array;
	}

	public boolean add(Object o) {
		return delegate.add( o );
	}

	public boolean contains(Object o) {
		return delegate.contains( o );
	}

	public boolean remove(Object o) {
		return delegate.remove( o );
	}

	public boolean addAll(Collection c) {
		return delegate.addAll( c );
	}

	public boolean containsAll(Collection c) {
		return delegate.containsAll( c );
	}
	
	public boolean removeAll(Collection c) {
		return delegate.removeAll( c );
	}

	public boolean retainAll(Collection c) {
		return delegate.retainAll( c );
	}

	public Object[] toArray(Object[] a) {
		Object[] array = delegate.toArray( a );
		
		for ( int i = 0; i < array.length; i++ ) {
			try {
				array[ i ] = reproject( (Feature) array[ i ] );
			} 
			catch (IOException e) {
				throw new RuntimeException( e );
			}
		}
		
		return array;
	}

	public FeatureReader reader() throws IOException {
		throw new UnsupportedOperationException( "Use iterator instead" );
	}

	public Envelope getBounds() {
		Envelope bounds = new Envelope();
		Iterator i = iterator();
		
		try {
			if ( !i.hasNext() ) {
				bounds.setToNull();
				return bounds;
			}
			else {
				Feature first = (Feature) i.next();
				 bounds.init( first.getBounds() );
			}
			
			for (; i.hasNext(); ) {
				Feature f = (Feature) i.next();
				bounds.expandToInclude( f.getBounds() );
			}	
		}
		finally {
			close( i );
		}
		
		return bounds;
	}

	public FeatureCollection collection() throws IOException {
		return this;
	}

	public String getID() {
		return delegate.getID();
	}

	public Object[] getAttributes(Object[] array) {
		return delegate.getAttributes( array );
	}

	public Object getAttribute(String name) {
		return delegate.getAttribute( name );
	}

	public Object getAttribute(int index) {
		return delegate.getAttribute( index );
	}

	public void setAttribute(int index, Object value) throws IllegalAttributeException, ArrayIndexOutOfBoundsException {
		delegate.setAttribute( index, value );
	}

	public int getNumberOfAttributes() {
		return delegate.getNumberOfAttributes();
	}

	public void setAttribute(String name, Object value) throws IllegalAttributeException {
		delegate.setAttribute( name, value );
	}

	public Geometry getDefaultGeometry() {
		return delegate.getDefaultGeometry();
	}

	public void setDefaultGeometry(Geometry geometry) throws IllegalAttributeException {
		delegate.setDefaultGeometry( geometry );
	}
	
	Feature reproject( Feature feature ) throws IOException {
		
		Object[] attributes = new Object[ schema.getAttributeCount() ];
		for ( int i = 0; i < attributes.length; i++ ) {
			AttributeType type = schema.getAttributeType( i );
			
			Object object = feature.getAttribute( type.getName() );
			
			if ( object instanceof Geometry ) {
				//check for crs
				Geometry geometry = (Geometry) object;
				CoordinateReferenceSystem crs = (CoordinateReferenceSystem) geometry.getUserData();
				if ( crs == null ) {
					// no crs specified on geometry, check default
					if ( defaultSource != null ) {
						crs = defaultSource;
					}
				}
				
				if ( crs != null ) {
					//if equal, nothing to do
					if ( crs.equals( target ) )
						continue;
					
					GeometryCoordinateSequenceTransformer transformer = 
						(GeometryCoordinateSequenceTransformer) transformers.get( crs );
				
					if ( transformer == null ) {
						transformer = new GeometryCoordinateSequenceTransformer();
						MathTransform2D tx;
						try {
							tx = (MathTransform2D) FactoryFinder.getCoordinateOperationFactory(hints)
									.createOperation(crs,target).getMathTransform();
						} catch ( Exception e ) {
							String msg = "Could not transform for crs: " + crs;
							throw (IOException) new IOException( msg ).initCause( e );
						}
						
		        		transformer.setMathTransform( tx );
		        		transformers.put( crs, transformer );
					}
					
					//do the transformation
					try {
						object = transformer.transform( geometry );
					} 
					catch (TransformException e) {
						String msg = "Error occured transforming " + geometry.toString();
						throw (IOException) new IOException( msg ).initCause( e );
					}
				}
			}
			
			attributes[ i ] = object;
		}
		
		try {
			return schema.create( attributes, feature.getID() );
		} 
		catch (IllegalAttributeException e) {
			String msg = "Error creating reprojeced feature";
			throw (IOException) new IOException( msg ).initCause( e );
		}
	}
	
	class ReprojectingFeatureIterator implements FeatureIterator {

		FeatureIterator delegate;
		
		public ReprojectingFeatureIterator( FeatureIterator delegate ) {
			this.delegate = delegate;
		}
		
		public FeatureIterator getDelegate() {
			return delegate;
		}
		
		public boolean hasNext() {
			return delegate.hasNext();
		}

		public Feature next() throws NoSuchElementException {
			Feature feature = delegate.next();
			try {
				return reproject( feature );
			} 
			catch (IOException e) {
				throw new RuntimeException( e );
			}
		}

		public void close() {
			delegate = null;
		}
		
	}
	
	class ReprojectingIterator implements Iterator {

		Iterator delegate;
		
		public ReprojectingIterator( Iterator delegate ) {
			this.delegate = delegate;
		}
		
		public Iterator getDelegate() {
			return delegate;
		}
		
		public void remove() {
			delegate.remove();
		}

		public boolean hasNext() {
			return delegate.hasNext();
		}

		public Object next() {
			Feature feature = (Feature) delegate.next();
			try {
				return reproject( feature );
			} 
			catch (IOException e) {
				throw new RuntimeException( e );
			}
		}
		
	}

}
