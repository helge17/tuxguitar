package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.audio.TGSoundChain;
import app.tuxguitar.midi.synth.TGProgram;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.vst3.TGVst3Element;
import app.tuxguitar.vst3.TGVst3State;

/**
 * Keeps the state a VST3 plugin of a channel's sound chain holds, on its element (by the element's id): what a gesture in
 * the plugin's own editor, or a preset chosen there, did (see TGLivePlugins). Refused when the element is no VST3 plugin.
 */
public class TGSetPluginStateAction extends TGSoundChainAction {

	public static final String NAME = "action.audio.sound-chain.set-plugin-state";

	// the element's id (String) and the plugin's state (TGVst3State)
	public static final String ATTRIBUTE_ELEMENT = "soundChainElement";
	public static final String ATTRIBUTE_STATE = "pluginState";

	public TGSetPluginStateAction(TGContext context) {
		super(context, NAME);
	}

	protected boolean change(TGActionContext context, TGChannel channel, TGProgram chain) {
		TGProgramElement plugin = TGSoundChain.element(chain, context.getAttribute(ATTRIBUTE_ELEMENT));
		if (plugin == null || !TGVst3Element.isVst3(plugin)) {
			return false;
		}
		TGVst3Element.setState(plugin, context.<TGVst3State>getAttribute(ATTRIBUTE_STATE));
		return true;
	}
}
