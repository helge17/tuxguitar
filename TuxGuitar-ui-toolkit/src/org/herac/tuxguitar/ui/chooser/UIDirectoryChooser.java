package org.herac.tuxguitar.ui.chooser;

import java.io.File;

public interface UIDirectoryChooser {
	
	void setText(String text);
	
	void setDefaultPath(File defaultPath);
	
	void choose(UIDirectoryChooserHandler selectionHandler);
}
