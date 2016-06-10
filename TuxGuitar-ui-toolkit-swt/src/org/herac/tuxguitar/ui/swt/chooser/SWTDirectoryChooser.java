package org.herac.tuxguitar.ui.swt.chooser;

import java.io.File;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.herac.tuxguitar.ui.chooser.UIDirectoryChooser;
import org.herac.tuxguitar.ui.chooser.UIDirectoryChooserHandler;
import org.herac.tuxguitar.ui.swt.widget.SWTWindow;

public class SWTDirectoryChooser implements UIDirectoryChooser {

	private SWTWindow window;
	private String text;
	private File defaultPath;
	
	public SWTDirectoryChooser(SWTWindow window) {
		this.window = window;
	}
	
	public void choose(UIDirectoryChooserHandler selectionHandler) {
		DirectoryDialog dialog = new DirectoryDialog(this.window.getControl());
		if( this.text != null ) {
			dialog.setText(this.text);
		}
		
		if( this.defaultPath != null ) {
			dialog.setFilterPath(this.defaultPath.getAbsolutePath());
		}
		
		String path = dialog.open();
		
		selectionHandler.onSelectDirectory(path != null ? new File(path) : null); 
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public void setDefaultPath(File defaultPath) {
		this.defaultPath = defaultPath;
	}
}
