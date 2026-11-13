package app.tuxguitar.midi.synth.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.jupiter.api.Test;

import app.tuxguitar.midi.synth.TGAudioBuffer;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.properties.TGProperties;
import app.tuxguitar.util.properties.TGPropertiesManager;

/**
 * Pins the Gervill source of the TuxGuitar Synthesizer today, headless: it loads a General MIDI program from the
 * soundbank in its settings (Gervill's emergency soundbank when there is none) on a thread of its own, then sounds
 * the notes it is sent into 16-bit buffers at 44100 Hz.
 */
public class TestGervillProcessor {

	private static final int FRAMES = TGAudioBuffer.BUFFER_SIZE / 2;

	private final TGContext context = new TGContext();
	private final Map<String, String> config = new HashMap<String, String>();

	public TestGervillProcessor() {
		TGPropertiesManager properties = TGPropertiesManager.getInstance(this.context);
		properties.setPropertiesFactory(MapProperties::new);
		properties.addPropertiesReader("config", (TGProperties read, String module) -> {
			if (module.equals("tuxguitar-synth-gervill")) {
				this.config.forEach(read::setValue);
			}
		});
	}

	static final class MapProperties implements TGProperties {
		private final Map<String, String> values = new HashMap<String, String>();

		public String getValue(String key) {
			return this.values.get(key);
		}

		public void setValue(String key, String value) {
			this.values.put(key, value);
		}

		public void remove(String key) {
			this.values.remove(key);
		}

		public void clear() {
			this.values.clear();
		}

		public void update(Properties properties) {
		}

		public Set<String> getStringKeys() {
			return this.values.keySet();
		}
	}

	private GervillProcessor loaded(String program, String channelMode) throws InterruptedException {
		return loaded(program, channelMode, null);
	}

	private GervillProcessor loaded(String program, String channelMode, String soundbank) throws InterruptedException {
		GervillProcessor processor = (GervillProcessor) new GervillProcessorFactory(this.context).createProcessor();
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("gervil.program", program);
		if (channelMode != null) {
			parameters.put("gervil.channel.mode", channelMode);
		}
		parameters.put("gervil.soundbank.path", soundbank);
		processor.restoreParameters(parameters);
		long deadline = System.currentTimeMillis() + 30000;
		while (processor.isBusy() && System.currentTimeMillis() < deadline) {
			Thread.sleep(5);
		}
		assertFalse(processor.isBusy());
		return processor;
	}

	// the left side of the next buffers
	private static float[] render(GervillProcessor processor, int buffers) {
		float[] left = new float[buffers * FRAMES];
		float[][] samples = new float[TGAudioBuffer.CHANNELS][FRAMES];
		for (int b = 0; b < buffers; b++) {
			TGAudioBuffer buffer = new TGAudioBuffer();
			processor.fillBuffer(buffer);
			buffer.read(samples);
			System.arraycopy(samples[0], 0, left, b * FRAMES, FRAMES);
		}
		return left;
	}

	static double peak(float[] samples) {
		double peak = 0;
		for (float sample : samples) {
			peak = Math.max(peak, Math.abs(sample));
		}
		return peak;
	}

	private static int strongestFrequency(float[] samples) {
		return strongestFrequency(samples, TGAudioBuffer.SAMPLE_RATE);
	}

	// the frequency, in whole Hz from 100 to 2000, where the samples have the most energy
	private static int strongestFrequency(float[] samples, float sampleRate) {
		int strongest = 0;
		double most = -1;
		for (int frequency = 100; frequency <= 2000; frequency++) {
			double re = 0;
			double im = 0;
			for (int i = 0; i < samples.length; i++) {
				double phase = 2 * Math.PI * frequency * i / sampleRate;
				re += samples[i] * Math.cos(phase);
				im += samples[i] * Math.sin(phase);
			}
			if (re * re + im * im > most) {
				most = re * re + im * im;
				strongest = frequency;
			}
		}
		return strongest;
	}

	// a timed note is a MIDI message: no key above 127
	@Test
	public void refusesATimedNoteOutsideMidi() throws InterruptedException {
		GervillProcessor processor = loaded("0:0", null);
		assertThrows(IllegalArgumentException.class, () -> processor.sendNoteOn(128, 95, 0, false, 0));
	}

	@Test
	public void aLoadedProgramIsSilentUntilANote() throws InterruptedException {
		GervillProcessor processor = loaded("0:0", null);
		assertTrue(processor.isOpen());
		assertEquals(0, peak(render(processor, 4)), 0);
	}

