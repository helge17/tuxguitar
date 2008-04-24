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
import org.herac.tuxguitar.song.models.TGTupleto;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeTupletoDurationAction extends Action{
	public static final String NAME = "action.note.duration.change-tupleto";
	
	public ChangeTupletoDurationAction() {
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
			TGTupleto tupleto = defaultTupleto();
			if(e.widget.getData() != null && e.widget.getData() instanceof TGTupleto){
				tupleto = (TGTupleto)e.widget.getData();
			}
			
			if(getSelectedDuration().getTupleto().isEqual(tupleto)){
				setTupleto(noTupleto());
			}else{
				setTupleto(tupleto);
			}
		}
		else{
			if(getSelectedDuration().getTupleto().isEqual(TGTupleto.NORMAL)){
				setTupleto(defaultTupleto());
			}else{
				setTupleto(noTupleto());
			}
		}
		setDurations();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo());
		
		return 0;
	}
	
	private TGTupleto noTupleto(){
		TGTupleto tupleto = getSongManager().getFactory().newTupleto();
		tupleto.setEnters(1);
		tupleto.setTimes(1);
		return tupleto;
	}
	
	private TGTupleto defaultTupleto(){
		TGTupleto tupleto = getSongManager().getFactory().newTupleto();
		tupleto.setEnters(3);
		tupleto.setTimes(2);
		return tupleto;
	}
	
	private void setTupleto(TGTupleto tupleto){
		getSelectedDuration().getTupleto().setEnters(tupleto.getEnters());
		getSelectedDuration().getTupleto().setTimes(tupleto.getTimes());
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
