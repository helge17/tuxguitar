package org.herac.tuxguitar.ui.chooser;

import org.herac.tuxguitar.ui.resource.UIFontModel;

public interface UIFontChooser {

	void setText(String text);

	void setDefaultModel(UIFontModel defaultModel);
	
	void choose(UIFontChooserHandler selectionHandler);
}