	@Test
	public void aNoteSoundsAtItsPitch() throws InterruptedException {
		GervillProcessor processor = loaded("0:80", null);
		processor.sendNoteOn(69, 127, 0, false);
		render(processor, 4);
		float[] sounding = render(processor, 8);
		assertTrue(peak(sounding) > 0.05, "peak " + peak(sounding));
		assertEquals(440, strongestFrequency(sounding), 9);
	}

	// untimed, or timed on a frame of the next buffer
	@Test
	public void aNoteOffEndsTheNote() throws InterruptedException {
		for (boolean timed : new boolean[] {false, true}) {
			GervillProcessor processor = loaded("0:80", null);
			processor.sendNoteOn(69, 127, 0, false);
			double sounding = peak(render(processor, 8));
			if (timed) {
				processor.sendNoteOff(69, 0, 0, false, 100);
			} else {
				processor.sendNoteOff(69, 0, 0, false);
			}
			render(processor, 130);
			double released = peak(render(processor, 4));
			assertTrue(released < sounding / 10, timed + ": released " + released + ", sounding " + sounding);
		}
	}

	// untimed, or timed on a frame of the next buffer: Volume 0 on every channel Gervill has (16 in voice mode) silences
	// notes of voice 0 and 9 (on channels 0 and 10)
	@Test
	public void aControlChangeReachesTheSynthesizer() throws InterruptedException {
		for (boolean timed : new boolean[] {false, true}) {
			GervillProcessor processor = loaded("0:80", "3");
			if (timed) {
				processor.sendControlChange(7, 0, 100);
			} else {
				processor.sendControlChange(7, 0);
			}
			processor.sendNoteOn(69, 127, 0, false);
			processor.sendNoteOn(69, 127, 9, false);
			assertEquals(0, peak(render(processor, 8)), 0.0001, "timed " + timed);
		}
	}

	@Test
	public void aPitchBendRaisesTheNote() throws InterruptedException {
		GervillProcessor processor = loaded("0:80", null);
		processor.sendPitchBend(127, 0, false);
		processor.sendNoteOn(69, 127, 0, false);
		render(processor, 4);
		assertEquals(494, strongestFrequency(render(processor, 8)), 10);
	}

	// a timed note, or a timed bend, goes to the Gervill channel of its voice: in bend mode a bent note's is channel 1,
	// where the other one, untimed, goes
	@Test
	public void timedNotesAndBendsGoToTheChannelOfTheirVoice() throws InterruptedException {
		for (boolean timedNote : new boolean[] {false, true}) {
			GervillProcessor processor = loaded("0:80", "2");
			if (timedNote) {
				processor.sendPitchBend(127, 0, true);
				processor.sendNoteOn(69, 127, 0, true, 0);
			} else {
				processor.sendPitchBend(127, 0, true, 0);
				processor.sendNoteOn(69, 127, 0, true);
			}
			render(processor, 4);
			assertEquals(494, strongestFrequency(render(processor, 8)), 10, "timed note " + timedNote);
		}
	}

	// a bend timed on the first frame sounds exactly as the same bend sent untimed before it: its 7 bits are the high
	// ones of MIDI's 14
	@Test
	public void aTimedBendIsTheUntimedBend() throws InterruptedException {
		float[][] bent = new float[2][];
		for (int timed = 0; timed < 2; timed++) {
			GervillProcessor processor = loaded("0:80", null);
			if (timed == 1) {
				processor.sendPitchBend(100, 0, false, 0);
			} else {
				processor.sendPitchBend(100, 0, false);
			}
			processor.sendNoteOn(69, 127, 0, false);
			bent[timed] = render(processor, 8);
		}
		assertTrue(peak(bent[0]) > 0.05);
		assertArrayEquals(bent[0], bent[1]);
	}

	@Test
	public void theProgramIsKeptInItsParameters() throws InterruptedException {
		GervillProcessor processor = loaded(" 0 : 25 ", "3");
		Map<String, String> parameters = new HashMap<String, String>();
		processor.storeParameters(parameters);
		assertEquals("{gervil.channel.mode=3, gervil.program=0:25, gervil.soundbank.path=null}", new java.util.TreeMap<String, String>(parameters).toString());
	}

	@Test
	public void aProgramThatIsNotBankAndProgramLoadsTheFirstProgram() throws InterruptedException {
		GervillProcessor processor = loaded("25", null);
		assertEquals(0, processor.getProgram().getBank());
		assertEquals(0, processor.getProgram().getProgram());
		assertEquals(GervillProgram.CHANNEL_MODE_BEND, processor.getProgram().getChannelMode());
	}

