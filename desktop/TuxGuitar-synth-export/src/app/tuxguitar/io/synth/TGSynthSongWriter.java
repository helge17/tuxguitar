package app.tuxguitar.io.synth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.base.TGSongWriterHandle;
import app.tuxguitar.midi.synth.TGAudioBuffer;
import app.tuxguitar.midi.synth.TGAudioBufferProcessor;
import app.tuxguitar.midi.synth.TGAudioLine;
import app.tuxguitar.midi.synth.TGSynthModel;
import app.tuxguitar.midi.synth.TGSynthSettings;
import app.tuxguitar.player.base.MidiChannel;
import app.tuxguitar.player.base.MidiParameters;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiSequenceParser;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGChannelParameter;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGSynthSongWriter implements TGSongWriter {

	public static final TGFileFormat FILE_FORMAT = new TGSynthAudioFormat();

	private TGContext context;

	public TGSynthSongWriter(TGContext context) {
		this.context = context;
	}

	public TGFileFormat getFileFormat() {
		return FILE_FORMAT;
	}

	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		try{
			TGSynthAudioSettings settings = handle.getContext().getAttribute(TGSynthAudioSettings.class.getName());
			if( settings == null ) {
				settings = new TGSynthAudioSettings();
			}

			OutputStream out = handle.getOutputStream();
			TGSong tgSong = handle.getSong();
			TGSongManager tgSongManager = new TGSongManager();

			MidiSequenceParser midiSequenceParser = new MidiSequenceParser(tgSong, tgSongManager, MidiSequenceParser.DEFAULT_EXPORT_FLAGS | MidiSequenceParser.ADD_BANK_SELECT);
			TGSynthSequenceHandler midiSequenceHandler = new TGSynthSequenceHandler(tgSong.countTracks());
			midiSequenceParser.parse(midiSequenceHandler);
			if(!midiSequenceHandler.getEvents().isEmpty()) {
				TGSynthModel synthModel = new TGSynthModel(this.context);
				TGAudioBufferProcessor audioProcessor = new TGAudioBufferProcessor(synthModel);
				ByteArrayOutputStream audioBuffer = new ByteArrayOutputStream();
				TGSynthSequencer sequence = new TGSynthSequencer(synthModel, midiSequenceHandler.getEvents());

				this.loadSynthPrograms(synthModel, tgSong);

				sequence.start();
				long duration = 0;
				while(!sequence.isEnded()) {
					sequence.dispatchEvents();

					audioProcessor.process();
					audioBuffer.write(audioProcessor.getBuffer().getBuffer(), 0, audioProcessor.getBuffer().getLength());
					duration += audioProcessor.getBuffer().getLength();
					sequence.forward();
				}
				duration = duration / TGAudioBuffer.CHANNELS;
				ByteArrayInputStream byteBuffer = new ByteArrayInputStream(audioBuffer.toByteArray());
				AudioInputStream sourceStream = new AudioInputStream(byteBuffer, TGAudioLine.AUDIO_FORMAT, duration);
				AudioInputStream targetStream = AudioSystem.getAudioInputStream(settings.getFormat(), sourceStream);
				AudioSystem.write(targetStream, settings.getType(), out);
			}
		} catch(Throwable throwable) {
			throw new TGFileFormatException(throwable);
		}
	}

	private void loadSynthPrograms(TGSynthModel tgSynthModel, TGSong tgSong) throws MidiPlayerException {
		TGSynthSettings tgSynthSettings = new TGSynthSettings(this.context);
		tgSynthSettings.loadPrograms(tgSynthModel);

		// Add channels
		Iterator<TGChannel> tgChannels = tgSong.getChannels();
		while( tgChannels.hasNext() ){
			TGChannel tgChannel = tgChannels.next();
			if( tgChannel.getChannelId() > 0 ) {
				MidiChannel midiChannel = tgSynthModel.openChannel(tgChannel.getChannelId());
				// send parameters
				midiChannel.sendParameter(MidiParameters.SENDING_PARAMS, Boolean.TRUE.toString());

				Iterator<TGChannelParameter> parameters = tgChannel.getParameters();
				while( parameters.hasNext() ){
					TGChannelParameter parameter = (TGChannelParameter) parameters.next();
					midiChannel.sendParameter(parameter.getKey(), parameter.getValue());
				}

				midiChannel.sendParameter(MidiParameters.SENDING_PARAMS, Boolean.FALSE.toString());
			}
		}
	}
}
