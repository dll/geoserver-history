/*
 * Created on Feb 18, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.vfny.geoserver.form.data;

import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.vfny.geoserver.config.DataConfig;
import org.vfny.geoserver.config.NameSpaceConfig;
/**
 * DataNamespacesNewForm purpose.
 * <p>
 * Description of DataNamespacesNewForm ...
 * </p>
 * 
 * @author rgould, Refractions Research, Inc.
 * @author $Author: jive $ (last modification)
 * @version $Id: DataNamespacesNewForm.java,v 1.2 2004/02/27 22:07:06 jive Exp $
 */
public class DataNamespacesNewForm extends ActionForm {
    private String prefix;
    
    public void reset(ActionMapping arg0, HttpServletRequest request) {
        super.reset(arg0, request);

        prefix ="";
    }

    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        if ((getPrefix() == null) || getPrefix().equals("")) {
            errors.add("prefix",
            new ActionError("error.prefix.required", getPrefix()));
        } else if (!Pattern.matches("^\\w*$", getPrefix())) {
            errors.add("dataStoreID",
            new ActionError("error.prefix.invalid", getPrefix()));
        }        
        return errors;
    }
	/**
	 * Access prefix property.
	 * 
	 * @return Returns the prefix.
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Set prefix to prefix.
	 *
	 * @param prefix The prefix to set.
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
