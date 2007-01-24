package org.geoserver.xml.ows.v1_0_0;


import javax.xml.namespace.QName;

import net.opengis.ows.v1_0_0.OWSFactory;
import net.opengis.ows.v1_0_0.SectionsType;

import org.geotools.xml.AbstractComplexBinding;
import org.geotools.xml.ElementInstance;
import org.geotools.xml.Node;

/**
 * Binding object for the type http://www.opengis.net/ows:SectionsType.
 *
 * <p>
 *	<pre>
 *	 <code>
 *  &lt;complexType name="SectionsType"&gt;
 *      &lt;annotation&gt;
 *          &lt;documentation&gt;Unordered list of zero or more names of requested sections in complete service metadata document. Each Section value shall contain an allowed section name as specified by each OWS specification. See Sections parameter subclause for more information.  &lt;/documentation&gt;
 *      &lt;/annotation&gt;
 *      &lt;sequence&gt;
 *          &lt;element maxOccurs="unbounded" minOccurs="0" name="Section" type="string"/&gt;
 *      &lt;/sequence&gt;
 *  &lt;/complexType&gt; 
 *		
 *	  </code>
 *	 </pre>
 * </p>
 *
 * @generated
 */
public class SectionsTypeBinding extends AbstractComplexBinding {

	OWSFactory owsfactory;		
	public SectionsTypeBinding( OWSFactory owsfactory ) {
		this.owsfactory = owsfactory;
	}

	/**
	 * @generated
	 */
	public QName getTarget() {
		return OWS.SECTIONSTYPE;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *	
	 * @generated modifiable
	 */	
	public Class getType() {
		return SectionsType.class;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *	
	 * @generated modifiable
	 */	
	public Object parse(ElementInstance instance, Node node, Object value) 
		throws Exception {
		
		SectionsType sections = owsfactory.createSectionsType();
		sections.getSection().addAll( node.getChildValues( "Section") );
		
		return sections;
	}

}