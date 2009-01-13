package org.geoserver.wfs.response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import net.opengis.wfs.FeatureCollectionType;
import net.opengis.wfs.GetFeatureType;

import org.geoserver.data.util.IOUtils;
import org.geoserver.platform.Operation;
import org.geoserver.platform.ServiceException;
import org.geoserver.wfs.WFSException;
import org.geoserver.wfs.WFSGetFeatureOutputFormat;
import org.geotools.feature.FeatureCollection;
import org.geotools.gml.producer.FeatureTransformer;
import org.geotools.gml2.bindings.GML2EncodingUtils;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class Ogr2OgrOutputFormat extends WFSGetFeatureOutputFormat {
    /**
     * The fs path to ogr2ogr. If null, we'll assume ogr2ogr is in the PATH and
     * that we can execute it just by running ogr2ogr
     */
    String ogrPath = null;

    /**
     * The full path to ogr2ogr
     */
    String ogrExecutable = "ogr2ogr";

    /**
     * The output formats we can generate using ogr2ogr. Marking it as
     */
    static Map<String, OgrParameters> formats = new HashMap<String, OgrParameters>();

    public Ogr2OgrOutputFormat() {
        // initialize with the key set of formats, so that it will change as
        // we register new formats
        super(formats.keySet());
    }

    /**
     * Returns the ogr2ogr executable full path
     * 
     * @return
     */
    public String getOgrExecutable() {
        return ogrExecutable;
    }

    /**
     * Sets the ogr2ogr executable full path. The default value is simply
     * "ogr2ogr", which will work if ogr2ogr is in the path
     * 
     * @param ogrExecutable
     */
    public void setOgrExecutable(String ogrExecutable) {
        this.ogrExecutable = ogrExecutable;
    }

    /**
     * @see WFSGetFeatureOutputFormat#getMimeType(Object, Operation)
     */
    public String getMimeType(Object value, Operation operation) throws ServiceException {
        return "application/zip";
    }

    /**
     * Adds a ogr format among the supported ones
     * 
     * @param parameters
     */
    public void addFormat(OgrParameters parameters) {
        formats.put(parameters.formatName, parameters);
    }

    /**
     * Programmatically removes a format
     * 
     * @param parameters
     */
    public void removeFormat(String format) {
        formats.remove(format);
    }

    /**
     * Writes out the data to an OGR known format (GML/shapefile) to disk and
     * then ogr2ogr each generated file into the destination format. Finally,
     * zips up all the resulting files.
     */
    @Override
    protected void write(FeatureCollectionType featureCollection, OutputStream output,
            Operation getFeature) throws IOException, ServiceException {

        // figure out which output format we're going to generate
        GetFeatureType gft = (GetFeatureType) getFeature.getParameters()[0];
        OgrParameters format = formats.get(gft.getOutputFormat());
        if (format == null)
            throw new WFSException("Unknown output format " + gft.getOutputFormat());

        // create the first temp directory, used for dumping gs generated
        // content
        File tempGS = org.geoserver.data.util.IOUtils.createTempDirectory("ogrtmpin");
        File tempOGR = org.geoserver.data.util.IOUtils.createTempDirectory("ogrtmpout");

        // build the ogr wrapper used to run the ogr2ogr commands
        OGRWrapper wrapper = new OGRWrapper(tempOGR, format, ogrExecutable);

        // actually export each feature collection
        try {
            Iterator outputFeatureCollections = featureCollection.getFeature().iterator();
            FeatureCollection<SimpleFeatureType, SimpleFeature> curCollection;

            while (outputFeatureCollections.hasNext()) {
                curCollection = (FeatureCollection<SimpleFeatureType, SimpleFeature>) outputFeatureCollections
                        .next();

                // if(curCollection.getSchema().getGeometryDescriptor() == null)
                // {
                // throw new
                // WFSException("Cannot write geometryless shapefiles, yet "
                // + curCollection.getSchema() + " has no geometry field");
                // }

                // write out the gml
                File gml = writeGML(tempGS, curCollection);

                // convert with ogr2ogr
                String epsgCode = null;
                final SimpleFeatureType schema = curCollection.getSchema();
                final CoordinateReferenceSystem crs = schema.getCoordinateReferenceSystem();
                if (crs != null)
                    epsgCode = "EPSG:" + GML2EncodingUtils.epsgCode(crs);
                wrapper.convert(gml, schema.getTypeName(), epsgCode);

                // wipe out the input dir contents
                IOUtils.emptyDirectory(tempGS);
            }

            // scan the output directory and zip it all
            ZipOutputStream zipOut = new ZipOutputStream(output);
            IOUtils.zipDirectory(tempOGR, zipOut, null);

            // delete the input and output directories
            IOUtils.delete(tempGS);
            IOUtils.delete(tempOGR);
        } catch (Exception e) {
            throw new ServiceException("Exception occurred during output generation", e);
        }
    }

    private File writeGML(File tempDir,
            FeatureCollection<SimpleFeatureType, SimpleFeature> curCollection) throws Exception {
        // // we want to write out just one gml file
        // WfsFactory fac = WfsFactory.eINSTANCE;
        // FeatureCollectionType fct = fac.createFeatureCollectionType();
        // fct.getFeature().add(curCollection);
        //        
        // // create the temp file for this output
        // File outFile = new File(tempDir,
        // curCollection.getSchema().getTypeName() + ".gml");
        //
        // // write out
        // OutputStream os = null;
        // try {
        // os = new FileOutputStream(outFile);
        //            
        // // let's invoke the
        // gmlof.write(fct, os, null);
        // } finally {
        // os.close();
        // }
        //        
        // return outFile;

        // create the temp file for this output
        File outFile = new File(tempDir, curCollection.getSchema().getTypeName() + ".gml");

        // write out
        OutputStream os = null;
        try {
            os = new FileOutputStream(outFile);

            // let's invoke the transformer
            FeatureTransformer ft = new FeatureTransformer();
            ft.setNumDecimals(16);
            ft.setNamespaceDeclarationEnabled(false);
            ft.getFeatureNamespaces().declarePrefix("topp",
                    curCollection.getSchema().getName().getNamespaceURI());
            ft.transform(curCollection, os);
        } finally {
            os.close();
        }

        return outFile;
    }

}