package org.herac.tuxguitar.editor.action.note;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.TGVoice;
import org.herac.tuxguitar.util.TGBeatRange;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGNoteRange;

public class TGDeleteNoteOrRestAction extends TGActionBase {

	public static final String NAME = "action.beat.general.delete-note-or-rest";

	public TGDeleteNoteOrRestAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		TGNoteRange noteRange = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGBeatRange beats = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT_RANGE);
		if (noteRange!=null && !noteRange.isEmpty()) {
			for (TGNote note : noteRange.getNotes()) {
				TGVoice voice = note.getVoice();
				TGBeat beat = voice.getBeat();
				TGMeasure measure = beat.getMeasure();
				removeNote(context, measure, beat, voice, note.getString());
			}
		}
		else if (beats!=null && !beats.isEmpty()) {
			TGVoice voice = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_VOICE);
			TGString string = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
			for (TGBeat beat : beats.getBeats()) {
				removeNote(context, beat.getMeasure(), beat, voice, string.getNumber());
			}
		}
	}
	private void removeNote(TGActionContext context, TGMeasure measure, TGBeat beat, TGVoice voice, int string) {
		TGSongManager songManager = getSongManager(context);
		if (beat.isTextBeat() && beat.isRestBeat()) {
			songManager.getMeasureManager().removeText(beat);
		} else if (voice.isRestVoice()) {
			if (songManager.isFreeEditionMode(measure)) {
				songManager.getMeasureManager().removeVoiceWithoutControl(voice);
			} else {
				songManager.getMeasureManager().removeVoice(voice, true);
			}
		} else {
			songManager.getMeasureManager().removeNote(measure, beat.getStart(), voice.getIndex(), string);
		}
	}

}
