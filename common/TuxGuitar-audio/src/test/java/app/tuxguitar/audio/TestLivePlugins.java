package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.audio.action.TGAddVst3EffectAction;
import app.tuxguitar.audio.action.TGEffectAction;
import app.tuxguitar.audio.action.TGSetEffectEnabledAction;
import app.tuxguitar.audio.action.TGSetPluginParameterAction;
import app.tuxguitar.audio.action.TGSetPluginStateAction;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.midi.synth.TGSynthChannel;
import app.tuxguitar.midi.synth.TGSynthModel;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.vst3.TGVst3EffectProcessor;
import app.tuxguitar.vst3.TGVst3Exception;
import app.tuxguitar.vst3.TGVst3Hosting;
import app.tuxguitar.vst3.TGVst3Instance;
import app.tuxguitar.vst3.TGVst3PluginProcessor;

/**
 * The plugins of the song's sound chains as they run, with the real host process and test plugins: those of the
 * synthesizer playing live (it tells them when a channel's chain changes), else one kept open for the element, which
 * follows the song. A gesture of a plugin's own editor changes the song once drained, as an app drains them on the
 * thread that edits the song; an editor closes when its plugin no longer runs; saving takes a running plugin's state.
 * The guitar's chain has the effect "Test Gain" (its Gain starts at 2.0).
 */
public class TestLivePlugins {

	private final SongActionFixture fixture = SongActionFixture.onDocument();
	private final TGLivePlugins live = TGLivePlugins.getInstance(this.fixture.context);
	private final TGChannel guitar = this.fixture.songManager.getChannel(this.fixture.song, this.fixture.song.getTrack(0).getChannelId());
	// the elements whose state the song took, in order
	private final List<String> changes = new ArrayList<String>();
	private TGVst3Hosting hosting;

