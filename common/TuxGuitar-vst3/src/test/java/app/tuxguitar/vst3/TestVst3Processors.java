package app.tuxguitar.vst3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.tuxguitar.midi.synth.TGAudioBuffer;
import app.tuxguitar.midi.synth.TGAudioProcessor;
import app.tuxguitar.midi.synth.TGMidiProcessor;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.midi.synth.TGSynthManager;
import app.tuxguitar.player.base.MidiTransport;
import app.tuxguitar.util.TGContext;

/**
 * VST3 effects and instruments as the TuxGuitar Synthesizer's processors, opened from sound chain elements in the host
 * process, with the real test plugins.
 */
public class TestVst3Processors {

	private static final int FRAMES = TGVst3Processor.FRAMES;

	private final TGContext context = new TGContext();
	private final TGVst3Hosting hosting = TGVst3Hosting.getInstance(this.context);
	private final List<TGAudioProcessor> opened = new ArrayList<TGAudioProcessor>();

	@BeforeEach
	public void setUp() throws TGVst3Exception {
		this.hosting.configure(TestPlugins.host(), Arrays.asList(TestPlugins.folder(), TestPlugins.vst2Folder()));
		this.hosting.scan();
		TGVst3Processors.install(this.context);
		TGVst3Processors.install(this.context);
	}

	@AfterEach
	public void tearDown() {
		this.opened.forEach(TGAudioProcessor::close);
		this.hosting.close();
	}

	private TGProgramElement element(String name) {
		return TGVst3Element.create("e1", this.hosting.find(name));
	}

	private <T extends TGAudioProcessor> T open(T processor, TGProgramElement element) {
		processor.setSampleRate(44100f);
		processor.restoreParameters(element.getParameters());
		this.opened.add(processor);
		return processor;
	}

	private TGAudioProcessor effect(TGProgramElement element) {
		return open(TGSynthManager.getInstance(this.context).findAudioProcessorFactory(element.getType()).createProcessor(), element);
	}

	private TGMidiProcessor instrument(String name) {
		TGProgramElement element = element(name);
		return open(TGSynthManager.getInstance(this.context).findMidiProcessorFactory(element.getType()).createProcessor(), element);
	}

	private static float[][] level(float value) {
		return TestPlugins.block(FRAMES, value, value);
	}

	private int parameter(String plugin, String title) throws TGVst3Exception {
		TGVst3Instance instance = this.hosting.open(this.hosting.find(plugin), 44100f, FRAMES);
		int id = instance.findParameter(title);
		instance.close();
		return id;
	}

	@Test
	public void scanningTheFoldersListsEveryPluginByName() {
		List<String> read = new ArrayList<String>();
		this.hosting.getPlugins().forEach(plugin -> read.add(plugin.getName() + (plugin.isInstrument() ? " (instrument)" : "")));
		assertEquals(Arrays.asList("Test Click (instrument)", "Test Clip 0.5", "Test Drone", "Test Echo", "Test Gain", "Test Hang", "Test Latency",
			"Test Sine (instrument)", "Test Stereo", "Test Tempo"), read);
		assertNull(this.hosting.find("Old Amp"));
	}

	@Test
	public void anEffectElementOpensItsPluginWithTheParametersSet() throws TGVst3Exception {
		TGProgramElement element = element("Test Gain");
		TGVst3Element.setParameter(element, parameter("Test Gain", "Gain"), 0.25);
		float[][] block = level(0.4f);
		effect(element).process(block);
		assertEquals(0.1f, block[0][0], 1e-6f);
		assertEquals(0.1f, block[1][FRAMES - 1], 1e-6f);
	}

	@Test
	public void aFloatBlockKeepsWhatGoesAboveFullScale() {
		float[][] block = level(0.8f);
		effect(element("Test Gain")).process(block);
		assertEquals(1.6f, block[0][0], 1e-6f);
	}

	@Test
	public void aSixteenBitBufferGoesThroughTheEffectToo() {
		TGAudioBuffer buffer = new TGAudioBuffer();
		buffer.write(level(0.25f));
		effect(element("Test Gain")).fillBuffer(buffer);
		float[][] block = new float[2][FRAMES];
		buffer.read(block);
		assertEquals(0.5f, block[0][0], 1e-4f);
	}

	@Test
	public void anEffectReportsItsPluginsLatency() throws TGVst3Exception {
		TGProgramElement element = element("Test Latency");
		TGVst3Element.setParameter(element, parameter("Test Latency", "Latency"), 2048);
		assertEquals(2048, effect(element).getLatency());
	}

