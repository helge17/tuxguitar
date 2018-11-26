package org.herac.tuxguitar.app.view.dialog.chord;

import org.herac.tuxguitar.app.system.icons.TGColorManager;
import org.herac.tuxguitar.app.system.icons.TGColorManager.TGSkinnableColor;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.util.TGContext;

public class TGChordStyleAdapter {
	
	public static final String COLOR_FOREGROUND = "widget.chordEditor.foregroundColor";
	public static final String COLOR_BACKGROUND = "widget.chordEditor.backgroundColor";
	public static final String COLOR_SELECTION = "widget.chordEditor.selectionColor";
	public static final String COLOR_TONIC = "widget.chordEditor.tonicColor";
	
	public static final TGSkinnableColor[] SKINNABLE_COLORS = new TGSkinnableColor[] {
		new TGSkinnableColor(COLOR_FOREGROUND, new UIColorModel(0x00, 0x00, 0x00)),
		new TGSkinnableColor(COLOR_BACKGROUND, new UIColorModel(0xff, 0xff, 0xff)),
		new TGSkinnableColor(COLOR_SELECTION, new UIColorModel(0x00, 0x00, 0xff)),
		new TGSkinnableColor(COLOR_TONIC, new UIColorModel(0x80, 0x00, 0x00))
	};
	
	private TGChordStyleAdapter() {
		super();
	}
	
	public static final void appendColors(TGContext context) {
		TGColorManager.getInstance(context).appendSkinnableColors(SKINNABLE_COLORS);
	}
}