	@Test
	public void aProgramThatIsNotANumberIsIgnored() throws InterruptedException {
		GervillProcessor processor = loaded("a:b", null);
		assertEquals(-1, processor.getProgram().getProgram());
	}

	@Test
	public void theChannelModeChoosesGervillsChannelPerVoiceOrBend() throws InterruptedException {
		GervillProcessor single = loaded("0:0", "1");
		assertEquals(0, single.resolveChannel(5, true));
		GervillProcessor bend = loaded("0:0", "2");
		assertEquals(1, bend.resolveChannel(5, true));
		assertEquals(0, bend.resolveChannel(5, false));
		GervillProcessor voice = loaded("0:0", "3");
		assertEquals(0, voice.resolveChannel(0, false));
		assertEquals(8, voice.resolveChannel(8, false));
		assertEquals(10, voice.resolveChannel(9, false));
		assertEquals(10, voice.resolveChannel(-1, false));
	}

	// each channel mode opens as many of Gervill's channels as it sends notes to
	@Test
	public void everyChannelModeSoundsItsNotes() throws InterruptedException {
		GervillProcessor single = loaded("0:80", "1");
		single.sendNoteOn(69, 127, 3, true);
		assertTrue(peak(render(single, 8)) > 0.05);
		GervillProcessor bend = loaded("0:80", "2");
		bend.sendNoteOn(69, 127, 3, true);
		assertTrue(peak(render(bend, 8)) > 0.05);
		GervillProcessor voice = loaded("0:80", "3");
		voice.sendNoteOn(69, 127, 8, false);
		voice.sendNoteOn(72, 127, 9, false);
		assertTrue(peak(render(voice, 8)) > 0.05);
	}

	@Test
	public void anEmptySoundbankPathPlaysTheDefaultSoundbank() throws InterruptedException {
		GervillProcessor processor = loaded("0:80", null, "");
		processor.sendNoteOn(69, 127, 0, false);
		assertTrue(peak(render(processor, 8)) > 0.05);
	}

	@Test
	public void aShortSoundbankPathThatIsNoFileLeavesTheProgramSilent() throws InterruptedException {
		GervillProcessor processor = loaded("0:80", null, "x");
		processor.sendNoteOn(69, 127, 0, false);
		assertEquals(0, peak(render(processor, 4)), 0);
	}

	@Test
	public void aProgramPlaysFromTheSoundbankFileItNames() throws Exception {
		java.io.File file = java.io.File.createTempFile("soundbank", ".sf2");
		file.deleteOnExit();
		media.sound.EmergencySoundbank.createSoundbank().save(file);
		GervillProcessor processor = loaded("0:80", null, file.getAbsolutePath());
		processor.sendNoteOn(69, 127, 0, false);
		render(processor, 4);
		assertEquals(440, strongestFrequency(render(processor, 8)), 9);
	}

	@Test
	public void theDefaultSoundbankIsTheOneOfTheSettingsOrElseTheEmergencySoundbank() throws Exception {
		java.io.File file = java.io.File.createTempFile("soundbank", ".sf2");
		file.deleteOnExit();
		media.sound.SF2Soundbank saved = media.sound.EmergencySoundbank.createSoundbank();
		saved.setName("from the settings");
		saved.save(file);
		GervillSoundbankFactory factory = new GervillSoundbankFactory();
		assertEquals("from the settings", factory.createDefaultSoundbank(withSoundbank(file.getAbsolutePath())).getName());
		assertNotEquals("from the settings", factory.createDefaultSoundbank(withSoundbank(null)).getName());
		assertNotEquals("from the settings", factory.createDefaultSoundbank(withSoundbank("no-such-soundbank.sf2")).getName());
	}

	private static TGContext withSoundbank(String soundbankPath) {
		TestGervillProcessor test = new TestGervillProcessor();
		test.config.put(GervillSettings.GERVILL_SOUNDBANK_PATH, soundbankPath);
		return test.context;
	}

	@Test
	public void aClosedProcessorIsNotOpenAndFillsNothing() throws InterruptedException {
		GervillProcessor processor = loaded("0:80", null);
		processor.sendNoteOn(69, 127, 0, false);
		processor.finalize();
		assertFalse(processor.isOpen());
		assertFalse(processor.isBusy());
		assertEquals(0, peak(render(processor, 1)), 0);
	}

