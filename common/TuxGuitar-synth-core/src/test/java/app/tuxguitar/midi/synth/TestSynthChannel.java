package app.tuxguitar.midi.synth;

import static app.tuxguitar.midi.synth.FakeProcessors.program;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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
	private TGSynthChannel channel;

	@BeforeEach
	public void setUp() throws MidiPlayerException {
		TGSynthManager manager = TGSynthManager.getInstance(this.context);
		manager.addExtension(TGMidiProcessorFactory.class, this.source);
		manager.addExtension(TGMidiProcessorFactory.class, this.otherSource);
		manager.addExtension(TGAudioProcessorFactory.class, this.twice);
		manager.addExtension(TGAudioProcessorFactory.class, this.lift);
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
		TGAudioBuffer buffer = new TGAudioBuffer();
		this.channel.fillChain(buffer);
		float[][] samples = FakeProcessors.read(buffer);
		assertEquals(SOURCE + 0.1f, samples[0][0], QUANTUM);
		assertEquals(SOURCE + 0.1f, samples[1][0], QUANTUM);
		assertEquals(44100f, this.model.getSampleRate(), 0f);
	}

	@Test
	public void aClosedSourceHasASilentChain() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source", "lift"));
		this.channel.sendProgramChange(5);
		this.source.last().close();
		TGAudioBuffer buffer = new TGAudioBuffer();
		this.channel.fillChain(buffer);
		assertEquals(0f, FakeProcessors.read(buffer)[0][0], 0f);
	}

	@Test
	public void aChannelWithoutAProgramHasASilentChain() throws MidiPlayerException {
		TGAudioBuffer buffer = FakeProcessors.level(0.5f, 0.5f);
		this.channel.fillChain(buffer);
		assertEquals(0f, FakeProcessors.read(buffer)[0][0], 0f);
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

	@Test
	public void bendsAndControlsSentBeforeTheProgramReachItWhenItLoads() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source"));
		this.channel.sendNoteOn(60, 100, 0, false);
		this.channel.sendNoteOff(60, 0, 0, false);
		this.channel.sendAllNotesOff();
		this.channel.sendControlChange(MidiControllers.REVERB, 40);
		this.channel.sendPitchBend(70, 1, false);
		this.channel.sendProgramChange(5);
		assertEquals(Arrays.asList("cc 91 40", "bend 70 1 false"), this.source.last().received);
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

	@Test
	public void aBankSelectSilencesTheChannelUntilTheNextProgramChange() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source"));
		this.model.getProgram(1, 5).copyFrom(program("other"));
		this.channel.sendProgramChange(5);
		this.channel.sendControlChange(MidiControllers.BANK_SELECT, 1);
		assertEquals(0f, left(), 0f);
		this.channel.sendProgramChange(5);
		assertEquals(OTHER_SOURCE, left(), QUANTUM);
	}

	private void sendCustomProgram(String type) throws MidiPlayerException {
		this.channel.sendParameter(MidiParameters.SENDING_PARAMS, Boolean.TRUE.toString());
		if (type != null) {
			this.channel.sendParameter(TGSynthChannel.CUSTOM_PROGRAM_PREFIX + ".id", "custom");
			this.channel.sendParameter(TGSynthChannel.CUSTOM_PROGRAM_PREFIX + ".type", type);
		}
		this.channel.sendParameter(MidiParameters.SENDING_PARAMS, Boolean.FALSE.toString());
	}

	// today a custom program freezes the channel's sound: Program changes and bank selects are ignored (the README's
	// deliberate changes make the source follow the channel's Bank and Program instead)
	@Test
	public void aCustomProgramIgnoresProgramChangesAndBankSelects() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source"));
		sendCustomProgram("other");
		assertEquals(OTHER_SOURCE, left(), QUANTUM);
		this.channel.sendProgramChange(5);
		this.channel.sendControlChange(MidiControllers.BANK_SELECT, 1);
		assertEquals(OTHER_SOURCE, left(), QUANTUM);
	}

	@Test
	public void withoutACustomProgramTheParametersBringBackTheBankAndProgram() throws MidiPlayerException {
		this.model.getProgram(0, 5).copyFrom(program("source"));
		sendCustomProgram("other");
		this.channel.sendProgramChange(5);
		sendCustomProgram(null);
		assertEquals(SOURCE, left(), QUANTUM);
	}

	@Test
	public void withoutACustomProgramTheParametersBringBackProgramZero() throws MidiPlayerException {
		this.model.getProgram(0, 0).copyFrom(program("source"));
		this.channel.sendProgramChange(0);
		sendCustomProgram(null);
		assertEquals(SOURCE, left(), QUANTUM);
	}

	@Test
	public void withoutACustomProgramOrAProgramChangeTheParametersSilenceTheChannel() throws MidiPlayerException {
		sendCustomProgram("other");
		sendCustomProgram(null);
		assertEquals(0f, left(), 0f);
		assertNull(this.channel.getProcessor());
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
