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
import org.herac.tuxguitar.util.TGContext;

public class TGGoPreviousMeasureAction extends TGActionBase{

	public static final String NAME = "action.measure.go-previous";

	public TGGoPreviousMeasureAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		if( MidiPlayer.getInstance(getContext()).isRunning() ){
			TGTransport.getInstance(getContext()).gotoPrevious();
		}
		else{
			if (!Boolean.TRUE.equals(context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_KEEP_SELECTION))) {
				TablatureEditor.getInstance(getContext()).getTablature().getSelector().clearSelection();
			}
			TGMeasure measureFrom = (TGMeasure) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
			TGMeasure measureTo = getSongManager(context).getTrackManager().getPrevMeasure(measureFrom);
			if( measureTo != null ){
				context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measureTo);
				context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, measureTo.getBeat(0));
				TGActionManager.getInstance(this.getContext()).execute(TGMoveToAction.NAME, context);
			}
		}
	}
}
