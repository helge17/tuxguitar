package app.tuxguitar.audio.ui;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import app.tuxguitar.app.action.TGActionAdapterManager;
import app.tuxguitar.audio.TGAudioSettings;
import app.tuxguitar.audio.TGLiveAudio;
import app.tuxguitar.audio.TGLivePlugins;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;
import app.tuxguitar.util.configuration.TGConfigManager;
import app.tuxguitar.util.plugin.TGPlugin;

/**
 * TuxGuitar's audio on the desktop: at startup (before the MIDI Port opens) it installs the headless audio features
 * (TGLiveAudio.install: takes, note entry, saving, the recording veto, which also refuses what the desktop disables
 * while playing), maps the audio actions, loads the native audio backend from the native folder (VST3 hosting too) and
 * opens the devices of the settings, so the TuxGuitar Synthesizer plays through them. Then, off the UI thread, it scans
 * the VST3 folders, every 100 ms asks the plugins kept open that nothing else calls (so their editors' gestures come
 * back), and every 50 ms has the UI thread drain the gestures of plugins' editors into the song. Glue only:
 * every decision is in common/TuxGuitar-audio, which is tested.
 */
public class TGAudioPlugin implements TGPlugin {

	public static final String MODULE_ID = "tuxguitar-audio";

	private static final long DRAIN_MILLISECONDS = 50;
	// how often the plugins kept open that nothing else calls (their element runs in no render) are asked
	private static final long POLL_MILLISECONDS = 100;

	private ScheduledExecutorService plugins;

	public String getModuleId() {
		return MODULE_ID;
	}

	public void connect(TGContext context) {
		TGLiveAudio audio = TGLiveAudio.install(context, TGActionAdapterManager.getInstance(context).getDisableOnPlayInterceptor()::containsActionId);
		TGAudioActions.map(context);
		audio.load(TGLiveAudio.nativeFolder(System.getProperties()));
		open(context, TGAudioSettings.read(config(context)));
		this.plugins = scheduler("TuxGuitar VST3 plugins");
		this.plugins.execute(() -> System.err.println(audio.scanPlugins()));
		this.plugins.scheduleWithFixedDelay(() -> drainLater(context), DRAIN_MILLISECONDS, DRAIN_MILLISECONDS, TimeUnit.MILLISECONDS);
		this.plugins.scheduleWithFixedDelay(TGLivePlugins.getInstance(context)::poll, POLL_MILLISECONDS, POLL_MILLISECONDS, TimeUnit.MILLISECONDS);
	}

	public void disconnect(TGContext context) {
		this.plugins.shutdownNow();
		TGLiveAudio.getInstance(context).close();
	}

	static TGConfigManager config(TGContext context) {
		return new TGConfigManager(context, MODULE_ID);
	}

	// the devices of the settings; the log says why there is none
	static void open(TGContext context, TGAudioSettings settings) {
		TGLiveAudio audio = TGLiveAudio.getInstance(context);
		audio.open(settings);
		System.err.println(audio.describe());
	}

	// one daemon thread running tasks in turn
	static ScheduledExecutorService scheduler(String name) {
		return Executors.newSingleThreadScheduledExecutor(runnable -> {
			Thread thread = new Thread(runnable, name);
			thread.setDaemon(true);
			return thread;
		});
	}

	// the gestures ended in plugins' editors change the song on the UI thread
	private static void drainLater(TGContext context) {
		TGLivePlugins live = TGLivePlugins.getInstance(context);
		if (live.hasEnded()) {
			TGSynchronizer.getInstance(context).executeLater(live::drain);
		}
	}
}
