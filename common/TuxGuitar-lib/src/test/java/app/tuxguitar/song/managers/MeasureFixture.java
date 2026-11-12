package app.tuxguitar.song.managers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGText;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;

/**
 * A 4/4 measure of a new song, filled beat by beat, and printed as a layout of voice 0:
 * "kind@position", position in quarters from the measure start;
 * n = note, r = rest, t = rest carrying a text, _ = beat whose voice 0 is empty.
 */
public class MeasureFixture {

	public static final long QUARTER = TGDuration.WHOLE_PRECISE_DURATION / 4;

	public final TGSongManager songManager = new TGSongManager();
	public final TGFactory factory = songManager.getFactory();
	public final TGMeasureManager manager = songManager.getMeasureManager();
	public final TGMeasure measure = songManager.newSong().getTrack(0).getMeasure(0);

	// absolute precise position of "offset" from the measure start
	public long at(long offset) {
		return measure.getPreciseStart() + offset;
	}

	public TGDuration duration(int value) {
		TGDuration duration = factory.newDuration();
		duration.setValue(value);
		return duration;
	}

	public TGBeat beatAt(long offset) {
		TGBeat beat = manager.getBeatPrecise(measure, at(offset));
		if (beat == null) {
			beat = factory.newBeat();
			beat.setPreciseStart(at(offset));
			beat.setMeasure(measure);
			manager.addBeat(measure, beat);
			manager.orderBeats(measure);
		}
		return beat;
	}

	public TGVoice rest(long offset, int value, int voiceIndex) {
		TGVoice voice = beatAt(offset).getVoice(voiceIndex);
		voice.setEmpty(false);
		voice.getDuration().copyFrom(duration(value));
		return voice;
	}

	public TGVoice textRest(long offset, int value, int voiceIndex) {
		TGVoice voice = rest(offset, value, voiceIndex);
		TGText text = factory.newText();
		text.setValue("text");
		voice.getBeat().setText(text);
		return voice;
	}

	public TGVoice note(long offset, int value, int voiceIndex) {
		TGVoice voice = rest(offset, value, voiceIndex);
		TGNote note = factory.newNote();
		note.setString(1);
		voice.addNote(note);
		return voice;
	}

	// quarter notes in voice 0 at each of the given quarter positions
	public void quarterNotes(int... positions) {
		for (int position : positions) {
			note(position * QUARTER, TGDuration.QUARTER, 0);
		}
	}

	// in position order: some operations append beats without sorting the measure
	public String layout() {
		List<TGBeat> beats = new ArrayList<TGBeat>(measure.getBeats());
		beats.sort(Comparator.comparingLong(TGBeat::getPreciseStart));
		StringBuilder layout = new StringBuilder();
		for (TGBeat beat : beats) {
			TGVoice voice = beat.getVoice(0);
			String kind = voice.isEmpty() ? "_" : (!voice.isRestVoice() ? "n" : (beat.isTextBeat() ? "t" : "r"));
			double position = (beat.getPreciseStart() - measure.getPreciseStart()) / (double) QUARTER;
			String formatted = (position == Math.rint(position)) ? String.valueOf((long) position) : String.valueOf(position);
			layout.append(layout.length() == 0 ? "" : " ").append(kind).append('@').append(formatted);
		}
		return layout.toString();
	}

	// a note at a fret of a string, in voice 0 of the beat at the measure start
	public TGNote fretted(int string, int fret) {
		return fretted(0, string, fret);
	}

	// a note at a fret of a string, in voice 0 of the beat at "offset" from the measure start
	public TGNote fretted(long offset, int string, int fret) {
		return fretted(offset, string, fret, 0);
	}

	// a note at a fret of a string, in voice "voiceIndex" of the beat at "offset" from the measure start
	public TGNote fretted(long offset, int string, int fret, int voiceIndex) {
		TGVoice voice = beatAt(offset).getVoice(voiceIndex);
		TGNote note = factory.newNote();
		note.setString(string);
		note.setValue(fret);
		voice.setEmpty(false);
		voice.addNote(note);
		return note;
	}

	// the track plays on the percussion channel
	public void percussion() {
		TGTrack track = measure.getTrack();
		songManager.getChannel(track.getSong(), track.getChannelId()).setBank(TGChannel.DEFAULT_PERCUSSION_BANK);
	}
}
