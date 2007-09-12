/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package net.opengis.ows.impl;

import java.util.Collection;

import net.opengis.ows.HTTPType;
import net.opengis.ows.OwsPackage;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>HTTP Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link net.opengis.ows.impl.HTTPTypeImpl#getGroup <em>Group</em>}</li>
 *   <li>{@link net.opengis.ows.impl.HTTPTypeImpl#getGet <em>Get</em>}</li>
 *   <li>{@link net.opengis.ows.impl.HTTPTypeImpl#getPost <em>Post</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class HTTPTypeImpl extends EObjectImpl implements HTTPType {
	/**
     * The cached value of the '{@link #getGroup() <em>Group</em>}' attribute list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getGroup()
     * @generated
     * @ordered
     */
	protected FeatureMap group;

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected HTTPTypeImpl() {
        super();
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected EClass eStaticClass() {
        return OwsPackage.Literals.HTTP_TYPE;
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public FeatureMap getGroup() {
        if (group == null) {
            group = new BasicFeatureMap(this, OwsPackage.HTTP_TYPE__GROUP);
        }
        return group;
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getGet() {
        return getGroup().list(OwsPackage.Literals.HTTP_TYPE__GET);
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getPost() {
        return getGroup().list(OwsPackage.Literals.HTTP_TYPE__POST);
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case OwsPackage.HTTP_TYPE__GROUP:
                return ((InternalEList)getGroup()).basicRemove(otherEnd, msgs);
            case OwsPackage.HTTP_TYPE__GET:
                return ((InternalEList)getGet()).basicRemove(otherEnd, msgs);
            case OwsPackage.HTTP_TYPE__POST:
                return ((InternalEList)getPost()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case OwsPackage.HTTP_TYPE__GROUP:
                if (coreType) return getGroup();
                return ((FeatureMap.Internal)getGroup()).getWrapper();
            case OwsPackage.HTTP_TYPE__GET:
                return getGet();
            case OwsPackage.HTTP_TYPE__POST:
                return getPost();
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
            case OwsPackage.HTTP_TYPE__GROUP:
                ((FeatureMap.Internal)getGroup()).set(newValue);
                return;
            case OwsPackage.HTTP_TYPE__GET:
                getGet().clear();
                getGet().addAll((Collection)newValue);
                return;
            case OwsPackage.HTTP_TYPE__POST:
                getPost().clear();
                getPost().addAll((Collection)newValue);
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
            case OwsPackage.HTTP_TYPE__GROUP:
                getGroup().clear();
                return;
            case OwsPackage.HTTP_TYPE__GET:
                getGet().clear();
                return;
            case OwsPackage.HTTP_TYPE__POST:
                getPost().clear();
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
            case OwsPackage.HTTP_TYPE__GROUP:
                return group != null && !group.isEmpty();
            case OwsPackage.HTTP_TYPE__GET:
                return !getGet().isEmpty();
            case OwsPackage.HTTP_TYPE__POST:
                return !getPost().isEmpty();
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
        result.append(" (group: ");
        result.append(group);
        result.append(')');
        return result.toString();
    }

} //HTTPTypeImpl