	@Test
	public void aSoundbankThatCannotBeReadLeavesTheProgramSilent() throws InterruptedException {
		GervillProcessor processor = new GervillProcessor(this.context);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("gervil.program", "0:80");
		parameters.put("gervil.soundbank.path", "no-such-soundbank.sf2");
		processor.restoreParameters(parameters);
		while (processor.isBusy()) {
			Thread.sleep(5);
		}
		processor.sendNoteOn(69, 127, 0, false);
		assertEquals(0, peak(render(processor, 4)), 0);
	}

	@Test
	public void programsAreEqualByBankProgramChannelModeAndSoundbank() {
		GervillProgram a = new GervillProgram();
		GervillProgram b = new GervillProgram();
		b.setSoundbankPath("");
		assertEquals(a, b);
		b.setBank(1);
		assertNotEquals(a, b);
		b.copyFrom(a);
		b.setProgram(2);
		assertNotEquals(a, b);
		b.copyFrom(a);
		b.setChannelMode(GervillProgram.CHANNEL_MODE_VOICE);
		assertNotEquals(a, b);
		b.copyFrom(a);
		b.setSoundbankPath("x.sf2");
		assertNotEquals(a, b);
		assertNotEquals(a, "a");
		b.copyFrom(a);
		assertEquals(a, b);
	}

	@Test
	public void theSettingsKeepTheSoundbank() {
		this.config.put(GervillSettings.GERVILL_SOUNDBANK_PATH, "a.sf2");
		GervillSettings settings = new GervillSettings(this.context);
		assertEquals("a.sf2", settings.getSoundbankPath());
		settings.setSoundbankPath("b.sf2");
		settings.setSoundbankFolder("banks");
		assertEquals("b.sf2", settings.getSoundbankPath());
		assertEquals("banks", settings.getSoundbankFolder());
		settings.save();
		assertEquals("gervill", new GervillProcessorFactory(this.context).getType());
	}

	// a synthesizer opened at the audio device's rate renders its notes at that rate, so they keep their pitch
	@Test
	public void aSynthesizerAtAnotherSampleRateSoundsNotesAtTheirPitch() throws Exception {
		app.tuxguitar.midi.synth.TGSynthManager.getInstance(this.context).addExtension(app.tuxguitar.midi.synth.TGMidiProcessorFactory.class, new GervillProcessorFactory(this.context));
		app.tuxguitar.midi.synth.TGSynthModel model = new app.tuxguitar.midi.synth.TGSynthModel(this.context, 48000);
		app.tuxguitar.midi.synth.TGProgram program = new app.tuxguitar.midi.synth.TGProgram();
		app.tuxguitar.midi.synth.TGProgramElement receiver = new app.tuxguitar.midi.synth.TGProgramElement();
		receiver.setId("gm");
		receiver.setType(GervillProcessorFactory.TYPE);
		receiver.setParameter("gervil.program", "0:80");
		program.setReceiver(receiver);
		model.getProgram(0, 80).copyFrom(program);
		app.tuxguitar.midi.synth.TGSynthChannel channel = (app.tuxguitar.midi.synth.TGSynthChannel) model.openChannel(1);
		channel.sendProgramChange(80);
		while (model.isBusy()) {
			Thread.sleep(5);
		}
		TGAudioBuffer silence = new TGAudioBuffer();
		channel.fillChain(silence);
		assertEquals(0f, FakeSilence.peak(silence), 0f, "signed 16-bit: silence is 0");
		channel.sendNoteOn(69, 127, 0, false);
		float[] left = new float[12 * FRAMES];
		float[][] samples = new float[TGAudioBuffer.CHANNELS][FRAMES];
		for (int b = 0; b < 12; b++) {
			TGAudioBuffer buffer = new TGAudioBuffer();
			channel.fillChain(buffer);
			buffer.read(samples);
			System.arraycopy(samples[0], 0, left, b * FRAMES, FRAMES);
		}
		float[] sounding = java.util.Arrays.copyOfRange(left, 4 * FRAMES, left.length);
		assertEquals(440, strongestFrequency(sounding, 48000), 9);
		assertEquals(48000f, model.getSampleRate(), 0f);
	}

	private static final class FakeSilence {
		static float peak(TGAudioBuffer buffer) {
			float[][] samples = new float[TGAudioBuffer.CHANNELS][FRAMES];
			buffer.read(samples);
			float peak = 0;
			for (float sample : samples[0]) {
				peak = Math.max(peak, Math.abs(sample));
			}
			return peak;
		}
	}
}
