package org.getoserver.wfsv;

import org.w3c.dom.Document;
import static org.custommonkey.xmlunit.XMLAssert.*;

public class HistoryRelatedTests extends WFSVTestSupport {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // build some history the other tests will use
        String transaction = //
        "<wfs:Transaction service=\"WFSV\" version=\"1.0.0\"\r\n"
                + "  xmlns:topp=\"http://www.openplans.org/topp\"\r\n"
                + "  xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:wfs=\"http://www.opengis.net/wfs\"\r\n"
                + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
                + "  xmlns:gml=\"http://www.opengis.net/gml\"\r\n"
                + "  xsi:schemaLocation=\"http://www.opengis.net/wfs\r\n"
                + "    http://schemas.opengis.net/wfs/1.0.0/WFS-transaction.xsd\r\n"
                + "    http://www.openplans.org/topp\r\n"
                + "    http://localhost:8080/geoserver/wfsv?request=DescribeFeatureType&amp;version=1.0.0&amp;typeName=topp:archsites\"\r\n"
                + "  handle=\"Inserting, updating and deleting\">\r\n"
                + "  <wfs:Insert>\r\n"
                + "    <topp:archsites>\r\n"
                + "      <topp:cat>2</topp:cat>\r\n"
                + "      <topp:str1>Alien crash site</topp:str1>\r\n"
                + "      <topp:the_geom>\r\n"
                + "        <gml:Point srsName=\"http://www.opengis.net/gml/srs/epsg.xml#26713\">\r\n"
                + "          <gml:coordinates decimal=\".\" cs=\",\" ts=\" \">604000,4930000</gml:coordinates>\r\n"
                + "        </gml:Point>\r\n" //
                + "      </topp:the_geom>\r\n"
                + "    </topp:archsites>\r\n" // 
                + "  </wfs:Insert>\r\n"
                + "  <wfs:Update typeName=\"topp:archsites\">\r\n" // 
                + "    <wfs:Property>\r\n" + "      <wfs:Name>str1</wfs:Name>\r\n"
                + "      <wfs:Value>Signature Rock, updated</wfs:Value>\r\n"
                + "    </wfs:Property>\r\n" // 
                + "    <ogc:Filter>\r\n" // 
                + "      <ogc:FeatureId fid=\"archsites.1\" />\r\n" // 
                + "    </ogc:Filter>\r\n" // 
                + "  </wfs:Update>\r\n" // 
                + "  <wfs:Delete typeName=\"topp:archsites\">\r\n" // 
                + "    <ogc:Filter>\r\n" // 
                + "      <ogc:FeatureId fid=\"archsites.2\" />\r\n"// 
                + "    </ogc:Filter>\r\n" // 
                + "  </wfs:Delete>\r\n" + "</wfs:Transaction>\r\n";
        Document doc = postAsDOM(root(), transaction);

