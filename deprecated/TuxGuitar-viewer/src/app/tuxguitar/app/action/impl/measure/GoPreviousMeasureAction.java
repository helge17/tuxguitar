package app.tuxguitar.app.action.impl.measure;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.editors.tab.Caret;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.graphics.control.TGMeasureImpl;
import app.tuxguitar.graphics.control.TGTrackImpl;
import app.tuxguitar.util.TGContext;

public class GoPreviousMeasureAction extends TGActionBase {

	public static final String NAME = "action.measure.go-previous";

	public GoPreviousMeasureAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		if(TuxGuitar.instance().getPlayer().isRunning()){
			TuxGuitar.instance().getTransport().gotoPrevious();
		}
		else{
			Caret caret = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret();
			TGTrackImpl track = caret.getTrack();
			TGMeasureImpl measure = (TGMeasureImpl)getSongManager(context).getTrackManager().getPrevMeasure(caret.getMeasure());
			if(track != null && measure != null){
				caret.update(track.getNumber(),measure.getStart(),caret.getSelectedString().getNumber());
			}
		}
	}
}
