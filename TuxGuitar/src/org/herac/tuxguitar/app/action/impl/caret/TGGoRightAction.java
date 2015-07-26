package org.herac.tuxguitar.app.action.impl.caret;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.transport.TGTransport;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.editor.action.measure.TGAddMeasureAction;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGGoRightAction extends TGActionBase{
	
	public static final String NAME = "action.caret.go-right";
	
	public TGGoRightAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		if( MidiPlayer.getInstance(getContext()).isRunning() ){
			TGTransport.getInstance(getContext()).gotoNext();
		}
		else{
			Caret caret = TablatureEditor.getInstance(getContext()).getTablature().getCaret();
			if(!caret.moveRight()){
				TGSong song = ((TGSong) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG));
				context.setAttribute(TGAddMeasureAction.ATTRIBUTE_MEASURE_NUMBER, Integer.valueOf((song.countMeasureHeaders() + 1)));
				
				TGActionManager tgActionManager = TGActionManager.getInstance(getContext());
				tgActionManager.execute(TGAddMeasureAction.NAME, context);
				
				caret.moveRight();
			}
		}
	}
}
