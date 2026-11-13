package app.tuxguitar.midi.synth;

import static app.tuxguitar.midi.synth.FakeProcessors.program;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiControllers;
import app.tuxguitar.player.base.MidiParameters;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.util.TGContext;

/**
 * Pins what a synthesizer channel does today: which program it plays, its sound chain, Volume and Balance, and what
 * reaches its source.
 */
public class TestSynthChannel {

	private static final float SOURCE = 0.25f;
	private static final float OTHER_SOURCE = 0.125f;
	// the 16-bit samples between processors lose up to a few steps of 1/32767 each
	private static final float QUANTUM = 0.001f;

	private final TGContext context = new TGContext();
	private final TGSynthModel model = new TGSynthModel(this.context);
	private final FakeProcessors.SourceFactory source = new FakeProcessors.SourceFactory("source", SOURCE);
	private final FakeProcessors.SourceFactory otherSource = new FakeProcessors.SourceFactory("other", OTHER_SOURCE);
	private final FakeProcessors.EffectFactory twice = new FakeProcessors.EffectFactory("twice", sample -> sample * 2);
	private final FakeProcessors.EffectFactory lift = new FakeProcessors.EffectFactory("lift", sample -> sample + 0.1);
	private final FakeProcessors.EffectFactory eightfold = new FakeProcessors.EffectFactory("eightfold", sample -> sample * 8, true);
	private final FakeProcessors.EffectFactory eighth = new FakeProcessors.EffectFactory("eighth", sample -> sample / 8, true);
	private TGSynthChannel channel;

	@BeforeEach
	public void setUp() throws MidiPlayerException {
		TGSynthManager manager = TGSynthManager.getInstance(this.context);
		manager.addExtension(TGMidiProcessorFactory.class, this.source);
		manager.addExtension(TGMidiProcessorFactory.class, this.otherSource);
		manager.addExtension(TGAudioProcessorFactory.class, this.twice);
		manager.addExtension(TGAudioProcessorFactory.class, this.lift);
		manager.addExtension(TGAudioProcessorFactory.class, this.eightfold);
		manager.addExtension(TGAudioProcessorFactory.class, this.eighth);
		this.channel = (TGSynthChannel) this.model.openChannel(3);
		this.channel.sendControlChange(MidiControllers.BALANCE, 0);
	}

	private float[] render() {
		TGAudioBuffer buffer = new TGAudioBuffer();
		this.channel.fillBuffer(buffer);
		float[][] samples = FakeProcessors.read(buffer);
		return new float[] {samples[0][0], samples[1][0], samples[0][FakeProcessors.FRAMES - 1]};
	}

	private float left() {
		return render()[0];
	}

	// what the chain sounds into a block that held a level before
	private float[][] chain() {
		float[][] block = FakeProcessors.read(FakeProcessors.level(0.5f, 0.5f));
		this.channel.fillChain(block);
		return block;
	}

