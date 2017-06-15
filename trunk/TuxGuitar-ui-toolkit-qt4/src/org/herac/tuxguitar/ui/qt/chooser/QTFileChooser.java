package org.herac.tuxguitar.ui.qt.chooser;

import java.io.File;
import java.util.List;

import org.herac.tuxguitar.ui.chooser.UIFileChooser;
import org.herac.tuxguitar.ui.chooser.UIFileChooserFormat;
import org.herac.tuxguitar.ui.chooser.UIFileChooserHandler;
import org.herac.tuxguitar.ui.qt.widget.QTAbstractWindow;

import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QFileDialog.FileMode;

public class QTFileChooser implements UIFileChooser {
	
	public static final int STYLE_OPEN = 1;
	public static final int STYLE_SAVE = 2;
	
	private int style;
	private QTAbstractWindow<?> window;
	private String text;
	private File defaultPath;
	private List<UIFileChooserFormat> supportedFormats;
	
	public QTFileChooser(QTAbstractWindow<?> window, int style) {
		this.window = window;
		this.style = style;
	}
	
	public void choose(UIFileChooserHandler selectionHandler) {
		File selection = null;
		
		QFileDialog dialog = new QFileDialog(this.window.getControl());
		
		if( this.text != null ) {
			dialog.setWindowTitle(this.text);
		}
		
		String initialFileName = this.createInitialFileName();
		if( initialFileName != null ) {
			dialog.selectFile(initialFileName);
		}
		
		String extensionFilters = createExtensionFilters();
		if( extensionFilters != null && extensionFilters.length() > 0 ) {
			dialog.setFilter(extensionFilters);
		}
		
		dialog.setFileMode(STYLE_SAVE == this.style ? FileMode.AnyFile : FileMode.ExistingFile);
		
		if( dialog.exec() == QDialog.DialogCode.Accepted.value() ) {
			List<String> selectedFiles = dialog.selectedFiles();
			if( selectedFiles != null && !selectedFiles.isEmpty() ) {
				selection = new File(selectedFiles.get(0));
			}
		}
		
		selectionHandler.onSelectFile(selection); 
	}
	
	public String createInitialFileName() {
		if( this.defaultPath != null ) {
			return this.defaultPath.getAbsolutePath();
		}
		return null;
	}
	
	public String createExtensionFilters() {
		StringBuffer sb = new StringBuffer();
		if( this.supportedFormats != null ) {
			for(int f = 0; f < this.supportedFormats.size(); f ++) {
				UIFileChooserFormat supportedFormat = this.supportedFormats.get(f);
				if( f > 0 ) {
					sb.append(";;");
				}
				sb.append(supportedFormat.getName());
				sb.append(" (");
				
				for(int e = 0; e < supportedFormat.getExtensions().size(); e ++) {
					if( e > 0 ) {
						sb.append(" ");
					}
					sb.append("*." + supportedFormat.getExtensions().get(e));
				}
				
				sb.append(")");
			}
		}
		return sb.toString();
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
