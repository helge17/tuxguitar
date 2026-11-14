package app.tuxguitar.song.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGSong;

/**
 * The song's percussion channel, the one Count In and the Metronome click on: its first percussion channel, or none.
 * A new song has one melodic channel.
 */
public class TestPercussionChannel {

	private final TGSongManager manager = new TGSongManager();
	private final TGSong song = manager.newSong();

	// "none / no" before percussion channels are added, then the first one / "yes"
	@Test
	public void isTheFirstPercussionChannel() {
		String without = manager.getPercussionChannel(song) + " / " + (manager.isAnyPercussionChannel(song) ? "yes" : "no");
		manager.addChannel(song);
		TGChannel first = manager.addPercussionChannel(song);
		manager.addPercussionChannel(song);

		assertEquals("null / no", without);
		assertEquals(first, manager.getPercussionChannel(song));
		assertEquals(true, manager.isAnyPercussionChannel(song));
	}
}
