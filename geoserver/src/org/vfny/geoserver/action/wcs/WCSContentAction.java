/* Copyright (c) 2001, 2003 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */

package org.vfny.geoserver.action.wcs;

import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.vfny.geoserver.action.ConfigAction;
import org.vfny.geoserver.config.WCSConfig;
import org.vfny.geoserver.form.wcs.WCSContentForm;
import org.vfny.geoserver.global.UserContainer;


/**
 * DOCUMENT ME!
 *
 * @author $Author: Alessio Fabiani (alessio.fabiani@gmail.com) $ (last modification)
 * @author $Author: Simone Giannecchini (simboss_ml@tiscali.it) $ (last modification)
 */
public final class WCSContentAction extends ConfigAction {
    public ActionForward execute(ActionMapping mapping, ActionForm form,
        UserContainer user, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        WCSContentForm contentForm = (WCSContentForm) form;

        boolean enabled = contentForm.isEnabled();

        if (contentForm.isEnabledChecked() == false) {
            enabled = false;
        }
        
        String onlineResource = contentForm.getOnlineResource();
//        String[] selectedFeatures = contentForm.getSelectedFeatures();
//        String[] features = contentForm.getFeatures();

        WCSConfig config = getWCSConfig();
        config.setEnabled(enabled);
        config.setOnlineResource(new URL(onlineResource));

        getApplicationState().notifyConfigChanged();

        return mapping.findForward("config");
    }
}
