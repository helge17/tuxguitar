package org.herac.tuxguitar.editor.action.note;

import java.util.Iterator;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGChord;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.util.TGContext;

public class TGInsertChordAction extends TGActionBase {
	
	public static final String NAME = "action.beat.general.insert-chord";
	
	public TGInsertChordAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		TGSongManager songManager = getSongManager(context);
		TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		TGBeat beat = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		TGVoice voice = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE);
		TGChord chord = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHORD);
		Integer velocity = (Integer) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY);
		
		boolean restBeat = beat.isRestBeat();
		if(!restBeat || chord.countNotes() > 0 ) {
			// Add the chord notes to the tablature if this is a "rest" beat
			if( restBeat ){
				
				Iterator<TGString> it = track.getStrings().iterator();
				while (it.hasNext()) {
					TGString string = (TGString) it.next();
					
					int value = chord.getFretValue(string.getNumber() - 1);
					if (value >= 0) {
						TGNote note = songManager.getFactory().newNote();
						note.setValue(value);
						note.setVelocity(velocity);
						note.setString(string.getNumber());
						
						TGDuration duration = songManager.getFactory().newDuration();
						duration.copyFrom(voice.getDuration());
						
						songManager.getMeasureManager().addNote(beat, note, duration, voice.getIndex());
					}
				}
			}
			
			songManager.getMeasureManager().addChord(beat, chord);
		}
	}
}
