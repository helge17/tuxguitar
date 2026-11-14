package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.player.base.MidiTransport;

/**
 * What the song plays live, followed a stretch at a time from what the transport is told: a song of 4 measures at 120
 * bpm, Play looped over measures 2 to 3 (from quarter 4) in Training Mode from 50% by 50 (a pass at 50% lasts 8 s,
 * 352800 samples; at 100% 176400), followed two passes at a time.
 */
public class TestLiveTimeline {

	private final SongActionFixture fixture = SongActionFixture.onDocument();
	private final TGLiveTimeline followed = new TGLiveTimeline(this.fixture.context, 2);

	public TestLiveTimeline() {
		this.fixture.measureCount(4);
		MidiPlayerMode mode = MidiPlayer.getInstance(this.fixture.context).getMode();
		mode.setLoop(true);
		mode.setLoopSHeader(2);
		mode.setLoopEHeader(3);
		mode.setType(MidiPlayerMode.TYPE_CUSTOM);
		mode.setCustomPercentFrom(50);
		mode.setCustomPercentTo(100);
		mode.setCustomPercentIncrement(50);
		mode.reset();
		this.followed.setSampleRate(44100);
	}

	// the transport's span from the quarter note on
	private static MidiTransport.Span at(double quarters) {
		MidiTransport transport = new MidiTransport();
		transport.clear();
		transport.add(0, true, 60, 4, 4, quarters, 0);
		return transport.get(0);
	}

	// a stretch on the timeline goes on with it; the transport going back to the loop's start (its next pass) starts a
	// new timeline there, at the mode's next loop (100%), even before the player has taken that loop on
	@Test
	public void aLoopsNextPassStartsANewTimelineAtTheModesNextLoop() {
		boolean started = this.followed.follow(at(4.0), 512);
		long first = this.followed.getTimeline().length();
		boolean goesOn = this.followed.follow(at(4.0 + 512 / 44100.0), 512);
		boolean next = this.followed.follow(at(4.0), 512);

		assertEquals("true false true | 529200 352800 0", started + " " + goesOn + " " + next + " | " + first + " " + this.followed.getTimeline().length() + " " + this.followed.getPosition());
	}
}
