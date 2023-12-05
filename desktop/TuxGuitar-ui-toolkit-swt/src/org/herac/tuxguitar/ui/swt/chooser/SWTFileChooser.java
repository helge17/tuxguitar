package org.herac.tuxguitar.ui.swt.chooser;

import java.io.File;
import java.util.List;

import org.eclipse.swt.widgets.FileDialog;
import org.herac.tuxguitar.ui.chooser.UIFileChooser;
import org.herac.tuxguitar.ui.chooser.UIFileChooserFormat;
import org.herac.tuxguitar.ui.chooser.UIFileChooserHandler;
import org.herac.tuxguitar.ui.swt.widget.SWTWindow;

public class SWTFileChooser implements UIFileChooser {

	private int style;
	private SWTWindow window;
	private String text;
	private File defaultPath;
	private List<UIFileChooserFormat> supportedFormats;
	
	public SWTFileChooser(SWTWindow window, int style) {
		this.window = window;
		this.style = style;
	}
	
	public void choose(UIFileChooserHandler selectionHandler) {
		FileDialog dialog = new FileDialog(this.window.getControl(), this.style);
		if( this.text != null ) {
			dialog.setText(this.text);
		}
		
		dialog.setFileName(this.createFileName());
		dialog.setFilterPath(this.createFilterPath());
		
		if( this.supportedFormats != null ) {
			FilterList filter = new FilterList(this.supportedFormats);
			
			dialog.setFilterNames(filter.getFilterNames());
			dialog.setFilterExtensions(filter.getFilterExtensions());
		}
		String path = dialog.open();
		
		selectionHandler.onSelectFile(path != null ? new File(path) : null); 
	}
	
	public String createFilterPath() {
		if( this.defaultPath != null ) {
			File directory = (this.defaultPath.isDirectory() ? this.defaultPath : this.defaultPath.getParentFile());
			if( directory != null ) {
				return (directory.getAbsolutePath());
			}
		}
		return null;
	}
	
	public String createFileName() {
		if( this.defaultPath != null && !this.defaultPath.isDirectory() ) {
			return this.defaultPath.getName();
		}
		return null;
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
	
	private static class FilterList {
		
		private String[] filterExtensions;
		private String[] filterNames;
		
		public  FilterList(List<UIFileChooserFormat> formats) {
			int size = formats.size();
			this.filterNames = new String[size];
			this.filterExtensions = new String[size];
			for(int i = 0; i < size; i ++){
				UIFileChooserFormat format = formats.get(i);
				this.filterNames[i] = format.getName();
				this.filterExtensions[i] = createFilterExtensions(format);
			}
		}
		
		private String createFilterExtensions(UIFileChooserFormat format) {
			String separator = "";
			StringBuffer sb = new StringBuffer();
			if( format.getExtensions() != null ) {
				for(String extension : format.getExtensions()) {
					sb.append(separator);
					sb.append("*.");
					sb.append(extension);
					separator = ";";
				}
			}
			return sb.toString();
		}
		
		public String[] getFilterExtensions() {
			return this.filterExtensions;
		}
		
		public String[] getFilterNames() {
			return this.filterNames;
		}
	}
}
