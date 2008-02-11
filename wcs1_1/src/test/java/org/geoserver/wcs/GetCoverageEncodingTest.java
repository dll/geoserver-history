package org.geoserver.wcs;

import static org.custommonkey.xmlunit.XMLAssert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.geoserver.wcs.test.WCSTestSupport;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.DataSourceException;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.w3c.dom.Document;

import com.mockrunner.mock.web.MockHttpServletResponse;

public class GetCoverageEncodingTest extends WCSTestSupport {

    // @Override
    // protected String getDefaultLogConfiguration() {
    // return "/DEFAULT_LOGGING.properties";
    // }

    public void testKvpBasic() throws Exception {
        String request = "wcs?service=WCS&version=1.1.1&request=GetCoverage" + "&identifier="
                + layerId(WCSTestSupport.TASMANIA_BM)
                + "&BoundingBox=-90,-180,90,180,urn:ogc:def:crs:EPSG:4326"
                + "&GridBaseCRS=urn:ogc:def:crs:EPSG:4326" + "&format=geotiff";
        MockHttpServletResponse response = getAsServletResponse(request);
        // System.out.println(response.getOutputStreamContent());
        // make sure we got a multipart
        assertTrue(response.getContentType().matches("multipart/related;\\s*boundary=\".*\""));

        // parse the multipart, check there are two parts
        Multipart multipart = getMultipart(response);
        assertEquals(2, multipart.getCount());

        // now check the first part is a proper description
        BodyPart coveragesPart = multipart.getBodyPart(0);
        assertEquals("text/xml", coveragesPart.getContentType());
        System.out.println("Coverages part: " + coveragesPart.getContent());
        assertEquals("<urn:ogc:wcs:1.1:coverages>", coveragesPart.getHeader("Content-ID")[0]);
        // read the xml document into a dom
        Document dom = dom(coveragesPart.getDataHandler().getInputStream());
        checkValidationErrors(dom, WCS11_SCHEMA);
        assertXpathEvaluatesTo(WCSTestSupport.TASMANIA_BM.getLocalPart(),
                "wcs:Coverages/wcs:Coverage/ows:Title", dom);

        // the second part is the actual coverage
        BodyPart coveragePart = multipart.getBodyPart(1);
        assertEquals("image/tiff", coveragePart.getContentType());
        assertEquals("<theCoverage>", coveragePart.getHeader("Content-ID")[0]);

        // make sure we can read the coverage back
        InputStream is = coveragePart.getDataHandler().getInputStream();
        readCoverage(is);
    }

    private Multipart getMultipart(MockHttpServletResponse response) throws MessagingException,
            IOException {
        String content = response.getOutputStreamContent();
        MimeMessage body = new MimeMessage((Session) null, new ByteArrayInputStream(content
                .getBytes()));
        Multipart multipart = (Multipart) body.getContent();
        return multipart;
    }

    private GridCoverage2D readCoverage(InputStream is) throws IOException, FileNotFoundException,
            DataSourceException {
        // for some funny reason reading directly from the input stream does not
        // work we have to create a temp file instead
        File f = storeToTempFile(is, ".tiff");
        GeoTiffReader reader = new GeoTiffReader(f);
        GridCoverage2D coverage = (GridCoverage2D) reader.read(null);
        reader.dispose();
        return coverage;
    }

    private File storeToTempFile(InputStream is, String extension) throws IOException,
            FileNotFoundException {
        File f = File.createTempFile("coverage", extension, dataDirectory.getDataDirectoryRoot());
        FileOutputStream fos = new FileOutputStream(f);
        byte[] buffer = new byte[4096];
        int read = 0;
        while ((read = is.read(buffer)) > 0)
            fos.write(buffer, 0, read);
        fos.close();
        return f;
    }

    public void testTiffOutput() throws Exception {
        String request = "wcs?service=WCS&version=1.1.1&request=GetCoverage" + "&identifier="
                + layerId(WCSTestSupport.TASMANIA_BM)
                + "&BoundingBox=-90,-180,90,180,urn:ogc:def:crs:EPSG:4326"
                + "&GridBaseCRS=urn:ogc:def:crs:EPSG:4326" + "&format=image/tiff";
        MockHttpServletResponse response = getAsServletResponse(request);

        // parse the multipart, check there are two parts
        Multipart multipart = getMultipart(response);
        assertEquals(2, multipart.getCount());
        BodyPart coveragePart = multipart.getBodyPart(1);
        assertEquals("image/tiff", coveragePart.getContentType());
        assertEquals("<theCoverage>", coveragePart.getHeader("Content-ID")[0]);

        // make sure we can read the coverage back
        InputStream is = coveragePart.getDataHandler().getInputStream();
        File temp = storeToTempFile(is, ".tiff");
        ImageReader reader = ImageIO.getImageReadersByFormatName("tiff").next();
        reader.setInput(ImageIO.createImageInputStream(temp));
        reader.read(0);
    }

    public void testPngOutput() throws Exception {
        String request = "wcs?service=WCS&version=1.1.1&request=GetCoverage" + "&identifier="
                + layerId(WCSTestSupport.TASMANIA_BM)
                + "&BoundingBox=-90,-180,90,180,urn:ogc:def:crs:EPSG:4326"
                + "&GridBaseCRS=urn:ogc:def:crs:EPSG:4326" + "&format=image/png";
        MockHttpServletResponse response = getAsServletResponse(request);

        // parse the multipart, check there are two parts
        Multipart multipart = getMultipart(response);
        assertEquals(2, multipart.getCount());
        BodyPart coveragePart = multipart.getBodyPart(1);
        assertEquals("image/png", coveragePart.getContentType());
        assertEquals("<theCoverage>", coveragePart.getHeader("Content-ID")[0]);

        // make sure we can read the coverage back
        InputStream is = coveragePart.getDataHandler().getInputStream();
        File temp = storeToTempFile(is, ".png");
        ImageReader reader = ImageIO.getImageReadersByFormatName("png").next();
        reader.setInput(ImageIO.createImageInputStream(temp));
        reader.read(0);
    }

    public void testGeotiffNamesGalore() throws Exception {
        String requestBase = "wcs?service=WCS&version=1.1.1&request=GetCoverage" + "&identifier="
                + layerId(WCSTestSupport.TASMANIA_BM)
                + "&BoundingBox=-90,-180,90,180,urn:ogc:def:crs:EPSG:4326"
                + "&GridBaseCRS=urn:ogc:def:crs:EPSG:4326";
        ensureTiffFormat(getAsServletResponse(requestBase + "&format=geotiff"));
        ensureTiffFormat(getAsServletResponse(requestBase + "&format=geotiff"));
        ensureTiffFormat(getAsServletResponse(requestBase + "&format=image/geotiff"));
        ensureTiffFormat(getAsServletResponse(requestBase + "&format=GEotIFF"));
        ensureTiffFormat(getAsServletResponse(requestBase
                + "&format=image/tiff;subtype%3D\"geotiff\""));
    }

    private void ensureTiffFormat(MockHttpServletResponse response) throws MessagingException,
            IOException {
        // make sure we got a multipart
        assertTrue("Content type not mulipart but " + response.getContentType(), response
                .getContentType().matches("multipart/related;\\s*boundary=\".*\""));

        // parse the multipart, check the second part is a geotiff
        Multipart multipart = getMultipart(response);
        BodyPart coveragePart = multipart.getBodyPart(1);
        assertEquals("image/tiff", coveragePart.getContentType());
    }
}
