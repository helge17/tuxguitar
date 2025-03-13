package org.herac.tuxguitar.app.action.impl.measure;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.impl.caret.TGMoveToAction;
import org.herac.tuxguitar.app.transport.TGTransport;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGGoFirstMeasureAction extends TGActionBase{

	public static final String NAME = "action.measure.go-first";

	public TGGoFirstMeasureAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		if( MidiPlayer.getInstance(getContext()).isRunning() ){
			TGTransport.getInstance(getContext()).gotoFirst();
		}
		else{
			if (!Boolean.TRUE.equals(context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_KEEP_SELECTION))) {
				TablatureEditor.getInstance(getContext()).getTablature().getSelector().clearSelection();
			}
			TGTrack track = (TGTrack) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
			TGMeasure firstMeasure = getSongManager(context).getTrackManager().getFirstMeasure(track);
			if( firstMeasure != null ){
				context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, firstMeasure);
				context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, firstMeasure.getBeat(0));
				TGActionManager.getInstance(this.getContext()).execute(TGMoveToAction.NAME, context);
			}
		}
	}
}
