package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.audio.TGLivePlugins;
import app.tuxguitar.audio.TGSoundChain;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.midi.synth.TGProgramElement;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.vst3.TGVst3Exception;

/**
 * Opens the editor of a VST3 plugin of the context's channel's sound chain: an effect by its number
 * (TGEffectAction.ATTRIBUTE_EFFECT), or, without one, the VST3 instrument that is the source. It changes nothing in the
 * song. Refused when that is no VST3 plugin, or the plugin cannot be opened.
 */
public class TGOpenPluginEditorAction extends TGActionBase {

	public static final String NAME = "action.audio.sound-chain.open-plugin-editor";

	public TGOpenPluginEditorAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		TGChannel channel = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL);
		TGProgramElement plugin = TGSoundChain.plugin(TGSoundChain.get(channel), context.getAttribute(TGEffectAction.ATTRIBUTE_EFFECT));
		try {
			if (plugin != null) {
				TGLivePlugins.getInstance(getContext()).openEditor(channel.getChannelId(), plugin.getId());
				context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
			}
		} catch (TGVst3Exception e) {
			// the plugin could not be opened: no editor
		}
	}
}
