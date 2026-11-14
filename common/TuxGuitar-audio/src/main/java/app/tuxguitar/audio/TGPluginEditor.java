package app.tuxguitar.audio;

import java.util.List;
import java.util.stream.Collectors;

import app.tuxguitar.vst3.TGVst3Exception;

/**
 * An open editor of a VST3 plugin of a sound chain: the plugin's own editor, in a window of its host process, or, for a
 * plugin without one, TuxGuitar's parameter editor, which shows a row per parameter of the plugin (its preset list is no
 * parameter there), at the values the plugin held when they were read. It is open while its plugin runs. Its watcher
 * (one at a time; an app's window) is told each time the plugin may hold other values (its chain ran again: a change,
 * an undo, a redo; the rows are read again on refresh) and once the editor closes; on any thread.
 */
public final class TGPluginEditor {

	// the rows as the plugin holds its values now
	interface Rows {
		List<TGPluginParameterRow> read() throws TGVst3Exception;
	}

	private final boolean own;
	private final Rows plugin;
	private volatile List<TGPluginParameterRow> rows;
	private boolean open = true;
	private Runnable watcher = () -> { };

	TGPluginEditor(boolean own, Rows plugin) throws TGVst3Exception {
		this.own = own;
		this.plugin = plugin;
		this.rows = plugin.read();
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

	// the rows read again from the plugin (a plugin gone keeps the rows it had)
	public void refresh() {
		try {
			this.rows = this.plugin.read();
		} catch (TGVst3Exception e) {
			// its host stopped: nothing more to read
		}
	}

	public synchronized boolean isOpen() {
		return this.open;
	}

	// the watcher from now on; told at once when the editor is closed already
	public synchronized void watch(Runnable watcher) {
		this.watcher = watcher;
		if (!this.open) {
			watcher.run();
		}
	}

	// its plugin may hold other values
	synchronized void changed() {
		this.watcher.run();
	}

	// its plugin no longer runs
	synchronized void close() {
		this.open = false;
		this.watcher.run();
	}
}
