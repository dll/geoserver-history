/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.gs;

import jaitools.media.jai.contour.ContourDescriptor;

import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collection;

import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;

import org.geoserver.wps.jts.DescribeParameter;
import org.geoserver.wps.jts.DescribeProcess;
import org.geoserver.wps.jts.DescribeResult;
import org.geoserver.wps.raster.CoverageUtilities;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.collection.SpatialIndexFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.process.ProcessException;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.util.ProgressListener;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.util.AffineTransformation;

/**
 * A process that wraps a {@link GridCoverage2D} as a collection of point feature.
 * 
 * @author Simone Giannecchini, GeoSolutions
 * 
 */
@DescribeProcess(title = "Contour", description = "Perform the contouring on a provided raster")
public class ContourProcess implements GeoServerProcess {
	

	
    @DescribeResult(name = "result", description = "The contours feature collection")
    public SimpleFeatureCollection execute(
            @DescribeParameter(name = "data", description = "The raster to be used as the source") GridCoverage2D gc2d,
            @DescribeParameter(name = "band", description = "The source image band to process",min=0, max=1) Integer band,
            @DescribeParameter(name = "levels", description = "Values for which to generate contours") double[] levels,
            @DescribeParameter(name = "interval", description = "Interval between contour values (ignored if levels arg is supplied)",min=0) Double interval,
            @DescribeParameter(name = "simplify", description = "Values for which to generate contours",min=0) Boolean simplify,
            @DescribeParameter(name = "smooth", description = "Values for which to generate contours",min=0) Boolean smooth,
            @DescribeParameter(name = "roi", description = "The geometry used to delineate the area of interest in model space",min=0) Geometry roi,
            ProgressListener progressListener)
            throws ProcessException {
    	
    	
    	//
    	// initial checks
    	//
        if (gc2d ==null) {
            throw new ProcessException("Invalid input, source grid coverage should be not null");
        }
        if (band != null && (band < 0 || band>=gc2d.getNumSampleDimensions())) {
            throw new ProcessException("Invalid input, invalid band number:"+band);
        }
        boolean hasValues=!(levels== null || levels.length==0);
        if(!hasValues&&interval==null){
        	throw new ProcessException("One between interval and values must be valid");
        	
        }
        
        //
        // GRID TO WORLD preparation
        //
        final AffineTransform mt2D = (AffineTransform) gc2d.getGridGeometry().getGridToCRS2D(PixelOrientation.CENTER);
        
        // get the rendered image
        final RenderedImage raster=gc2d.getRenderedImage();
        
        // perform jai operation
        ParameterBlockJAI pb = new ParameterBlockJAI("Contour");
        pb.setSource("source0", raster);
        
        if(roi!=null){
        	pb.setParameter("roi", CoverageUtilities.prepareROI(roi,mt2D));
        }
        if(band != null) {
            pb.setParameter("band", band);
        }
        if(interval!=null){
        	pb.setParameter("interval", interval);
        } else {
        	final ArrayList<Double> elements= new ArrayList<Double>(levels.length);
        	for(double level:levels)
        		elements.add(level);
        	pb.setParameter("levels", elements);
        }
        if(simplify != null) { 
            pb.setParameter("simplify", simplify);
        }
        if(smooth != null) {
            pb.setParameter("smooth", smooth);
        }

        final RenderedOp dest = JAI.create("Contour", pb);
		@SuppressWarnings("unchecked")
		final Collection<LineString> prop = (Collection<LineString>) dest.getProperty(ContourDescriptor.CONTOUR_PROPERTY_NAME);
        
        // wrap as a feature collection and return
		final SimpleFeatureType schema=CoverageUtilities.createFeatureType(gc2d,LineString.class);
        final SimpleFeatureBuilder builder = new SimpleFeatureBuilder(schema);
        int i=0;
        final ListFeatureCollection featureCollection= new ListFeatureCollection(schema);
        final AffineTransformation jtsTransformation=new AffineTransformation(
    			mt2D.getScaleX(),
    			mt2D.getShearX(),
    			mt2D.getTranslateX(),
    			mt2D.getShearY(),
    			mt2D.getScaleY(),
    			mt2D.getTranslateY());
        for(LineString line:prop){
        	
        	// get value
        	Double  value= (Double) line.getUserData();
        	line.setUserData(null);
        	// filter coordinates in place
        	line.apply(jtsTransformation);
        	
        	// create feature and add to list
        	builder.set("the_geom", line);
        	builder.set("value", value);
        	
        	featureCollection.add(builder.buildFeature(String.valueOf(i++)));
        	
        }
        
        //return value
        
        return featureCollection;

    }
    
    /**
     * Applies an offset to the X and Y coordinates
     */
    public static class OffsetOrdinateFilter implements CoordinateSequenceFilter {
        double offsetX;
        double offsetY;

        public OffsetOrdinateFilter(double offsetX, double offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        public void filter(CoordinateSequence seq, int i) {
            seq.setOrdinate(i, 0, seq.getOrdinate(i, 0) + offsetX);
            seq.setOrdinate(i, 1, seq.getOrdinate(i, 1) + offsetY);
        }

        public boolean isDone() {
            return false;
        }

        public boolean isGeometryChanged() {
            return true;
        }

    }

}