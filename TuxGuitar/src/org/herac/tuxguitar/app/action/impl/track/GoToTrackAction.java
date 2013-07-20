/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action.impl.track;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GoToTrackAction extends TGActionBase{
	
	public static final String NAME = "action.track.goto";
	
	public static final String PROPERTY_TRACK = "track";
	
	public GoToTrackAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE);
	}
	
	protected void processAction(TGActionContext context){
		Object data = context.getAttribute(PROPERTY_TRACK);
		if(data instanceof TGTrackImpl){
			TGTrackImpl track = (TGTrackImpl)data;
			getEditor().getTablature().getCaret().update(track.getNumber());
		}
	}
}
