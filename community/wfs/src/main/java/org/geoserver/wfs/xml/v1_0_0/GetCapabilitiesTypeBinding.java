package org.geoserver.wfs.xml.v1_0_0;


import org.geotools.xml.*;

import net.opengis.ows.v1_0_0.OWSFactory;
import net.opengis.wfs.GetCapabilitiesType;
import net.opengis.wfs.WFSFactory;		

import javax.xml.namespace.QName;

/**
 * Binding object for the type http://www.opengis.net/wfs:GetCapabilitiesType.
 *
 * <p>
 *	<pre>
 *	 <code>
 *  &lt;xsd:complexType name="GetCapabilitiesType"&gt;       &lt;xsd:annotation&gt;
 *              &lt;xsd:documentation&gt;             This type defines the
 *              GetCapabilities operation.  In response             to a
 *              GetCapabilities request, a Web Feature Service must
 *              generate a capabilities XML document that validates against
 *              the schemas defined in WFS-capabilities.xsd.
 *          &lt;/xsd:documentation&gt;       &lt;/xsd:annotation&gt;
 *          &lt;xsd:attribute fixed="1.0.0" name="version" type="xsd:string"
 *      use="optional"/&gt;       &lt;xsd:attribute fixed="WFS" name="service"
 *          type="xsd:string" use="required"/&gt;    &lt;/xsd:complexType&gt; 
 *		
 *	  </code>
 *	 </pre>
 * </p>
 *
 * @generated
 */
public class GetCapabilitiesTypeBinding extends AbstractComplexBinding {

	/**
	 * Wfs factory
	 */
	WFSFactory wfsFactory;
	/**
	 * Ows Factory
	 */
	OWSFactory owsFactory;
	
	public GetCapabilitiesTypeBinding( WFSFactory wfsFactory, OWSFactory owsFactory ) {
		this.wfsFactory = wfsFactory;
		this.owsFactory = owsFactory;
	}

	/**
	 * @generated
	 */
	public QName getTarget() {
		return WFS.GETCAPABILITIESTYPE;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *	
	 * @generated modifiable
	 */	
	public Class getType() {
		return GetCapabilitiesType.class;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *	
	 * @generated modifiable
	 */	
	public Object parse(ElementInstance instance, Node node, Object value) 
		throws Exception {
		
		GetCapabilitiesType getCapabilities = wfsFactory.createGetCapabilitiesType();
		getCapabilities.setAcceptVersions( owsFactory.createAcceptVersionsType() );
		
		WFSBindingUtils.service( getCapabilities, node );
		
		getCapabilities.getAcceptVersions().getVersion().add( node.getAttributeValue( "version" ) );
		
		
		return getCapabilities;
	}

}