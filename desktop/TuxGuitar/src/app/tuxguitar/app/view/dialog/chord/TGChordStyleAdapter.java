package app.tuxguitar.app.view.dialog.chord;

import app.tuxguitar.app.system.icons.TGColorManager;
import app.tuxguitar.app.system.icons.TGColorManager.TGSkinnableColor;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.ui.appearance.UIAppearance;
import app.tuxguitar.ui.appearance.UIColorAppearance;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.util.TGContext;

public class TGChordStyleAdapter {

	public static final String COLOR_FOREGROUND = "widget.chordEditor.foregroundColor";
	public static final String COLOR_BACKGROUND = "widget.chordEditor.backgroundColor";
	public static final String COLOR_SELECTION = "widget.chordEditor.selectionColor";
	public static final String COLOR_TONIC = "widget.chordEditor.tonicColor";

	private TGChordStyleAdapter() {
		super();
	}

	public static final void appendColors(TGContext context) {
		UIAppearance appearance = TGApplication.getInstance(context).getAppearance();
		TGColorManager.getInstance(context).appendSkinnableColors(new TGSkinnableColor[] {
			new TGSkinnableColor(COLOR_FOREGROUND, appearance.getColorModel(UIColorAppearance.InputForeground)),
			new TGSkinnableColor(COLOR_BACKGROUND, appearance.getColorModel(UIColorAppearance.InputBackground)),
			new TGSkinnableColor(COLOR_SELECTION, appearance.getColorModel(UIColorAppearance.InputSelectedBackground)),
			new TGSkinnableColor(COLOR_TONIC, new UIColorModel(0x80, 0x00, 0x00))
		});
	}
}
