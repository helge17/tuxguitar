package app.tuxguitar.song.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Iterator;

import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;

/**
 * A song of two guitar tracks and three 4/4 measures, measure n of track t holding one quarter note of fret
 * (10 * t + n) on string 1, key signature n and clef n; read back as one line per track:
 * "track t: number@start/key/clef:fret+beat ..." (start and beat in quarters; a percussion track has no key).
 */
abstract class SegmentFixture {

	protected final TGSongManager songManager = new TGSongManager();
	protected final TGFactory factory = songManager.getFactory();
	protected final TGSongSegmentHelper helper = new TGSongSegmentHelper(songManager);
	protected final TGSong song = songManager.newSong();

	protected SegmentFixture() {
		songManager.addTrack(song);
		songManager.addNewMeasureBeforeEnd(song);
		songManager.addNewMeasureBeforeEnd(song);
		for (int t = 0; t < song.countTracks(); t++) {
			TGTrack track = song.getTrack(t);
			for (int m = 0; m < track.countMeasures(); m++) {
				TGMeasure measure = track.getMeasure(m);
				measure.setKeySignature(m + 1);
				measure.setClef(m + 1);
				note(measure, (10 * track.getNumber()) + measure.getNumber());
			}
		}
	}

	protected void note(TGMeasure measure, int fret) {
		TGBeat beat = factory.newBeat();
		beat.setPreciseStart(measure.getPreciseStart());
		TGVoice voice = beat.getVoice(0);
		voice.setEmpty(false);
		voice.getDuration().setValue(TGDuration.QUARTER);
		TGNote note = factory.newNote();
		note.setString(1);
		note.setValue(fret);
		voice.addNote(note);
		songManager.getMeasureManager().addBeat(measure, beat);
	}

	protected long start(int number) {
		return songManager.getMeasureHeader(song, number).getStart();
	}

	protected void percussion(TGTrack track) {
		TGChannel channel = songManager.getChannel(song, track.getChannelId());
		channel.setBank(TGChannel.DEFAULT_PERCUSSION_BANK);
	}

	protected String describe() {
		StringBuilder sb = new StringBuilder();
		Iterator<TGMeasureHeader> headers = song.getMeasureHeaders();
		int index = 0;
		while (headers.hasNext()) {
			TGMeasureHeader header = headers.next();
			assertEquals(++index, header.getNumber());
		}
		for (int t = 0; t < song.countTracks(); t++) {
			TGTrack track = song.getTrack(t);
			sb.append("track ").append(track.getNumber()).append(':');
			for (int m = 0; m < track.countMeasures(); m++) {
				TGMeasure measure = track.getMeasure(m);
				assertEquals(song.getMeasureHeader(m), measure.getHeader());
				sb.append(' ').append(measure.getNumber()).append('@').append(measure.getStart() / TGDuration.QUARTER_TIME)
					.append('/').append(measure.getKeySignature()).append('/').append(measure.getClef()).append(':');
				for (int b = 0; b < measure.countBeats(); b++) {
					TGBeat beat = measure.getBeat(b);
					for (TGNote note : beat.getVoice(0).getNotes()) {
						sb.append(note.getValue()).append('+').append((beat.getStart() - measure.getStart()) / TGDuration.QUARTER_TIME).append(' ');
					}
				}
				sb.setLength(sb.length() - (sb.charAt(sb.length() - 1) == ' ' ? 1 : 0));
			}
			sb.append('\n');
		}
		return sb.toString();
	}
}
