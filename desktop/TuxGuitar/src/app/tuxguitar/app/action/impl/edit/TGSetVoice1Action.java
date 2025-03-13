package app.tuxguitar.app.action.impl.edit;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGSetVoice1Action extends TGActionBase{

	public static final String NAME = "action.edit.voice-1";

	public TGSetVoice1Action(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TablatureEditor.getInstance(getContext()).getTablature().getCaret().setVoice(0);
	}
}
