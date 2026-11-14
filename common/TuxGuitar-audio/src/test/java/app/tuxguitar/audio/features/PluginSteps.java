package app.tuxguitar.audio.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import app.tuxguitar.audio.TGLivePlugins;
import app.tuxguitar.audio.TGSoundChain;
import app.tuxguitar.audio.action.TGAddVst3EffectAction;
import app.tuxguitar.audio.action.TGChooseGeneralMidiAction;
import app.tuxguitar.audio.action.TGChooseTakesAction;
import app.tuxguitar.audio.action.TGChooseVst3InstrumentAction;
import app.tuxguitar.audio.action.TGEffectAction;
import app.tuxguitar.audio.action.TGMoveEffectUpAction;
import app.tuxguitar.audio.action.TGRemoveEffectAction;
import app.tuxguitar.audio.action.TGSetEffectEnabledAction;
import app.tuxguitar.audio.action.TGSetPluginParameterAction;
import app.tuxguitar.audio.action.TGSoundChainAction;
import app.tuxguitar.audio.undo.TGUndoableOnSuccess;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.undo.impl.channel.TGUndoableModifyChannelController;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.vst3.TGVst3EffectProcessor;
import app.tuxguitar.vst3.TGVst3Element;
import app.tuxguitar.vst3.TGVst3Exception;
import app.tuxguitar.vst3.TGVst3Host;
import app.tuxguitar.vst3.TGVst3Hosting;
import app.tuxguitar.vst3.TGVst3Instance;
import app.tuxguitar.vst3.TGVst3Plugin;
import app.tuxguitar.vst3.TGVst3Processors;
import io.cucumber.java.After;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Steps on VST3 plugins: the VST3 folders and their scan, the plugins of a track's sound chain and their parameters,
 * and the host process they run in. The test plugins are the real bundles the native build makes (the system property
 * tg.vst3.native names its directory), run in the real host process.
 */
public class PluginSteps {

	private final AudioWorld world;
	private List<TGVst3Plugin> scanned = new ArrayList<TGVst3Plugin>();
	// a plugin a Then opened to look at its process (closed with the hosting)
	private TGVst3Instance opened;

	public PluginSteps(AudioWorld world) {
		this.world = world;
	}

	private TGVst3Hosting hosting() {
		return TGVst3Hosting.getInstance(world.fixture().context);
	}

	private static File nativeBuild() {
		return new File(System.getProperty("tg.vst3.native"));
	}

	private static File host() {
		File bin = new File(nativeBuild(), "bin");
		File windows = new File(bin, "tuxguitar-vst3-host.exe");
		return (windows.isFile() ? windows : new File(bin, "tuxguitar-vst3-host"));
	}

	// as TuxGuitar does when it starts: the VST3 folders scanned, VST3 plugins given to the synthesizer
	@Given("the VST3 folders hold the test plugins")
	public void theVst3FoldersHoldTheTestPlugins() throws TGVst3Exception {
		hosting().configure(host(), Arrays.asList(new File(new File(nativeBuild(), "VST3"), "Release")));
		world.reporting().install(world.fixture().context);
		TGVst3Processors.install(world.fixture().context);
		this.scanned = hosting().scan();
	}

	@After
	public void stopTheHost() {
		TGLivePlugins.getInstance(world.fixture().context).close();
		hosting().close();
	}

	@Given("the VST3 folders also hold the VST 2 plugin {string}")
	public void theVst3FoldersAlsoHoldTheVst2Plugin(String name) throws IOException {
		File folder = Files.createTempDirectory("vst3-folder-").toFile();
		folder.deleteOnExit();
		File plugin = new File(folder, name + ".dll");
		Files.copy(new File(new File(nativeBuild(), "vst2"), name + ".dll").toPath(), plugin.toPath());
		plugin.deleteOnExit();
		List<File> folders = new ArrayList<File>(hosting().getFolders());
		folders.add(folder);
		hosting().configure(host(), folders);
	}

