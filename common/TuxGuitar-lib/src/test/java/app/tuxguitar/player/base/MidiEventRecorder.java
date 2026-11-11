package app.tuxguitar.player.base;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.song.models.TGTimeSignature;

/**
 * A MidiSequenceHandler that records every event it receives as one readable line,
 * so a test can assert on exactly what MidiSequenceParser produced.
 * Line formats ("t" track, "c" channel, "k" key, "v" velocity, "s" midi voice, "b" bend mode 0/1):
 *   on|off TICK tT cC kK vV sS bB
 *   bend TICK tT cC VALUE sS bB
 *   cc TICK tT cC CONTROLLER=VALUE
 */
class MidiEventRecorder extends MidiSequenceHandler {

	private final List<String> events = new ArrayList<String>();

	MidiEventRecorder(int tracks) {
		super(tracks);
	}

	List<String> events() {
		return this.events;
	}

	// recorded lines starting with one of the given kinds ("on", "off", "bend", "cc", ...), in order
	List<String> events(String... kinds) {
		List<String> selected = new ArrayList<String>();
		for (String event : this.events) {
			for (String kind : kinds) {
				if (event.startsWith(kind + " ")) {
					selected.add(event);
				}
			}
		}
		return selected;
	}

	@Override
	public void addNoteOn(long tick, int track, int channel, int note, int velocity, int voice, boolean bendMode) {
		this.events.add(note("on", tick, track, channel, note, velocity, voice, bendMode));
	}

	@Override
	public void addNoteOff(long tick, int track, int channel, int note, int velocity, int voice, boolean bendMode) {
		this.events.add(note("off", tick, track, channel, note, velocity, voice, bendMode));
	}

	@Override
	public void addPitchBend(long tick, int track, int channel, int value, int voice, boolean bendMode) {
		this.events.add("bend " + tick + " t" + track + " c" + channel + " " + value + " s" + voice + " b" + (bendMode ? 1 : 0));
	}

	@Override
	public void addControlChange(long tick, int track, int channel, int controller, int value) {
		this.events.add("cc " + tick + " t" + track + " c" + channel + " " + controller + "=" + value);
	}

	@Override
	public void addProgramChange(long tick, int track, int channel, int instrument) {
		this.events.add("program " + tick + " t" + track + " c" + channel + " " + instrument);
	}

	@Override
	public void addTempoInUSQ(long tick, int track, int usq) {
		this.events.add("tempo " + tick + " t" + track + " " + usq);
	}

	@Override
	public void addTrackName(long tick, int track, String name) {
		this.events.add("name " + tick + " t" + track + " " + name);
	}

	@Override
	public void addTimeSignature(long tick, int track, TGTimeSignature ts) {
		this.events.add("ts " + tick + " t" + track + " " + ts.getNumerator() + "/" + ts.getDenominator().getValue());
	}

	@Override
	public void notifyFinish() {
		this.events.add("finish");
	}

	private static String note(String kind, long tick, int track, int channel, int key, int velocity, int voice, boolean bendMode) {
		return kind + " " + tick + " t" + track + " c" + channel + " k" + key + " v" + velocity + " s" + voice + " b" + (bendMode ? 1 : 0);
	}
}
