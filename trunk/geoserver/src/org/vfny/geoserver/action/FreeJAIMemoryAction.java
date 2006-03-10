/*
 * Created on Jan 28, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.vfny.geoserver.action;

import javax.media.jai.JAI;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.vfny.geoserver.global.UserContainer;

import com.sun.media.jai.util.SunTileCache;

/**
 * Free JAI Memory by running the Tile Cahce flushing. 
 * <p>
 * This represents an action that interacts with the running GeoServer
 * application (it is not really a config action, it is just that I want the
 * user to be logged in).
 * </p>
 * @author $Author: Alessio Fabiani $ (last modification)
 */
public class FreeJAIMemoryAction extends ConfigAction {
    /* (non-Javadoc)
     * @see org.vfny.geoserver.action.ConfigAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, org.vfny.geoserver.global.UserContainer, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            UserContainer user, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	final JAI defaultJAI=JAI.getDefaultInstance();
    	final SunTileCache JAICache=(SunTileCache)defaultJAI.getTileCache();
    	final long capacityBefore = JAICache.getMemoryCapacity();
    	final long usageBefore=JAICache.getCacheMemoryUsed();
    	
    	JAICache.flush();
    	JAICache.setMemoryCapacity(0);//to be sure we realease all tiles
    	System.gc();
    	System.gc();
    	System.gc();
    	System.gc();
    	System.gc();
    	System.gc();
    	JAICache.setMemoryCapacity(capacityBefore);
    	final  long usageAfter=JAICache.getCacheMemoryUsed();
        // Provide status message
        //
        ActionErrors errors = new ActionErrors();
        errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("message.JAI.memory",new Long(usageAfter-usageBefore)));        
        request.setAttribute(Globals.ERROR_KEY, errors);
        
    	// return back to the admin screen
    	//
        return mapping.findForward("admin");
    }
}