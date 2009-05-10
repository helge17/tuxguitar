/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.insert;

import java.util.Iterator;

import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.chord.ChordDialog;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.editors.tab.TGMeasureImpl;
import org.herac.tuxguitar.gui.editors.tab.TGTrackImpl;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGVoice;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class InsertChordAction extends Action {
	public static final String NAME = "action.insert.chord";
	
	public InsertChordAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		Caret caret = getEditor().getTablature().getCaret();
		TGTrackImpl track = caret.getTrack();
		TGMeasureImpl measure = caret.getMeasure();
		TGBeat beat = caret.getSelectedBeat();
		if (track != null && measure != null && beat != null) {
			//Si el acorde llego en el data del widget solo lo agrego
			if(e.widget.getData() instanceof TGChord){
				TGChord chord = ((TGChord)e.widget.getData()).clone(getSongManager().getFactory());
				insertChord(chord, track, measure, beat, caret.getVoice());
			}
			//sino muestro el editor de acordes
			else{
				Shell shell = TuxGuitar.instance().getShell();
				ChordDialog dialog = new ChordDialog();
				
				int result = dialog.open(shell, measure,beat, caret.getPosition());
				if( result == ChordDialog.RESULT_SAVE ){
					insertChord(dialog.getChord(), track, measure, beat, caret.getVoice());
				}
				else if( result == ChordDialog.RESULT_CLEAN ){
					removeChord( measure, beat);
				}
			}
		}
		return 0;
	}
	
	protected void insertChord(TGChord chord, TGTrackImpl track, TGMeasureImpl measure, TGBeat beat, int voiceIndex) {
		boolean restBeat = beat.isRestBeat();
		if(!restBeat || chord.countNotes() > 0 ) {
			
			//comienza el undoable
			UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
			
			// Add the chord notes to the tablature
			// Only if this is a "rest" beat
			TGVoice voice = beat.getVoice(voiceIndex);
			if( restBeat ){
				
				Iterator it = track.getStrings().iterator();
				while (it.hasNext()) {
					TGString string = (TGString) it.next();
					
					int value = chord.getFretValue(string.getNumber() - 1);
					if (value >= 0) {
						TGNote note = getSongManager().getFactory().newNote();
						note.setValue(value);
						note.setVelocity(getEditor().getTablature().getCaret().getVelocity());
						note.setString(string.getNumber());
						
						TGDuration duration = getSongManager().getFactory().newDuration();
						voice.getDuration().copy(duration);
						
						getSongManager().getMeasureManager().addNote(beat,note,duration,voice.getIndex());
					}
				}
			}
			
			getSongManager().getMeasureManager().addChord(beat, chord);
			TuxGuitar.instance().getFileHistory().setUnsavedFile();
			fireUpdate(measure.getNumber());
			
			//termia el undoable
			addUndoableEdit(undoable.endUndo());
		}
	}
	
	protected void removeChord(TGMeasureImpl measure, TGBeat beat) {
		if( beat.isChordBeat() ){
			//comienza el undoable
			UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
			
			getSongManager().getMeasureManager().removeChord(measure, beat.getStart());
			TuxGuitar.instance().getFileHistory().setUnsavedFile();
			fireUpdate(measure.getNumber());
			
			//termia el undoable
			addUndoableEdit(undoable.endUndo());
		}
	}
}