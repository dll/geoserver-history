/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.vfny.geoserver.wms.responses.map.svg;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

import junit.framework.Test;

import org.geoserver.wms.WMSTestSupport;
import org.geotools.data.FeatureSource;
import org.geotools.data.memory.MemoryDataStore;
import org.geotools.feature.AttributeTypeFactory;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeFactory;
import org.geotools.styling.Style;
import org.vfny.geoserver.wms.WMSMapContext;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class SVGMapProducerTest extends WMSTestSupport {
    
    /**
     * This is a READ ONLY TEST so we can use one time setup
     */
    public static Test suite() {
        return new OneTimeTestSetup(new SVGMapProducerTest());
    }
    
    public void testHeterogeneousGeometry() throws Exception {
        GeometryFactory gf = new GeometryFactory();
        Point point = gf.createPoint(new Coordinate(10, 10));
        LineString line = gf.createLineString(new Coordinate[] {
                new Coordinate(50, 50), new Coordinate(100, 100) });
        Polygon polygon = gf.createPolygon(gf
                .createLinearRing(new Coordinate[] { new Coordinate(0, 0),
                        new Coordinate(0, 200), new Coordinate(200, 200),
                        new Coordinate(200, 0), new Coordinate(0, 0) }), null);

        AttributeTypeFactory atf = AttributeTypeFactory.defaultInstance();
        FeatureTypeFactory ff = FeatureTypeFactory.newInstance("test");

        ff.addType(atf.newAttributeType("geom", Geometry.class));

        FeatureType type = ff.getFeatureType();

        Feature f1 = type.create(new Object[] { point });
        Feature f2 = type.create(new Object[] { line });
        Feature f3 = type.create(new Object[] { polygon });

        MemoryDataStore ds = new MemoryDataStore();
        ds.createSchema(type);
        ds.addFeatures(new Feature[] { f1, f2, f3 });

        FeatureSource fs = ds.getFeatureSource("test");

        final WMSMapContext map = new WMSMapContext();
        map.setAreaOfInterest(new Envelope(-250, 250, -250, 250));
        map.setMapWidth(300);
        map.setMapHeight(300);
        map.setBgColor(Color.red);
        map.setTransparent(false);

        Style basicStyle = getCatalog().getStyle("Default");
        map.addLayer(fs, basicStyle);

        SVGMapProducer producer = new SVGMapProducer();
        producer.setMapContext(map);
        producer.produceMap();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        producer.writeTo(out);
    }
}
