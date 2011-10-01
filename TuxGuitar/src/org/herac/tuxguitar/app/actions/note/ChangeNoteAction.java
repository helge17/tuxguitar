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
import org.herac.tuxguitar.app.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGTrack;


/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeNoteAction extends Action {
	
	private static final String NAME = "action.note.general.change";
	
	public static final String PROPERTY_TRACK = "track";
	public static final String PROPERTY_START = "start";
	public static final String PROPERTY_FRET = "fret";
	public static final String PROPERTY_STRING = "string";
	public static final String PROPERTY_VELOCITY = "velocity";
	public static final String PROPERTY_DURATION = "duration";
	
	public ChangeNoteAction() {
		this(NAME, AUTO_LOCK | AUTO_UNLOCK | DISABLE_ON_PLAYING);
	}
	
	protected ChangeNoteAction(String name, int flags) {
		super(name, flags);
	}
	
	protected int execute(ActionData actionData){
		long start = getPropertyStart(actionData);
		int fret = getPropertyFret(actionData);
		int string = getPropertyString(actionData);
		int velocity = getPropertyVelocity(actionData);
		TGDuration tgDuration = getPropertyDuration(actionData);
		
		TGTrack track = getSongManager().getTrack( getPropertyTrack(actionData) );
		TGMeasure measure = (track != null ? getSongManager().getTrackManager().getMeasureAt(track, start) : null);
		
		if( track != null && measure != null && fret >= 0) {
			this.addNote(measure, tgDuration, start, fret, string, velocity);
			this.fireUpdate(measure.getNumber());
			
			return AUTO_UPDATE;
		}
		
		return 0;
	}
	
	private void addNote(TGMeasure measure,TGDuration duration, long start, int value,int string, int velocity) {
		TGNote note = getSongManager().getFactory().newNote();
		note.setValue(value);
		note.setVelocity(velocity);
		note.setString(string);
		
		//comienza el undoable
		UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		
		getSongManager().getMeasureManager().addNote(measure,start,note,duration.clone(getSongManager().getFactory()), getEditor().getTablature().getCaret().getVoice());
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo());
		
		//reprodusco las notas en el pulso
		TuxGuitar.instance().playBeat(getEditor().getTablature().getCaret().getSelectedBeat());
	}
	
	private long getPropertyStart(ActionData actionData){
		Object propertyValue = actionData.get(PROPERTY_START);
		if( propertyValue instanceof Long ){
			return ((Long)propertyValue).longValue();
		}
		return getEditor().getTablature().getCaret().getPosition();
	}
	
	private int getPropertyTrack(ActionData actionData){
		Object propertyValue = actionData.get(PROPERTY_TRACK);
		if( propertyValue instanceof Integer ){
			return ((Integer)propertyValue).intValue();
		}
		return getEditor().getTablature().getCaret().getTrack().getNumber();
	}
	
	private int getPropertyVelocity(ActionData actionData){
		Object propertyValue = actionData.get(PROPERTY_VELOCITY);
		if( propertyValue instanceof Integer ){
			return ((Integer)propertyValue).intValue();
		}
		return getEditor().getTablature().getCaret().getVelocity();
	}
	
	private TGDuration getPropertyDuration(ActionData actionData){
		Object propertyValue = actionData.get(PROPERTY_DURATION);
		if( propertyValue instanceof TGDuration ){
			return ((TGDuration)propertyValue);
		}
		return getEditor().getTablature().getCaret().getDuration();
	}
	
	private int getPropertyString(ActionData actionData){
		Object propertyValue = actionData.get(PROPERTY_STRING);
		if( propertyValue instanceof Integer ){
			return ((Integer)propertyValue).intValue();
		}
		return getEditor().getTablature().getCaret().getSelectedString().getNumber();
	}
	
	private int getPropertyFret(ActionData actionData){
		Object propertyValue = actionData.get(PROPERTY_FRET);
		if( propertyValue instanceof Integer ){
			return ((Integer)propertyValue).intValue();
		}
		return -1;
	}
}