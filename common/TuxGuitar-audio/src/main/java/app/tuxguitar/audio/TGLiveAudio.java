package app.tuxguitar.audio;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import app.tuxguitar.audio.action.TGRecordingVeto;
import app.tuxguitar.audio.device.TGAudioBackend;
import app.tuxguitar.audio.device.TGAudioDeviceException;
import app.tuxguitar.audio.device.TGAudioDeviceInfo;
import app.tuxguitar.audio.device.TGPortAudio;
import app.tuxguitar.midi.synth.TGAudioBuffer;
import app.tuxguitar.midi.synth.TGSynthAudioExporter;
import app.tuxguitar.midi.synth.TGSynthManager;
import app.tuxguitar.midi.synth.TGSynthModel;
import app.tuxguitar.midi.synth.TGSynthOutput;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonUtil;
import app.tuxguitar.vst3.TGVst3Exception;
import app.tuxguitar.vst3.TGVst3Folders;
import app.tuxguitar.vst3.TGVst3Hosting;

/**
 * The app's live audio, one per context: the live audio device opened on the devices the settings choose, at the rate
 * the TuxGuitar Synthesizer renders at, is the session's device, its armed tracks metered and kept monitored
 * (TGLiveInput), and the synthesizer plays through it (it is the output the synth manager gives the synthesizer once
 * installed). Without a device (the backend did not load, or the devices did not open) the synthesizer plays through
 * its own output and getProblem says why. When the devices are chosen again, the TuxGuitar Synthesizer's MIDI Port, if
 * open, is opened again so its synthesizer plays through the new device, with the song's channels open.
 */
public final class TGLiveAudio implements TGSynthOutput {

	// the system property an app gives the folder of the native libraries with
	public static final String NATIVE_PATH = "tuxguitar.audio.native.path";

	private final TGContext context;
	private TGAudioBackend backend;
	private TGLiveDevice device;
	private TGLiveInput input;
	// the output device open: "<name> (<host API>)"
	private String output;
	private String problem;
	private boolean installed;

	private TGLiveAudio(TGContext context) {
		this.context = context;
	}

