package app.tuxguitar.ui.chooser;

import app.tuxguitar.ui.resource.UIColorModel;

public interface UIColorChooser {

	void setText(String text);

	void setDefaultModel(UIColorModel defaultModel);

	void choose(UIColorChooserHandler selectionHandler);
}
