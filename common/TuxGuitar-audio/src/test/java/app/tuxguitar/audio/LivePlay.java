package app.tuxguitar.audio;

import java.util.Iterator;

import app.tuxguitar.midi.synth.TGAudioBufferProcessor;
import app.tuxguitar.midi.synth.TGSynthModel;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiSequenceParser;
import app.tuxguitar.player.base.MidiTransmitter;
import app.tuxguitar.player.impl.sequencer.MidiSequencerImpl;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * The song played live, headless, as the TuxGuitar Synthesizer plays it: TuxGuitar's sequencer on the synthesizer's
 * sample clock (General MIDI programs through Gervill, each track's channel with its sound chain), its blocks rendered
 * as the synthesizer's thread renders them, until the sequencer stops; then the channels close. What is heard is not
 * kept: this is for what the plugins are told.
 */
public final class LivePlay {

	private static final int MOST_BLOCKS = 100000;

	private LivePlay() {
	}

	public static void play(TGContext context, TGSongManager songManager, TGSong song, long start, int sampleRate) throws MidiPlayerException {
		TGSynthModel model = TGMidiTaps.model(context, sampleRate);
		Iterator<TGTrack> tracks = song.getTracks();
		while (tracks.hasNext()) {
			int channel = tracks.next().getChannelId();
			model.openChannel(channel);
			TGMidiTaps.sendParameters(model, songManager, song, channel);
		}
		MidiSequencerImpl sequencer = new MidiSequencerImpl(context);
		sequencer.setTransmitter(new MidiTransmitter());
		new MidiSequenceParser(song, songManager, MidiSequenceParser.DEFAULT_PLAY_FLAGS).parse(sequencer.createSequence(song.countTracks() + 2));
		sequencer.setClock(model);
		sequencer.start();
		sequencer.setTickPosition(start);
		TGAudioBufferProcessor blocks = new TGAudioBufferProcessor(model);
		for (int block = 0; block < MOST_BLOCKS && sequencer.isRunning(); block++) {
			blocks.process();
		}
		model.closeChannels();
	}
}