	public static TGLiveAudio getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGLiveAudio.class.getName(), TGLiveAudio::new);
	}

	// what an app installs at startup: audio tracks sound their takes (TGLiveTakes), entering a note sounds it
	// (TGNoteEntry), songs save their takes and plugin states (TGAudioSaving), editing actions are refused during a
	// recording, those of the app's rule too (desktop: those it disables while playing), and the synthesizer plays
	// through the live device
	public static synchronized TGLiveAudio install(TGContext context, Predicate<String> refused) {
		TGLiveAudio audio = getInstance(context);
		if (!audio.installed) {
			TGLiveTakes.install(context);
			TGNoteEntry.install(context);
			TGAudioSaving.install(context);
			TGRecordingVeto.getInstance(context).refuse(refused);
			TGSynthManager.getInstance(context).addExtension(TGSynthOutput.class, audio);
			TGSynthManager.getInstance(context).addExtension(TGSynthAudioExporter.class, new TGAudioExport(context));
			audio.installed = true;
		}
		return audio;
	}

	// a line for the log and the settings: the device open, or why there is none
	public synchronized String describe() {
		if (this.device == null) {
			return "TuxGuitar audio: no device (" + this.problem + "), the TuxGuitar Synthesizer plays through Java Sound";
		}
		return "TuxGuitar audio: " + this.output + " at " + this.device.getSampleRate() + " Hz, latency in " + this.device.getInputLatency() + " out " + this.device.getOutputLatency() + " frames";
	}

	// to choose from: the backend's host APIs, and the devices of one with inputs, or outputs; none without a backend
	public synchronized List<String> getHostApis() {
		return (this.backend != null ? this.backend.getHostApis() : Collections.<String>emptyList());
	}

	public synchronized List<String> getInputs(String hostApi) {
		return (this.backend != null ? TGAudioSettings.inputs(this.backend, hostApi) : Collections.<String>emptyList());
	}

	public synchronized List<String> getOutputs(String hostApi) {
		return (this.backend != null ? TGAudioSettings.outputs(this.backend, hostApi) : Collections.<String>emptyList());
	}

	// the folder of the native libraries: the one the app gives (NATIVE_PATH), else the first folder Java loads
	// libraries from that holds the native audio library, else the first of them (empty entries skipped)
	public static File nativeFolder(Properties system) {
		List<String> folders = Arrays.stream(system.getProperty("java.library.path", "").split(Pattern.quote(File.pathSeparator))).filter(folder -> !folder.isEmpty()).collect(Collectors.toList());
		String library = System.mapLibraryName(TGPortAudio.LIBRARY);
		String holding = folders.stream().filter(folder -> new File(folder, library).isFile()).findFirst().orElse(folders.stream().findFirst().orElse("."));
		return new File(system.getProperty(NATIVE_PATH, holding));
	}

	// the native audio backend, from the folder; if it does not load, that is the problem. VST3 plugins are hosted by the
	// host process in the folder, from the system's VST3 folders
	public void load(File folder) {
		this.load(folder, System.getProperties(), System.getenv());
	}

	// on the system the properties and the environment describe
	synchronized void load(File folder, Properties system, Map<String, String> env) {
		String os = system.getProperty("os.name", "");
		TGVst3Hosting.getInstance(this.context).configure(TGVst3Hosting.executable(folder, os), TGVst3Folders.standard(os, env, new File(system.getProperty("user.home", "."))));
		try {
			this.use(TGPortAudio.load(folder));
		} catch (TGAudioDeviceException | LinkageError e) {
			this.fail("the native audio library did not load from " + folder.getAbsolutePath() + ": " + e.getMessage());
		}
	}

	// the VST3 folders scanned for plugins (it takes a while: an app runs it off its UI thread); a line for the log
	public String scanPlugins() {
		TGVst3Hosting hosting = TGVst3Hosting.getInstance(this.context);
		try {
			return "TuxGuitar audio: " + hosting.scan().size() + " VST3 plugins in " + hosting.getFolders();
		} catch (TGVst3Exception e) {
			return "TuxGuitar audio: no VST3 plugins (" + e.getMessage() + ")";
		}
	}

	// devices are chosen on this backend from now on
	public synchronized void use(TGAudioBackend backend) {
		this.backend = backend;
	}

	// the devices the settings choose on the backend, instead of those open; none without a backend
	public synchronized void open(TGAudioSettings settings) {
		this.close();
		TGAudioSession.getInstance(this.context).setRecordingOffset(settings.getRecordingOffset());
		try {
			this.start(this.backend, settings);
		} catch (TGAudioDeviceException e) {
			this.problem = e.getMessage();
		}
		this.reopenSynthesizer();
	}

	private void start(TGAudioBackend backend, TGAudioSettings settings) throws TGAudioDeviceException {
		if (backend == null) {
			throw new TGAudioDeviceException(this.problem);
		}
		int input = settings.chooseInput(backend);
		int output = settings.chooseOutput(backend);
		this.device = TGLiveDevice.open(backend, input, settings.countInputChannels(backend, input), output, TGAudioBuffer.SAMPLE_RATE);
		TGAudioDeviceInfo chosen = backend.getDevices().get(output);
		this.output = chosen.getName() + " (" + chosen.getHostApi() + ")";
		this.input = TGLiveInput.install(this.context, this.device);
		TGAudioSession.getInstance(this.context).setDevice(this.device);
		this.problem = null;
	}

	private void reopenSynthesizer() {
		MidiPlayer player = MidiPlayer.getInstance(this.context);
		if (player.isOutputPortOpen(TGAudioSession.SYNTHESIZER_PORT_KEY)) {
			player.closeOutputPort();
			player.openOutputPort(TGAudioSession.SYNTHESIZER_PORT_KEY);
			player.resetChannels();
		}
	}

	// no device: the backend did not load, for that reason
	public synchronized void fail(String problem) {
		this.close();
		this.problem = problem;
	}

	public synchronized void close() {
		if (this.device != null) {
			this.input.close();
			this.device.close();
			TGAudioSession.getInstance(this.context).setDevice(null);
			this.device = null;
		}
	}

	// why there is no device; null while there is one
	public synchronized String getProblem() {
		return this.problem;
	}

	// the input channels opened on the device, which tracks record from; none without a device
	public synchronized int countInputChannels() {
		return (this.device != null ? this.device.getInputChannels() : 0);
	}

	// the synthesizer opening plays through the device, if there is one; either way its chains' plugins are the live
	// plugins (TGLivePlugins hears each change of them)
	public synchronized boolean play(TGSynthModel synthesizer) {
		synthesizer.addChannelListener(TGLivePlugins.getInstance(this.context));
		if (this.device == null) {
			return false;
		}
		this.device.play(synthesizer);
		return true;
	}

	public synchronized void stop(TGSynthModel synthesizer) {
		if (this.device != null) {
			this.device.stop(synthesizer);
		}
	}
}
