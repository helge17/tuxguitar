package app.tuxguitar.player.base;

import java.util.List;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;

/**
 * A new song (one 4/4 track at 120 bpm, standard guitar tuning, channel 1), filled note by note,
 * then run through MidiSequenceParser into a MidiEventRecorder.
 * Positions are given in sixteenths from the measure start; the first measure starts at tick 960.
 * String 1 is tuned to key 64, string 2 to 59, ..., string 6 to 40.
 */
class ParserFixture {

	static final long SIXTEENTH_PRECISE = TGDuration.WHOLE_PRECISE_DURATION / 16;

	final TGSongManager songManager = new TGSongManager();
	final TGFactory factory = songManager.getFactory();
	final TGSong song = songManager.newSong();
	final TGTrack track = song.getTrack(0);
	final TGChannel channel = song.getChannel(0);

	int flags = 0;
	int transpose = 0;
	int metronomeChannelId = 0;
	int sHeader = -1;

	TGMeasure measure(int index) {
		while (track.countMeasures() <= index) {
			songManager.addNewMeasureBeforeEnd(song);
		}
		return track.getMeasure(index);
	}

	TGBeat beat(int measureIndex, int sixteenth) {
		TGMeasure measure = measure(measureIndex);
		long preciseStart = measure.getPreciseStart() + sixteenth * SIXTEENTH_PRECISE;
		for (TGBeat beat : measure.getBeats()) {
			if (beat.getPreciseStart() == preciseStart) {
				return beat;
			}
		}
		TGBeat beat = factory.newBeat();
		beat.setPreciseStart(preciseStart);
		songManager.getMeasureManager().addBeat(measure, beat);
		songManager.getMeasureManager().orderBeats(measure);
		return beat;
	}

	TGNote note(int measureIndex, int sixteenth, int durationValue, int string, int fret, int voiceIndex) {
		TGVoice voice = beat(measureIndex, sixteenth).getVoice(voiceIndex);
		voice.setEmpty(false);
		voice.getDuration().setValue(durationValue);
		TGNote note = factory.newNote();
		note.setString(string);
		note.setValue(fret);
		voice.addNote(note);
		return note;
	}

	TGNote note(int sixteenth, int durationValue, int string, int fret) {
		return note(0, sixteenth, durationValue, string, fret, 0);
	}

	// a quarter note on the first beat of the first measure
	TGNote quarter(int string, int fret) {
		return note(0, TGDuration.QUARTER, string, fret);
	}

	void percussion() {
		channel.setBank(TGChannel.DEFAULT_PERCUSSION_BANK);
	}

	MidiEventRecorder parse() {
		return parse(new MidiEventRecorder(song.countTracks() + 2));
	}

	<R extends MidiSequenceHandler> R parse(R recorder) {
		MidiSequenceParser parser = new MidiSequenceParser(song, songManager, flags);
		parser.setTranspose(transpose);
		parser.setMetronomeChannelId(metronomeChannelId);
		parser.setSHeader(sHeader);
		parser.parse(recorder);
		return recorder;
	}

	List<String> notes() {
		return parse().events("on", "off");
	}

	// notes and pitch bends; the first one is always the track's initial bend reset "bend 960 t1 c1 64 s-1 b0"
	List<String> notesAndBends() {
		return parse().events("bend", "on", "off");
	}

	// the exact semitones of each pitch bend, "TICK sVOICE SEMITONES" to the thousandth; the first one is always the
	// track's initial reset "960 s-1 0.0"
	List<String> semitones() {
		List<String> semitones = new java.util.ArrayList<String>();
		parse(new MidiEventRecorder(song.countTracks() + 2) {
			public void addPitchBend(long tick, int track, int channel, int value, double bend, int voice, boolean bendMode) {
				semitones.add(tick + " s" + voice + " " + (Math.round(bend * 1000) / 1000.0));
			}
		});
		return semitones;
	}
}
