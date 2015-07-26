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
import org.herac.tuxguitar.app.undo.undoables.measure.UndoableAddMeasure;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.song.models.TGMeasure;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GoNextMeasureAction extends TGActionBase{
	
	public static final String NAME = "action.measure.go-next";
	
	public GoNextMeasureAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		Caret caret = getEditor().getTablature().getCaret();
		//si es el ultimo compas, agrego uno nuevo
		if(getSongManager().getTrackManager().isLastMeasure(caret.getMeasure())){
			int number = (caret.getSong().countMeasureHeaders() + 1);
			
			//comienza el undoable
			UndoableAddMeasure undoable = UndoableAddMeasure.startUndo(number);
			
			this.getSongManager().addNewMeasure(caret.getSong(), number);
			this.updateMeasure(number);
			this.moveToNext();
			
			//termia el undoable
			this.addUndoableEdit(undoable.endUndo());
		}
		else{
			this.moveToNext();
		}
	}
	
	private void moveToNext(){
		if(TuxGuitar.getInstance().getPlayer().isRunning()){
			TuxGuitar.getInstance().getTransport().gotoNext();
		}
		else{
			Caret caret = getEditor().getTablature().getCaret();
			TGTrackImpl track = caret.getTrack();
			TGMeasure measure = getSongManager().getTrackManager().getNextMeasure(caret.getMeasure());
			if(track != null && measure != null){
				caret.update(track.getNumber(),measure.getStart(),caret.getSelectedString().getNumber());
			}
		}
	}
}
