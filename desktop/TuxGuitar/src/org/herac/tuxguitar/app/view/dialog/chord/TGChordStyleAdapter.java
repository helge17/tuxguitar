package org.herac.tuxguitar.app.view.dialog.chord;

import org.herac.tuxguitar.app.system.icons.TGColorManager;
import org.herac.tuxguitar.app.system.icons.TGColorManager.TGSkinnableColor;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.ui.appearance.UIAppearance;
import org.herac.tuxguitar.ui.appearance.UIColorAppearance;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.util.TGContext;

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
