/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions.note;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.actions.ActionData;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGNote;


/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeNoteAction extends Action {
	
	private static final String NAME = "action.note.general.change";
	
	public static final String PROPERTY_FRET_NUMBER = "fretNumber";
	
	private static final int DELAY = 1000;
	
	private static int lastAddedValue;
	
	private static int lastAddedString;
	
	private static long lastAddedStart;
	
	private static long lastAddedTime;
	
	public ChangeNoteAction() {
		this(NAME, AUTO_LOCK | AUTO_UNLOCK | DISABLE_ON_PLAYING);
	}
	
	protected ChangeNoteAction(String name, int flags) {
		super(name, flags);
	}
	
	protected int execute(ActionData actionData){
		Object propertyFretNumber = actionData.get(PROPERTY_FRET_NUMBER);
		if( propertyFretNumber instanceof Integer ){
			int value = ((Integer)propertyFretNumber).intValue();
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
						if( newValue < 30 || getSongManager().isPercussionChannel(caret.getTrack().getChannelId()) ){
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
		TuxGuitar.instance().playBeat(getEditor().getTablature().getCaret().getSelectedBeat());
	}
}