package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;

/**
 * Moving the track of a new song (six strings, tuned to drop D) from channel to channel.
 */
public class TestChangeChannel {

	private static final String DROP_D = "64 59 55 50 45 38";
	private static final String STANDARD = "64 59 55 50 45 40";
	private static final String PERCUSSION = "0 0 0 0 0 0";

	private final TGSongManager manager = new TGSongManager();
	private final TGSong song = manager.newSong();
	private final TGTrack track = song.getTrack(0);
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
}
