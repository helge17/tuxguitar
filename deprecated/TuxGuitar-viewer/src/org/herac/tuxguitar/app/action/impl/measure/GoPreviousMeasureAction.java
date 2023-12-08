package org.herac.tuxguitar.app.action.impl.measure;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.util.TGContext;

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