	@BeforeEach
	public void setUp() throws TGVst3Exception {
		this.hosting = TestPluginHosting.host(this.fixture.context);
		this.fixture.map(new TGSetPluginStateAction(this.fixture.context) {
			protected boolean change(TGActionContext context, TGChannel channel, TGProgram chain) {
				changes.add(context.getAttribute(ATTRIBUTE_ELEMENT));
				return super.change(context, channel, chain);
			}
		});
		this.fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, this.guitar);
		this.addGain();
	}

	@AfterEach
	public void tearDown() {
		this.live.close();
		this.hosting.close();
	}

	private void addGain() {
		this.fixture.actionContext.setAttribute(TGAddVst3EffectAction.ATTRIBUTE_PLUGIN, this.hosting.find("Test Gain"));
		this.fixture.execute(new TGAddVst3EffectAction(this.fixture.context));
	}

	private TGVst3Instance gain(String element) throws TGVst3Exception {
		return this.live.get(this.guitar.getChannelId(), element);
	}

	// the kept plugin's Gain, and whether it is the one opened first
	private String read(TGVst3Instance first) throws TGVst3Exception {
		return gain("effect-1").getParameter(1) + (gain("effect-1") == first ? " kept" : " opened again");
	}

	private static void gesture(TGVst3Instance plugin, double value) throws TGVst3Exception {
		plugin.message("TG.gesture", Map.of("id", 1L), Map.of("values", TGVst3Instance.doubles(value)));
	}

	// the Gain of the song's element: of the plugin opened afresh from it
	private double songGain(String element) throws TGVst3Exception {
		TGVst3EffectProcessor fresh = new TGVst3EffectProcessor(this.hosting);
		try {
			fresh.restoreParameters(TGSoundChain.element(TGSoundChain.get(this.guitar), element).getParameters());
			return fresh.getInstance().getParameter(1);
		} finally {
			fresh.close();
		}
	}

	// the guitar's chain playing on a synthesizer the live plugins listen to
	private TGSynthChannel playLive(TGSynthModel synthesizer) throws MidiPlayerException {
		synthesizer.addChannelListener(this.live);
		return TestPluginHosting.play(synthesizer, this.fixture.songManager, this.fixture.song, this.guitar);
	}

	// the plugin the synthesizer's channel runs for the element
	private static TGVst3Instance running(TGSynthChannel channel, String element) {
		TGProgramElement effect = TGSoundChain.element(channel.getProgram(), element);
		return ((TGVst3PluginProcessor) channel.getProcessor().getAudioProcessor(effect)).getInstance();
	}

	@Test
	public void theKeptPluginFollowsTheSongAndIsNotOpenedAgainForItsOwnGesture() throws TGVst3Exception {
		TGVst3Instance first = gain("effect-1");
		gesture(first, 3.0);
		this.live.drain();
		String afterGesture = read(first);
		this.fixture.actionContext.setAttribute(TGEffectAction.ATTRIBUTE_EFFECT, 1);
		this.fixture.actionContext.setAttribute(TGSetPluginParameterAction.ATTRIBUTE_PARAMETER, 1);
		this.fixture.actionContext.setAttribute(TGSetPluginParameterAction.ATTRIBUTE_VALUE, 5.0);
		this.fixture.execute(new TGSetPluginParameterAction(this.fixture.context));

		assertEquals("3.0 kept | 5.0 opened again", afterGesture + " | " + read(first));
	}

	// the thread that hears a gesture (the audio thread) does not change the song: draining does, once per element
	@Test
	public void gesturesChangeTheSongOnceDrainedOneChangeWithTheLastState() throws TGVst3Exception {
		gesture(gain("effect-1"), 3.0);
		gesture(gain("effect-1"), 4.0);
		String before = this.live.hasEnded() + " " + this.changes + " " + songGain("effect-1");
		this.live.drain();
		assertEquals("true [] 2.0 | false [effect-1] 4.0", before + " | " + this.live.hasEnded() + " " + this.changes + " " + songGain("effect-1"));
	}

	@Test
	public void eachElementChangesOnceInTheOrderOfItsLastGesture() throws TGVst3Exception {
		this.addGain();
		gesture(gain("effect-1"), 3.0);
		gesture(gain("effect-2"), 4.0);
		gesture(gain("effect-1"), 5.0);
		this.live.drain();
		assertEquals("[effect-2, effect-1] 5.0 4.0", this.changes + " " + songGain("effect-1") + " " + songGain("effect-2"));
	}

	// the synthesizer's plugin, not a second one kept open; once the channel closes, one is kept open again
	@Test
	public void theLivePluginIsTheOneTheSynthesizerRunsWhileItsChannelIsOpen() throws Exception {
		TGSynthModel synthesizer = LiveOutput.synthesizer(this.fixture.context, 44100);
		TGSynthChannel channel = playLive(synthesizer);
		TGVst3Instance running = running(channel, "effect-1");
		boolean same = (gain("effect-1") == running);
		synthesizer.closeChannel(channel);
		assertEquals("true false", same + " " + (gain("effect-1") == running));
	}

	// the chain loaded again (as each time the song starts playing) keeps the plugin, and its editor; the channel closed
	// ends both
	@Test
	public void anEditorStaysOpenWhileItsPluginRunsAndClosesWithIt() throws Exception {
		TGSynthModel synthesizer = LiveOutput.synthesizer(this.fixture.context, 44100);
		TGSynthChannel channel = playLive(synthesizer);
		TGVst3Instance running = running(channel, "effect-1");
		this.live.openEditor(this.guitar.getChannelId(), "effect-1");
		TGMidiTaps.sendParameters(synthesizer, this.fixture.songManager, this.fixture.song, this.guitar.getChannelId());
		String reloaded = (running(channel, "effect-1") == running) + " " + (this.live.getEditor(this.guitar.getChannelId(), "effect-1") != null);
		synthesizer.closeChannel(channel);
		assertEquals("true true | false", reloaded + " | " + (this.live.getEditor(this.guitar.getChannelId(), "effect-1") != null));
	}

	// a plugin changed as it runs (not through the song) gives the song its state; a disabled one is opened afresh
	@Test
	public void savingTakesARunningPluginsStateUnlessItIsDisabled() throws Exception {
		TGSynthChannel channel = playLive(LiveOutput.synthesizer(this.fixture.context, 44100));
		running(channel, "effect-1").setParameter(1, 4.0);
		TGPluginStates.store(this.fixture.context, this.fixture.song, this.fixture.songManager.getFactory(), 44100f);
		double enabled = songGain("effect-1");
		running(channel, "effect-1").setParameter(1, 5.0);
		this.fixture.actionContext.setAttribute(TGEffectAction.ATTRIBUTE_EFFECT, 1);
		this.fixture.actionContext.setAttribute(TGSetEffectEnabledAction.ATTRIBUTE_ENABLED, false);
		this.fixture.execute(new TGSetEffectEnabledAction(this.fixture.context));
		TGPluginStates.store(this.fixture.context, this.fixture.song, this.fixture.songManager.getFactory(), 44100f);
		assertEquals("4.0 4.0", enabled + " " + songGain("effect-1"));
	}

	// its host process gone (crashed), the plugin is opened afresh from the song: the chain keeps a state, not parameters
	@Test
	public void savingOpensAfreshAPluginWhoseRunningOneIsGone() throws Exception {
		TGSynthChannel channel = playLive(LiveOutput.synthesizer(this.fixture.context, 44100));
		this.fixture.actionContext.setAttribute(TGEffectAction.ATTRIBUTE_EFFECT, 1);
		this.fixture.actionContext.setAttribute(TGSetPluginParameterAction.ATTRIBUTE_PARAMETER, 1);
		this.fixture.actionContext.setAttribute(TGSetPluginParameterAction.ATTRIBUTE_VALUE, 3.0);
		this.fixture.execute(new TGSetPluginParameterAction(this.fixture.context));
		ProcessHandle host = ProcessHandle.of(running(channel, "effect-1").getHost().getPid()).get();
		host.destroyForcibly();
		host.onExit().join();
		TGPluginStates.store(this.fixture.context, this.fixture.song, this.fixture.songManager.getFactory(), 44100f);
		Map<String, String> saved = TGSoundChain.element(TGSoundChain.get(this.guitar), "effect-1").getParameters();
		assertEquals("true false 3.0", saved.containsKey("vst3.state") + " " + saved.containsKey("vst3.parameter.1") + " " + songGain("effect-1"));
	}
}
