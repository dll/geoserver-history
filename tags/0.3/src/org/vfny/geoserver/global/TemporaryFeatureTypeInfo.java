package org.vfny.geoserver.global;

/**
 *  This is a very simple class that lets you wrap a temporary datastore into a FeatureTypeInfo.
 *   This is being used by the UserLayer#InlineFeature, and will probably be used by the 
 *   UserLayer#OWS (remote WFS).
 *  
 *   Currently the only thing that you need to do for this is #getFeatureSource().
 * 
 *   We throw errors for everything else so people dont screw up!
 * 
 */

import java.io.File;
import java.io.IOException;

import java.util.List;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;

import org.geotools.feature.FeatureType;

import org.geotools.filter.Filter;

import org.geotools.styling.Style;

import org.opengis.feature.Query;
import org.vfny.geoserver.global.dto.FeatureTypeInfoDTO;

import org.w3c.dom.Element;

import com.vividsolutions.jts.geom.Envelope;

public class TemporaryFeatureTypeInfo extends FeatureTypeInfo
{

	/**
	 * 
	 * @uml.property name="ds"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private DataStore ds;

	/**
	 * 
	 * @uml.property name="ft"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private FeatureType ft;

    /**
     * 
     * @param ds
     * @param ft
     */
    public TemporaryFeatureTypeInfo(DataStore ds,FeatureType ft)
	{
    	super();
    	this.ds = ds;
    	this.ft = ft;    	
    }

    public FeatureSource getFeatureSource() throws IOException 
	{
    	return ds.getFeatureSource(ft.getTypeName());
    }
    
    public Filter getDefinitionQuery() {
    	return null;
    }
    
    
    Object toDTO() 
    {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

    
    public int getNumDecimals() {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

    
    public DataStoreInfo getDataStoreInfo() {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

   
    public Style getDefaultStyle(){
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }
    
    
    public boolean isEnabled() {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

   
    public String getPrefix() {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

   
    public NameSpaceInfo getNameSpace() {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }
    
  
    public String getName() {        
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }    

   
    

  
    public Envelope getBoundingBox() throws IOException {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

    
   

  
    public Envelope getLatLongBoundingBox() throws IOException {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

    
    public String getSRS() {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

    
    private synchronized FeatureTypeInfoDTO getGeneratedDTO()
        throws IOException {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

   
    protected String getAttribute(Element elem, String attName,
        boolean mandatory) throws ConfigurationException {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

    private static Envelope getLatLongBBox(String fromSrId, Envelope bbox) {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

   
    public String getAbstract() {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

    
    public List getKeywords() {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

  
    public String getTitle() {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

   
    public String getSchemaName() {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

  
    public void setSchemaName(String string) {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

   
    public String getSchemaBase() {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

   
    public void setSchemaBase(String string) {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

    
    public String getTypeName() {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

    
    public FeatureType getFeatureType() throws IOException {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

   
    private FeatureType getFeatureType(FeatureSource fs)
        throws IOException {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

    
    public DataStoreInfo getDataStoreMetaData() {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

   
    public List getAttributeNames() {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }



    public List getAttributes() {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

   
    public synchronized AttributeTypeInfo AttributeTypeMetaData(
        String attributeName) {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }

   
    public boolean containsMetaData(String key) {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }



    public void putMetaData(String key, Object value) {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }



    public Object getMetaData(String key) {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }
    


    public LegendURL getLegendURL() {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }        


    public File getSchemaFile() {
    	throw new IllegalArgumentException("TemporaryFeatureTypeInfo - not supported");
    }
}