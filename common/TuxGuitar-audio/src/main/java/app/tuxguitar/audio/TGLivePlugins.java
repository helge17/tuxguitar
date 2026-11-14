package app.tuxguitar.audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.audio.action.TGSetPluginStateAction;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.midi.synth.TGAudioProcessor;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.midi.synth.TGSynthChannel;
import app.tuxguitar.midi.synth.TGSynthChannelListener;
import app.tuxguitar.midi.synth.TGSynthChannelProcessor;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonUtil;
import app.tuxguitar.vst3.TGVst3Element;
import app.tuxguitar.vst3.TGVst3Exception;
import app.tuxguitar.vst3.TGVst3Gestures;
import app.tuxguitar.vst3.TGVst3Instance;
import app.tuxguitar.vst3.TGVst3Parameter;
import app.tuxguitar.vst3.TGVst3PluginProcessor;

/**
 * The VST3 plugins of the song's sound chains as they run, each known by its channel and the id of its element of the
 * channel's chain: those of a render that runs (it binds them; the synthesizer playing live tells this listener each
 * time a channel's chain changes); otherwise one kept open for the element, as the TuxGuitar Synthesizer keeps its
 * chains open, opened from the song's element and opened again when the element changes (by undo, say). A plugin's
 * editor is its own, or, without one, TuxGuitar's parameter editor; it closes once its plugin no longer runs (the chain
 * changed, the channel closed).
 *
 * A gesture in a plugin's own editor, or a preset chosen there, is heard with its host's replies, on the thread that
 * called the plugin (live, the audio thread), which only notes it. Draining, on the thread that edits the song (an
 * app's UI thread), changes the song: each element whose plugin ended gestures keeps that plugin's state, by one
 * TGSetPluginStateAction run on an action context of its own (one undo step per element).
 */
public final class TGLivePlugins implements TGSynthChannelListener {

	// what a render that is not live (an export) binds its plugins to: nothing
	public static final TGLivePlugins NONE = new TGLivePlugins(null, false);

	private final TGContext context;
	private final boolean live;
	// by key (see key): the instances of the renders that run
	private final Map<String, TGVst3Instance> running = new HashMap<String, TGVst3Instance>();
	// by key: the processors kept open while no render runs the element, and the element's parameters they were opened with
	private final Map<String, Kept> kept = new HashMap<String, Kept>();
	private final Map<String, Editor> editors = new HashMap<String, Editor>();
	// the gestures ended, in order, until drained (the audio thread adds, the app's UI thread drains)
	private final ConcurrentLinkedQueue<Gesture> ended = new ConcurrentLinkedQueue<Gesture>();

	private TGLivePlugins(TGContext context, boolean live) {
		this.context = context;
		this.live = live;
	}