	// a plugin's tail as it reports it; one that never ends is endless; a closed one has none
	@Test
	public void anEffectReportsItsPluginsTail() {
		TGAudioProcessor echo = effect(element("Test Echo"));
		int echoTail = echo.getTail();
		int droneTail = effect(element("Test Drone")).getTail();
		echo.close();
		assertEquals("13230 " + TGAudioProcessor.INFINITE_TAIL + " 0", echoTail + " " + droneTail + " " + echo.getTail());
	}

	@Test
	public void anInstrumentPlaysEachNoteOnItsFrame() {
		TGMidiProcessor click = instrument("Test Click");
		click.sendNoteOn(60, 127, 0, false, 300);
		click.sendNoteOn(62, 127, 0, false);
		float[][] block = new float[2][FRAMES];
		click.process(block);
		assertEquals(0.5f, block[0][0]);
		assertEquals(0f, block[0][1]);
		assertEquals(0.5f, block[1][300]);
		click.process(block);
		assertEquals(0f, block[0][300]);
	}

	// a transport of two spans: the song at 120 bpm in 4/4 from quarter 3.5, then from frame 200 at 60 bpm in 3/4
	private static MidiTransport twoSpans() {
		MidiTransport transport = new MidiTransport();
		transport.clear();
		transport.add(0, true, 120, 4, 4, 3.5, 77175);
		transport.add(200, true, 60, 3, 4, 4, 88200);
		return transport;
	}

	// a block is processed in parts, one per span of the transport, each told its span; the signal passes whole
	@Test
	public void eachSpanOfTheTransportIsProcessedToldItsOwnState() throws TGVst3Exception {
		TGVst3Processor tempo = (TGVst3Processor) effect(element("Test Tempo"));
		tempo.setTransport(twoSpans());
		float[][] block = level(0.25f);
		tempo.process(block);
		java.nio.ByteBuffer told = java.nio.ByteBuffer.wrap(tempo.getInstance().message("TG.report", new HashMap<String, Long>(), new HashMap<String, byte[]>()))
			.order(java.nio.ByteOrder.nativeOrder());
		StringBuilder read = new StringBuilder();
		while (told.hasRemaining()) {
			read.append(told.getDouble() + " " + told.getInt() + "/" + told.getInt() + " " + told.getInt() + " " + told.getDouble() + " " + told.getLong() + "; ");
		}
		assertEquals("120.0 4/4 1 3.5 77175; 60.0 3/4 1 4.0 88200; ", read.toString());
		assertEquals(0.25f, block[0][199]);
		assertEquals(0.25f, block[1][200]);
		assertEquals(0.25f, block[0][FRAMES - 1]);
	}

	// the notes of a part of a block, each on its frame counted from the part's first
	@Test
	public void aPartOfABlockHoldsTheNotesOnItsFrames() {
		List<TGVst3NoteEvent> events = Arrays.asList(TGVst3NoteEvent.noteOn(0, 60, 1, 1f), TGVst3NoteEvent.noteOn(199, 61, 2, 1f),
			TGVst3NoteEvent.noteOn(200, 62, 3, 1f), TGVst3NoteEvent.noteOff(300, 60, 1));
		StringBuilder read = new StringBuilder();
		for (int[] part : new int[][] {{0, 200}, {200, FRAMES}}) {
			for (TGVst3NoteEvent event : TGVst3NoteEvent.between(events, part[0], part[1])) {
				read.append(event.getType() + ":" + event.getPitch() + "@" + event.getOffset() + " ");
			}
			read.append("| ");
		}
		assertEquals("1:60@0 1:61@199 | 1:62@0 2:60@100 | ", read.toString());
	}

	// a note goes to the part of the block its frame is in, on that frame
	@Test
	public void aNoteInALaterSpanSoundsOnItsFrame() {
		TGMidiProcessor click = instrument("Test Click");
		click.setTransport(twoSpans());
		click.sendNoteOn(60, 127, 0, false, 300);
		float[][] block = new float[2][FRAMES];
		click.process(block);
		assertEquals(0f, block[0][100]);
		assertEquals(0.5f, block[0][300]);
		assertEquals(0f, block[1][301]);
	}

	// a Test Sine block's periods: how often its left side crosses zero going up
	private static long periods(TGMidiProcessor sine) {
		float[][] block = new float[2][FRAMES];
		sine.process(block);
		return java.util.stream.IntStream.range(1, FRAMES).filter(i -> block[0][i - 1] <= 0 && block[0][i] > 0).count();
	}