	@When("I scan the VST3 folders")
	public void scanTheVst3Folders() throws TGVst3Exception {
		this.scanned = hosting().scan();
	}

	private String names(boolean instruments) {
		return this.scanned.stream().filter(plugin -> plugin.isInstrument() == instruments).map(TGVst3Plugin::getName).collect(Collectors.joining(", "));
	}

	// "the VST3 effects read ..." or "the VST3 instruments read ..."
	@Then("the VST3 {word} read {string}")
	public void theVst3PluginsRead(String kind, String expected) {
		assertEquals(expected, names(kind.equals("instruments")));
	}

	@Then("neither the VST3 effects nor the VST3 instruments name {string}")
	public void neitherNames(String name) {
		assertFalse(names(false).contains(name));
		assertFalse(names(true).contains(name));
		assertFalse(this.scanned.isEmpty());
	}

	// each plugin runs in a host process of its own, which ends when the plugin closes (the render closed the one that
	// played): the plugin is opened again as TuxGuitar opens it, and its process looked at
	@Then("{string} runs in a process other than TuxGuitar's")
	public void runsInAnotherProcess(String name) throws TGVst3Exception {
		this.opened = hosting().open(plugin(name), TestInterface.SAMPLE_RATE, 512);
		TGVst3Host host = this.opened.getHost();
		assertEquals(Arrays.asList(name), host.getOpened());
		assertNotEquals(ProcessHandle.current().pid(), host.getPid());
		assertTrue(ProcessHandle.of(host.getPid()).isPresent());
	}

	@Then("neither TuxGuitar nor the plugin process listens on a network port")
	public void nothingListensOnANetworkPort() throws TGVst3Exception {
		TGVst3Host host = this.opened.getHost();
		assertEquals(0, host.networkListeners(ProcessHandle.current().pid()));
		assertEquals(0, host.networkListeners(host.getPid()));
	}

	private TGVst3Plugin plugin(String name) {
		TGVst3Plugin plugin = hosting().find(name);
		assertNotNull(plugin, name);
		return plugin;
	}

