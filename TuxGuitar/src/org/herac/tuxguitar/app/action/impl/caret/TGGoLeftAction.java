package org.herac.tuxguitar.app.action.impl.caret;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.transport.TGTransport;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;

public class TGGoLeftAction extends TGActionBase{
	
	public static final String NAME = "action.caret.go-left";
	
	public TGGoLeftAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		if( MidiPlayer.getInstance(getContext()).isRunning() ){
			TGTransport.getInstance(getContext()).gotoPrevious();
		}
		else{
			TablatureEditor.getInstance(getContext()).getTablature().getCaret().moveLeft();
		}
	}
}
