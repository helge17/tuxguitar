package app.tuxguitar.app.action.impl.measure;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.editors.tab.Caret;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.graphics.control.TGTrackImpl;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.util.TGContext;

public class GoNextMeasureAction extends TGActionBase {

	public static final String NAME = "action.measure.go-next";

	public GoNextMeasureAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		if( TuxGuitar.instance().getPlayer().isRunning()){
			TuxGuitar.instance().getTransport().gotoNext();
		}
		else{
			Caret caret = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret();
			TGTrackImpl track = caret.getTrack();
			TGMeasure measure = getSongManager(context).getTrackManager().getNextMeasure(caret.getMeasure());
			if(track != null && measure != null){
				caret.update(track.getNumber(),measure.getStart(),caret.getSelectedString().getNumber());
			}
		}
	}
}
