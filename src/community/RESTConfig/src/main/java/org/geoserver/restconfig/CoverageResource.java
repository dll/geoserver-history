/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.geoserver.restconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.geoserver.catalog.impl.MetadataLinkInfoImpl;
import org.geoserver.config.GeoServer;
import org.geoserver.rest.MapResource;
import org.geoserver.rest.format.DataFormat;
import org.geoserver.rest.format.FreemarkerFormat;
import org.geoserver.rest.format.MapJSONFormat;
import org.geoserver.rest.format.MapXMLFormat;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.referencing.CRS;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;
import org.springframework.context.ApplicationContext;
import org.vfny.geoserver.config.CoverageConfig;
import org.vfny.geoserver.config.DataConfig;
import org.vfny.geoserver.global.ConfigurationException;
import org.vfny.geoserver.global.Data;
import org.vfny.geoserver.global.GeoserverDataDirectory;
import org.vfny.geoserver.global.MetaDataLink;
import org.vfny.geoserver.global.dto.DataDTO;
import org.vfny.geoserver.global.xml.XMLConfigWriter;


/**
 * Restlet for Style resources
 *
 * @author $Author: Tobia Di Pisa (tobia.dipisa@geo-solutions.it) $ (last modification)
 * @author $Author: Alessio Fabiani (alessio.fabiani@geo-solutions.it) $ (last
 */
public class CoverageResource extends MapResource {
    private DataConfig myDC;
    private Data myData;

    public CoverageResource(Context context, Request request, Response response){
        super(context,request,response);
    }

    public CoverageResource(Data d, DataConfig dc, Context context, Request request, Response response){
        super(context,request,response);
        setData(d);
        setDataConfig(dc);
    }

    @Override
    protected Map<String, DataFormat> createSupportedFormats(Request request,
            Response response) {
        Map m = new HashMap();
        m.put("html",
            new FreemarkerFormat("HTMLTemplates/coverage.ftl", getClass(), MediaType.TEXT_HTML) {
                public Map readRepresentation(Representation rep) {
                    try {
                        SAXBuilder builder = new SAXBuilder();
                        Document doc = builder.build(rep.getStream());
                        Map map = new HashMap();

                        // TODO: Why does this stop working when I specify that the node has to be a span?
                        map.put("Label", getText("//*[@class='label']", doc));
                        map.put("Description", getText("//*[@class='description']", doc));
                        map.put("Keywords", getList("//*[@class='keyword']", doc));
                        map.put("DefaultStyle", getText("//*[@class='default-style']", doc));
                        map.put("SupplementaryStyles",
                            getList("//*[@class='supplementary-style']", doc));
                        map.put("OnlineResource", getText("//*[@class='online-resource']", doc));
                        map.put("CRS", getText("//*[@class='crs']", doc));
                        map.put("SupportedRequestCRSs",
                            getList("//*[@class='supported-request-crs']", doc));
                        map.put("SupportedResponseCRSs",
                            getList("//*[@class='supported-response-crs']", doc));
                        // TODO: Special case for Envelope
                        map.put("NativeFormat", getText("//*[@class='native-format']", doc));
                        map.put("SupportedFormats", getList("//*[@class='supported-format']", doc));
                        map.put("DefaultInterpolationMethod",
                            getText("//*[@class='default-interpolation']", doc));
                        map.put("InterpolationMethods",
                            getList("//*[@class='supported-interpolation']", doc));

                        return map;
                    } catch (Exception e) {

                        return new HashMap();
                    }
                }

                /**
                 * @param xpath the XPath to retrieve
                 * @param doc the Document from which to retrieve it
                 * @return the text body of the first matched element, or
                 *     null if none is matched
                 */
                private String getText(String xpath, Document doc)
                    throws JDOMException {
                    XPath path = XPath.newInstance(xpath);
                    Element elem = (Element) path.selectSingleNode(doc);

                    return ((elem == null) ? null : elem.getText());
                }

                /**
                 * @param xpath the XPath to retrieve
                 * @param doc the Document from which to retrieve it
                 * @return a list of all the text nodes underneath the nodes matched by the XPath
                 */
                private List getList(String xpath, Document doc)
                    throws JDOMException {
                    XPath path = XPath.newInstance(xpath);
                    List elems = (List) path.selectNodes(doc);
                    List results = new ArrayList();

                    Iterator it = elems.iterator();

                    while (it.hasNext()) {
                        Element elem = (Element) it.next();
                        String text = elem.getText();
                        results.add(text);
                    }

                    return results;
                }
            });
        m.put("json", new MapJSONFormat());
        m.put("xml", new MapXMLFormat("coverage"));
        m.put(null, m.get("html"));

        return m;
    }

    public CoverageResource(Context context, Request request, Response response,
        ApplicationContext appCon) {
        super(context, request, response);
        myDC = (DataConfig) appCon.getBean("dataConfig");
        myData = (Data) appCon.getBean("data");
    }

    public void setDataConfig(DataConfig dc){
        myDC = dc;
    }

    public DataConfig getDataConfig(){
        return myDC;
    }

    public void setData(Data d){
        myData = d;
    }

    public Data getData(){
        return myData;
    }

