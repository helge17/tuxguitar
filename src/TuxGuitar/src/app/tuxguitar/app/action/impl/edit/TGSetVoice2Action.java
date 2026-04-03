package app.tuxguitar.app.action.impl.edit;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;

public class TGSetVoice2Action extends TGActionBase{

	public static final String NAME = "action.edit.voice-2";

	public TGSetVoice2Action(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TablatureEditor.getInstance(getContext()).getTablature().getCaret().setVoice(1);
	}
}
