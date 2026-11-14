package app.tuxguitar.audio;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An open editor of a VST3 plugin of a sound chain: the plugin's own editor, in a window of its host process, or, for a
 * plugin without one, TuxGuitar's parameter editor, which shows a row per parameter of the plugin (its preset list is no
 * parameter there).
 */
public final class TGPluginEditor {

	private final boolean own;
	private final List<TGPluginParameterRow> rows;

	public TGPluginEditor(boolean own, List<TGPluginParameterRow> rows) {
		this.own = own;
		this.rows = rows;
	}

	public boolean isOwn() {
		return this.own;
	}

	public List<TGPluginParameterRow> getRows() {
		return this.rows;
	}

	// the titles of the rows
	public List<String> getParameters() {
		return this.rows.stream().map(TGPluginParameterRow::getTitle).collect(Collectors.toList());
	}
}