	// runs the sound chain action on the track's channel, undoably when it succeeds, as the app runs a channel change
	private void change(int track, TGSoundChainAction action) {
		world.fixture().actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, world.channel(world.track(track)));
		world.executeUndoable(action, new TGUndoableOnSuccess(new TGUndoableModifyChannelController()));
	}

	@When("I add the VST3 effect {string} to track {int}")
	public void addTheVst3Effect(String name, int track) {
		world.fixture().actionContext.setAttribute(TGAddVst3EffectAction.ATTRIBUTE_PLUGIN, plugin(name));
		change(track, new TGAddVst3EffectAction(world.fixture().context));
	}

	@Given("I have added the VST3 effect {string} to track {int}")
	public void haveAddedTheVst3Effect(String name, int track) {
		addTheVst3Effect(name, track);
		assertTrue(world.succeeded());
	}

	@When("I choose the VST3 instrument {string} for track {int}")
	public void chooseTheVst3Instrument(String name, int track) {
		world.fixture().actionContext.setAttribute(TGAddVst3EffectAction.ATTRIBUTE_PLUGIN, plugin(name));
		change(track, new TGChooseVst3InstrumentAction(world.fixture().context));
	}

	@Given("I have chosen the VST3 instrument {string} for track {int}")
	public void haveChosenTheVst3Instrument(String name, int track) {
		chooseTheVst3Instrument(name, track);
		assertTrue(world.succeeded());
	}

	@When("I go back to the General MIDI program for track {int}")
	public void goBackToTheGeneralMidiProgram(int track) {
		change(track, new TGChooseGeneralMidiAction(world.fixture().context));
	}

	@When("I give track {int}'s channel the source \"takes\"")
	public void giveTheSourceTakes(int track) {
		change(track, new TGChooseTakesAction(world.fixture().context));
	}

	private void changeEffect(int effect, int track, TGEffectAction action) {
		world.fixture().actionContext.setAttribute(TGEffectAction.ATTRIBUTE_EFFECT, effect);
		change(track, action);
	}

	@When("I move VST3 effect {int} of track {int} up")
	public void moveUp(int effect, int track) {
		changeEffect(effect, track, new TGMoveEffectUpAction(world.fixture().context));
	}

	@Given("I have moved VST3 effect {int} of track {int} up")
	public void haveMovedUp(int effect, int track) {
		moveUp(effect, track);
		assertTrue(world.succeeded());
	}

	@When("I remove VST3 effect {int} of track {int}")
	public void remove(int effect, int track) {
		changeEffect(effect, track, new TGRemoveEffectAction(world.fixture().context));
	}

	@Given("I have removed VST3 effect {int} of track {int}")
	public void haveRemoved(int effect, int track) {
		remove(effect, track);
		assertTrue(world.succeeded());
	}

	@ParameterType("enable|have disabled")
	public Boolean enabling(String enabling) {
		return enabling.equals("enable");
	}

	// "I enable ..." (a When) and "I have disabled ..." (a Given)
	@When("I {enabling} VST3 effect {int} of track {int}")
	public void setEnabled(boolean enabled, int effect, int track) {
		world.fixture().actionContext.setAttribute(TGSetEffectEnabledAction.ATTRIBUTE_ENABLED, enabled);
		changeEffect(effect, track, new TGSetEffectEnabledAction(world.fixture().context));
		assertTrue(world.succeeded());
	}

	@Given("I have kept VST3 effect {int} of track {int} enabled")
	public void haveKeptEnabled(int effect, int track) {
		assertTrue(element(effect, track).isEnabled());
	}

	// an effect of the track's sound chain by its number, or without one its source
	private TGProgramElement element(Integer effect, int track) {
		TGProgram chain = TGSoundChain.get(world.channel(world.track(track)));
		return (effect != null ? chain.getOutput(effect - 1) : chain.getReceiver());
	}

	// the id of the plugin's parameter with the title, as its parameter list gives it
	private int parameter(TGProgramElement element, String title) throws TGVst3Exception {
		TGVst3Instance instance = hosting().open(TGVst3Element.plugin(element.getParameters(), TGVst3Element.INSTRUMENT.equals(element.getType())), 44100f, 512);
		int id = instance.findParameter(title);
		instance.close();
		assertNotEquals(-1, id, title);
		return id;
	}

	private void setParameter(String title, Integer effect, int track, double value) throws TGVst3Exception {
		world.fixture().actionContext.setAttribute(TGEffectAction.ATTRIBUTE_EFFECT, effect);
		world.fixture().actionContext.setAttribute(TGSetPluginParameterAction.ATTRIBUTE_PARAMETER, parameter(element(effect, track), title));
		world.fixture().actionContext.setAttribute(TGSetPluginParameterAction.ATTRIBUTE_VALUE, value);
		change(track, new TGSetPluginParameterAction(world.fixture().context));
		world.fixture().actionContext.setAttribute(TGEffectAction.ATTRIBUTE_EFFECT, null);
		assertTrue(world.succeeded());
	}

	@Given("I have set {string} of VST3 effect {int} of track {int} to {double}")
	public void haveSetAnEffectsParameter(String title, int effect, int track, double value) throws TGVst3Exception {
		setParameter(title, effect, track, value);
	}

	// the value the plugin holds once opened from the effect's element
	@Then("{string} of VST3 effect {int} of track {int} is {double}")
	public void theParameterIs(String title, int effect, int track, double value) throws TGVst3Exception {
		TGProgramElement element = element(effect, track);
		TGVst3EffectProcessor processor = new TGVst3EffectProcessor(hosting());
		processor.restoreParameters(element.getParameters());
		try {
			assertEquals(value, processor.getInstance().getParameter(parameter(element, title)), 1e-9);
		} finally {
			processor.close();
		}
	}

	@Given("I have set {string} of the VST3 instrument of track {int} to {double}")
	public void haveSetTheInstrumentsParameter(String title, int track, double value) throws TGVst3Exception {
		setParameter(title, null, track, value);
	}
}
