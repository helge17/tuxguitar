/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.measure;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.editors.tab.TGTrackImpl;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableAddMeasure;
import org.herac.tuxguitar.song.models.TGMeasure;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GoNextMeasureAction extends Action{
	public static final String NAME = "action.measure.go-next";
	
	public GoNextMeasureAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		Caret caret = getEditor().getTablature().getCaret();
		//si es el ultimo compas, agrego uno nuevo
		if(getSongManager().getTrackManager().isLastMeasure(caret.getMeasure())){
			int number = (getSongManager().getSong().countMeasureHeaders() + 1);
			
			//comienza el undoable
			UndoableAddMeasure undoable = UndoableAddMeasure.startUndo(number);
			
			this.getSongManager().addNewMeasure(number);
			this.fireUpdate(number);
			this.moveToNext();
			
			//termia el undoable
			this.addUndoableEdit(undoable.endUndo());
		}
		else{
			this.moveToNext();
		}
		
		return 0;
	}
	
	private void moveToNext(){
		if(TuxGuitar.instance().getPlayer().isRunning()){
			TuxGuitar.instance().getTransport().gotoNext();
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
