package app.tuxguitar.app.view.toolbar.main;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Configuration of main tool bar
 */

public class TGMainToolBarConfig {

	private static final String[] DEFAULT_LEFT_CONTENT = new String[] { "file.new", "file.open", "file.save",
			"file.save-as", "file.print-preview", "file.print", "toolbar.separator", "edit.undo", "edit.redo", "toolbar.separator",
			"view.show-edit-toolbar", "view.show-table-viewer", "view.show-instruments", "view.show-fretboard",
			"view.zoom.out", "view.zoom.reset", "view.zoom.in", "view.layout", "toolbar.separator", "composition.properties",
			"toolbar.separator", "track.add", "track.remove", "toolbar.separator", "marker" };
	private static final String[] DEFAULT_CENTER_CONTENT = new String[] { "transport.first", "transport.previous",
			"transport.start", "transport.stop", "transport.next", "transport.last", "toolbar.separator", "toolbar.timeCounter",
			"toolbar.tempoIndicator", "toolbar.separator", "transport.metronome", "transport.count-down", "transport.mode" };
	// WARNING, last item of right area shall ALWAYS be the tool bar settings button
	private static final String[] DEFAULT_RIGHT_CONTENT = new String[] {"toolbar.settings"};

	private List<String> leftAreaContent;
	private List<String> centerAreaContent;
	private List<String> rightAreaContent;
	private String name;

	public TGMainToolBarConfig(String name) {
		this.name = name;
		this.setDefaults();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLeftAreaContent(List<String> content) {
		this.leftAreaContent = content;
	}

	public void setCenterAreaContent(List<String> content) {
		this.centerAreaContent = content;
	}

	public void setRightAreaContent(List<String> content) {
		this.rightAreaContent = content;
	}

	public void setDefaults() {
		leftAreaContent = new ArrayList<String>(Arrays.asList(DEFAULT_LEFT_CONTENT));
		centerAreaContent = new ArrayList<String>(Arrays.asList(DEFAULT_CENTER_CONTENT));
		rightAreaContent = new ArrayList<String>(Arrays.asList(DEFAULT_RIGHT_CONTENT));
	}

	public List<String> getAreaContent(int area) {
		if (area == TGMainToolBar.LEFT_AREA) {
			return (this.leftAreaContent);
		}
		if (area == TGMainToolBar.CENTER_AREA) {
			return (this.centerAreaContent);
		}
		if (area == TGMainToolBar.RIGHT_AREA) {
			return (this.rightAreaContent);
		}
		throw new IllegalArgumentException("unexpected area");
	}

	public TGMainToolBarConfig clone() {
		TGMainToolBarConfig clone = new TGMainToolBarConfig(this.name);
		clone.setLeftAreaContent(new ArrayList<String>(this.leftAreaContent));
		clone.setCenterAreaContent(new ArrayList<String>(this.centerAreaContent));
		clone.setRightAreaContent(new ArrayList<String>(this.rightAreaContent));
		return clone;
	}

}