        // let's just ensure the transaction was successful
        assertXpathEvaluatesTo("1", "count(/wfs:WFS_TransactionResponse)", doc);
        assertXpathEvaluatesTo("archsites.5",
                "/wfs:WFS_TransactionResponse/wfs:InsertResult/ogc:FeatureId/@fid", doc);
        assertXpathEvaluatesTo("1",
                "count(/wfs:WFS_TransactionResponse/wfs:TransactionResult/wfs:Status/wfs:SUCCESS)",
                doc);
    }

    public void testGetFeatureBeforeAfter() throws Exception {
        // ask the old state, make sure the updates do not appear
        String before = "<wfs:GetFeature service=\"WFSV\" version=\"1.0.0\"\r\n"
                + "  outputFormat=\"GML2\"\r\n"
                + "  xmlns:topp=\"http://www.openplans.org/topp\"\r\n"
                + "  xmlns:wfs=\"http://www.opengis.net/wfs\"\r\n"
                + "  xmlns:ogc=\"http://www.opengis.net/ogc\"\r\n"
                + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
                + "  xsi:schemaLocation=\"http://www.opengis.net/wfs\r\n"
                + "                      http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd\">\r\n"
                + "  <wfs:Query typeName=\"topp:archsites\" featureVersion=\"1\"/>\r\n"
                + "</wfs:GetFeature>\r\n";
        Document doc = postAsDOM(root(), before);
        assertXpathEvaluatesTo("4", "count(/wfs:FeatureCollection/gml:featureMember)", doc);
        assertXpathEvaluatesTo("Signature Rock", "//topp:archsites[@fid=\"archsites.1\"]/topp:str1", doc);
        assertXpathEvaluatesTo("1", "count(//topp:archsites[@fid=\"archsites.2\"])", doc);
        assertXpathEvaluatesTo("0", "count(//topp:archsites[@fid=\"archsites.5\"])", doc);

        // ask the current state, make sure the updates do show
        String current = "<wfs:GetFeature service=\"WFSV\" version=\"1.0.0\"\r\n"
                + "  outputFormat=\"GML2\"\r\n"
                + "  xmlns:topp=\"http://www.openplans.org/topp\"\r\n"
                + "  xmlns:wfs=\"http://www.opengis.net/wfs\"\r\n"
                + "  xmlns:ogc=\"http://www.opengis.net/ogc\"\r\n"
                + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
                + "  xsi:schemaLocation=\"http://www.opengis.net/wfs\r\n"
                + "                      http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd\">\r\n"
                + "  <wfs:Query typeName=\"topp:archsites\"/>\r\n" + "</wfs:GetFeature>\r\n";
        doc = postAsDOM(root(), current);
        assertXpathEvaluatesTo("4", "count(/wfs:FeatureCollection/gml:featureMember)", doc);
        assertXpathEvaluatesTo("Signature Rock, updated", "//topp:archsites[@fid=\"archsites.1\"]/topp:str1", doc);
        assertXpathEvaluatesTo("0", "count(//topp:archsites[@fid=\"archsites.2\"])", doc);
        assertXpathEvaluatesTo("1", "count(//topp:archsites[@fid=\"archsites.5\"])", doc);
    }
    
    public void testVersionedFeatureCollection() throws Exception {
        String request = "<wfsv:GetVersionedFeature service=\"WFSV\" version=\"1.0.0\"\r\n" + 
        		"  outputFormat=\"GML2\"\r\n" + 
        		"  xmlns:topp=\"http://www.openplans.org/topp\"\r\n" + 
        		"  xmlns:wfs=\"http://www.opengis.net/wfs\"\r\n" + 
        		"  xmlns:wfsv=\"http://www.opengis.net/wfsv\"\r\n" + 
        		"  xmlns:ogc=\"http://www.opengis.net/ogc\"\r\n" + 
        		"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + 
        		"  xsi:schemaLocation=\"http://www.opengis.net/wfsv\r\n" + 
        		"                      http://localhost:8080/geoserver/schemas/wfs/1.0.0/WFS-versioninig.xsd\">\r\n" + 
        		"  <wfs:Query typeName=\"topp:archsites\">\r\n" + 
        		"    <ogc:Filter>\r\n" + 
        		"       <ogc:FeatureId fid=\"archsites.5\"/>\r\n" + 
        		"    </ogc:Filter>\r\n" + 
        		"  </wfs:Query>\r\n" + 
        		"</wfsv:GetVersionedFeature>";
        Document doc = postAsDOM(root(), request);
        assertXpathEvaluatesTo("1", "count(/wfs:FeatureCollection/gml:featureMember)", doc);
        assertXpathEvaluatesTo("1", "count(//topp:archsites[@fid=\"archsites.5\"])", doc);
        assertXpathEvaluatesTo("1", "count(//topp:createdBy)", doc);
        assertXpathEvaluatesTo("1", "count(//topp:creationDate)", doc);
        assertXpathEvaluatesTo("1", "count(//topp:creationMessage)", doc);
        assertXpathEvaluatesTo("1", "count(//topp:lastUpdateVersion)", doc);
        assertXpathEvaluatesTo("1", "count(//topp:lastUpdatedBy)", doc);
        assertXpathEvaluatesTo("1", "count(//topp:lastUpdateDate)", doc);
        assertXpathEvaluatesTo("1", "count(//topp:lastUpdateMessage)", doc);
        assertXpathEvaluatesTo("anonymous", "//topp:archsites[@fid=\"archsites.5\"]/topp:createdBy", doc);
        assertXpathEvaluatesTo("Inserting, updating and deleting", "//topp:archsites[@fid=\"archsites.5\"]/topp:lastUpdateMessage", doc);
    }
}
