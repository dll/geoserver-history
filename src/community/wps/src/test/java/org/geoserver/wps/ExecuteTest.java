package org.geoserver.wps;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.geotools.gml3.GMLConfiguration;
import org.geotools.referencing.operation.projection.Polyconic;
import org.hsqldb.lib.StringInputStream;
import org.w3c.dom.Document;

import com.mockrunner.mock.web.MockHttpServletResponse;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

public class ExecuteTest extends WPSTestSupport {
	
	/* TODO GET requests A.4.4.1 */

    public void testDataInline() throws Exception { // Standard Test A.4.4.2, A.4.4.4
        String xml =  
          "<wps:Execute service='WPS' version='1.0.0' xmlns:wps='http://www.opengis.net/wps/1.0.0' " + 
              "xmlns:ows='http://www.opengis.net/ows/1.1'>" + 
            "<ows:Identifier>gt:buffer</ows:Identifier>" + 
             "<wps:DataInputs>" + 
                "<wps:Input>" + 
                    "<ows:Identifier>geom1</ows:Identifier>" + 
                    "<wps:Data>" +
                      "<wps:ComplexData>" + 
                        "<gml:Polygon xmlns:gml='http://www.opengis.net/gml'>" +
                          "<gml:exterior>" + 
                            "<gml:LinearRing>" + 
                              "<gml:coordinates>1 1 2 1 2 2 1 2 1 1</gml:coordinates>" + 
                            "</gml:LinearRing>" + 
                          "</gml:exterior>" + 
                        "</gml:Polygon>" +
                      "</wps:ComplexData>" + 
                    "</wps:Data>" +     
                "</wps:Input>" + 
                "<wps:Input>" + 
                   "<ows:Identifier>buffer</ows:Identifier>" + 
                   "<wps:Data>" + 
                     "<wps:LiteralData>1</wps:LiteralData>" + 
                   "</wps:Data>" + 
                "</wps:Input>" + 
           "</wps:DataInputs>" +
           "<wps:ResponseForm>" +  
             "<wps:ResponseDocument storeExecuteResponse='false'>" + 
               "<wps:Output>" +
                 "<ows:Identifier>result</ows:Identifier>" +
               "</wps:Output>" + 
             "</wps:ResponseDocument>" +
           "</wps:ResponseForm>" + 
         "</wps:Execute>";
        System.out.println(xml);
        
        Document d = postAsDOM( "wps", xml );
        checkValidationErrors(d);
        
        assertEquals( "wps:ExecuteResponse", d.getDocumentElement().getNodeName() );
        
        assertXpathExists( "/wps:ExecuteResponse/wps:Status/wps:ProcessSucceeded", d);
        assertXpathExists( 
            "/wps:ExecuteResponse/wps:ProcessOutputs/wps:Output/wps:Data/wps:ComplexData/gml:Polygon", d);
    }
    
    public void testDataInlineRawOutput() throws Exception { // Standard Test A.4.4.3
        String xml =  
          "<wps:Execute service='WPS' version='1.0.0' xmlns:wps='http://www.opengis.net/wps/1.0.0' " + 
              "xmlns:ows='http://www.opengis.net/ows/1.1'>" + 
            "<ows:Identifier>gt:buffer</ows:Identifier>" + 
             "<wps:DataInputs>" + 
                "<wps:Input>" + 
                    "<ows:Identifier>geom1</ows:Identifier>" + 
                    "<wps:Data>" +
                      "<wps:ComplexData>" + 
                        "<gml:Polygon xmlns:gml='http://www.opengis.net/gml'>" +
                          "<gml:exterior>" + 
                            "<gml:LinearRing>" + 
                              "<gml:coordinates>1 1 2 1 2 2 1 2 1 1</gml:coordinates>" + 
                            "</gml:LinearRing>" + 
                          "</gml:exterior>" + 
                        "</gml:Polygon>" +
                      "</wps:ComplexData>" + 
                    "</wps:Data>" +     
                "</wps:Input>" + 
                "<wps:Input>" + 
                   "<ows:Identifier>buffer</ows:Identifier>" + 
                   "<wps:Data>" + 
                     "<wps:LiteralData>1</wps:LiteralData>" + 
                   "</wps:Data>" + 
                "</wps:Input>" + 
           "</wps:DataInputs>" +
           "<wps:ResponseForm>" + 
           "    <wps:RawDataOutput>" + 
           "        <ows:Identifier>result</ows:Identifier>" + 
           "    </wps:RawDataOutput>" + 
           "  </wps:ResponseForm>" +
           "</wps:Execute>";
        
        Document d = postAsDOM( "wps", xml );
        checkValidationErrors(d, new GMLConfiguration());
        
        assertEquals( "gml:Polygon", d.getDocumentElement().getNodeName() );
    }
    
