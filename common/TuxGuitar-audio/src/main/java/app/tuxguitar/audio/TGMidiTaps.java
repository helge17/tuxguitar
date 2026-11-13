package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import app.tuxguitar.midi.synth.TGAudioBuffer;
import app.tuxguitar.midi.synth.TGMidiProcessorFactory;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.midi.synth.TGSynthChannel;
import app.tuxguitar.midi.synth.TGSynthManager;
import app.tuxguitar.midi.synth.TGSynthModel;
import app.tuxguitar.midi.synth.impl.GervillProcessorFactory;
import app.tuxguitar.player.base.MidiParameters;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.player.base.MidiReceiver;
import app.tuxguitar.player.base.MidiSequenceHandler;
import app.tuxguitar.player.base.MidiSequenceParser;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGChannelParameter;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTimeSignature;
import app.tuxguitar.util.TGContext;

/**
 * The taps of the channels of tracks with notes, headless: the TuxGuitar Synthesizer's channels at the audio device's
 * rate, each playing the General MIDI program of its channel through Gervill (the programs kept in the synthesizer's
 * settings are not read here), fed the events of the MIDI sequence parser on the timeline's clock and rendered in
 * blocks of 512 samples. Before each block its events go to the synthesizer's receiver of their frames, as the
 * sequencer sends them live: a note starts on its own sample, and is heard from it (the chain's latency is taken out,
 * see TGBlocks). Each tap is the channel's chain before Volume and Balance, left and right, with that latency.
 */
public final class TGMidiTaps {

	private static final int FRAMES = TGBlocks.FRAMES;
	private static final int FLAGS = MidiSequenceParser.ADD_DEFAULT_CONTROLS | MidiSequenceParser.ADD_MIXER_MESSAGES | MidiSequenceParser.ADD_BANK_SELECT;

	private TGMidiTaps() {
	}

	// the channels left out are the audio channels: they have no notes. The blocks run on until the slowest chain has
	// sounded the timeline's last sample
	public static Map<Integer, TGTap> render(TGContext context, TGSongManager songManager, TGSong song, MidiPlayerMode mode, TGTimeline timeline, Set<Integer> leftOut) throws MidiPlayerException {
		TGSynthModel model = model(context, timeline.getSampleRate());
		TreeMap<Long, List<Event>> events = schedule(songManager, song, mode, timeline);
		events.values().forEach(at -> at.removeIf(event -> leftOut.contains(event.channel)));
		Map<Integer, float[][]> taps = new HashMap<Integer, float[][]>();
		for (List<Event> at : events.values()) {
			for (Event event : at) {
				open(model, taps, event.channel, (int) timeline.length());
			}
		}
		for (Integer channel : taps.keySet()) {
			sendParameters(model, songManager, song, channel);
		}
		for (long block = 0; block < timeline.length() + model.getLatency(); block += FRAMES) {
			send(model, events.headMap(block + FRAMES), block);
			fill(model, taps, block);
		}
		Map<Integer, TGTap> rendered = new HashMap<Integer, TGTap>();
		taps.forEach((channel, samples) -> rendered.put(channel, new TGTap(samples, model.getChannelById(channel).getLatency())));
		model.closeChannels();
		return rendered;
	}

	// the events before the end of the block, on their frames of it (those before it on its first), then forgotten
	private static void send(TGSynthModel model, SortedMap<Long, List<Event>> due, long block) throws MidiPlayerException {
		for (Map.Entry<Long, List<Event>> at : due.entrySet()) {
			MidiReceiver receiver = model.at((int) Math.max(0, at.getKey() - block));
			for (Event event : at.getValue()) {
				event.send(receiver);
				waitWhileLoading(model);
			}
		}
		due.clear();
	}

	// the channel's parameters, its custom program (its sound chain) among them, go out before its events, as the player
	// sends them when playing starts
	private static void sendParameters(TGSynthModel model, TGSongManager songManager, TGSong song, int channel) throws MidiPlayerException {
		MidiReceiver receiver = model.at(0);
		receiver.sendParameter(channel, MidiParameters.SENDING_PARAMS, Boolean.TRUE.toString());
		Iterator<TGChannelParameter> parameters = songManager.getChannel(song, channel).getParameters();
		while (parameters.hasNext()) {
			TGChannelParameter parameter = parameters.next();
			receiver.sendParameter(channel, parameter.getKey(), parameter.getValue());
		}
		receiver.sendParameter(channel, MidiParameters.SENDING_PARAMS, Boolean.FALSE.toString());
		waitWhileLoading(model);
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

	// the next block of every channel, rendered at the block's start, into its tap its chain's latency earlier
	private static void fill(TGSynthModel model, Map<Integer, float[][]> taps, long block) {
		float[][] samples = new float[TGAudioBuffer.CHANNELS][FRAMES];
		for (Map.Entry<Integer, float[][]> tap : taps.entrySet()) {
			TGSynthChannel channel = model.getChannelById(tap.getKey());
			TGAudioBuffer buffer = new TGAudioBuffer();
			channel.fillChain(buffer);
			buffer.read(samples);
			TGBlocks.place(samples, tap.getValue(), block - channel.getLatency());
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
		void to(MidiReceiver receiver) throws MidiPlayerException;
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

		void send(MidiReceiver receiver) throws MidiPlayerException {
			this.send.to(receiver);
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
			this.events.add(new Event(tick, channel, synth -> synth.sendNoteOn(channel, note, velocity, voice, bendMode)));
		}

		public void addNoteOff(long tick, int track, int channel, int note, int velocity, int voice, boolean bendMode) {
			this.events.add(new Event(tick, channel, synth -> synth.sendNoteOff(channel, note, velocity, voice, bendMode)));
		}

		public void addPitchBend(long tick, int track, int channel, int value, int voice, boolean bendMode) {
			this.events.add(new Event(tick, channel, synth -> synth.sendPitchBend(channel, value, voice, bendMode)));
		}

		public void addControlChange(long tick, int track, int channel, int controller, int value) {
			this.events.add(new Event(tick, channel, synth -> synth.sendControlChange(channel, controller, value)));
		}

		public void addProgramChange(long tick, int track, int channel, int instrument) {
			this.events.add(new Event(tick, channel, synth -> synth.sendProgramChange(channel, instrument)));
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