	public static TGLivePlugins getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGLivePlugins.class.getName(), key -> new TGLivePlugins(key, true));
	}

	private static String key(int channel, String element) {
		return channel + "/" + element;
	}

	// a live render runs the processor for the element of the channel's chain (one that runs no VST3 plugin is left out)
	public synchronized void bind(int channel, String element, Object processor) {
		TGVst3Instance instance = (processor instanceof TGVst3PluginProcessor ? ((TGVst3PluginProcessor) processor).getInstance() : null);
		if (instance != null && this.live) {
			this.running.put(key(channel, element), this.listened(channel, element, instance));
		}
	}

	// the processors of the synthesizer channel's chain (its source, when it has one, and its effects)
	public synchronized void bind(int channel, TGSynthChannel synthesizer) {
		TGProgram chain = synthesizer.getProgram();
		TGSynthChannelProcessor processors = synthesizer.getProcessor();
		if (chain.getReceiver() != null) {
			this.bind(channel, chain.getReceiver().getId(), processors.getProcessor());
		}
		for (int i = 0; i < chain.countOutputs(); i++) {
			this.bind(channel, chain.getOutput(i).getId(), processors.getAudioProcessor(chain.getOutput(i)));
		}
	}

	// the render that ran the channel's plugins ended
	public synchronized void unbind(int channel) {
		this.forget(channel);
		this.closeEditors();
	}

	private void forget(int channel) {
		this.running.keySet().removeIf(key -> key.startsWith(channel + "/"));
	}

	// the live synthesizer's channel runs the processors of its chain now, or none (closed)
	public synchronized void chainChanged(TGSynthChannel synthesizer) {
		this.forget(synthesizer.getId());
		if (synthesizer.getProcessor() != null) {
			this.bind(synthesizer.getId(), synthesizer);
		}
		this.closeEditors();
	}

	private TGVst3Instance listened(int channel, String element, TGVst3Instance instance) {
		instance.setListener(new TGVst3Gestures(() -> this.ended.add(new Gesture(channel, element, instance))));
		return instance;
	}

	// the plugin of the element of the channel's chain as it runs
	public synchronized TGVst3Instance get(int channel, String element) throws TGVst3Exception {
		TGVst3Instance instance = this.running.get(key(channel, element));
		return (instance != null ? instance : this.kept(channel, element));
	}

	// the plugin of the element as it runs, or the one kept open for it; null when neither is (none is opened)
	public synchronized TGVst3Instance find(int channel, String element) {
		return this.current(key(channel, element));
	}

	private TGVst3Instance current(String key) {
		Kept kept = this.kept.get(key);
		return this.running.getOrDefault(key, (kept != null ? kept.instance : null));
	}

	// the plugin kept open for the element, opened again when the song's element is not what it was opened with
	private TGVst3Instance kept(int channel, String element) throws TGVst3Exception {
		TGProgram chain = this.chain(channel);
		TGProgramElement song = TGSoundChain.element(chain, element);
		Kept kept = this.kept.get(key(channel, element));
		if (kept == null || !kept.parameters.equals(song.getParameters())) {
			this.close(kept);
			kept = this.open(channel, chain, song);
			this.kept.put(key(channel, element), kept);
		}
		return kept.instance;
	}

	// as the TuxGuitar Synthesizer opens it, at the audio device's rate
	private Kept open(int channel, TGProgram chain, TGProgramElement element) throws TGVst3Exception {
		TGAudioProcessor processor = TGPluginStates.processor(this.context, element, chain);
		processor.setSampleRate(TGAudioSaving.sampleRate(this.context));
		processor.restoreParameters(element.getParameters());
		TGVst3Instance instance = ((TGVst3PluginProcessor) processor).getInstance();
		if (instance == null) {
			processor.close();
			throw new TGVst3Exception("the VST3 plugin " + TGVst3Element.getName(element) + " could not be opened");
		}
		return new Kept(processor, this.listened(channel, element.getId(), instance), element.getParameters());
	}

	private void close(Kept kept) {
		if (kept != null) {
			kept.processor.close();
		}
	}

	// the sound chain of the channel of the document's song
	private TGProgram chain(int channel) {
		TGDocumentManager document = TGDocumentManager.getInstance(this.context);
		return TGSoundChain.get(document.getSongManager().getChannel(document.getSong(), channel));
	}

	// each plugin kept open whose element no render runs (nothing else calls it) is asked for its first parameter, so
	// what its editor did since comes with the reply; one without parameters is not asked. Any thread may poll
	public synchronized void poll() {
		this.kept.forEach((key, kept) -> {
			if (!this.running.containsKey(key)) {
				ask(kept.instance);
			}
		});
	}

	private static void ask(TGVst3Instance instance) {
		instance.getParameters().stream().findFirst().ifPresent(first -> read(instance, first.getId()));
	}

	private static void read(TGVst3Instance instance, int parameter) {
		try {
			instance.getParameter(parameter);
		} catch (TGVst3Exception e) {
			// its host stopped: there is nothing more to hear from it
		}
	}

	// whether a gesture ended since the last drain (any thread may ask)
	public boolean hasEnded() {
		return !this.ended.isEmpty();
	}

	// the gestures ended since the last drain change the song: one change per element, with its plugin's state now, in
	// the order of each element's last gesture. Run where the app edits the song (its UI thread)
	public void drain() {
		Map<String, Gesture> last = new LinkedHashMap<String, Gesture>();
		for (Gesture gesture = this.ended.poll(); gesture != null; gesture = this.ended.poll()) {
			last.remove(key(gesture.channel, gesture.element));
			last.put(key(gesture.channel, gesture.element), gesture);
		}
		last.values().forEach(gesture -> this.store(gesture.channel, gesture.element, gesture.instance));
	}

	// the song's element keeps the plugin's state, one change of the song
	private void store(int channel, String element, TGVst3Instance instance) {
		TGDocumentManager document = TGDocumentManager.getInstance(this.context);
		TGActionContext change = new TGActionContext() {
		};
		change.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER, document.getSongManager());
		change.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, document.getSong());
		change.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, document.getSongManager().getChannel(document.getSong(), channel));
		change.setAttribute(TGSetPluginStateAction.ATTRIBUTE_ELEMENT, element);
		try {
			change.setAttribute(TGSetPluginStateAction.ATTRIBUTE_STATE, instance.getState());
			TGActionManager.getInstance(this.context).execute(TGSetPluginStateAction.NAME, change);
			this.kept(channel, element, instance);
		} catch (TGVst3Exception | TGActionException e) {
			// the plugin is gone, or the song refused the change: the song stays as it was
		}
	}

	// the plugin kept for the element did the change the song's element now holds: it is not opened again for it
	private synchronized void kept(int channel, String element, TGVst3Instance instance) {
		Kept kept = this.kept.get(key(channel, element));
		if (kept != null && kept.instance == instance) {
			this.kept.put(key(channel, element), new Kept(kept.processor, instance, TGSoundChain.element(this.chain(channel), element).getParameters()));
		}
	}

	// the plugin's own editor, or TuxGuitar's parameter editor for a plugin without one
	public synchronized TGPluginEditor openEditor(int channel, String element) throws TGVst3Exception {
		TGVst3Instance instance = this.get(channel, element);
		TGPluginEditor editor = new TGPluginEditor(instance.openEditor(), rows(instance));
		this.editors.put(key(channel, element), new Editor(editor, instance));
		return editor;
	}

	// the rows TuxGuitar's parameter editor shows, at the values the plugin holds: not the parameter choosing presets
	private static List<TGPluginParameterRow> rows(TGVst3Instance instance) throws TGVst3Exception {
		List<TGPluginParameterRow> rows = new ArrayList<TGPluginParameterRow>();
		for (TGVst3Parameter parameter : instance.getParameters()) {
			if (!parameter.choosesPresets()) {
				rows.add(new TGPluginParameterRow(parameter.getId(), parameter.getTitle(), instance.getParameter(parameter.getId()), parameter.getMinimum(), parameter.getMaximum(), parameter.getSteps()));
			}
		}
		return rows;
	}

	// the editor open for the element, or null
	public synchronized TGPluginEditor getEditor(int channel, String element) {
		Editor editor = this.editors.get(key(channel, element));
		return (editor != null ? editor.editor : null);
	}

	// TuxGuitar's parameter editor open for the channel's effect (by its number; none: the VST3 instrument that is the
	// source), or null when that plugin's editor is its own or none is open
	public synchronized TGPluginEditor parameterEditor(TGChannel channel, Integer effect) {
		TGProgramElement plugin = TGSoundChain.plugin(TGSoundChain.get(channel), effect);
		TGPluginEditor editor = (plugin != null ? this.getEditor(channel.getChannelId(), plugin.getId()) : null);
		return (editor != null && !editor.isOwn() ? editor : null);
	}

	// the editors whose plugin is no longer the element's (it stopped running) close
	private void closeEditors() {
		this.editors.entrySet().removeIf(editor -> editor.getValue().instance != this.current(editor.getKey()) && editor.getValue().close());
	}

	// the plugins kept open close (their editors with them)
	public synchronized void close() {
		new ArrayList<Kept>(this.kept.values()).forEach(this::close);
		this.kept.clear();
		this.running.clear();
		this.editors.clear();
	}

	private static final class Kept {
		private final TGAudioProcessor processor;
		private final TGVst3Instance instance;
		private final Map<String, String> parameters;

		Kept(TGAudioProcessor processor, TGVst3Instance instance, Map<String, String> parameters) {
			this.processor = processor;
			this.instance = instance;
			this.parameters = new HashMap<String, String>(parameters);
		}
	}

	private static final class Editor {
		private final TGPluginEditor editor;
		private final TGVst3Instance instance;

		Editor(TGPluginEditor editor, TGVst3Instance instance) {
			this.editor = editor;
			this.instance = instance;
		}

		// the plugin's own editor closes; a plugin that is gone has none left to close
		boolean close() {
			try {
				this.instance.closeEditor();
			} catch (TGVst3Exception e) {
				// its host stopped, the editor with it
			}
			return true;
		}
	}

	private static final class Gesture {
		private final int channel;
		private final String element;
		private final TGVst3Instance instance;

		Gesture(int channel, String element, TGVst3Instance instance) {
			this.channel = channel;
			this.element = element;
			this.instance = instance;
		}
	}
}
