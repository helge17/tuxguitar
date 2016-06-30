package org.herac.tuxguitar.ui.jfx.chooser;

import org.herac.tuxguitar.ui.chooser.UIFontChooser;
import org.herac.tuxguitar.ui.chooser.UIFontChooserHandler;
import org.herac.tuxguitar.ui.jfx.widget.JFXWindow;
import org.herac.tuxguitar.ui.resource.UIFontModel;

public class JFXFontChooser implements UIFontChooser {

	private JFXWindow window;
	private String text;
	private UIFontModel defaultModel;
	
	public JFXFontChooser(JFXWindow window) {
		this.window = window;
	}
	
	public void choose(UIFontChooserHandler selectionHandler) {
		selectionHandler.onSelectFont(this.defaultModel); 
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public void setDefaultModel(UIFontModel defaultModel) {
		this.defaultModel = defaultModel;
	}
}
