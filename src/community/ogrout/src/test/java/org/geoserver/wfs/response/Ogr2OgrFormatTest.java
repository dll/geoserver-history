package org.geoserver.wfs.response;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;
import junit.framework.TestResult;
import net.opengis.wfs.FeatureCollectionType;
import net.opengis.wfs.GetFeatureType;
import net.opengis.wfs.WfsFactory;

import org.geoserver.platform.Operation;
import org.geoserver.platform.Service;
import org.geotools.data.DataStore;
import org.geotools.data.property.PropertyDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.util.Version;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Ogr2OgrFormatTest extends TestCase {

    DataStore dataStore;

    Ogr2OgrOutputFormat ogr;

    Operation op;

    FeatureCollectionType fct;

    GetFeatureType gft;

    @Override
    public void run(TestResult result) {
        if (!Ogr2OgrTestUtil.isOgrAvailable())
            System.out.println("Skipping ogr2ogr format tests, ogr2ogr could not be found, "
                    + getName());
        else
            super.run(result);
    }

    @Override
    protected void setUp() throws Exception {
        // the data source we'll use for the tests
        dataStore = new PropertyDataStore(new File("./src/test/java/org/geoserver/wfs/response"));

        // the output format (and let's add a few output formats to play with
        ogr = new Ogr2OgrOutputFormat();
        ogr.addFormat(new OgrParameters("KML", "OGR-KML", ".kml", null));
        ogr.addFormat(new OgrParameters("CSV", "OGR-CSV", ".csv", null));
        ogr.addFormat(new OgrParameters("SHP", "OGR-SHP", ".shp", null));
        ogr.setOgrExecutable(Ogr2OgrTestUtil.getOgr2Ogr());

        // the EMF objects used to talk with the output format
        gft = WfsFactory.eINSTANCE.createGetFeatureType();
        fct = WfsFactory.eINSTANCE.createFeatureCollectionType();
        op = new Operation("GetFeature", new Service("WFS", null, new Version("1.0.0")), null,
                new Object[] { gft });
    }

    public void testCanHandle() {
        gft.setOutputFormat("OGR-KML");
        assertTrue(ogr.canHandle(op));
        gft.setOutputFormat("OGR-CSV");
        assertTrue(ogr.canHandle(op));
        gft.setOutputFormat("RANDOM_FORMAT");
        assertTrue(ogr.canHandle(op));
    }

    public void testContentType() {
        assertEquals("application/zip", ogr.getMimeType(null, op));
    }

    public void testSimpleKML() throws Exception {
        // prepare input
        FeatureCollection fc = dataStore.getFeatureSource("Buildings").getFeatures();
        fct.getFeature().add(fc);

        // write out
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        gft.setOutputFormat("OGR-KML");
        ogr.write(fct, bos, op);

        // read back
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bos.toByteArray()));
        ZipEntry entry = zis.getNextEntry();
        assertNotNull(entry);
        assertEquals("Buildings.kml", entry.getName());

        // parse the kml to check we really got it right
        Document dom = dom(zis);
    }
    
    public void testSimpleCSV() throws Exception {
        // prepare input
        FeatureCollection fc = dataStore.getFeatureSource("Buildings").getFeatures();
        fct.getFeature().add(fc);

        // write out
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        gft.setOutputFormat("OGR-CSV");
        ogr.write(fct, bos, op);

        // read back
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bos.toByteArray()));
        ZipEntry entry = zis.getNextEntry();
        assertNotNull(entry);
        // ogr will create a folder with a file in it...
        assertEquals("Buildings.csv/Buildings.csv", entry.getName());
        String csv = read(zis);
        
        // couple simple checks
        String[] lines = csv.split("\n");
        // headers and the two lines
        assertEquals(3, lines.length);
        assertTrue(csv.contains("123 Main Street"));
    }
    
    public void testGeometrylessCSV() throws Exception {
        // prepare input
        FeatureCollection fc = dataStore.getFeatureSource("Geometryless").getFeatures();
        fct.getFeature().add(fc);

        // write out
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        gft.setOutputFormat("OGR-CSV");
        ogr.write(fct, bos, op);

        // read back
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bos.toByteArray()));
        ZipEntry entry = zis.getNextEntry();
        assertNotNull(entry);
        // ogr will create a folder with a file in it...
        assertEquals("Geometryless.csv/Geometryless.csv", entry.getName());
        String csv = read(zis);
        
        // couple simple checks
        String[] lines = csv.split("\n");
        // headers and the feature lines
        assertEquals(4, lines.length);
        // let's see if one of the expected lines is there
        assertTrue(csv.contains("Alessia"));
    }

    /**
     * Utility method to print out a dom.
     */
    protected void print(Document dom) throws Exception {
        TransformerFactory txFactory = TransformerFactory.newInstance();
        try {
            txFactory.setAttribute("{http://xml.apache.org/xalan}indent-number", new Integer(2));
        } catch (Exception e) {
            // some
        }

        Transformer tx = txFactory.newTransformer();
        tx.setOutputProperty(OutputKeys.METHOD, "xml");
        tx.setOutputProperty(OutputKeys.INDENT, "yes");

        tx.transform(new DOMSource(dom), new StreamResult(new OutputStreamWriter(System.out,
                "utf-8")));
    }
    
    protected String read(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Parses a stream into a dom.
     * 
     * @param input
     * @param skipDTD
     *            If true, will skip loading and validating against the
     *            associated DTD
     */
    protected Document dom(InputStream input) throws ParserConfigurationException, SAXException,
            IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);

        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(input);
    }

}