	// the note on voice 0 (MIDI's first), key 69, is 440 Hz: 6 periods begin in a block of 512 at 44100 Hz; 12
	// semitones up (880 Hz), 11
	@Test
	public void aBendTunesTheNoteItsVoicePlaysByItsExactSemitones() {
		TGMidiProcessor sine = instrument("Test Sine");
		sine.sendNoteOn(69, 127, 0, true, 0);
		sine.sendPitchBend(127, 12.0, 0, true, 0);

		assertEquals(11, periods(sine));
	}

	// a bend of another voice, of none (the channel's, as the 7-bit reset at the start) or of one past MIDI's 128, or one
	// without semitones, leaves the note alone
	@Test
	public void aBendOfAnotherVoiceOrOfNoneLeavesTheNoteAlone() {
		TGMidiProcessor sine = instrument("Test Sine");
		sine.sendNoteOn(69, 127, 1, true, 0);
		sine.sendPitchBend(127, 12.0, 2, true, 0);
		sine.sendPitchBend(127, 12.0, -1, false, 0);
		sine.sendPitchBend(127, 12.0, 128, true, 0);
		sine.sendPitchBend(127, 1, true, 0);

		assertEquals(6, periods(sine));
	}

	// two notes of key 69 on voices 0 and 1 (unison on two strings), what is done to the note of voice 1, then that
	// note stopped: the periods of what Test Sine still sounds
	private long periodsOfTheFirstOfTwoUnisonNotes(java.util.function.Consumer<TGMidiProcessor> toTheSecond) {
		TGMidiProcessor sine = instrument("Test Sine");
		sine.sendNoteOn(69, 127, 0, true, 0);
		sine.sendNoteOn(69, 127, 1, true, 0);
		toTheSecond.accept(sine);
		sine.sendNoteOff(69, 0, 1, true, 0);
		return periods(sine);
	}

	// unison notes are two notes: the note-off of one leaves the other sounding (440 Hz, 6 periods)
	@Test
	public void theNoteOffOfOneUnisonNoteLeavesTheOtherSounding() {
		assertEquals(6, periodsOfTheFirstOfTwoUnisonNotes(sine -> { }));
	}

	// a note of no voice (a beat played as a note is entered) is none of a voice's notes: notes of no voice started and
	// stopped, of the same key or another, leave the note of voice 0 sounding (440 Hz, 6 periods)
	@Test
	public void aNoteOfNoVoiceIsNotTheNoteOfAVoice() {
		TGMidiProcessor sine = instrument("Test Sine");
		sine.sendNoteOn(69, 127, 0, false, 0);
		for (int key : new int[] {69, 59}) {
			sine.sendNoteOn(key, 127, -1, false, 0);
			sine.sendNoteOff(key, 0, -1, false, 0);
		}

		assertEquals(6, periods(sine));
	}

	// where the notes of no voice end, those of voice 0 begin: the top key of no voice stopped leaves voice 0's lowest
	// key sounding (8.18 Hz, peaking at 0.5: 0.058 on sample 100)
	@Test
	public void theTopKeyOfNoVoiceIsNotTheLowestKeyOfVoiceZero() {
		TGMidiProcessor sine = instrument("Test Sine");
		sine.sendNoteOn(0, 127, 0, false, 0);
		sine.sendNoteOn(127, 127, -1, false, 0);
		sine.sendNoteOff(127, 0, -1, false, 0);
		float[][] block = new float[2][FRAMES];
		sine.process(block);

		assertEquals(0.058f, block[0][100], 0.001f);
	}

	// a bend of one of two unison notes tunes that one alone: once it stops, the other still sounds untuned (440 Hz, 6
	// periods, not 880 Hz's 11)
	@Test
	public void aBendOfOneUnisonNoteLeavesTheOtherUntuned() {
		assertEquals(6, periodsOfTheFirstOfTwoUnisonNotes(sine -> sine.sendPitchBend(127, 12.0, 1, true, 0)));
	}