    public Map getMap() {
//        String coverageStore = (String) getRequest().getAttributes().get("folder");
//        String coverageName = (String) getRequest().getAttributes().get("layer");
//        String qualified = coverageStore + ":" + coverageName;
//        CoverageConfig cc = (CoverageConfig) myDC.getCoverages().get(qualified);
        Map coverages = myDC.getCoverages();

        String coverageStoreName = (String)getRequest().getAttributes().get("layer");
        CoverageConfig cc = null;
        
        Iterator it = coverages.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String key = (String)entry.getKey();
            if (key.startsWith(coverageStoreName)){
                cc = (CoverageConfig) entry.getValue();
            }
        }
        
        return getMap(cc);
    }

    public boolean allowPut() {
        return true;
    }

    public void putMap(Map details) throws Exception {
        //TODO: create the coverage
        String coverageStore = (String) getRequest().getAttributes().get("folder");
        String coverageName = (String) getRequest().getAttributes().get("layer");
        String qualified = coverageStore + ":" + coverageName;
        CoverageConfig cc = null;
        cc = (CoverageConfig) myDC.getCoverages().get(qualified);

        cc.setWmsPath((String) details.get("WMSPath"));

        GeneralEnvelope env = listAsEnvelope((List) details.get("Envelope"), (String)details.get("CRS"));
        env.setCoordinateReferenceSystem(CRS.decode((String)details.get("CRS")));
        cc.setEnvelope(env);
        cc.setDefaultStyle((String) details.get("DefaultStyle"));
        
        ArrayList styles = new ArrayList();
        if (details.get("SupplementaryStyles") != null){
            styles.addAll((List) details.get("SupplementaryStyles"));
        }
        cc.setStyles(styles);

        cc.setLabel((String) details.get("Label"));
        cc.setDescription((String) details.get("Description"));
        cc.setMetadataLink(makeLink((String) details.get("OnlineResource")));
        cc.setKeywords((List) details.get("Keywords"));
        cc.setRequestCRSs((List) details.get("SupportedRequestCRSs"));
        cc.setResponseCRSs((List) details.get("SupportedResponseCRSs"));
        cc.setNativeFormat((String) details.get("NativeFormat"));
        cc.setSupportedFormats((List) details.get("SupportedFormats"));
        cc.setDefaultInterpolationMethod((String) details.get("DefaultInterpolationMethod"));
        cc.setInterpolationMethods((List) details.get("InterpolationMethods"));

        myDC.addCoverage(qualified, cc);
        saveConfiguration();

        getResponse().setEntity(new StringRepresentation("Saved configuration for " + qualified + ".", MediaType.TEXT_PLAIN));
        getResponse().setStatus(Status.SUCCESS_OK);
    }

    private MetaDataLink makeLink(String s) {
        MetadataLinkInfoImpl linkInfo = new MetadataLinkInfoImpl();
        linkInfo.setAbout(s);
        linkInfo.setType("other");
        return new MetaDataLink(linkInfo);
    }

    private GeneralEnvelope listAsEnvelope(List coords, String srsName) throws Exception{
        GeneralEnvelope genv = new GeneralEnvelope(CRS.decode(srsName));
        genv.setRange(0,
                Double.valueOf((String)coords.get(0)),
                Double.valueOf((String)coords.get(1))
                );
        genv.setRange(1, 
                Double.valueOf((String)coords.get(2)),
                Double.valueOf((String)coords.get(3))
                );
        return genv;
    }

    public static Map getMap(CoverageConfig cc) {
        Map m = new HashMap();
        m.put("Name", cc.getName());
        m.put("WMSPath", cc.getWmsPath());

        GeneralEnvelope env = cc.getEnvelope();
        List envPoints = new ArrayList();
            envPoints.add(env.getLowerCorner().getOrdinate(0));
            envPoints.add(env.getLowerCorner().getOrdinate(1));
            envPoints.add(env.getUpperCorner().getOrdinate(0));
            envPoints.add(env.getUpperCorner().getOrdinate(1));
        m.put("Envelope", envPoints);
        m.put("CRS", env.getCoordinateReferenceSystem().getIdentifiers().toArray()[0].toString());
        m.put("DefaultStyle", cc.getDefaultStyle());
        m.put("SupplementaryStyles", cc.getStyles()); // TODO: does this return a list of strings or something else?
        m.put("Label", cc.getLabel());
        m.put("Description", cc.getDescription());
        m.put("OnlineResource", "" /*cc.getMetadataLink().getAbout()*/); // TODO: get the actual URL, this may take some digging
        m.put("Keywords", cc.getKeywords());
        m.put("SupportedRequestCRSs", cc.getRequestCRSs());
        m.put("SupportedResponseCRSs", cc.getResponseCRSs());
        m.put("NativeFormat", cc.getNativeFormat());
        m.put("SupportedFormats", cc.getSupportedFormats());
        m.put("DefaultInterpolationMethod", cc.getDefaultInterpolationMethod());
        m.put("InterpolationMethods", cc.getInterpolationMethods());

        return m;
    }

    private void saveConfiguration() throws ConfigurationException {
        synchronized (GeoServer.CONFIGURATION_LOCK) {
            getData().load(getDataConfig().toDTO());
            XMLConfigWriter.store((DataDTO)getData().toDTO(),
                    GeoserverDataDirectory.getGeoserverDataDirectory()
                    );
        }
    }

}
