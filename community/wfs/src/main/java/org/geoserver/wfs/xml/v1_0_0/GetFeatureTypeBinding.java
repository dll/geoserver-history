package org.geoserver.wfs.xml.v1_0_0;


import java.math.BigInteger;

import org.geotools.xml.*;

import net.opengis.wfs.GetFeatureType;
import net.opengis.wfs.QueryType;
import net.opengis.wfs.WFSFactory;		

import javax.xml.namespace.QName;

/**
 * Binding object for the type http://www.opengis.net/wfs:GetFeatureType.
 *
 * <p>
 *	<pre>
 *	 <code>
 *  &lt;xsd:complexType name="GetFeatureType"&gt;       &lt;xsd:annotation&gt;
 *              &lt;xsd:documentation&gt;             A GetFeature element
 *              contains one or more Query elements             that
 *              describe a query operation on one feature type.  In
 *              response to a GetFeature request, a Web Feature Service
 *              must be able to generate a GML2 response that validates
 *              using a schema generated by the DescribeFeatureType request.
 *              A Web Feature Service may support other possibly non-XML
 *              (and even binary) output formats as long as those formats
 *              are advertised in the capabilities document.
 *          &lt;/xsd:documentation&gt;       &lt;/xsd:annotation&gt;
 *          &lt;xsd:sequence&gt;          &lt;xsd:element maxOccurs="unbounded"
 *          ref="wfs:Query"/&gt;       &lt;/xsd:sequence&gt;       &lt;xsd:attribute
 *          fixed="1.0.0" name="version" type="xsd:string" use="required"/&gt;
 *          &lt;xsd:attribute fixed="WFS" name="service" type="xsd:string"
 *      use="required"/&gt;       &lt;xsd:attribute name="handle"
 *          type="xsd:string" use="optional"/&gt;       &lt;xsd:attribute
 *          default="GML2" name="outputFormat" type="xsd:string"
 *          use="optional"&gt;          &lt;xsd:annotation&gt;
 *                  &lt;xsd:documentation&gt;                The outputFormat
 *                  attribute is used to specify the output
 *                  format that the Web Feature Service should generate in
 *                  response to a GetFeature or GetFeatureWithLock element.
 *                  The default value of GML2 indicates that the output is
 *                  an                XML document that conforms to the
 *                  Geography Markup Language                (GML)
 *                  Implementation Specification V2.0.                 Other
 *                  values may be used to specify other formats as long
 *                  as those values are advertised in the capabilities
 *                  document.                For example, the value WKB may
 *                  be used to indicate that a                 Well Known
 *                  Binary format be used to encode the output.
 *              &lt;/xsd:documentation&gt;          &lt;/xsd:annotation&gt;
 *      &lt;/xsd:attribute&gt;       &lt;xsd:attribute name="maxFeatures"
 *          type="xsd:positiveInteger" use="optional"&gt;
 *              &lt;xsd:annotation&gt;             &lt;xsd:documentation&gt;
 *                  The maxFeatures attribute is used to specify the maximum
 *                  number of features that a GetFeature operation should
 *                  generate (regardless of the actual number of query
 *                  hits).             &lt;/xsd:documentation&gt;
 *          &lt;/xsd:annotation&gt;       &lt;/xsd:attribute&gt;    &lt;/xsd:complexType&gt; 
 *		
 *	  </code>
 *	 </pre>
 * </p>
 *
 * @generated
 */
public class GetFeatureTypeBinding extends AbstractComplexBinding {

	WFSFactory wfsfactory;		
	public GetFeatureTypeBinding( WFSFactory wfsfactory ) {
		this.wfsfactory = wfsfactory;
	}

	/**
	 * @generated
	 */
	public QName getTarget() {
		return WFS.GETFEATURETYPE;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *	
	 * @generated modifiable
	 */	
	public Class getType() {
		return GetFeatureType.class;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *	
	 * @generated modifiable
	 */	
	public Object parse(ElementInstance instance, Node node, Object value) 
		throws Exception {
		
		GetFeatureType getFeature = wfsfactory.createGetFeatureType();
		
		WFSBindingUtils.service( getFeature, node );
		WFSBindingUtils.version( getFeature, node );
		WFSBindingUtils.outputFormat( getFeature, node, "GML2" );
		
		if ( node.getAttributeValue( "handle" ) != null )
			getFeature.setHandle( (String) node.getAttributeValue( "handle" ) );
		
		//get the max features
		BigInteger maxFeatures = null;
		Number number = (Number) node.getAttributeValue( "maxFeatures" );
		if ( number != null ) {
			if ( number instanceof BigInteger ) {
				maxFeatures = (BigInteger) number;
			}
			else {
				maxFeatures = BigInteger.valueOf( number.longValue() );
			}
			getFeature.setMaxFeatures( maxFeatures );
		}
		
		//queries
		getFeature.getQuery().addAll( node.getChildValues( QueryType.class ) );
		return getFeature;
	}

}