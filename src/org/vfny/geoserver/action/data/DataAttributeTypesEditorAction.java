/*
 * Created on Jan 13, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.vfny.geoserver.action.data;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.vfny.geoserver.action.ConfigAction;
import org.vfny.geoserver.config.AttributeTypeInfoConfig;
import org.vfny.geoserver.form.data.DataAttributeTypesEditorForm;
/**
 * @author User
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class DataAttributeTypesEditorAction extends ConfigAction {
	public ActionForward execute(ActionMapping mapping,
			ActionForm incomingForm,
			HttpServletRequest request,
			HttpServletResponse response)
	throws IOException, ServletException {
		
		//PROCESS EDITING OF ATTRIBUTE TYPE
		
		DataAttributeTypesEditorForm form = (DataAttributeTypesEditorForm)incomingForm;
		
		String fragment = form.getFragment();
		int minOccurs = Integer.parseInt(form.getMinOccurs());
		int maxOccurs = Integer.parseInt(form.getMaxOccurs());
		String selectedType = form.getSelectedType();
		boolean nillible = form.isNillible();
		boolean ref = form.isRef();
		String name = form.getName();
		
		AttributeTypeInfoConfig atiConfig = null; //Retrive ATIConfig info to update it.
		
		return mapping.findForward("dataConfigFeatureTypes");
	}
}
