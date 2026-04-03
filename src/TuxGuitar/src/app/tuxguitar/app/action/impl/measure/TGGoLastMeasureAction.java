package app.tuxguitar.app.action.impl.measure;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.caret.TGMoveToAction;
import app.tuxguitar.app.transport.TGTransport;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGGoLastMeasureAction extends TGActionBase{

	public static final String NAME = "action.measure.go-last";

	public TGGoLastMeasureAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		if( MidiPlayer.getInstance(getContext()).isRunning() ){
			TGTransport.getInstance(getContext()).gotoLast();
		}
		else{
			if (!Boolean.TRUE.equals(context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_KEEP_SELECTION))) {
				TablatureEditor.getInstance(getContext()).getTablature().getSelector().clearSelection();
			}
			TGTrack track = (TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
			TGMeasure lastMeasure = getSongManager(context).getTrackManager().getLastMeasure(track);
			if( lastMeasure != null ){
				context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, lastMeasure);
				context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, lastMeasure.getBeat(0));
				TGActionManager.getInstance(this.getContext()).execute(TGMoveToAction.NAME, context);
			}
		}
	}
}
