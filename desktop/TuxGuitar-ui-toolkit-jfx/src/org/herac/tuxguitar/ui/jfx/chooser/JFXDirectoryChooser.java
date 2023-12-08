package org.herac.tuxguitar.ui.jfx.chooser;

import java.io.File;

import javafx.stage.DirectoryChooser;

import org.herac.tuxguitar.ui.chooser.UIDirectoryChooser;
import org.herac.tuxguitar.ui.chooser.UIDirectoryChooserHandler;
import org.herac.tuxguitar.ui.jfx.widget.JFXWindow;

public class JFXDirectoryChooser implements UIDirectoryChooser {

	private JFXWindow window;
	private String text;
	private File defaultPath;
	
	public JFXDirectoryChooser(JFXWindow window) {
		this.window = window;
	}
	
	public void choose(UIDirectoryChooserHandler selectionHandler) {
		DirectoryChooser dialog = new DirectoryChooser();
		if( this.text != null ) {
			dialog.setTitle(this.text);
		}
		
		File initialDirectory = this.createInitialDirectory();
		if( initialDirectory != null ) {
			dialog.setInitialDirectory(initialDirectory);
		}
		
		File path = dialog.showDialog(this.window.getStage());
		
		selectionHandler.onSelectDirectory(path); 
	}
	
	public File createInitialDirectory() {
		if( this.defaultPath != null ) {
			File directory = (this.defaultPath.isDirectory() ? this.defaultPath : this.defaultPath.getParentFile());
			if( directory != null && directory.exists() ) {
				return directory;
			}
		}
		return null;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public void setDefaultPath(File defaultPath) {
		this.defaultPath = defaultPath;
	}
}