	@Test
	public void anInstrumentHearsTheVelocityAndTheNoteOff() {
		TGMidiProcessor sine = instrument("Test Sine");
		sine.sendNoteOn(69, 79, 0, false);
		sine.sendNoteOff(69, 0, 0, false, 256);
		float[][] block = new float[2][FRAMES];
		sine.process(block);
		float peak = 0f;
		for (int i = 0; i < 256; i++) {
			peak = Math.max(peak, block[0][i]);
		}
		assertEquals(0.5f * 79 / 127, peak, 0.001f);
		assertEquals(0f, block[0][256]);
		sine.sendNoteOn(69, 127, 0, false);
		sine.process(block);
		sine.sendNoteOff(69, 0, 0, false);
		sine.process(block);
		assertEquals(0f, block[0][0]);
		assertEquals(0f, block[0][100]);
	}

	@Test
	public void aStoredEffectKeepsItsStateAndDropsTheParametersSetBefore() throws TGVst3Exception {
		TGProgramElement element = element("Test Gain");
		TGVst3Element.setParameter(element, parameter("Test Gain", "Gain"), 3.0);
		Map<String, String> stored = new HashMap<String, String>();
		effect(element).storeParameters(stored);
		TGProgramElement copy = new TGProgramElement();
		copy.setType(TGVst3Element.EFFECT);
		stored.forEach(copy::setParameter);
		float[][] block = level(0.25f);
		effect(copy).process(block);
		assertEquals(0.75f, block[0][0], 1e-6f);
		assertTrue(TGVst3Element.parameters(stored).isEmpty());
		assertEquals("Test Gain", TGVst3Element.getName(copy));
		assertTrue(TGVst3Element.isVst3(copy));
		assertTrue(TGVst3Element.isVst3(element("Test Sine")));
		assertFalse(TGVst3Element.isVst3(new TGProgramElement()));
	}

	@Test
	public void restoringAnotherPluginOpensItInstead() {
		TGAudioProcessor processor = effect(element("Test Gain"));
		TGVst3Instance gain = ((TGVst3Processor) processor).getInstance();
		processor.restoreParameters(element("Test Gain").getParameters());
		assertTrue(gain == ((TGVst3Processor) processor).getInstance());
		processor.restoreParameters(element("Test Clip 0.5").getParameters());
		float[][] block = level(0.8f);
		processor.process(block);
		assertEquals(0.5f, block[0][0]);
	}

	// an element without a state is its plugin as it opens: the plugin running it, restored from it again (an undo of the
	// plugin's first change, say), goes back to that, whatever it was set to since
	@Test
	public void aRunningPluginRestoredFromAnElementWithoutAStateIsAsItOpened() throws TGVst3Exception {
		TGProgramElement element = element("Test Gain");
		TGAudioProcessor processor = effect(element);
		TGVst3Instance gain = ((TGVst3Processor) processor).getInstance();
		byte[] opened = gain.getState().getComponent();
		gain.message("TG.preset", Map.of("index", 1L), new HashMap<String, byte[]>());
		processor.restoreParameters(element.getParameters());
		assertTrue(gain == ((TGVst3Processor) processor).getInstance());
		org.junit.jupiter.api.Assertions.assertArrayEquals(opened, gain.getState().getComponent());
	}

	@Test
	public void anEffectWhoseHostStoppedSoundsNothingAndTheStopIsReported() throws TGVst3Exception {
		TGAudioProcessor processor = effect(element("Test Gain"));
		TestPlugins.kill(((TGVst3Processor) processor).getInstance().getHost().getPid());
		float[][] block = level(0.25f);
		processor.process(block);
		assertEquals(0f, block[0][0]);
		assertFalse(processor.isOpen());
		assertEquals(0, processor.getLatency());
		processor.storeParameters(new HashMap<String, String>());
		effect(element("Test Gain")).process(block = level(0.25f));
		assertEquals(0.5f, block[0][0]);
		assertEquals(1, this.hosting.getStops().size());
		assertTrue(this.hosting.getStops().get(0).startsWith("the VST3 host stopped"), this.hosting.getStops().get(0));
	}

	// told by an offline transport, an effect waits for a block its plugin answers late, so the block is not silent
	@Test
	public void anEffectToldItsClockIsOfflineWaitsForALateBlock() throws TGVst3Exception {
		TGVst3Processor hang = (TGVst3Processor) effect(element("Test Hang"));
		hang.getInstance().setParameter(hang.getInstance().findParameter("Hang at"), 0);
		hang.getInstance().setParameter(hang.getInstance().findParameter("Hang for"), 300);
		MidiTransport transport = new MidiTransport();
		transport.setOffline(true);
		hang.setTransport(transport);
		float[][] block = level(0.25f);
		hang.process(block);
		assertEquals(0.25f, block[1][FRAMES - 1]);
	}

