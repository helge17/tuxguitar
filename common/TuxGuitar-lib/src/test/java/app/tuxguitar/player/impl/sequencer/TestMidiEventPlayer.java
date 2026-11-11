package app.tuxguitar.player.impl.sequencer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiTransmitter;
import app.tuxguitar.util.TGContext;

/**
 * Pins which events MidiEventPlayer.process sends as the sequencer moves to a tick: the events reached since the
 * last tick, unless their track is muted or another track is soloed; and, right after a reset, the system, control
 * change and program change events already passed, so the channels are set up again.
 *
 * A sent event is described as "type@tick/track"; type n = note on, c = control change, p = program change,
 * s = system event.
 */
public class TestMidiEventPlayer {

	private final List<String> sent = new ArrayList<String>();
	private long position = 0;

	private final MidiSequencerImpl sequencer = new MidiSequencerImpl(new TGContext()) {
		public long getTickPosition() {
			return position;
		}

		public void sendEvent(MidiEvent event) {
			sent.add(describe(event));
		}
	};

	private final MidiEventPlayer player = new MidiEventPlayer(sequencer);

	public TestMidiEventPlayer() {
		sequencer.setTransmitter(new MidiTransmitter());
		sequencer.getMidiTrackController().init(2);
	}

	private static String describe(MidiEvent event) {
		String type = "?";
		switch (event.getType()) {
			case MidiEvent.MIDI_EVENT_NOTEON: type = "n"; break;
			case MidiEvent.MIDI_EVENT_CONTROL_CHANGE: type = "c"; break;
			case MidiEvent.MIDI_EVENT_PROGRAM_CHANGE: type = "p"; break;
			case MidiEvent.MIDI_SYSTEM_EVENT: type = "s"; break;
			default: break;
		}
		return type + "@" + event.getTick() + "/" + event.getTrack();
	}

	private void add(long tick, int type, int track) {
		player.addEvent(new MidiEvent(tick, type, track, new byte[0]));
	}

	private String playTo(long tick) throws MidiPlayerException {
		sent.clear();
		position = tick;
		player.process();
		return String.join(" ", sent);
	}

	@Test
	public void sendsTheEventsReachedSinceTheLastTick() throws MidiPlayerException {
		add(0, MidiEvent.MIDI_EVENT_NOTEON, 0);
		add(10, MidiEvent.MIDI_EVENT_NOTEON, 0);
		add(20, MidiEvent.MIDI_EVENT_NOTEON, 1);
		add(30, MidiEvent.MIDI_EVENT_NOTEON, 1);

		assertEquals("n@0/0 n@10/0", playTo(10));
		assertEquals("n@20/1", playTo(20));
		assertEquals("", playTo(20));
	}

	@Test
	public void afterAResetResendsTheChannelSetupAlreadyPassed() throws MidiPlayerException {
		add(0, MidiEvent.MIDI_SYSTEM_EVENT, MidiEvent.ALL_TRACKS);
		add(0, MidiEvent.MIDI_EVENT_PROGRAM_CHANGE, 0);
		add(0, MidiEvent.MIDI_EVENT_CONTROL_CHANGE, 0);
		add(5, MidiEvent.MIDI_EVENT_NOTEON, 0);
		add(20, MidiEvent.MIDI_EVENT_NOTEON, 1);
		playTo(10);

		position = 20;
		player.reset();

		assertEquals("s@0/-1 p@0/0 c@0/0 n@20/1", playTo(20));
	}

	@Test
	public void theChannelSetupIsResentOnlyOnce() throws MidiPlayerException {
		add(0, MidiEvent.MIDI_EVENT_PROGRAM_CHANGE, 0);
		playTo(10);
		player.reset();

		assertEquals("p@0/0", playTo(10));
		assertEquals("", playTo(10));
	}

	@Test
	public void skipsTheEventsOfAMutedTrack() throws MidiPlayerException {
		sequencer.getMidiTrackController().setMute(1, true);
		add(0, MidiEvent.MIDI_EVENT_NOTEON, 0);
		add(0, MidiEvent.MIDI_EVENT_NOTEON, 1);
		add(0, MidiEvent.MIDI_SYSTEM_EVENT, MidiEvent.ALL_TRACKS);

		assertEquals("n@0/0 s@0/-1", playTo(10));
	}

	@Test
	public void whileATrackIsSoloedSkipsTheOthers() throws MidiPlayerException {
		sequencer.getMidiTrackController().setSolo(1, true);
		add(0, MidiEvent.MIDI_EVENT_NOTEON, 0);
		add(0, MidiEvent.MIDI_EVENT_NOTEON, 1);
		add(0, MidiEvent.MIDI_SYSTEM_EVENT, MidiEvent.ALL_TRACKS);

		assertEquals("n@0/1 s@0/-1", playTo(10));
	}
}
