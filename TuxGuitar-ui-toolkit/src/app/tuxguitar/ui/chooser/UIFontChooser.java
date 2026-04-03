package app.tuxguitar.ui.chooser;

import app.tuxguitar.ui.resource.UIFontModel;

public interface UIFontChooser {

	void setText(String text);

	void setDefaultModel(UIFontModel defaultModel);

	void choose(UIFontChooserHandler selectionHandler);
}
