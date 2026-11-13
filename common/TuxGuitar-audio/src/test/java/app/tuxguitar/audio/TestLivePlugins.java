package app.tuxguitar.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.tuxguitar.audio.action.TGAddVst3EffectAction;
import app.tuxguitar.audio.action.TGEffectAction;
import app.tuxguitar.audio.action.TGSetPluginParameterAction;
import app.tuxguitar.audio.action.TGSetPluginStateAction;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.vst3.TGVst3Exception;
import app.tuxguitar.vst3.TGVst3Hosting;
import app.tuxguitar.vst3.TGVst3Instance;
import app.tuxguitar.vst3.TGVst3Processors;

/**
 * The plugins TuxGuitar keeps open for the song's sound chains while no render runs them, with the real host process and
 * test plugins: each follows its element as the song changes, and a gesture of its own changes the song.
 */
public class TestLivePlugins {

	private final SongActionFixture fixture = SongActionFixture.onDocument();
	private final TGVst3Hosting hosting = TGVst3Hosting.getInstance(this.fixture.context);
	private final TGLivePlugins live = TGLivePlugins.getInstance(this.fixture.context);
	private final TGChannel guitar = this.fixture.songManager.getChannel(this.fixture.song, this.fixture.song.getTrack(0).getChannelId());

	@BeforeEach
	public void setUp() throws TGVst3Exception {
		File build = new File(System.getProperty("tg.vst3.native"));
		File windows = new File(new File(build, "bin"), "tuxguitar-vst3-host.exe");
		File host = (windows.isFile() ? windows : new File(new File(build, "bin"), "tuxguitar-vst3-host"));
		this.hosting.configure(host, Arrays.asList(new File(new File(build, "VST3"), "Release")));
		this.hosting.scan();
		TGVst3Processors.install(this.fixture.context);
		this.fixture.map(new TGSetPluginStateAction(this.fixture.context));
		this.fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, this.guitar);
		this.fixture.actionContext.setAttribute(TGAddVst3EffectAction.ATTRIBUTE_PLUGIN, this.hosting.find("Test Gain"));
		this.fixture.execute(new TGAddVst3EffectAction(this.fixture.context));
	}

	@AfterEach
	public void tearDown() {
		this.live.close();
		this.hosting.close();
	}

	private TGVst3Instance gain() throws TGVst3Exception {
		return this.live.get(this.guitar.getChannelId(), "effect-1");
	}

	// the kept plugin's Gain, and whether it is the one opened first
	private String read(TGVst3Instance first) throws TGVst3Exception {
		return gain().getParameter(1) + (gain() == first ? " kept" : " opened again");
	}

	@Test
	public void theKeptPluginFollowsTheSongAndIsNotOpenedAgainForItsOwnGesture() throws TGVst3Exception {
		TGVst3Instance first = gain();
		first.message("TG.gesture", Map.of("id", 1L), Map.of("values", TGVst3Instance.doubles(3.0)));
		String afterGesture = read(first);
		this.fixture.actionContext.setAttribute(TGEffectAction.ATTRIBUTE_EFFECT, 1);
		this.fixture.actionContext.setAttribute(TGSetPluginParameterAction.ATTRIBUTE_PARAMETER, 1);
		this.fixture.actionContext.setAttribute(TGSetPluginParameterAction.ATTRIBUTE_VALUE, 5.0);
		this.fixture.execute(new TGSetPluginParameterAction(this.fixture.context));

		assertEquals("3.0 kept | 5.0 opened again", afterGesture + " | " + read(first));
	}
}
