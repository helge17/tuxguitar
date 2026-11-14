package app.tuxguitar.audio.ui;

import app.tuxguitar.app.action.TGActionAdapterManager;
import app.tuxguitar.audio.TGAudioSettings;
import app.tuxguitar.audio.TGLiveAudio;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.configuration.TGConfigManager;
import app.tuxguitar.util.plugin.TGPlugin;

/**
 * TuxGuitar's audio on the desktop: at startup (before the MIDI Port opens) it installs the headless audio features
 * (TGLiveAudio.install: takes, note entry, saving, the recording veto, which also refuses what the desktop disables
 * while playing), maps the audio actions, loads the native audio backend from the native folder and opens the devices
 * of the settings, so the TuxGuitar Synthesizer plays through them. Glue only: every decision is in
 * common/TuxGuitar-audio, which is tested.
 */
public class TGAudioPlugin implements TGPlugin {

	public static final String MODULE_ID = "tuxguitar-audio";

	public String getModuleId() {
		return MODULE_ID;
	}

	public void connect(TGContext context) {
		TGLiveAudio audio = TGLiveAudio.install(context, TGActionAdapterManager.getInstance(context).getDisableOnPlayInterceptor()::containsActionId);
		TGAudioActions.map(context);
		audio.load(TGLiveAudio.nativeFolder(System.getProperties()));
		open(context, TGAudioSettings.read(config(context)));
	}

	public void disconnect(TGContext context) {
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
}
