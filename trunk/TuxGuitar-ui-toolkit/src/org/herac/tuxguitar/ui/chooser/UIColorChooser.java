package org.herac.tuxguitar.ui.chooser;

import org.herac.tuxguitar.ui.resource.UIColorModel;

public interface UIColorChooser {

	void setText(String text);

	void setDefaultModel(UIColorModel defaultModel);
	
	void choose(UIColorChooserHandler selectionHandler);
}
