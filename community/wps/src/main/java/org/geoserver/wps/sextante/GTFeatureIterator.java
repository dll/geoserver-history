package org.geoserver.wps.sextante;

import java.util.NoSuchElementException;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;

import es.unex.sextante.dataObjects.FeatureImpl;
import es.unex.sextante.dataObjects.IFeature;
import es.unex.sextante.dataObjects.IFeatureIterator;

public class GTFeatureIterator implements IFeatureIterator {

	private FeatureIterator<SimpleFeature> m_Iter;

    public GTFeatureIterator(FeatureCollection<SimpleFeatureType, SimpleFeature> fc){

    	m_Iter = fc.features();

    }

    public boolean hasNext() {

    	if (m_Iter != null){
    		return m_Iter.hasNext();
    	}
    	else{
    		return false;
    	}

    }

    public IFeature next() throws NoSuchElementException{

    	if (m_Iter != null){
        	if (!m_Iter.hasNext()){
        		throw new NoSuchElementException();
        	}
    		SimpleFeature gtFeat = m_Iter.next();
    		Object values[] = new Object[gtFeat.getAttributeCount() - 1];
    		for (int i = 1; i < gtFeat.getAttributeCount(); i++) {
    			values[i - 1] = gtFeat.getAttribute(i);
			}
    		IFeature feat = new FeatureImpl((Geometry) gtFeat.getDefaultGeometry(), values);
    		return feat;
    	}
    	else{
    		return null;
    	}

    }

	public void close() {

		m_Iter.close();

	}


}