    public void testWKTInlineRawOutput() throws Exception { // Standard Test A.4.4.3
        String xml =  
          "<wps:Execute service='WPS' version='1.0.0' xmlns:wps='http://www.opengis.net/wps/1.0.0' " + 
              "xmlns:ows='http://www.opengis.net/ows/1.1'>" + 
            "<ows:Identifier>gt:buffer</ows:Identifier>" + 
             "<wps:DataInputs>" + 
                "<wps:Input>" + 
                    "<ows:Identifier>geom1</ows:Identifier>" + 
                    "<wps:Data>" +
                      "<wps:ComplexData mimeType=\"application/wkt\">" +
                        "<![CDATA[POLYGON((1 1, 2 1, 2 2, 1 2, 1 1))]]>" +
                      "</wps:ComplexData>" + 
                    "</wps:Data>" +     
                "</wps:Input>" + 
                "<wps:Input>" + 
                   "<ows:Identifier>buffer</ows:Identifier>" + 
                   "<wps:Data>" + 
                     "<wps:LiteralData>1</wps:LiteralData>" + 
                   "</wps:Data>" + 
                "</wps:Input>" + 
           "</wps:DataInputs>" +
           "<wps:ResponseForm>" + 
           "    <wps:RawDataOutput mimeType=\"application/wkt\">" + 
           "        <ows:Identifier>result</ows:Identifier>" + 
           "    </wps:RawDataOutput>" + 
           "  </wps:ResponseForm>" +
           "</wps:Execute>";
        
        print(dom(new StringInputStream("<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n" + xml)));
        
        MockHttpServletResponse response = postAsServletResponse( "wps", xml );
        System.out.println(response.getOutputStreamContent());
        assertEquals("application/wkt", response.getContentType());
        Geometry g = new WKTReader().read(response.getOutputStreamContent());
        assertTrue(g instanceof Polygon);
    }


    public void testFeatureCollectionInline() throws Exception { // Standard Test A.4.4.2, A.4.4.4
        String xml = "<wps:Execute service='WPS' version='1.0.0' xmlns:wps='http://www.opengis.net/wps/1.0.0' " + 
              "xmlns:ows='http://www.opengis.net/ows/1.1'>" + 
              "<ows:Identifier>gt:BufferFeatureCollection</ows:Identifier>" + 
               "<wps:DataInputs>" + 
                  "<wps:Input>" + 
                      "<ows:Identifier>features</ows:Identifier>" + 
                      "<wps:Data>" +
                        "<wps:ComplexData>" + 
                             readFileIntoString("states-FeatureCollection.xml") + 
                        "</wps:ComplexData>" + 
                      "</wps:Data>" +     
                  "</wps:Input>" + 
                  "<wps:Input>" + 
                     "<ows:Identifier>buffer</ows:Identifier>" + 
                     "<wps:Data>" + 
                       "<wps:LiteralData>10</wps:LiteralData>" + 
                     "</wps:Data>" + 
                  "</wps:Input>" + 
                 "</wps:DataInputs>" +
                 "<wps:ResponseForm>" +  
                   "<wps:ResponseDocument storeExecuteResponse='false'>" + 
                     "<wps:Output>" +
                       "<ows:Identifier>result</ows:Identifier>" +
                     "</wps:Output>" + 
                   "</wps:ResponseDocument>" +
                 "</wps:ResponseForm>" + 
               "</wps:Execute>";
        
        Document d = postAsDOM( "wps", xml );
        print(d);
        // checkValidationErrors(d);
        
        assertEquals( "wps:ExecuteResponse", d.getDocumentElement().getNodeName() );
        
        assertXpathExists( "/wps:ExecuteResponse/wps:Status/wps:ProcessSucceeded", d);
        assertXpathExists( 
            "/wps:ExecuteResponse/wps:ProcessOutputs/wps:Output/wps:Data/wps:ComplexData/wfs:FeatureCollection", d);
    }
    
    String readFileIntoString( String filename ) throws IOException {
        BufferedReader in = 
            new BufferedReader( new InputStreamReader(getClass().getResourceAsStream( filename ) ) );
        StringBuffer sb = new StringBuffer();
        String line = null;
        while( (line = in.readLine() ) != null ) {
            sb.append( line );
        }
        in.close();
        return sb.toString();
    }
    
    public void testPlainAddition() throws Exception { // Standard Test A.4.4.3
        String xml = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\r\n" + 
            "<wps:Execute service=\"WPS\" version=\"1.0.0\"\r\n" + 
            "        xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\"\r\n" + 
            "        xmlns:xlink=\"http://www.w3.org/1999/xlink\">\r\n" + 
            "        <ows:Identifier>gt:DoubleAddition</ows:Identifier>\r\n" + 
            "        <wps:DataInputs>\r\n" + 
            "                <wps:Input>\r\n" + 
            "                        <ows:Identifier>input_a</ows:Identifier>\r\n" + 
            "                        <wps:Data>\r\n" + 
            "                                <wps:LiteralData>7</wps:LiteralData>\r\n" + 
            "                        </wps:Data>\r\n" + 
            "                </wps:Input>\r\n" + 
            "                <wps:Input>\r\n" + 
            "                        <ows:Identifier>input_b</ows:Identifier>\r\n" + 
            "                        <wps:Data>\r\n" + 
            "                                <wps:LiteralData>7</wps:LiteralData>\r\n" + 
            "                        </wps:Data>\r\n" + 
            "                </wps:Input>\r\n" + 
            "        </wps:DataInputs>\r\n" + 
            "        <wps:ResponseForm>\r\n" + 
            "                <wps:RawDataOutput>\r\n" + 
            "                        <ows:Identifier>result</ows:Identifier>\r\n" + 
            "                </wps:RawDataOutput>\r\n" + 
            "        </wps:ResponseForm>\r\n" + 
            "</wps:Execute>";
        
         MockHttpServletResponse response = postAsServletResponse(root(), xml);
         assertEquals("text/plain", response.getContentType());
         assertEquals("14.0", response.getOutputStreamContent());
    }
	
	/* TODO Updating of Response requests A.4.4.5 */
	
	/* TODO Language selection requests A.4.4.6 */
    
}
