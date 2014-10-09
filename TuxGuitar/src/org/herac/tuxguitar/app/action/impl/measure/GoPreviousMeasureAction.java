/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action.impl.measure;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GoPreviousMeasureAction extends TGActionBase{
	
	public static final String NAME = "action.measure.go-previous";
	
	public GoPreviousMeasureAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		if(TuxGuitar.getInstance().getPlayer().isRunning()){
			TuxGuitar.getInstance().getTransport().gotoPrevious();
		}
		else{
			Caret caret = getEditor().getTablature().getCaret();
			TGTrackImpl track = caret.getTrack();
			TGMeasureImpl measure = (TGMeasureImpl)getSongManager().getTrackManager().getPrevMeasure(caret.getMeasure());
			if(track != null && measure != null){
				caret.update(track.getNumber(),measure.getStart(),caret.getSelectedString().getNumber());
			}
		}
	}
}
