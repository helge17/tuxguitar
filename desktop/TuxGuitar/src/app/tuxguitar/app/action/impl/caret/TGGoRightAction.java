package app.tuxguitar.app.action.impl.caret;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.transport.TGTransport;
import app.tuxguitar.app.view.component.tab.Caret;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.measure.TGAddMeasureAction;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

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
			Tablature tablature = TablatureEditor.getInstance(getContext()).getTablature();
			if (!Boolean.TRUE.equals(context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_KEEP_SELECTION))) {
				tablature.getSelector().clearSelection();
			}
			Caret caret = tablature.getCaret();
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
