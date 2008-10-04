/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.note;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.editors.tab.TGMeasureImpl;
import org.herac.tuxguitar.gui.system.keybindings.KeyBindingConstants;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGNote;


/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeNoteAction extends Action {
	
	public static final String NAME = "action.note.general.change";
	
	private static final int DELAY = 1000;
	
	private static int lastAddedValue;
	
	private static int lastAddedString;
	
	private static long lastAddedStart;
	
	private static long lastAddedTime;
	
	public ChangeNoteAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | DISABLE_ON_PLAYING);
	}
	
	protected int execute(TypedEvent e){
		if (e instanceof KeyEvent) {
			int value = getValueOf(((KeyEvent) e).keyCode);
			if (value >= 0) {
				Caret caret = getEditor().getTablature().getCaret();
				TGMeasureImpl measure = caret.getMeasure();
				TGDuration duration = caret.getDuration();
				int string = caret.getSelectedString().getNumber();
				int velocity = caret.getVelocity();
				long start = caret.getPosition();
				long time = System.currentTimeMillis();
				
				if(lastAddedStart == start && lastAddedString == string){
					if (lastAddedValue > 0 && lastAddedValue < 10 && time <  ( lastAddedTime + DELAY ) ){
						int newValue = ( ( lastAddedValue * 10 ) + value );
						if(newValue < 30 || caret.getTrack().isPercussionTrack()){
							value = newValue;
						}
					}
				}
				
				this.addNote(measure, duration, start, value, string, velocity);
				this.fireUpdate(measure.getNumber());
				
				lastAddedValue = value;
				lastAddedStart = start;
				lastAddedString = string;
				lastAddedTime = time;
				
				return AUTO_UPDATE;
			}
		}
		return 0;
	}
	
	private void addNote(TGMeasureImpl measure,TGDuration duration, long start, int value,int string, int velocity) {
		TGNote note = getSongManager().getFactory().newNote();
		note.setValue(value);
		note.setVelocity(velocity);
		note.setString(string);
		
		//comienza el undoable
		UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		
		//getSongManager().getMeasureManager().addNote(measure,start,note,duration.clone(getSongManager().getFactory()) );
		getSongManager().getMeasureManager().addNote(measure,start,note,duration.clone(getSongManager().getFactory()), getEditor().getTablature().getCaret().getVoice() );
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo());
		
		//reprodusco las notas en el pulso
		getEditor().getTablature().getCaret().getSelectedBeat().play();
	}
	
	private int getValueOf(int keyCode){
		switch(keyCode){
		case KeyBindingConstants.NUMBER_0:
		case KeyBindingConstants.KEYPAD_0:
			return 0;
		case KeyBindingConstants.NUMBER_1:
		case KeyBindingConstants.KEYPAD_1:
			return 1;
		case KeyBindingConstants.NUMBER_2:
		case KeyBindingConstants.KEYPAD_2:
			return 2;
		case KeyBindingConstants.NUMBER_3:
		case KeyBindingConstants.KEYPAD_3:
			return 3;
		case KeyBindingConstants.NUMBER_4:
		case KeyBindingConstants.KEYPAD_4:
			return 4;
		case KeyBindingConstants.NUMBER_5:
		case KeyBindingConstants.KEYPAD_5:
			return 5;
		case KeyBindingConstants.NUMBER_6:
		case KeyBindingConstants.KEYPAD_6:
			return 6;
		case KeyBindingConstants.NUMBER_7:
		case KeyBindingConstants.KEYPAD_7:
			return 7;
		case KeyBindingConstants.NUMBER_8:
		case KeyBindingConstants.KEYPAD_8:
			return 8;
		case KeyBindingConstants.NUMBER_9:
		case KeyBindingConstants.KEYPAD_9:
			return 9;
		}
		return -1;
	}
}