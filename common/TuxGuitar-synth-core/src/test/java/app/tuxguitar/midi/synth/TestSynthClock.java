package app.tuxguitar.midi.synth;

import static app.tuxguitar.midi.synth.FakeProcessors.program;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.player.base.MidiParameters;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiReceiver;
import app.tuxguitar.util.TGContext;

/**
 * The synthesizer is a sample clock: before it renders each block of 512 frames it runs its listener (the sequencer
 * playing on it), and the events sent to the receiver of a frame reach the channel's source on that frame of the block.
 * A program change, a parameter, all notes off and the pitch bend reset apply at the block's start.
 *
 * Channels 3 and 4 play program 5, a source that writes " @frame" after a timed event; program 7 is another source.
 */
public class TestSynthClock {

	private final TGContext context = new TGContext();
	private final TGSynthModel model = new TGSynthModel(this.context);
	private final FakeProcessors.SourceFactory source = new FakeProcessors.SourceFactory("source", 0.25f);
	private final FakeProcessors.SourceFactory other = new FakeProcessors.SourceFactory("other", 0.125f);
	private final List<String> log = new ArrayList<String>();

	public TestSynthClock() throws MidiPlayerException {
		TGSynthManager.getInstance(this.context).addExtension(TGMidiProcessorFactory.class, this.source);
		TGSynthManager.getInstance(this.context).addExtension(TGMidiProcessorFactory.class, this.other);
		this.model.getProgram(0, 5).copyFrom(program("source"));
		this.model.getProgram(0, 7).copyFrom(program("other"));
		this.model.openChannel(3).sendProgramChange(5);
		this.model.openChannel(4).sendProgramChange(5);
	}

	private List<String> received(int channel) {
		return this.source.created.get(channel - 3).received;
	}

	@Test
	public void eventsOnAFrameReachTheChannelsSourceOnIt() throws MidiPlayerException {
		this.model.at(30).sendNoteOn(3, 60, 100, 2, true);
		this.model.at(31).sendPitchBend(3, 70, 2, true);
		this.model.at(32).sendControlChange(3, 91, 40);
		this.model.at(33).sendNoteOff(3, 60, 0, 2, true);
		this.model.at(34).sendNoteOn(9, 60, 100, 2, true);

		assertEquals("[on 60 100 2 true @30, bend 70 2 true @31, cc 91 40 @32, off 60 0 2 true @33] []", received(3) + " " + received(4));
	}

	// channel 3 gets program 7 (another source), channel 4 a custom program of that source
	@Test
	public void programChangesParametersAndResetsApplyAtTheBlocksStart() throws MidiPlayerException {
		MidiReceiver frame = this.model.at(30);
		frame.sendAllNotesOff();
		frame.sendPitchBendReset();
		frame.sendProgramChange(3, 7);
		frame.sendParameter(4, MidiParameters.SENDING_PARAMS, Boolean.TRUE.toString());
		frame.sendParameter(4, TGSynthChannel.CUSTOM_PROGRAM_PREFIX + ".id", "custom");
		frame.sendParameter(4, TGSynthChannel.CUSTOM_PROGRAM_PREFIX + ".type", "other");
		frame.sendParameter(4, MidiParameters.SENDING_PARAMS, Boolean.FALSE.toString());

		assertEquals("[cc 123 0, bend 64 -1 false] [cc 123 0, bend 64 -1 false] 2", received(3) + " " + received(4) + " " + this.other.created.size());
	}

	@Test
	public void theListenerRunsBeforeEachBlockWithItsFrames() {
		TGAudioBufferProcessor blocks = new TGAudioBufferProcessor(this.model);
		this.model.setListener((clock, frames) -> this.log.add(frames + " on " + (clock == this.model ? "the synthesizer" : clock)));
		blocks.process();
		blocks.process();
		this.model.setListener(null);
		blocks.process();

		assertEquals("[512 on the synthesizer, 512 on the synthesizer]", this.log.toString());
	}

	// what plays on is not stopped by a failing listener
	@Test
	public void aFailingListenerLeavesTheBlockToRender() {
		TGAudioBufferProcessor blocks = new TGAudioBufferProcessor(this.model);
		this.model.setListener((clock, frames) -> {
			throw new MidiPlayerException("failing");
		});
		blocks.process();
		this.model.setListener((clock, frames) -> {
			throw new IllegalStateException("failing");
		});
		blocks.process();

		assertTrue(FakeProcessors.read(blocks.getBuffer())[0][0] > 0.1f);
	}
}
