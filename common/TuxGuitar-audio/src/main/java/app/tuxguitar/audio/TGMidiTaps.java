package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import app.tuxguitar.midi.synth.TGAudioBuffer;
import app.tuxguitar.midi.synth.TGMidiProcessorFactory;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.midi.synth.TGSynthChannel;
import app.tuxguitar.midi.synth.TGSynthManager;
import app.tuxguitar.midi.synth.TGSynthModel;
import app.tuxguitar.midi.synth.impl.GervillProcessorFactory;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.player.base.MidiSequenceHandler;
import app.tuxguitar.player.base.MidiSequenceParser;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTimeSignature;
import app.tuxguitar.util.TGContext;

/**
 * The taps of the channels of tracks with notes, headless: the TuxGuitar Synthesizer's channels at the audio device's
 * rate, each playing the General MIDI program of its channel through Gervill (the programs kept in the synthesizer's
 * settings are not read here), fed the events of the MIDI sequence parser on the timeline's clock and rendered in
 * blocks of 512 samples. An event is sent at the start of the block it falls in, as the synthesizer does today. Each
 * tap is the channel's chain before Volume and Balance, left and right.
 */
public final class TGMidiTaps {

	private static final int FRAMES = TGAudioBuffer.BUFFER_SIZE / 2;
	private static final int FLAGS = MidiSequenceParser.ADD_DEFAULT_CONTROLS | MidiSequenceParser.ADD_MIXER_MESSAGES | MidiSequenceParser.ADD_BANK_SELECT;

	private TGMidiTaps() {
	}

	// the channels left out are the audio channels: they have no notes
	public static Map<Integer, float[][]> render(TGContext context, TGSongManager songManager, TGSong song, MidiPlayerMode mode, TGTimeline timeline, Set<Integer> leftOut) throws MidiPlayerException {
		TGSynthModel model = model(context, timeline.getSampleRate());
		TreeMap<Long, List<Event>> events = schedule(songManager, song, mode, timeline);
		events.values().forEach(at -> at.removeIf(event -> leftOut.contains(event.channel)));
		Map<Integer, float[][]> taps = new HashMap<Integer, float[][]>();
		for (List<Event> at : events.values()) {
			for (Event event : at) {
				open(model, taps, event.channel, (int) timeline.length());
			}
		}
		long length = timeline.length();
		for (long block = 0; block < length; block += FRAMES) {
			for (List<Event> at : events.headMap(block + FRAMES).values()) {
				for (Event event : at) {
					event.send(model.getChannelById(event.channel));
					waitWhileLoading(model);
				}
			}
			events.headMap(block + FRAMES).clear();
			fill(model, taps, block, length);
		}
		model.closeChannels();
		return taps;
	}

	// General MIDI programs from Gervill for every bank and program
	private static TGSynthModel model(TGContext context, int sampleRate) {
		TGSynthManager manager = TGSynthManager.getInstance(context);
		if (manager.findMidiProcessorFactory(GervillProcessorFactory.TYPE) == null) {
			manager.addExtension(TGMidiProcessorFactory.class, new GervillProcessorFactory(context));
		}
		TGSynthModel model = new TGSynthModel(context, sampleRate);
		for (int bank = 0; bank < TGSynthModel.BANKS_LENGTH; bank++) {
			for (int program = 0; program < TGSynthModel.PROGRAMS_LENGTH; program++) {
				model.getProgram(bank, program).copyFrom(generalMidi(bank, program));
			}
		}
		return model;
	}

	private static TGProgram generalMidi(int bank, int program) {
		TGProgramElement receiver = new TGProgramElement();
		receiver.setId("gm-" + bank + "-" + program);
		receiver.setType(GervillProcessorFactory.TYPE);
		receiver.setParameter("gervil.program", bank + ":" + program);
		TGProgram generalMidi = new TGProgram();
		generalMidi.setReceiver(receiver);
		return generalMidi;
	}

	private static void open(TGSynthModel model, Map<Integer, float[][]> taps, int channel, int length) throws MidiPlayerException {
		if (!taps.containsKey(channel)) {
			model.openChannel(channel);
			taps.put(channel, new float[2][length]);
		}
	}

	// a program's instrument loads on a thread of its own: the next event waits for it, so no note is sent to a program
	// still loading (an offline render can wait)
	private static void waitWhileLoading(TGSynthModel model) {
		while (model.isBusy()) {
			Thread.yield();
		}
	}

