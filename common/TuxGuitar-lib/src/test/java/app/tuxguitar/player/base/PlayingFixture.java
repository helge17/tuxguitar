package app.tuxguitar.player.base;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.thread.TGThreadManager;

/**
 * A PlayerSongFixture whose player plays through a FakeSequencer, its threads run step by step (SteppedThreads). The
 * sequencer's actions and the player's notifications ("started", "stopped", "count down started", "count down
 * stopped", "loop") are logged together, in order.
 */
class PlayingFixture extends PlayerSongFixture {

	private static final String[] NOTIFICATIONS = {"", "started", "stopped", "count down started", "count down stopped", "loop"};

	final List<String> log = new ArrayList<String>();
	final FakeSequencer sequencer = new FakeSequencer(log);
	final SteppedThreads threads = new SteppedThreads();

	PlayingFixture() {
		TGThreadManager.getInstance(context).setThreadHandler(threads);
		player.loadSequencer(sequencer);
		player.addListener(event -> log.add(NOTIFICATIONS[(Integer) event.getAttribute(MidiPlayerEvent.PROPERTY_NOTIFICATION_TYPE)]));
	}

	// what was logged since the last time, comma separated
	String takeLog() {
		String logged = String.join(", ", log);
		log.clear();
		return logged;
	}
}
