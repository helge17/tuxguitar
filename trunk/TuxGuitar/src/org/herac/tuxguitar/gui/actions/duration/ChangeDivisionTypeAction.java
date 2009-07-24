/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.duration;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGDivisionType;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeDivisionTypeAction extends Action{
	public static final String NAME = "action.note.duration.change-division-type";
	
	public ChangeDivisionTypeAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		//comienza el undoable
		UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
		
		boolean isKeyEvent = false;
		if(e instanceof KeyEvent){
			isKeyEvent = true;
		}
		if(!isKeyEvent){
			TGDivisionType divisionType = defaultDivisionType();
			if(e.widget.getData() != null && e.widget.getData() instanceof TGDivisionType){
				divisionType = (TGDivisionType)e.widget.getData();
			}
			
			if(getSelectedDuration().getDivision().isEqual(divisionType)){
				setDivisionType(noTuplet());
			}else{
				setDivisionType(divisionType);
			}
		}
		else{
			if(getSelectedDuration().getDivision().isEqual(TGDivisionType.NORMAL)){
				setDivisionType(defaultDivisionType());
			}else{
				setDivisionType(noTuplet());
			}
		}
		setDurations();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo());
		
		return 0;
	}
	
	private TGDivisionType noTuplet(){
		TGDivisionType divisionType = getSongManager().getFactory().newDivisionType();
		divisionType.setEnters(1);
		divisionType.setTimes(1);
		return divisionType;
	}
	
	private TGDivisionType defaultDivisionType(){
		TGDivisionType divisionType = getSongManager().getFactory().newDivisionType();
		divisionType.setEnters(3);
		divisionType.setTimes(2);
		return divisionType;
	}
	
	private void setDivisionType(TGDivisionType divisionType){
		getSelectedDuration().getDivision().setEnters(divisionType.getEnters());
		getSelectedDuration().getDivision().setTimes(divisionType.getTimes());
	}
	
	private void setDurations() {
		Caret caret = getEditor().getTablature().getCaret();
		caret.changeDuration(getSelectedDuration().clone(getSongManager().getFactory()));
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
	
	public TGDuration getSelectedDuration(){
		return getEditor().getTablature().getCaret().getDuration();
	}
}