	private static void fill(TGSynthModel model, Map<Integer, float[][]> taps, long block, long length) {
		float[][] samples = new float[TGAudioBuffer.CHANNELS][FRAMES];
		int count = (int) Math.min(FRAMES, length - block);
		for (Map.Entry<Integer, float[][]> tap : taps.entrySet()) {
			TGAudioBuffer buffer = new TGAudioBuffer();
			model.getChannelById(tap.getKey()).fillChain(buffer);
			buffer.read(samples);
			for (int side = 0; side < samples.length; side++) {
				System.arraycopy(samples[side], 0, tap.getValue()[side], (int) block, count);
			}
		}
	}

	// the parser's events, each at the sample of the timeline where its tick plays (none outside it); the events before
	// where playing starts (the channel settings at the start of the sequence among them) fall before sample 0, so they
	// go out with the first block, in tick order
	private static TreeMap<Long, List<Event>> schedule(TGSongManager songManager, TGSong song, MidiPlayerMode mode, TGTimeline timeline) {
		MidiSequenceParser parser = new MidiSequenceParser(song, songManager, FLAGS);
		if (mode.isLoop()) {
			parser.setSHeader(mode.getLoopSHeader());
			parser.setEHeader(mode.getLoopEHeader());
		}
		Events events = new Events(song.countTracks());
		parser.parse(events);
		List<Long> moves = TGSongTime.moves(song, mode);
		TreeMap<Long, List<Event>> scheduled = new TreeMap<Long, List<Event>>();
		for (int i = 0; i < timeline.count(); i++) {
			TGPlayedMeasure measure = timeline.get(i);
			long start = measure.getHeader().getStart() + moves.get(measure.getStep());
			long from = (i == 0 ? Long.MIN_VALUE : start + measure.getFrom());
			for (Event event : events.between(from, start + measure.getTo())) {
				scheduled.computeIfAbsent(timeline.sampleAt(i, event.tick - start), key -> new ArrayList<Event>()).add(event);
			}
		}
		return scheduled;
	}

	private interface Send {
		void to(TGSynthChannel channel) throws MidiPlayerException;
	}

	private static final class Event {
		private final long tick;
		private final int channel;
		private final Send send;

		Event(long tick, int channel, Send send) {
			this.tick = tick;
			this.channel = channel;
			this.send = send;
		}

		void send(TGSynthChannel channel) throws MidiPlayerException {
			this.send.to(channel);
		}
	}

	// what the parser emits, kept in tick order
	private static final class Events extends MidiSequenceHandler {
		private final List<Event> events = new ArrayList<Event>();

		Events(int tracks) {
			super(tracks);
		}

		List<Event> between(long from, long to) {
			List<Event> between = new ArrayList<Event>();
			for (Event event : this.events) {
				if (event.tick >= from && event.tick < to) {
					between.add(event);
				}
			}
			between.sort((a, b) -> Long.compare(a.tick, b.tick));
			return between;
		}

		public void addNoteOn(long tick, int track, int channel, int note, int velocity, int voice, boolean bendMode) {
			this.events.add(new Event(tick, channel, synth -> synth.sendNoteOn(note, velocity, voice, bendMode)));
		}

		public void addNoteOff(long tick, int track, int channel, int note, int velocity, int voice, boolean bendMode) {
			this.events.add(new Event(tick, channel, synth -> synth.sendNoteOff(note, velocity, voice, bendMode)));
		}

		public void addPitchBend(long tick, int track, int channel, int value, int voice, boolean bendMode) {
			this.events.add(new Event(tick, channel, synth -> synth.sendPitchBend(value, voice, bendMode)));
		}

		public void addControlChange(long tick, int track, int channel, int controller, int value) {
			this.events.add(new Event(tick, channel, synth -> synth.sendControlChange(controller, value)));
		}

		public void addProgramChange(long tick, int track, int channel, int instrument) {
			this.events.add(new Event(tick, channel, synth -> synth.sendProgramChange(instrument)));
		}

		public void addTempoInUSQ(long tick, int track, int usq) {
		}

		public void addTrackName(long tick, int track, String name) {
		}

		public void addTimeSignature(long tick, int track, TGTimeSignature ts) {
		}

		public void notifyFinish() {
		}
	}
}
