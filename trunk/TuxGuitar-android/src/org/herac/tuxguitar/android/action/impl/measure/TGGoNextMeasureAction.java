package org.herac.tuxguitar.android.action.impl.measure;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.android.TuxGuitar;
import org.herac.tuxguitar.android.action.TGActionBase;
import org.herac.tuxguitar.android.view.tablature.TGCaret;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGContext;

public class TGGoNextMeasureAction extends TGActionBase {
	
	public static final String NAME = "action.measure.go-next";
	
	public TGGoNextMeasureAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TuxGuitar tuxguitar = TuxGuitar.getInstance(getContext());
		if( tuxguitar.getPlayer().isRunning()){
			tuxguitar.getTransport().gotoNext();
		}
		else{
			TGCaret caret = getEditor().getCaret();
			TGTrack track = caret.getTrack();
			TGMeasure measure = getSongManager(context).getTrackManager().getNextMeasure(caret.getMeasure());
			if(track != null && measure != null){
				caret.update(track.getNumber(), measure.getStart(), caret.getSelectedString().getNumber());
			}
		}
	}
}
