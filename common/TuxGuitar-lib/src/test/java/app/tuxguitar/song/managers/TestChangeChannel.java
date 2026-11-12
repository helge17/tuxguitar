package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;

/**
 * Moving the track of a new song (six strings, tuned to drop D) from channel to channel, and changing its string count.
 */
public class TestChangeChannel {

	private static final String DROP_D = "64 59 55 50 45 38";
	private static final String STANDARD = "64 59 55 50 45 40";
	private static final String PERCUSSION = "0 0 0 0 0 0";

	private final MeasureFixture fixture = new MeasureFixture();
	private final TGSongManager manager = fixture.songManager;
	private final TGTrack track = fixture.measure.getTrack();
	private final TGSong song = track.getSong();
	private final TGChannel melodic = manager.getChannel(song, track.getChannelId());
	private final TGChannel otherMelodic = manager.addChannel(song);
	private final TGChannel percussion = manager.addPercussionChannel(song);

	public TestChangeChannel() {
		track.getString(6).setValue(38);
	}

	// open string values, first string first
	private String tuning() {
		StringBuilder tuning = new StringBuilder();
		for (TGString string : track.getStrings()) {
			tuning.append(tuning.length() == 0 ? "" : " ").append(string.getValue());
		}
		return tuning.toString();
	}

	// one line per move: the track's channel id and tuning after moving it to that channel
	private String journey(TGChannel... channels) {
		StringBuilder journey = new StringBuilder();
		for (TGChannel channel : channels) {
			manager.getTrackManager().changeChannel(track, channel);
			journey.append(track.getChannelId()).append(": ").append(tuning()).append('\n');
		}
		return journey.toString();
	}

	@Test
	public void stringsAreRetunedOnlyWhenMovingIntoOrOutOfPercussion() {
		assertEquals(""
				+ otherMelodic.getChannelId() + ": " + DROP_D + "\n"
				+ "-1: " + DROP_D + "\n"
				+ percussion.getChannelId() + ": " + PERCUSSION + "\n"
				+ "-1: " + STANDARD + "\n"
				+ percussion.getChannelId() + ": " + PERCUSSION + "\n"
				+ melodic.getChannelId() + ": " + STANDARD + "\n",
				journey(otherMelodic, null, percussion, null, percussion, melodic));
	}

	// one line per string count: the tuning, then the notes left as "s<string>f<fret>"
	private String countJourney(int... counts) {
		StringBuilder journey = new StringBuilder();
		for (int count : counts) {
			manager.getTrackManager().changeStringCount(track, count);
			journey.append(tuning()).append(':');
			for (TGNote note : fixture.beatAt(0).getVoice(0).getNotes()) {
				journey.append(" s").append(note.getString()).append('f').append(note.getValue());
			}
			journey.append('\n');
		}
		return journey.toString();
	}

	@Test
	public void aNewStringCountTakesTheDefaultTuningOfThatCountAndDropsTheNotesOnRemovedStrings() {
		fixture.fretted(1, 3);
		fixture.fretted(5, 2);
		fixture.fretted(6, 7);

		assertEquals(""
				+ DROP_D + ": s1f3 s5f2 s6f7\n"
				+ "43 38 33 28 23: s1f3 s5f2\n"
				+ "43 38 33 28: s1f3\n"
				+ "0 0 0 0 0 0 0 0: s1f3\n",
				countJourney(6, 5, 4, 8));
	}

	@Test
	public void aPercussionTrackGetsPercussionStrings() {
		manager.getTrackManager().changeChannel(track, percussion);
		assertEquals("0 0 0 0 0 0 0:\n", countJourney(7));
	}
}
