/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.opengis.ows;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Exception Report Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link net.opengis.ows.ExceptionReportType#getException <em>Exception</em>}</li>
 *   <li>{@link net.opengis.ows.ExceptionReportType#getLanguage <em>Language</em>}</li>
 *   <li>{@link net.opengis.ows.ExceptionReportType#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 *
 * @see net.opengis.ows.OwsPackage#getExceptionReportType()
 * @model extendedMetaData="name='ExceptionReport_._type' kind='elementOnly'"
 * @generated
 */
public interface ExceptionReportType extends EObject {
	/**
     * Returns the value of the '<em><b>Exception</b></em>' containment reference list.
     * The list contents are of type {@link net.opengis.ows.ExceptionType}.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Unordered list of one or more Exception elements that each describes an error. These Exception elements shall be interpreted by clients as being independent of one another (not hierarchical).
     * <!-- end-model-doc -->
     * @return the value of the '<em>Exception</em>' containment reference list.
     * @see net.opengis.ows.OwsPackage#getExceptionReportType_Exception()
     * @model type="net.opengis.ows.ExceptionType" containment="true" required="true"
     *        extendedMetaData="kind='element' name='Exception' namespace='##targetNamespace'"
     * @generated
     */
	EList getException();

	/**
     * Returns the value of the '<em><b>Language</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Identifier of the language used by all included exception text values. These language identifiers shall be as specified in IETF RFC 1766. When this attribute is omitted, the language used is not identified.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Language</em>' attribute.
     * @see #setLanguage(String)
     * @see net.opengis.ows.OwsPackage#getExceptionReportType_Language()
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.Language"
     *        extendedMetaData="kind='attribute' name='language'"
     * @generated
     */
	String getLanguage();

	/**
     * Sets the value of the '{@link net.opengis.ows.ExceptionReportType#getLanguage <em>Language</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Language</em>' attribute.
     * @see #getLanguage()
     * @generated
     */
	void setLanguage(String value);

	/**
     * Returns the value of the '<em><b>Version</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Specification version for OWS operation. The string value shall contain one x.y.z "version" value (e.g., "2.1.3"). A version number shall contain three non-negative integers separated by decimal points, in the form "x.y.z". The integers y and z shall not exceed 99. Each version shall be for the Implementation Specification (document) and the associated XML Schemas to which requested operations will conform. An Implementation Specification version normally specifies XML Schemas against which an XML encoded operation response must conform and should be validated. See Version negotiation subclause for more information.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Version</em>' attribute.
     * @see #setVersion(String)
     * @see net.opengis.ows.OwsPackage#getExceptionReportType_Version()
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     *        extendedMetaData="kind='attribute' name='version'"
     * @generated
     */
	String getVersion();

	/**
     * Sets the value of the '{@link net.opengis.ows.ExceptionReportType#getVersion <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Version</em>' attribute.
     * @see #getVersion()
     * @generated
     */
	void setVersion(String value);

} // ExceptionReportType
