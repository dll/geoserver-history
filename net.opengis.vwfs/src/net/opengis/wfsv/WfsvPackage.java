/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.opengis.wfsv;

import net.opengis.wfs.WfsPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see net.opengis.wfsv.WfsvFactory
 * @model kind="package"
 * @generated
 */
public interface WfsvPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "wfsv";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.opengis.net/wfsv";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "wfsv";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    WfsvPackage eINSTANCE = net.opengis.wfsv.impl.WfsvPackageImpl.init();

    /**
     * The meta object id for the '{@link net.opengis.wfsv.impl.DifferenceQueryTypeImpl <em>Difference Query Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.opengis.wfsv.impl.DifferenceQueryTypeImpl
     * @see net.opengis.wfsv.impl.WfsvPackageImpl#getDifferenceQueryType()
     * @generated
     */
    int DIFFERENCE_QUERY_TYPE = 0;

    /**
     * The feature id for the '<em><b>Filter</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_QUERY_TYPE__FILTER = 0;

    /**
     * The feature id for the '<em><b>From Feature Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_QUERY_TYPE__FROM_FEATURE_VERSION = 1;

    /**
     * The feature id for the '<em><b>Srs Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_QUERY_TYPE__SRS_NAME = 2;

    /**
     * The feature id for the '<em><b>To Feature Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_QUERY_TYPE__TO_FEATURE_VERSION = 3;

    /**
     * The feature id for the '<em><b>Type Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_QUERY_TYPE__TYPE_NAME = 4;

    /**
     * The number of structural features of the '<em>Difference Query Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_QUERY_TYPE_FEATURE_COUNT = 5;

    /**
     * The meta object id for the '{@link net.opengis.wfsv.impl.DocumentRootImpl <em>Document Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.opengis.wfsv.impl.DocumentRootImpl
     * @see net.opengis.wfsv.impl.WfsvPackageImpl#getDocumentRoot()
     * @generated
     */
    int DOCUMENT_ROOT = 1;

    /**
     * The feature id for the '<em><b>Mixed</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__MIXED = 0;

    /**
     * The feature id for the '<em><b>XMLNS Prefix Map</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__XMLNS_PREFIX_MAP = 1;

    /**
     * The feature id for the '<em><b>XSI Schema Location</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = 2;

    /**
     * The feature id for the '<em><b>Difference Query</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__DIFFERENCE_QUERY = 3;

    /**
     * The feature id for the '<em><b>Get Diff</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__GET_DIFF = 4;

    /**
     * The feature id for the '<em><b>Get Log</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__GET_LOG = 5;

    /**
     * The feature id for the '<em><b>Rollback</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__ROLLBACK = 6;

    /**
     * The feature id for the '<em><b>Versioned Delete</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__VERSIONED_DELETE = 7;

    /**
     * The feature id for the '<em><b>Versioned Update</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__VERSIONED_UPDATE = 8;

    /**
     * The number of structural features of the '<em>Document Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT_FEATURE_COUNT = 9;

    /**
     * The meta object id for the '{@link net.opengis.wfsv.impl.GetDiffTypeImpl <em>Get Diff Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.opengis.wfsv.impl.GetDiffTypeImpl
     * @see net.opengis.wfsv.impl.WfsvPackageImpl#getGetDiffType()
     * @generated
     */
    int GET_DIFF_TYPE = 2;

    /**
     * The feature id for the '<em><b>Handle</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GET_DIFF_TYPE__HANDLE = WfsPackage.BASE_REQUEST_TYPE__HANDLE;

    /**
     * The feature id for the '<em><b>Service</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GET_DIFF_TYPE__SERVICE = WfsPackage.BASE_REQUEST_TYPE__SERVICE;

    /**
     * The feature id for the '<em><b>Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GET_DIFF_TYPE__VERSION = WfsPackage.BASE_REQUEST_TYPE__VERSION;

    /**
     * The feature id for the '<em><b>Difference Query</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GET_DIFF_TYPE__DIFFERENCE_QUERY = WfsPackage.BASE_REQUEST_TYPE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Output Format</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GET_DIFF_TYPE__OUTPUT_FORMAT = WfsPackage.BASE_REQUEST_TYPE_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Get Diff Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GET_DIFF_TYPE_FEATURE_COUNT = WfsPackage.BASE_REQUEST_TYPE_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link net.opengis.wfsv.impl.GetLogTypeImpl <em>Get Log Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.opengis.wfsv.impl.GetLogTypeImpl
     * @see net.opengis.wfsv.impl.WfsvPackageImpl#getGetLogType()
     * @generated
     */
    int GET_LOG_TYPE = 3;

    /**
     * The feature id for the '<em><b>Handle</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GET_LOG_TYPE__HANDLE = WfsPackage.BASE_REQUEST_TYPE__HANDLE;

    /**
     * The feature id for the '<em><b>Service</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GET_LOG_TYPE__SERVICE = WfsPackage.BASE_REQUEST_TYPE__SERVICE;

    /**
     * The feature id for the '<em><b>Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GET_LOG_TYPE__VERSION = WfsPackage.BASE_REQUEST_TYPE__VERSION;

    /**
     * The feature id for the '<em><b>Difference Query</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GET_LOG_TYPE__DIFFERENCE_QUERY = WfsPackage.BASE_REQUEST_TYPE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Output Format</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GET_LOG_TYPE__OUTPUT_FORMAT = WfsPackage.BASE_REQUEST_TYPE_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Result Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GET_LOG_TYPE__RESULT_TYPE = WfsPackage.BASE_REQUEST_TYPE_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the '<em>Get Log Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GET_LOG_TYPE_FEATURE_COUNT = WfsPackage.BASE_REQUEST_TYPE_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link net.opengis.wfsv.impl.RollbackTypeImpl <em>Rollback Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.opengis.wfsv.impl.RollbackTypeImpl
     * @see net.opengis.wfsv.impl.WfsvPackageImpl#getRollbackType()
     * @generated
     */
    int ROLLBACK_TYPE = 4;

    /**
     * The feature id for the '<em><b>Safe To Ignore</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROLLBACK_TYPE__SAFE_TO_IGNORE = WfsPackage.NATIVE_TYPE__SAFE_TO_IGNORE;

    /**
     * The feature id for the '<em><b>Vendor Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROLLBACK_TYPE__VENDOR_ID = WfsPackage.NATIVE_TYPE__VENDOR_ID;

    /**
     * The feature id for the '<em><b>Filter</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROLLBACK_TYPE__FILTER = WfsPackage.NATIVE_TYPE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>From Feature Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROLLBACK_TYPE__FROM_FEATURE_VERSION = WfsPackage.NATIVE_TYPE_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Handle</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROLLBACK_TYPE__HANDLE = WfsPackage.NATIVE_TYPE_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Type Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROLLBACK_TYPE__TYPE_NAME = WfsPackage.NATIVE_TYPE_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>User</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROLLBACK_TYPE__USER = WfsPackage.NATIVE_TYPE_FEATURE_COUNT + 4;

    /**
     * The number of structural features of the '<em>Rollback Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROLLBACK_TYPE_FEATURE_COUNT = WfsPackage.NATIVE_TYPE_FEATURE_COUNT + 5;

    /**
     * The meta object id for the '{@link net.opengis.wfsv.impl.VersionedDeleteElementTypeImpl <em>Versioned Delete Element Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.opengis.wfsv.impl.VersionedDeleteElementTypeImpl
     * @see net.opengis.wfsv.impl.WfsvPackageImpl#getVersionedDeleteElementType()
     * @generated
     */
    int VERSIONED_DELETE_ELEMENT_TYPE = 5;

    /**
     * The feature id for the '<em><b>Filter</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VERSIONED_DELETE_ELEMENT_TYPE__FILTER = WfsPackage.DELETE_ELEMENT_TYPE__FILTER;

    /**
     * The feature id for the '<em><b>Handle</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VERSIONED_DELETE_ELEMENT_TYPE__HANDLE = WfsPackage.DELETE_ELEMENT_TYPE__HANDLE;

    /**
     * The feature id for the '<em><b>Type Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VERSIONED_DELETE_ELEMENT_TYPE__TYPE_NAME = WfsPackage.DELETE_ELEMENT_TYPE__TYPE_NAME;

    /**
     * The feature id for the '<em><b>Feature Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VERSIONED_DELETE_ELEMENT_TYPE__FEATURE_VERSION = WfsPackage.DELETE_ELEMENT_TYPE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Versioned Delete Element Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VERSIONED_DELETE_ELEMENT_TYPE_FEATURE_COUNT = WfsPackage.DELETE_ELEMENT_TYPE_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link net.opengis.wfsv.impl.VersionedUpdateElementTypeImpl <em>Versioned Update Element Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see net.opengis.wfsv.impl.VersionedUpdateElementTypeImpl
     * @see net.opengis.wfsv.impl.WfsvPackageImpl#getVersionedUpdateElementType()
     * @generated
     */
    int VERSIONED_UPDATE_ELEMENT_TYPE = 6;

    /**
     * The feature id for the '<em><b>Property</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VERSIONED_UPDATE_ELEMENT_TYPE__PROPERTY = WfsPackage.UPDATE_ELEMENT_TYPE__PROPERTY;

    /**
     * The feature id for the '<em><b>Filter</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VERSIONED_UPDATE_ELEMENT_TYPE__FILTER = WfsPackage.UPDATE_ELEMENT_TYPE__FILTER;

    /**
     * The feature id for the '<em><b>Handle</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VERSIONED_UPDATE_ELEMENT_TYPE__HANDLE = WfsPackage.UPDATE_ELEMENT_TYPE__HANDLE;

    /**
     * The feature id for the '<em><b>Input Format</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VERSIONED_UPDATE_ELEMENT_TYPE__INPUT_FORMAT = WfsPackage.UPDATE_ELEMENT_TYPE__INPUT_FORMAT;

    /**
     * The feature id for the '<em><b>Srs Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VERSIONED_UPDATE_ELEMENT_TYPE__SRS_NAME = WfsPackage.UPDATE_ELEMENT_TYPE__SRS_NAME;

    /**
     * The feature id for the '<em><b>Type Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VERSIONED_UPDATE_ELEMENT_TYPE__TYPE_NAME = WfsPackage.UPDATE_ELEMENT_TYPE__TYPE_NAME;

    /**
     * The feature id for the '<em><b>Feature Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VERSIONED_UPDATE_ELEMENT_TYPE__FEATURE_VERSION = WfsPackage.UPDATE_ELEMENT_TYPE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Versioned Update Element Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VERSIONED_UPDATE_ELEMENT_TYPE_FEATURE_COUNT = WfsPackage.UPDATE_ELEMENT_TYPE_FEATURE_COUNT + 1;


    /**
     * Returns the meta object for class '{@link net.opengis.wfsv.DifferenceQueryType <em>Difference Query Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Difference Query Type</em>'.
     * @see net.opengis.wfsv.DifferenceQueryType
     * @generated
     */
    EClass getDifferenceQueryType();

    /**
     * Returns the meta object for the attribute '{@link net.opengis.wfsv.DifferenceQueryType#getFilter <em>Filter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Filter</em>'.
     * @see net.opengis.wfsv.DifferenceQueryType#getFilter()
     * @see #getDifferenceQueryType()
     * @generated
     */
    EAttribute getDifferenceQueryType_Filter();

    /**
     * Returns the meta object for the attribute '{@link net.opengis.wfsv.DifferenceQueryType#getFromFeatureVersion <em>From Feature Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>From Feature Version</em>'.
     * @see net.opengis.wfsv.DifferenceQueryType#getFromFeatureVersion()
     * @see #getDifferenceQueryType()
     * @generated
     */
    EAttribute getDifferenceQueryType_FromFeatureVersion();

    /**
     * Returns the meta object for the attribute '{@link net.opengis.wfsv.DifferenceQueryType#getSrsName <em>Srs Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Srs Name</em>'.
     * @see net.opengis.wfsv.DifferenceQueryType#getSrsName()
     * @see #getDifferenceQueryType()
     * @generated
     */
    EAttribute getDifferenceQueryType_SrsName();

    /**
     * Returns the meta object for the attribute '{@link net.opengis.wfsv.DifferenceQueryType#getToFeatureVersion <em>To Feature Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>To Feature Version</em>'.
     * @see net.opengis.wfsv.DifferenceQueryType#getToFeatureVersion()
     * @see #getDifferenceQueryType()
     * @generated
     */
    EAttribute getDifferenceQueryType_ToFeatureVersion();

    /**
     * Returns the meta object for the attribute '{@link net.opengis.wfsv.DifferenceQueryType#getTypeName <em>Type Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type Name</em>'.
     * @see net.opengis.wfsv.DifferenceQueryType#getTypeName()
     * @see #getDifferenceQueryType()
     * @generated
     */
    EAttribute getDifferenceQueryType_TypeName();

    /**
     * Returns the meta object for class '{@link net.opengis.wfsv.DocumentRoot <em>Document Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Document Root</em>'.
     * @see net.opengis.wfsv.DocumentRoot
     * @generated
     */
    EClass getDocumentRoot();

    /**
     * Returns the meta object for the attribute list '{@link net.opengis.wfsv.DocumentRoot#getMixed <em>Mixed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Mixed</em>'.
     * @see net.opengis.wfsv.DocumentRoot#getMixed()
     * @see #getDocumentRoot()
     * @generated
     */
    EAttribute getDocumentRoot_Mixed();

    /**
     * Returns the meta object for the map '{@link net.opengis.wfsv.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
     * @see net.opengis.wfsv.DocumentRoot#getXMLNSPrefixMap()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_XMLNSPrefixMap();

    /**
     * Returns the meta object for the map '{@link net.opengis.wfsv.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>XSI Schema Location</em>'.
     * @see net.opengis.wfsv.DocumentRoot#getXSISchemaLocation()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_XSISchemaLocation();

    /**
     * Returns the meta object for the containment reference '{@link net.opengis.wfsv.DocumentRoot#getDifferenceQuery <em>Difference Query</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Difference Query</em>'.
     * @see net.opengis.wfsv.DocumentRoot#getDifferenceQuery()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_DifferenceQuery();

    /**
     * Returns the meta object for the containment reference '{@link net.opengis.wfsv.DocumentRoot#getGetDiff <em>Get Diff</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Get Diff</em>'.
     * @see net.opengis.wfsv.DocumentRoot#getGetDiff()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_GetDiff();

    /**
     * Returns the meta object for the containment reference '{@link net.opengis.wfsv.DocumentRoot#getGetLog <em>Get Log</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Get Log</em>'.
     * @see net.opengis.wfsv.DocumentRoot#getGetLog()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_GetLog();

    /**
     * Returns the meta object for the containment reference '{@link net.opengis.wfsv.DocumentRoot#getRollback <em>Rollback</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Rollback</em>'.
     * @see net.opengis.wfsv.DocumentRoot#getRollback()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_Rollback();

    /**
     * Returns the meta object for the containment reference '{@link net.opengis.wfsv.DocumentRoot#getVersionedDelete <em>Versioned Delete</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Versioned Delete</em>'.
     * @see net.opengis.wfsv.DocumentRoot#getVersionedDelete()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_VersionedDelete();

    /**
     * Returns the meta object for the containment reference '{@link net.opengis.wfsv.DocumentRoot#getVersionedUpdate <em>Versioned Update</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Versioned Update</em>'.
     * @see net.opengis.wfsv.DocumentRoot#getVersionedUpdate()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_VersionedUpdate();

    /**
     * Returns the meta object for class '{@link net.opengis.wfsv.GetDiffType <em>Get Diff Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Get Diff Type</em>'.
     * @see net.opengis.wfsv.GetDiffType
     * @generated
     */
    EClass getGetDiffType();

    /**
     * Returns the meta object for the containment reference list '{@link net.opengis.wfsv.GetDiffType#getDifferenceQuery <em>Difference Query</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Difference Query</em>'.
     * @see net.opengis.wfsv.GetDiffType#getDifferenceQuery()
     * @see #getGetDiffType()
     * @generated
     */
    EReference getGetDiffType_DifferenceQuery();

    /**
     * Returns the meta object for the attribute '{@link net.opengis.wfsv.GetDiffType#getOutputFormat <em>Output Format</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Output Format</em>'.
     * @see net.opengis.wfsv.GetDiffType#getOutputFormat()
     * @see #getGetDiffType()
     * @generated
     */
    EAttribute getGetDiffType_OutputFormat();

    /**
     * Returns the meta object for class '{@link net.opengis.wfsv.GetLogType <em>Get Log Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Get Log Type</em>'.
     * @see net.opengis.wfsv.GetLogType
     * @generated
     */
    EClass getGetLogType();

    /**
     * Returns the meta object for the containment reference list '{@link net.opengis.wfsv.GetLogType#getDifferenceQuery <em>Difference Query</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Difference Query</em>'.
     * @see net.opengis.wfsv.GetLogType#getDifferenceQuery()
     * @see #getGetLogType()
     * @generated
     */
    EReference getGetLogType_DifferenceQuery();

    /**
     * Returns the meta object for the attribute '{@link net.opengis.wfsv.GetLogType#getOutputFormat <em>Output Format</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Output Format</em>'.
     * @see net.opengis.wfsv.GetLogType#getOutputFormat()
     * @see #getGetLogType()
     * @generated
     */
    EAttribute getGetLogType_OutputFormat();

    /**
     * Returns the meta object for the attribute '{@link net.opengis.wfsv.GetLogType#getResultType <em>Result Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Result Type</em>'.
     * @see net.opengis.wfsv.GetLogType#getResultType()
     * @see #getGetLogType()
     * @generated
     */
    EAttribute getGetLogType_ResultType();

    /**
     * Returns the meta object for class '{@link net.opengis.wfsv.RollbackType <em>Rollback Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Rollback Type</em>'.
     * @see net.opengis.wfsv.RollbackType
     * @generated
     */
    EClass getRollbackType();

    /**
     * Returns the meta object for the attribute '{@link net.opengis.wfsv.RollbackType#getFilter <em>Filter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Filter</em>'.
     * @see net.opengis.wfsv.RollbackType#getFilter()
     * @see #getRollbackType()
     * @generated
     */
    EAttribute getRollbackType_Filter();

    /**
     * Returns the meta object for the attribute '{@link net.opengis.wfsv.RollbackType#getFromFeatureVersion <em>From Feature Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>From Feature Version</em>'.
     * @see net.opengis.wfsv.RollbackType#getFromFeatureVersion()
     * @see #getRollbackType()
     * @generated
     */
    EAttribute getRollbackType_FromFeatureVersion();

    /**
     * Returns the meta object for the attribute '{@link net.opengis.wfsv.RollbackType#getHandle <em>Handle</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Handle</em>'.
     * @see net.opengis.wfsv.RollbackType#getHandle()
     * @see #getRollbackType()
     * @generated
     */
    EAttribute getRollbackType_Handle();

    /**
     * Returns the meta object for the attribute '{@link net.opengis.wfsv.RollbackType#getTypeName <em>Type Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type Name</em>'.
     * @see net.opengis.wfsv.RollbackType#getTypeName()
     * @see #getRollbackType()
     * @generated
     */
    EAttribute getRollbackType_TypeName();

    /**
     * Returns the meta object for the attribute '{@link net.opengis.wfsv.RollbackType#getUser <em>User</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>User</em>'.
     * @see net.opengis.wfsv.RollbackType#getUser()
     * @see #getRollbackType()
     * @generated
     */
    EAttribute getRollbackType_User();

    /**
     * Returns the meta object for class '{@link net.opengis.wfsv.VersionedDeleteElementType <em>Versioned Delete Element Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Versioned Delete Element Type</em>'.
     * @see net.opengis.wfsv.VersionedDeleteElementType
     * @generated
     */
    EClass getVersionedDeleteElementType();

    /**
     * Returns the meta object for the attribute '{@link net.opengis.wfsv.VersionedDeleteElementType#getFeatureVersion <em>Feature Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Feature Version</em>'.
     * @see net.opengis.wfsv.VersionedDeleteElementType#getFeatureVersion()
     * @see #getVersionedDeleteElementType()
     * @generated
     */
    EAttribute getVersionedDeleteElementType_FeatureVersion();

    /**
     * Returns the meta object for class '{@link net.opengis.wfsv.VersionedUpdateElementType <em>Versioned Update Element Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Versioned Update Element Type</em>'.
     * @see net.opengis.wfsv.VersionedUpdateElementType
     * @generated
     */
    EClass getVersionedUpdateElementType();

    /**
     * Returns the meta object for the attribute '{@link net.opengis.wfsv.VersionedUpdateElementType#getFeatureVersion <em>Feature Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Feature Version</em>'.
     * @see net.opengis.wfsv.VersionedUpdateElementType#getFeatureVersion()
     * @see #getVersionedUpdateElementType()
     * @generated
     */
    EAttribute getVersionedUpdateElementType_FeatureVersion();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    WfsvFactory getWfsvFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals  {
        /**
         * The meta object literal for the '{@link net.opengis.wfsv.impl.DifferenceQueryTypeImpl <em>Difference Query Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.opengis.wfsv.impl.DifferenceQueryTypeImpl
         * @see net.opengis.wfsv.impl.WfsvPackageImpl#getDifferenceQueryType()
         * @generated
         */
        EClass DIFFERENCE_QUERY_TYPE = eINSTANCE.getDifferenceQueryType();

        /**
         * The meta object literal for the '<em><b>Filter</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DIFFERENCE_QUERY_TYPE__FILTER = eINSTANCE.getDifferenceQueryType_Filter();

        /**
         * The meta object literal for the '<em><b>From Feature Version</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DIFFERENCE_QUERY_TYPE__FROM_FEATURE_VERSION = eINSTANCE.getDifferenceQueryType_FromFeatureVersion();

        /**
         * The meta object literal for the '<em><b>Srs Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DIFFERENCE_QUERY_TYPE__SRS_NAME = eINSTANCE.getDifferenceQueryType_SrsName();

        /**
         * The meta object literal for the '<em><b>To Feature Version</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DIFFERENCE_QUERY_TYPE__TO_FEATURE_VERSION = eINSTANCE.getDifferenceQueryType_ToFeatureVersion();

        /**
         * The meta object literal for the '<em><b>Type Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DIFFERENCE_QUERY_TYPE__TYPE_NAME = eINSTANCE.getDifferenceQueryType_TypeName();

        /**
         * The meta object literal for the '{@link net.opengis.wfsv.impl.DocumentRootImpl <em>Document Root</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.opengis.wfsv.impl.DocumentRootImpl
         * @see net.opengis.wfsv.impl.WfsvPackageImpl#getDocumentRoot()
         * @generated
         */
        EClass DOCUMENT_ROOT = eINSTANCE.getDocumentRoot();

        /**
         * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DOCUMENT_ROOT__MIXED = eINSTANCE.getDocumentRoot_Mixed();

        /**
         * The meta object literal for the '<em><b>XMLNS Prefix Map</b></em>' map feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DOCUMENT_ROOT__XMLNS_PREFIX_MAP = eINSTANCE.getDocumentRoot_XMLNSPrefixMap();

        /**
         * The meta object literal for the '<em><b>XSI Schema Location</b></em>' map feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = eINSTANCE.getDocumentRoot_XSISchemaLocation();

        /**
         * The meta object literal for the '<em><b>Difference Query</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DOCUMENT_ROOT__DIFFERENCE_QUERY = eINSTANCE.getDocumentRoot_DifferenceQuery();

        /**
         * The meta object literal for the '<em><b>Get Diff</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DOCUMENT_ROOT__GET_DIFF = eINSTANCE.getDocumentRoot_GetDiff();

        /**
         * The meta object literal for the '<em><b>Get Log</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DOCUMENT_ROOT__GET_LOG = eINSTANCE.getDocumentRoot_GetLog();

        /**
         * The meta object literal for the '<em><b>Rollback</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DOCUMENT_ROOT__ROLLBACK = eINSTANCE.getDocumentRoot_Rollback();

        /**
         * The meta object literal for the '<em><b>Versioned Delete</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DOCUMENT_ROOT__VERSIONED_DELETE = eINSTANCE.getDocumentRoot_VersionedDelete();

        /**
         * The meta object literal for the '<em><b>Versioned Update</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DOCUMENT_ROOT__VERSIONED_UPDATE = eINSTANCE.getDocumentRoot_VersionedUpdate();

        /**
         * The meta object literal for the '{@link net.opengis.wfsv.impl.GetDiffTypeImpl <em>Get Diff Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.opengis.wfsv.impl.GetDiffTypeImpl
         * @see net.opengis.wfsv.impl.WfsvPackageImpl#getGetDiffType()
         * @generated
         */
        EClass GET_DIFF_TYPE = eINSTANCE.getGetDiffType();

        /**
         * The meta object literal for the '<em><b>Difference Query</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference GET_DIFF_TYPE__DIFFERENCE_QUERY = eINSTANCE.getGetDiffType_DifferenceQuery();

        /**
         * The meta object literal for the '<em><b>Output Format</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute GET_DIFF_TYPE__OUTPUT_FORMAT = eINSTANCE.getGetDiffType_OutputFormat();

        /**
         * The meta object literal for the '{@link net.opengis.wfsv.impl.GetLogTypeImpl <em>Get Log Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.opengis.wfsv.impl.GetLogTypeImpl
         * @see net.opengis.wfsv.impl.WfsvPackageImpl#getGetLogType()
         * @generated
         */
        EClass GET_LOG_TYPE = eINSTANCE.getGetLogType();

        /**
         * The meta object literal for the '<em><b>Difference Query</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference GET_LOG_TYPE__DIFFERENCE_QUERY = eINSTANCE.getGetLogType_DifferenceQuery();

        /**
         * The meta object literal for the '<em><b>Output Format</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute GET_LOG_TYPE__OUTPUT_FORMAT = eINSTANCE.getGetLogType_OutputFormat();

        /**
         * The meta object literal for the '<em><b>Result Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute GET_LOG_TYPE__RESULT_TYPE = eINSTANCE.getGetLogType_ResultType();

        /**
         * The meta object literal for the '{@link net.opengis.wfsv.impl.RollbackTypeImpl <em>Rollback Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.opengis.wfsv.impl.RollbackTypeImpl
         * @see net.opengis.wfsv.impl.WfsvPackageImpl#getRollbackType()
         * @generated
         */
        EClass ROLLBACK_TYPE = eINSTANCE.getRollbackType();

        /**
         * The meta object literal for the '<em><b>Filter</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ROLLBACK_TYPE__FILTER = eINSTANCE.getRollbackType_Filter();

        /**
         * The meta object literal for the '<em><b>From Feature Version</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ROLLBACK_TYPE__FROM_FEATURE_VERSION = eINSTANCE.getRollbackType_FromFeatureVersion();

        /**
         * The meta object literal for the '<em><b>Handle</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ROLLBACK_TYPE__HANDLE = eINSTANCE.getRollbackType_Handle();

        /**
         * The meta object literal for the '<em><b>Type Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ROLLBACK_TYPE__TYPE_NAME = eINSTANCE.getRollbackType_TypeName();

        /**
         * The meta object literal for the '<em><b>User</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ROLLBACK_TYPE__USER = eINSTANCE.getRollbackType_User();

        /**
         * The meta object literal for the '{@link net.opengis.wfsv.impl.VersionedDeleteElementTypeImpl <em>Versioned Delete Element Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.opengis.wfsv.impl.VersionedDeleteElementTypeImpl
         * @see net.opengis.wfsv.impl.WfsvPackageImpl#getVersionedDeleteElementType()
         * @generated
         */
        EClass VERSIONED_DELETE_ELEMENT_TYPE = eINSTANCE.getVersionedDeleteElementType();

        /**
         * The meta object literal for the '<em><b>Feature Version</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute VERSIONED_DELETE_ELEMENT_TYPE__FEATURE_VERSION = eINSTANCE.getVersionedDeleteElementType_FeatureVersion();

        /**
         * The meta object literal for the '{@link net.opengis.wfsv.impl.VersionedUpdateElementTypeImpl <em>Versioned Update Element Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see net.opengis.wfsv.impl.VersionedUpdateElementTypeImpl
         * @see net.opengis.wfsv.impl.WfsvPackageImpl#getVersionedUpdateElementType()
         * @generated
         */
        EClass VERSIONED_UPDATE_ELEMENT_TYPE = eINSTANCE.getVersionedUpdateElementType();

        /**
         * The meta object literal for the '<em><b>Feature Version</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute VERSIONED_UPDATE_ELEMENT_TYPE__FEATURE_VERSION = eINSTANCE.getVersionedUpdateElementType_FeatureVersion();

    }

} //WfsvPackage
