package org.geoserver.wfs.xml.v1_0_0;


import org.geotools.xml.*;

import net.opengis.wfs.WFSFactory;		

import javax.xml.namespace.QName;

/**
 * Binding object for the type http://www.opengis.net/wfs:StatusType.
 *
 * <p>
 *	<pre>
 *	 <code>
 *  &lt;xsd:complexType name="StatusType"&gt;
 *      &lt;xsd:choice&gt;
 *          &lt;xsd:element ref="wfs:SUCCESS"/&gt;
 *          &lt;xsd:element ref="wfs:FAILED"/&gt;
 *          &lt;xsd:element ref="wfs:PARTIAL"/&gt;
 *      &lt;/xsd:choice&gt;
 *  &lt;/xsd:complexType&gt; 
 *		
 *	  </code>
 *	 </pre>
 * </p>
 *
 * @generated
 */
public class StatusTypeBinding extends AbstractComplexBinding {

	WFSFactory wfsfactory;		
	public StatusTypeBinding( WFSFactory wfsfactory ) {
		this.wfsfactory = wfsfactory;
	}

	/**
	 * @generated
	 */
	public QName getTarget() {
		return WFS.STATUSTYPE;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *	
	 * @generated modifiable
	 */	
	public Class getType() {
		return null;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *	
	 * @generated modifiable
	 */	
	public Object parse(ElementInstance instance, Node node, Object value) 
		throws Exception {
		
		//TODO: implement
		return null;
	}

}