/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.track;

import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.actions.ActionData;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GoToTrackAction extends Action{
	
	public static final String NAME = "action.track.goto";
	
	public static final String PROPERTY_TRACK = "track";
	
	public GoToTrackAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE);
	}
	
	protected int execute(ActionData actionData){
		Object data = actionData.get(PROPERTY_TRACK);
		if(data instanceof TGTrackImpl){
			TGTrackImpl track = (TGTrackImpl)data;
			getEditor().getTablature().getCaret().update(track.getNumber());
		}
		return 0;
	}
}
