package org.herac.tuxguitar.android.action.impl.edit;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGSetVoice2Action extends TGActionBase{
	
	public static final String NAME = "action.edit.voice-2";
	
	public TGSetVoice2Action(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		getEditor().getCaret().setVoice(1);
	}
}