	@Test
	public void aProgramChangePlaysTheProgramOfTheChannelsBankAndProgram() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source"));
		this.channel.sendProgramChange(5);
		float[] rendered = render();
		assertEquals(SOURCE, rendered[0], QUANTUM);
		assertEquals(0f, rendered[1], QUANTUM);
		assertEquals(SOURCE, rendered[2], QUANTUM);
		assertEquals(3, this.channel.getId());
		assertEquals(this.channel, this.model.getChannelById(3));
	}

	// a program change that drops an effect closes its processor, which is no longer the element's
	@Test
	public void aProcessorDroppedFromTheChainHasNoElement() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source", "lift"));
		this.model.getProgram(0, 6).copyFrom(program("source"));
		this.channel.sendProgramChange(5);
		TGAudioProcessor lifting = this.channel.getProcessor().getOutput(0);
		String before = this.channel.getProcessor().getElement(lifting).getType();
		this.channel.sendProgramChange(6);

		assertEquals("lift null", before + " " + this.channel.getProcessor().getElement(lifting));
	}

	@Test
	public void aChannelWithoutAProgramIsSilent() throws MidiPlayerException {
		this.channel.sendProgramChange(5);
		assertEquals(0f, left(), 0f);
		assertNull(this.channel.getProcessor());
	}

	// volume, balance, then the left and right levels of a source at 0.25
	@Test
	public void volumeAndBalanceFollowTheConstantPowerLaw() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source"));
		this.channel.sendProgramChange(5);
		float[][] rows = {{127, 0, 0.25f, 0f}, {127, 127, 0f, 0.25f}, {127, 64, 0.17566f, 0.17789f}, {64, 0, 0.12598f, 0f}};
		for (float[] row : rows) {
			this.channel.sendControlChange(MidiControllers.VOLUME, (int) row[0]);
			this.channel.sendControlChange(MidiControllers.BALANCE, (int) row[1]);
			float[] rendered = render();
			assertEquals(row[2], rendered[0], QUANTUM, "left at volume " + row[0] + ", balance " + row[1]);
			assertEquals(row[3], rendered[1], QUANTUM, "right at volume " + row[0] + ", balance " + row[1]);
		}
	}

	@Test
	public void anOpenedChannelHasFullVolumeAtTheCentre() throws MidiPlayerException {
		TGSynthChannel fresh = (TGSynthChannel) this.model.openChannel(4);
		this.model.getProgram(0, 5).copyFrom(program("source"));
		fresh.sendProgramChange(5);
		TGAudioBuffer buffer = new TGAudioBuffer();
		fresh.fillBuffer(buffer);
		float[][] samples = FakeProcessors.read(buffer);
		assertEquals(0.17566f, samples[0][0], QUANTUM);
		assertEquals(0.17789f, samples[1][0], QUANTUM);
	}

	// what the chain sounds before Volume and Balance: the tap of the channel
	@Test
	public void theChainSoundsBeforeVolumeAndBalance() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source", "lift"));
		this.channel.sendProgramChange(5);
		this.channel.sendControlChange(MidiControllers.VOLUME, 10);
		float[][] samples = chain();
		assertEquals(SOURCE + 0.1f, samples[0][0], QUANTUM);
		assertEquals(SOURCE + 0.1f, samples[1][0], QUANTUM);
		assertEquals(44100f, this.model.getSampleRate(), 0f);
	}

	// 32-bit float between processors: the source at 0.25 eight times louder is 2.0, and an eighth of it 0.25 again
	@Test
	public void theChainHasRoomAboveFullScaleBetweenProcessors() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source", "eightfold", "eighth"));
		this.channel.sendProgramChange(5);
		float[][] block = chain();
		assertEquals(SOURCE, block[0][0], QUANTUM);
		assertEquals(SOURCE, block[1][FakeProcessors.FRAMES - 1], QUANTUM);
	}

	// Volume and Balance apply to the chain's float output, and only then is it written in 16 bits: 2.0 at a quarter
	// volume is 0.5, where clipping first would give a quarter of full scale
	@Test
	public void volumeAppliesToTheChainBeforeItIsClippedTo16Bits() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source", "eightfold"));
		this.channel.sendProgramChange(5);
		this.channel.sendControlChange(MidiControllers.VOLUME, 32);
		assertEquals(2f * 32 / 127, left(), QUANTUM);
		this.channel.sendControlChange(MidiControllers.VOLUME, 127);
		assertEquals(1f, left(), 0f);
	}

	// the transport the synthesizer's clock is told the song's state in, read by the chain's processors
	@Test
	public void theChainsProcessorsAreGivenTheSynthesizersTransport() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source", "lift"));
		this.channel.sendProgramChange(5);
		assertNotNull(this.model.getTransport());
		assertSame(this.model.getTransport(), this.source.last().transport);
		assertSame(this.model.getTransport(), this.lift.created.get(0).transport);
	}

	@Test
	public void aClosedSourceHasASilentChain() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source", "lift"));
		this.channel.sendProgramChange(5);
		this.source.last().close();
		assertEquals(0f, chain()[0][0], 0f);
	}

	@Test
	public void aChannelWithoutAProgramHasASilentChain() throws MidiPlayerException {
		assertEquals(0f, chain()[1][FakeProcessors.FRAMES - 1], 0f);
	}

	@Test
	public void effectsRunInProcessingOrder() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source", "twice", "lift"));
		this.model.getProgram(0, 6).copyFrom(program("source", "lift", "twice"));
		this.channel.sendProgramChange(5);
		assertEquals(0.6f, left(), QUANTUM);
		this.channel.sendProgramChange(6);
		assertEquals(0.7f, left(), QUANTUM);
	}

	@Test
	public void aDisabledEffectIsSkipped() throws MidiPlayerException {
		TGProgram program = program("source", "twice", "lift");
		program.getOutput(0).setEnabled(false);
		this.model.getProgram(0, 5).copyFrom(program);
		this.channel.sendProgramChange(5);
		assertEquals(0.35f, left(), QUANTUM);
	}

	// the tails of the enabled processors along the chain add up (an echo of an echo rings on for both), and one that
	// never ends makes the chain's endless; a channel without a program has none
	@Test
	public void theChainsTailIsTheSumOfItsEnabledProcessorsTails() throws MidiPlayerException {
		TGProgram program = program("source", "twice", "lift");
		this.model.getProgram(0, 5).copyFrom(program);
		program.getOutput(0).setEnabled(false);
		this.model.getProgram(0, 6).copyFrom(program);
		int none = this.channel.getTail();
		this.channel.sendProgramChange(5);
		this.source.last().tail = 100;
		this.twice.created.get(0).tail = 13230;
		this.lift.created.get(0).tail = 441;
		int all = this.channel.getTail();
		this.channel.sendProgramChange(6);
		int enabled = this.channel.getTail();
		this.lift.created.get(0).tail = TGAudioProcessor.INFINITE_TAIL;
		int endless = this.channel.getTail();

		assertEquals("0 13771 541 " + Integer.MAX_VALUE, none + " " + all + " " + enabled + " " + endless);
	}

	// the latencies of the enabled processors along the chain add up; a channel without a program, or whose source is
	// closed, sounds nothing and has none
	@Test
	public void theChainsLatencyIsTheSumOfItsEnabledProcessorsLatencies() throws MidiPlayerException {
		TGProgram program = program("source", "twice", "lift");
		this.model.getProgram(0, 5).copyFrom(program);
		program.getOutput(0).setEnabled(false);
		this.model.getProgram(0, 6).copyFrom(program);
		int none = this.channel.getLatency();
		this.channel.sendProgramChange(5);
		this.source.last().latency = 600;
		this.twice.created.get(0).latency = 2048;
		this.lift.created.get(0).latency = 256;
		int all = this.channel.getLatency();
		this.channel.sendProgramChange(6);
		int enabled = this.channel.getLatency();
		this.source.last().close();

		assertEquals("0 2904 856 0", none + " " + all + " " + enabled + " " + this.channel.getLatency());
	}

	@Test
	public void aDisabledSourceSilencesTheChain() throws MidiPlayerException {
		TGProgram program = program("source", "lift");
		program.getReceiver().setEnabled(false);
		this.model.getProgram(0, 5).copyFrom(program);
		this.channel.sendProgramChange(5);
		assertEquals(0.1f, left(), QUANTUM);
	}

	@Test
	public void aClosedSourceSilencesTheChain() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source", "lift"));
		this.channel.sendProgramChange(5);
		this.source.last().close();
		assertEquals(0f, left(), 0f);
	}

	@Test
	public void theProgramsParametersReachItsProcessors() throws MidiPlayerException {
		TGProgram program = program("source", "lift");
		program.getReceiver().setParameter("a", "1");
		program.getOutput(0).setParameter("b", "2");
		this.model.getProgram(0, 5).copyFrom(program);
		this.channel.sendProgramChange(5);
		assertEquals("{a=1}", this.source.last().restored.toString());
		assertEquals("{b=2}", this.lift.created.get(0).restored.toString());
	}

	@Test
	public void aProgramChangeToTheSameProgramKeepsItsProcessors() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source", "lift"));
		this.channel.sendProgramChange(5);
		this.channel.sendProgramChange(5);
		assertEquals(1, this.source.created.size());
		assertEquals(1, this.lift.created.size());
	}

	@Test
	public void changingProgramReplacesTheProcessorsItNoLongerHas() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source", "lift"));
		this.model.getProgram(0, 6).copyFrom(program("other"));
		this.channel.sendProgramChange(5);
		this.channel.sendProgramChange(6);
		assertFalse(this.source.last().isOpen());
		assertFalse(this.lift.created.get(0).isOpen());
		assertEquals(OTHER_SOURCE, left(), QUANTUM);
	}

	@Test
	public void notesBendsAndControlsReachTheSource() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source"));
		this.channel.sendProgramChange(5);
		this.channel.sendNoteOn(60, 100, 2, true);
		this.channel.sendPitchBend(70, 2, true);
		this.channel.sendControlChange(MidiControllers.REVERB, 40);
		this.channel.sendNoteOff(60, 0, 2, true);
		this.channel.sendAllNotesOff();
		assertEquals(Arrays.asList("on 60 100 2 true", "bend 70 2 true", "cc 91 40", "off 60 0 2 true", "cc 123 0"), this.source.last().received);
	}

	// timed ones too, then at once
	@Test
	public void bendsAndControlsSentBeforeTheProgramReachItWhenItLoads() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source"));
		this.channel.sendNoteOn(60, 100, 0, false);
		this.channel.sendNoteOff(60, 0, 0, false);
		this.channel.sendAllNotesOff();
		this.channel.sendControlChange(MidiControllers.REVERB, 40);
		this.channel.sendPitchBend(70, 1, false);
		this.channel.sendNoteOn(61, 100, 0, false, 30);
		this.channel.sendNoteOff(61, 0, 0, false, 31);
		this.channel.sendControlChange(MidiControllers.REVERB, 41, 32);
		this.channel.sendPitchBend(71, 1, false, 33);
		this.channel.sendProgramChange(5);
		assertEquals(Arrays.asList("cc 91 40", "bend 70 1 false", "cc 91 41", "bend 71 1 false"), this.source.last().received);
	}

	// the frames of the block about to be rendered; a source that cannot time events gets them at once. Volume, Balance
	// and Bank select are the channel's, at the block's start
	@Test
	public void timedNotesBendsAndControlsReachASourceOnTheirFrames() throws MidiPlayerException {
		FakeProcessors.SourceFactory untimed = new FakeProcessors.SourceFactory("untimed", SOURCE, false);
		TGSynthManager.getInstance(this.context).addExtension(TGMidiProcessorFactory.class, untimed);
		this.model.getProgram(0, 5).copyFrom(program("source"));
		this.model.getProgram(0, 6).copyFrom(program("untimed"));
		for (int program : new int[] {5, 6}) {
			this.channel.sendProgramChange(program);
			this.channel.sendNoteOn(60, 100, 2, true, 30);
			this.channel.sendPitchBend(70, 2, true, 31);
			this.channel.sendControlChange(MidiControllers.REVERB, 40, 32);
			this.channel.sendControlChange(MidiControllers.VOLUME, 100, 33);
			this.channel.sendNoteOff(60, 0, 2, true, 34);
		}

		assertEquals("[on 60 100 2 true @30, bend 70 2 true @31, cc 91 40 @32, off 60 0 2 true @34]"
				+ " [on 60 100 2 true, bend 70 2 true, cc 91 40, off 60 0 2 true]", this.source.last().received + " " + untimed.last().received);
		assertEquals(SOURCE * 100 / 127f, left(), QUANTUM);
	}

	@Test
	public void volumeBalanceAndBankSelectDoNotReachTheSource() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source"));
		this.channel.sendProgramChange(5);
		this.channel.sendControlChange(MidiControllers.VOLUME, 100);
		this.channel.sendControlChange(MidiControllers.BALANCE, 10);
		this.channel.sendControlChange(MidiControllers.BANK_SELECT, 0);
		assertTrue(this.source.last().received.isEmpty());
	}

	// a bank select silences the channel until the next program change plays the program of that bank. A custom
	// program keeps sounding: its source follows the channel's Bank and Program, told them when the program loads and
	// on each Bank select and Program change that changes them
	@Test
	public void aBankSelectSilencesTheChannelUntilTheNextProgramChangeWhileACustomProgramsSourceFollowsBoth() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source"));
		this.model.getProgram(1, 5).copyFrom(program("other"));
		this.channel.sendProgramChange(5);
		this.channel.sendControlChange(MidiControllers.BANK_SELECT, 1);
		float selected = left();
		this.channel.sendProgramChange(5);
		float changed = left();
		sendCustomProgram("source");
		this.channel.sendControlChange(MidiControllers.BANK_SELECT, 0);
		this.channel.sendProgramChange(5);
		this.channel.sendProgramChange(6);

		assertEquals(0f, selected, 0f);
		assertEquals(OTHER_SOURCE, changed, QUANTUM);
		assertEquals(SOURCE, left(), QUANTUM);
		assertEquals(Arrays.asList("program 1:5", "program 0:5", "program 0:6"), this.source.last().received);
	}

	private void sendCustomProgram(String type) throws MidiPlayerException {
		this.channel.sendParameter(MidiParameters.SENDING_PARAMS, Boolean.TRUE.toString());
		if (type != null) {
			this.channel.sendParameter(TGSynthChannel.CUSTOM_PROGRAM_PREFIX + ".id", "custom");
			this.channel.sendParameter(TGSynthChannel.CUSTOM_PROGRAM_PREFIX + ".type", type);
		}
		this.channel.sendParameter(MidiParameters.SENDING_PARAMS, Boolean.FALSE.toString());
	}

	// without a custom program the parameters silence a channel that had no program change, and bring back the bank and
	// program of one that had (program 0 too). A custom program's source has no program to follow before a program
	// change, and follows program 0 after one
	@Test
	public void withoutACustomProgramTheParametersBringBackTheBankAndProgramOrSilenceTheChannel() throws MidiPlayerException {
		this.model.getProgram(0, 0).copyFrom(program("source"));
		sendCustomProgram("other");
		sendCustomProgram(null);
		String silenced = left() + " " + this.channel.getProcessor();
		this.channel.sendProgramChange(0);
		sendCustomProgram("other");
		float custom = left();
		sendCustomProgram(null);

		assertEquals("0.0 null", silenced);
		assertEquals(OTHER_SOURCE, custom, QUANTUM);
		assertEquals(SOURCE, left(), QUANTUM);
		assertEquals("[] [program 0:0]", this.otherSource.created.get(0).received + " " + this.otherSource.last().received);
	}

	private void sendProgram(String prefix, String type) throws MidiPlayerException {
		this.channel.sendParameter(prefix + ".id", type);
		this.channel.sendParameter(prefix + ".type", type);
	}

	// a song of this version saves its sound chain apart from the custom program older versions read
	@Test
	public void aSoundChainPlaysInsteadOfTheCustomProgram() throws MidiPlayerException {
		this.channel.sendParameter(MidiParameters.SENDING_PARAMS, Boolean.TRUE.toString());
		sendProgram(TGSynthChannel.CUSTOM_PROGRAM_PREFIX, "source");
		sendProgram(TGSynthChannel.SOUND_CHAIN_PREFIX, "other");
		this.channel.sendParameter(MidiParameters.SENDING_PARAMS, Boolean.FALSE.toString());
		assertEquals(OTHER_SOURCE, left(), QUANTUM);
		assertEquals(0, this.source.created.size());
	}

	@Test
	public void updatingTheProgramReopensItsProcessors() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source"));
		this.channel.sendProgramChange(5);
		this.channel.getProgram().addOutput(FakeProcessors.element("lift", "lift"));
		this.channel.updateProgram();
		assertEquals(SOURCE + 0.1f, left(), QUANTUM);
		this.channel.updateProgram();
		assertEquals(1, this.source.created.size());
		assertEquals(1, this.lift.created.size());
		assertTrue(this.source.last().isOpen());
	}

	@Test
	public void closingAChannelClosesItsProcessorsAndForgetsIt() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source", "lift"));
		this.channel.sendProgramChange(5);
		this.model.closeChannels();
		assertFalse(this.source.last().isOpen());
		assertFalse(this.lift.created.get(0).isOpen());
		assertEquals(0, this.model.countChannels());
		assertNull(this.model.getChannel(0));
		assertNull(this.model.getChannelById(3));
		assertTrue(this.model.isChannelOpen(this.channel));
	}

	@Test
	public void theSynthesizerIsBusyWhileAProcessorOfAChannelIs() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source", "lift"));
		this.channel.sendProgramChange(5);
		assertFalse(this.model.isBusy());
		this.lift.created.get(0).busy = true;
		assertTrue(this.model.isBusy());
		this.lift.created.get(0).busy = false;
		this.source.last().busy = true;
		assertTrue(this.model.isBusy());
	}

	@Test
	public void programsExistForEveryBankAndProgramOnly() {
		assertEquals(null, this.model.getProgram(129, 0));
		assertEquals(null, this.model.getProgram(0, 128));
		assertEquals(null, this.model.getProgram(-1, 0));
		assertEquals(null, this.model.getProgram(0, -1));
		assertEquals(0, this.model.getProgram(128, 127).countOutputs());
		assertEquals(this.context, this.model.getContext());
	}
}