	// one host process per plugin: a plugin that crashes takes only its own process down, and is reported
	@Test
	public void aPluginThatCrashesSilencesOnlyItselfWhileAnotherKeepsSounding() throws TGVst3Exception {
		TGVst3Processor crashing = (TGVst3Processor) effect(element("Test Gain"));
		TGVst3Processor other = (TGVst3Processor) effect(element("Test Gain"));
		assertNotEquals(crashing.getInstance().getHost().getPid(), other.getInstance().getHost().getPid());
		assertThrows(TGVst3Exception.class, () -> crashing.getInstance().message("TG.crash", new HashMap<String, Long>(), new HashMap<String, byte[]>()));
		float[][] silenced = level(0.25f);
		crashing.process(silenced);
		float[][] sounding = level(0.25f);
		other.process(sounding);
		assertEquals(0f, silenced[0][0]);
		assertEquals(0.5f, sounding[0][0]);
		assertTrue(other.getInstance().getHost().isRunning());
		assertEquals(Arrays.asList("the VST3 host stopped (exit code 70)"), this.hosting.getStops());
	}

	// each plugin's host process ends when the plugin closes, which is no stop; a scan leaves no host process running
	@Test
	public void closingAPluginEndsItsHostProcessWhichIsNoStop() {
		long scanning = hostProcesses();
		TGVst3Processor processor = (TGVst3Processor) effect(element("Test Gain"));
		TGVst3Host host = processor.getInstance().getHost();
		long playing = hostProcesses();
		processor.close();
		assertEquals("0 1 0", scanning + " " + playing + " " + hostProcesses());
		assertFalse(host.isRunning());
		assertTrue(host.getExitReport().endsWith("(exit code 0)"), "ended on its own: " + host.getExitReport());
		assertTrue(this.hosting.getStops().isEmpty());
	}

	private static long hostProcesses() {
		return ProcessHandle.current().children().filter(process -> process.info().command().orElse("").contains("tuxguitar-vst3-host")).count();
	}

	@Test
	public void anElementWithoutAPluginSoundsNothing() {
		TGProgramElement empty = new TGProgramElement();
		empty.setType(TGVst3Element.EFFECT);
		TGAudioProcessor processor = effect(empty);
		float[][] block = level(0.25f);
		processor.process(block);
		assertEquals(0f, block[1][0]);
		assertFalse(processor.isOpen());
		assertFalse(processor.isBusy());
	}

	// the state a plugin holds replaces the parameters set on the element before it
	@Test
	public void anElementGivenAStateOpensWithItAndDropsTheParametersSetBefore() throws TGVst3Exception {
		TGProgramElement element = element("Test Gain");
		TGVst3Element.setParameter(element, 1, 3.0);
		TGVst3EffectProcessor source = (TGVst3EffectProcessor) effect(element("Test Gain"));
		source.getInstance().setParameter(1, 5.0);
		TGVst3Element.setState(element, source.getInstance().getState());
		TGVst3EffectProcessor reopened = (TGVst3EffectProcessor) effect(element);
		assertEquals("5.0 [vst3.class, vst3.controller, vst3.module, vst3.name, vst3.state]",
			reopened.getInstance().getParameter(1) + " " + new java.util.TreeSet<String>(element.getParameters().keySet()));
	}

	@Test
	public void anElementNamesAPluginByItsModuleAndClass() {
		Map<String, String> parameters = new HashMap<String, String>(element("Test Gain").getParameters());
		parameters.remove("vst3.class");
		assertNull(TGVst3Element.plugin(parameters, false));
		parameters = new HashMap<String, String>(element("Test Gain").getParameters());
		parameters.remove("vst3.module");
		assertNull(TGVst3Element.plugin(parameters, false));
	}

	@Test
	public void aStateWithoutAControllerStateRestoresTheComponentAlone() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("vst3.state", "AAE=");
		assertEquals(0, TGVst3Element.state(parameters).getController().length);
		assertEquals(2, TGVst3Element.state(parameters).getComponent().length);
	}

	@Test
	public void hostingNeedsTheHostConfigured() {
		TGVst3Hosting unconfigured = TGVst3Hosting.getInstance(new TGContext());
		TGVst3Exception refused = assertThrows(TGVst3Exception.class, () -> unconfigured.open(this.hosting.find("Test Gain"), 44100f, FRAMES));
		assertEquals("no VST3 host is configured", refused.getMessage());
		assertNotNull(this.hosting.getFolders());
		assertEquals(2, this.hosting.getFolders().size());
	}
}
