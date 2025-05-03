package app.tuxguitar.app.view.toolbar.main;

/**
 * Default configuration of main tool bar
 */

public class TGMainToolBarConfig {

	public static String[] getAreaContent(int area) {
		if (area == TGMainToolBar.LEFT_AREA) {
			return (new String[] { "file.new", "file.open", "file.save", "file.save-as", "file.print-preview",
					"file.print",
					"separator", "edit.undo", "edit.redo",
					"separator", "view.show-edit-toolbar",
					"view.show-table-viewer", "view.show-instruments", "view.show-fretboard", "view.zoom.out",
					"view.zoom.reset", "view.zoom.in", "view.layout",
					"separator", "composition.properties",
					"separator", "track.add", "track.remove",
					"separator", "marker" });
		}
		if (area == TGMainToolBar.CENTER_AREA) {
			return (new String[] { "transport.first", "transport.previous", "transport.start", "transport.stop",
					"transport.next", "transport.last",
					"separator", "timeCounter", "tempoIndicator",
					"separator", "transport.metronome", "transport.count-down", "transport.mode" });
		}
		if (area == TGMainToolBar.RIGHT_AREA) {
			return (new String[] {});
		}
		throw new IllegalArgumentException("unexpected area");
	}
}
