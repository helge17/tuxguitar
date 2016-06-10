package org.herac.tuxguitar.ui.chooser;

import java.io.File;
import java.util.List;

public interface UIFileChooser {
	
	void setText(String text);
	
	void setDefaultPath(File defaultPath);
	
	void setSupportedFormats(List<UIFileChooserFormat> supportedFormats);
	
	void choose(UIFileChooserHandler selectionHandler);
}
