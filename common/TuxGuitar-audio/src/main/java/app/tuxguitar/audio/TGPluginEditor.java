package app.tuxguitar.audio;

import java.util.List;

/**
 * An open editor of a VST3 plugin of a sound chain: the plugin's own editor, in a window of its host process, or, for a
 * plugin without one, TuxGuitar's parameter editor, which shows the plugin's parameters (its preset list is no
 * parameter there) by title.
 */
public final class TGPluginEditor {

	private final boolean own;
	private final List<String> parameters;

	public TGPluginEditor(boolean own, List<String> parameters) {
		this.own = own;
		this.parameters = parameters;
	}

	public boolean isOwn() {
		return this.own;
	}

	public List<String> getParameters() {
		return this.parameters;
	}
}
