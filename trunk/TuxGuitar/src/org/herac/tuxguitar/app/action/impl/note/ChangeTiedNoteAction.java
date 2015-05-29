/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action.impl.note;

import java.util.Iterator;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.editors.tab.Caret;
import org.herac.tuxguitar.app.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGVoice;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeTiedNoteAction extends TGActionBase{
	
	public static final String NAME = "action.note.general.tied";
	
	public ChangeTiedNoteAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected void processAction(TGActionContext context){
		Caret caret = getEditor().getTablature().getCaret();
		if(caret.getSelectedNote() != null){
			//comienza el undoable
			UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
			
			getSongManager().getMeasureManager().changeTieNote(caret.getSelectedNote());
			
			//termia el undoable
			addUndoableEdit(undoable.endUndo());
		}else{
			TGNote note = getSongManager().getFactory().newNote();
			note.setValue(0);
			note.setVelocity(caret.getVelocity());
			note.setString(caret.getSelectedString().getNumber());
			note.setTiedNote(true);
			
			TGDuration duration = getSongManager().getFactory().newDuration();
			duration.copyFrom(caret.getDuration());
			
			setTiedNoteValue(note,caret);
			
			//comienza el undoable
			UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
			
			getSongManager().getMeasureManager().addNote(caret.getSelectedBeat(),note,duration, caret.getVoice());
			
			//termia el undoable
			addUndoableEdit(undoable.endUndo());
		}
		TuxGuitar.getInstance().getFileHistory().setUnsavedFile();
		updateSong();
	}
	
	private void setTiedNoteValue(TGNote note,Caret caret){
		TGMeasure measure = caret.getMeasure();
		TGVoice voice = getSongManager().getMeasureManager().getPreviousVoice( measure.getBeats(), caret.getSelectedBeat(), caret.getVoice());
		while( measure != null){
			while( voice != null ){
				if(voice.isRestVoice()){
					note.setValue(0);
					return;
				}
				// Check if is there any note at same string.
				Iterator<TGNote> it = voice.getNotes().iterator();
				while( it.hasNext() ){
					TGNote current = (TGNote) it.next();
					if(current.getString() == note.getString()){
						note.setValue( current.getValue() );
						return;
					}
				}
				voice = getSongManager().getMeasureManager().getPreviousVoice( measure.getBeats(), voice.getBeat(), caret.getVoice());
			}
			measure = getSongManager().getTrackManager().getPrevMeasure(measure);
			if( measure != null ){
				voice = getSongManager().getMeasureManager().getLastVoice( measure.getBeats() , caret.getVoice());
			}
		}
	}
	
	public void updateSong() {
		updateMeasure(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
}
