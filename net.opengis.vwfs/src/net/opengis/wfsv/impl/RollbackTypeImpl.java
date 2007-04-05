/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.opengis.wfsv.impl;

import net.opengis.wfs.impl.NativeTypeImpl;

import net.opengis.wfsv.DifferenceQueryType;
import net.opengis.wfsv.RollbackType;
import net.opengis.wfsv.WfsvPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Rollback Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.opengis.wfsv.impl.RollbackTypeImpl#getFilter <em>Filter</em>}</li>
 *   <li>{@link net.opengis.wfsv.impl.RollbackTypeImpl#getFromFeatureVersion <em>From Feature Version</em>}</li>
 *   <li>{@link net.opengis.wfsv.impl.RollbackTypeImpl#getHandle <em>Handle</em>}</li>
 *   <li>{@link net.opengis.wfsv.impl.RollbackTypeImpl#getTypeName <em>Type Name</em>}</li>
 *   <li>{@link net.opengis.wfsv.impl.RollbackTypeImpl#getUser <em>User</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RollbackTypeImpl extends NativeTypeImpl implements RollbackType {
    /**
     * The default value of the '{@link #getFilter() <em>Filter</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFilter()
     * @generated
     * @ordered
     */
    protected static final Object FILTER_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getFilter() <em>Filter</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFilter()
     * @generated
     * @ordered
     */
    protected Object filter = FILTER_EDEFAULT;

    /**
     * The default value of the '{@link #getFromFeatureVersion() <em>From Feature Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFromFeatureVersion()
     * @generated
     * @ordered
     */
    protected static final String FROM_FEATURE_VERSION_EDEFAULT = "FIRST";

    /**
     * The cached value of the '{@link #getFromFeatureVersion() <em>From Feature Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFromFeatureVersion()
     * @generated
     * @ordered
     */
    protected String fromFeatureVersion = FROM_FEATURE_VERSION_EDEFAULT;

    /**
     * This is true if the From Feature Version attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean fromFeatureVersionESet = false;

    /**
     * The default value of the '{@link #getHandle() <em>Handle</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getHandle()
     * @generated
     * @ordered
     */
    protected static final String HANDLE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getHandle() <em>Handle</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getHandle()
     * @generated
     * @ordered
     */
    protected String handle = HANDLE_EDEFAULT;

    /**
     * The default value of the '{@link #getTypeName() <em>Type Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTypeName()
     * @generated
     * @ordered
     */
    protected static final Object TYPE_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getTypeName() <em>Type Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTypeName()
     * @generated
     * @ordered
     */
    protected Object typeName = TYPE_NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getUser() <em>User</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUser()
     * @generated
     * @ordered
     */
    protected static final String USER_EDEFAULT = "";

    /**
     * The cached value of the '{@link #getUser() <em>User</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUser()
     * @generated
     * @ordered
     */
    protected String user = USER_EDEFAULT;

    /**
     * This is true if the User attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean userESet = false;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected RollbackTypeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return WfsvPackage.Literals.ROLLBACK_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object getFilter() {
        return filter;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFilter(Object newFilter) {
        Object oldFilter = filter;
        filter = newFilter;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WfsvPackage.ROLLBACK_TYPE__FILTER, oldFilter, filter));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getFromFeatureVersion() {
        return fromFeatureVersion;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFromFeatureVersion(String newFromFeatureVersion) {
        String oldFromFeatureVersion = fromFeatureVersion;
        fromFeatureVersion = newFromFeatureVersion;
        boolean oldFromFeatureVersionESet = fromFeatureVersionESet;
        fromFeatureVersionESet = true;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WfsvPackage.ROLLBACK_TYPE__FROM_FEATURE_VERSION, oldFromFeatureVersion, fromFeatureVersion, !oldFromFeatureVersionESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetFromFeatureVersion() {
        String oldFromFeatureVersion = fromFeatureVersion;
        boolean oldFromFeatureVersionESet = fromFeatureVersionESet;
        fromFeatureVersion = FROM_FEATURE_VERSION_EDEFAULT;
        fromFeatureVersionESet = false;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.UNSET, WfsvPackage.ROLLBACK_TYPE__FROM_FEATURE_VERSION, oldFromFeatureVersion, FROM_FEATURE_VERSION_EDEFAULT, oldFromFeatureVersionESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetFromFeatureVersion() {
        return fromFeatureVersionESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getHandle() {
        return handle;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setHandle(String newHandle) {
        String oldHandle = handle;
        handle = newHandle;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WfsvPackage.ROLLBACK_TYPE__HANDLE, oldHandle, handle));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object getTypeName() {
        return typeName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTypeName(Object newTypeName) {
        Object oldTypeName = typeName;
        typeName = newTypeName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WfsvPackage.ROLLBACK_TYPE__TYPE_NAME, oldTypeName, typeName));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getUser() {
        return user;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUser(String newUser) {
        String oldUser = user;
        user = newUser;
        boolean oldUserESet = userESet;
        userESet = true;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WfsvPackage.ROLLBACK_TYPE__USER, oldUser, user, !oldUserESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetUser() {
        String oldUser = user;
        boolean oldUserESet = userESet;
        user = USER_EDEFAULT;
        userESet = false;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.UNSET, WfsvPackage.ROLLBACK_TYPE__USER, oldUser, USER_EDEFAULT, oldUserESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetUser() {
        return userESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case WfsvPackage.ROLLBACK_TYPE__FILTER:
                return getFilter();
            case WfsvPackage.ROLLBACK_TYPE__FROM_FEATURE_VERSION:
                return getFromFeatureVersion();
            case WfsvPackage.ROLLBACK_TYPE__HANDLE:
                return getHandle();
            case WfsvPackage.ROLLBACK_TYPE__TYPE_NAME:
                return getTypeName();
            case WfsvPackage.ROLLBACK_TYPE__USER:
                return getUser();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case WfsvPackage.ROLLBACK_TYPE__FILTER:
                setFilter((Object)newValue);
                return;
            case WfsvPackage.ROLLBACK_TYPE__FROM_FEATURE_VERSION:
                setFromFeatureVersion((String)newValue);
                return;
            case WfsvPackage.ROLLBACK_TYPE__HANDLE:
                setHandle((String)newValue);
                return;
            case WfsvPackage.ROLLBACK_TYPE__TYPE_NAME:
                setTypeName((Object)newValue);
                return;
            case WfsvPackage.ROLLBACK_TYPE__USER:
                setUser((String)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void eUnset(int featureID) {
        switch (featureID) {
            case WfsvPackage.ROLLBACK_TYPE__FILTER:
                setFilter(FILTER_EDEFAULT);
                return;
            case WfsvPackage.ROLLBACK_TYPE__FROM_FEATURE_VERSION:
                unsetFromFeatureVersion();
                return;
            case WfsvPackage.ROLLBACK_TYPE__HANDLE:
                setHandle(HANDLE_EDEFAULT);
                return;
            case WfsvPackage.ROLLBACK_TYPE__TYPE_NAME:
                setTypeName(TYPE_NAME_EDEFAULT);
                return;
            case WfsvPackage.ROLLBACK_TYPE__USER:
                unsetUser();
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case WfsvPackage.ROLLBACK_TYPE__FILTER:
                return FILTER_EDEFAULT == null ? filter != null : !FILTER_EDEFAULT.equals(filter);
            case WfsvPackage.ROLLBACK_TYPE__FROM_FEATURE_VERSION:
                return isSetFromFeatureVersion();
            case WfsvPackage.ROLLBACK_TYPE__HANDLE:
                return HANDLE_EDEFAULT == null ? handle != null : !HANDLE_EDEFAULT.equals(handle);
            case WfsvPackage.ROLLBACK_TYPE__TYPE_NAME:
                return TYPE_NAME_EDEFAULT == null ? typeName != null : !TYPE_NAME_EDEFAULT.equals(typeName);
            case WfsvPackage.ROLLBACK_TYPE__USER:
                return isSetUser();
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (filter: ");
        result.append(filter);
        result.append(", fromFeatureVersion: ");
        if (fromFeatureVersionESet) result.append(fromFeatureVersion); else result.append("<unset>");
        result.append(", handle: ");
        result.append(handle);
        result.append(", typeName: ");
        result.append(typeName);
        result.append(", user: ");
        if (userESet) result.append(user); else result.append("<unset>");
        result.append(')');
        return result.toString();
    }

} //RollbackTypeImpl