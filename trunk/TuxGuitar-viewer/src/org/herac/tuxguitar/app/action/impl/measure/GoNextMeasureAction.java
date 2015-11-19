package org.herac.tuxguitar.app.action.impl.measure;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.util.TGContext;

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
