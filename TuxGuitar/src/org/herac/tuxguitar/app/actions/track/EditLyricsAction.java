/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.track;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.actions.ActionData;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EditLyricsAction extends Action{
	
	public static final String NAME = "action.track.lyrics";
	
	public EditLyricsAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(ActionData actionData){
		if(TuxGuitar.instance().getLyricEditor().isDisposed()){
			TuxGuitar.instance().getLyricEditor().show();
		}else{
			TuxGuitar.instance().getLyricEditor().dispose();
		}
		return 0;
	}
}
