package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.file.TGWriteSongAction;
import app.tuxguitar.io.tg.TGStream;
import app.tuxguitar.midi.synth.TGAudioBuffer;
import app.tuxguitar.midi.synth.TGAudioProcessor;
import app.tuxguitar.midi.synth.TGAudioProcessorFactory;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.midi.synth.TGSynthManager;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.vst3.TGVst3Element;
import app.tuxguitar.vst3.TGVst3Plugin;

/**
 * Before the song is written, each VST3 plugin of its sound chains opens at the audio device's sample rate (TuxGuitar's
 * default without one) and gives its state. A fake VST3 effect type stands in for the plugin: it tells the rate it
 * opened at and stores it as its state.
 */
public class TestAudioSaving {

	private final TGContext context = new TGContext();
	private final TGSongManager songManager = new TGSongManager();
	private final TGSong song = this.songManager.newSong();
	private final List<Float> rates = new ArrayList<Float>();

	public TestAudioSaving() {
		TGAudioSaving.install(this.context);
		TGSynthManager.getInstance(this.context).addExtension(TGAudioProcessorFactory.class, new TGAudioProcessorFactory() {
			public String getType() {
				return TGVst3Element.EFFECT;
			}

			public TGAudioProcessor createProcessor() {
				return new RateEffect();
			}
		});
		TGProgram chain = TGSoundChain.get(this.song.getChannel(0));
		chain.addOutput(TGVst3Element.create("effect", new TGVst3Plugin("module", "class", "Test Gain", false)));
		TGSoundChain.save(this.song.getChannel(0), chain, this.songManager.getFactory());
		TGActionManager.getInstance(this.context).mapAction(TGWriteSongAction.NAME, new TGWriteSongAction(this.context));
	}

	private final class RateEffect implements TGAudioProcessor {
		private float rate;

		public void setSampleRate(float sampleRate) {
			this.rate = sampleRate;
		}

		public void storeParameters(Map<String, String> parameters) {
			rates.add(this.rate);
		}

		public void restoreParameters(Map<String, String> parameters) {
		}

		public void close() {
		}

		public void finalize() {
		}

		public boolean isOpen() {
			return true;
		}

		public boolean isBusy() {
			return false;
		}

		public void fillBuffer(TGAudioBuffer buffer) {
		}
	}

	private void save() {
		TGActionContext action = new TGActionContext() {
		};
		action.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, this.song);
		action.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER, this.songManager);
		action.setAttribute(TGWriteSongAction.ATTRIBUTE_OUTPUT_STREAM, new ByteArrayOutputStream());
		action.setAttribute(TGWriteSongAction.ATTRIBUTE_FORMAT, TGStream.TG_FORMAT);
		TGActionManager.getInstance(this.context).execute(TGWriteSongAction.NAME, action);
	}

	@Test
	public void eachPluginGivesItsStateAtTheAudioDevicesRateOrTuxGuitarsWithoutOne() {
		save();
		TGAudioSession.getInstance(this.context).setDevice(new SilentDevice() {
			public int getSampleRate() {
				return 48000;
			}
		});
		save();

		assertEquals("[44100.0, 48000.0]", this.rates.toString());
	}
}
