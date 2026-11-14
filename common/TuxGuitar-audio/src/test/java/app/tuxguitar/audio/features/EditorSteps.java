package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import app.tuxguitar.audio.TGLivePlugins;
import app.tuxguitar.audio.TGPluginEditor;
import app.tuxguitar.audio.TGSoundChain;
import app.tuxguitar.audio.action.TGEffectAction;
import app.tuxguitar.audio.action.TGOpenPluginEditorAction;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.vst3.TGVst3EffectProcessor;
import app.tuxguitar.vst3.TGVst3Element;
import app.tuxguitar.vst3.TGVst3Exception;
import app.tuxguitar.vst3.TGVst3Hosting;
import app.tuxguitar.vst3.TGVst3Instance;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Steps on a plugin's editors: opening them, and what the plugin does from its own editor ("the plugin sets X to v":
 * one gesture; "the plugin loads its preset P"), played through the test plugins' messages on the plugin as it runs.
 */
public class EditorSteps {

	// the presets of the test plugins, in order (Test Gain's: README)
	private static final List<String> PRESETS = Arrays.asList("Default", "Loud");

	private final AudioWorld world;
	private final PlaySteps play;
	private final RecordingSteps recording;
	// the state of a plugin before the plugin loaded a preset
	private byte[] beforePreset;

	public EditorSteps(AudioWorld world, PlaySteps play, RecordingSteps recording) {
		this.world = world;
		this.play = play;
		this.recording = recording;
	}

	private TGLivePlugins live() {
		return TGLivePlugins.getInstance(world.fixture().context);
	}

	private TGChannel channel(int track) {
		return world.channel(world.track(track));
	}

	private TGProgramElement effect(int effect, int track) {
		return TGSoundChain.get(channel(track)).getOutput(effect - 1);
	}

	// the plugin of the effect as it runs
	private TGVst3Instance running(int effect, int track) throws TGVst3Exception {
		return live().get(channel(track).getChannelId(), effect(effect, track).getId());
	}

	@When("I open the editor of VST3 effect {int} of track {int}")
	public void openTheEditor(int effect, int track) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel(track));
		world.fixture().actionContext.setAttribute(TGEffectAction.ATTRIBUTE_EFFECT, effect);
		world.execute(new TGOpenPluginEditorAction(world.fixture().context));
		assertTrue(world.succeeded());
	}

	private TGPluginEditor editor(int effect, int track) {
		return live().getEditor(channel(track).getChannelId(), effect(effect, track).getId());
	}

	@Then("the plugin's own editor of VST3 effect {int} of track {int} is open")
	public void itsOwnEditorIsOpen(int effect, int track) {
		assertTrue(editor(effect, track).isOwn());
	}

	@Then("TuxGuitar's parameter editor of VST3 effect {int} of track {int} is open")
	public void theParameterEditorIsOpen(int effect, int track) {
		assertFalse(editor(effect, track).isOwn());
	}

	@Then("the parameters of VST3 effect {int} of track {int} read {string}")
	public void theParametersRead(int effect, int track, String expected) {
		assertEquals(expected, String.join(", ", editor(effect, track).getParameters()));
	}

	// one gesture in the plugin's own editor, through the values
	private void gesture(String title, int effect, int track, double... values) throws TGVst3Exception {
		gesture(running(effect, track), title, values);
	}

	// then the app drains it at once, as its UI thread does within a few milliseconds
	private void gesture(TGVst3Instance instance, String title, double... values) throws TGVst3Exception {
		instance.message("TG.gesture", Map.of("id", (long) instance.findParameter(title)), Map.of("values", TGVst3Instance.doubles(values)));
		live().drain();
	}

	// the gesture, while the song plays, once it reaches the position
	private void schedule(String title, int effect, int track, double value, SongPosition at) {
		double quarters = (double) (at.tick(world.song()) - TGDuration.QUARTER_TIME) / TGDuration.QUARTER_TIME;
		world.reporting().schedule(() -> this.runningOrNone(effect, track), quarters, plugin -> gesture(plugin, title, value));
	}

	private TGVst3Instance runningOrNone(int effect, int track) {
		try {
			return running(effect, track);
		} catch (TGVst3Exception e) {
			return null;
		}
	}

	@When("the plugin sets {string} of VST3 effect {int} of track {int} to {double} at {position}")
	public void thePluginSetsWhilePlaying(String title, int effect, int track, double value, SongPosition at) {
		schedule(title, effect, track, value, at);
		this.play.playUnderWay();
		assertFalse(world.reporting().pending(), "the plugin did the gesture while the song played");
	}

	@Given("I have recorded from {position} {stop} while the plugin set {string} of VST3 effect {int} of track {int} to {double} at {position}")
	public void haveRecordedWhileThePluginSet(SongPosition from, RecordingStop stop, String title, int effect, int track, double value, SongPosition at) {
		schedule(title, effect, track, value, at);
		this.recording.haveRecorded(from, stop);
		assertFalse(world.reporting().pending(), "the plugin did the gesture while recording");
	}

	@Given("the plugin has set {string} of VST3 effect {int} of track {int} to {double}")
	@When("the plugin sets {string} of VST3 effect {int} of track {int} to {double}")
	public void thePluginSets(String title, int effect, int track, double value) throws TGVst3Exception {
		gesture(title, effect, track, value);
	}

	@Given("the plugin has set {string} of VST3 effect {int} of track {int} through {double} to {double}")
	public void thePluginHasSetThrough(String title, int effect, int track, double through, double value) throws TGVst3Exception {
		gesture(title, effect, track, through, value);
	}

	@Given("the plugin has loaded its preset {string} for VST3 effect {int} of track {int}")
	@When("the plugin loads its preset {string} for VST3 effect {int} of track {int}")
	public void thePluginLoadsItsPreset(String preset, int effect, int track) throws TGVst3Exception {
		this.beforePreset = state(effect(effect, track));
		running(effect, track).message("TG.preset", Map.of("index", (long) PRESETS.indexOf(preset)), Collections.<String, byte[]>emptyMap());
		live().drain();
	}

	// the state of the plugin opened from the element
	private byte[] state(TGProgramElement element) throws TGVst3Exception {
		TGVst3EffectProcessor processor = new TGVst3EffectProcessor(TGVst3Hosting.getInstance(world.fixture().context));
		try {
			processor.restoreParameters(element.getParameters());
			return processor.getInstance().getState().getComponent();
		} finally {
			processor.close();
		}
	}

	@Then("the state of VST3 effect {int} of track {int} is the preset {string}")
	public void theStateIsThePreset(int effect, int track, String preset) throws TGVst3Exception {
		TGVst3Instance fresh = TGVst3Hosting.getInstance(world.fixture().context).open(TGVst3Element.plugin(effect(effect, track).getParameters(), false), 44100f, 512);
		try {
			fresh.message("TG.preset", Map.of("index", (long) PRESETS.indexOf(preset)), Collections.<String, byte[]>emptyMap());
			assertArrayEquals(fresh.getState().getComponent(), state(effect(effect, track)));
		} finally {
			fresh.close();
		}
	}

	@Then("the state of VST3 effect {int} of track {int} is byte for byte what it was before the preset")
	public void theStateIsWhatItWasBeforeThePreset(int effect, int track) throws TGVst3Exception {
		assertArrayEquals(this.beforePreset, state(effect(effect, track)));
	}
}
