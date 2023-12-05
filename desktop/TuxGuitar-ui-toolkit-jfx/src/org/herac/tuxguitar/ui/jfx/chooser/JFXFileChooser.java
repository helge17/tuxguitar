package org.herac.tuxguitar.ui.jfx.chooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import org.herac.tuxguitar.ui.chooser.UIFileChooser;
import org.herac.tuxguitar.ui.chooser.UIFileChooserFormat;
import org.herac.tuxguitar.ui.chooser.UIFileChooserHandler;
import org.herac.tuxguitar.ui.jfx.widget.JFXWindow;

public class JFXFileChooser implements UIFileChooser {
	
	public static final int STYLE_OPEN = 1;
	public static final int STYLE_SAVE = 2;
	
	private int style;
	private JFXWindow window;
	private String text;
	private File defaultPath;
	private List<UIFileChooserFormat> supportedFormats;
	
	public JFXFileChooser(JFXWindow window, int style) {
		this.window = window;
		this.style = style;
	}
	
	public void choose(UIFileChooserHandler selectionHandler) {
		FileChooser dialog = new FileChooser();
		if( this.text != null ) {
			dialog.setTitle(this.text);
		}
		
		String initialFileName = this.createInitialFileName();
		if( initialFileName != null ) {
			dialog.setInitialFileName(initialFileName);
		}
		
		File initialDirectory = createInitialDirectory();
		if( initialDirectory != null ) {
			dialog.setInitialDirectory(initialDirectory);
		}
		
		List<ExtensionFilter> extensionFilters = createExtensionFilters();
		if( extensionFilters != null && !extensionFilters.isEmpty()) {
			dialog.getExtensionFilters().addAll(extensionFilters);
		}
		
		File path = (STYLE_OPEN == this.style ? dialog.showOpenDialog(this.window.getStage()) : dialog.showSaveDialog(this.window.getStage()));
		
		selectionHandler.onSelectFile(path); 
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
	
	public String createInitialFileName() {
		if( this.defaultPath != null && !this.defaultPath.isDirectory() ) {
			return this.defaultPath.getName();
		}
		return null;
	}
	
	public List<ExtensionFilter> createExtensionFilters() {
		List<ExtensionFilter> extensionFilters = new ArrayList<FileChooser.ExtensionFilter>();
		if( this.supportedFormats != null ) {
			for(UIFileChooserFormat supportedFormat : this.supportedFormats) {
				List<String> extensions = new ArrayList<String>();
				for(String extension : supportedFormat.getExtensions()) {
					extensions.add("*." + extension);
				}
				extensionFilters.add(new ExtensionFilter(supportedFormat.getName(), extensions));
			}
		}
		return extensionFilters;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public void setDefaultPath(File defaultPath) {
		this.defaultPath = defaultPath;
	}
	
	public void setSupportedFormats(List<UIFileChooserFormat> supportedFormats) {
		this.supportedFormats = supportedFormats;
	}
}
