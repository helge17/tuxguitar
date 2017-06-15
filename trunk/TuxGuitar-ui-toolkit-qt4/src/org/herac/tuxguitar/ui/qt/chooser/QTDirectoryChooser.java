package org.herac.tuxguitar.ui.qt.chooser;

import java.io.File;
import java.util.List;

import org.herac.tuxguitar.ui.chooser.UIDirectoryChooser;
import org.herac.tuxguitar.ui.chooser.UIDirectoryChooserHandler;
import org.herac.tuxguitar.ui.qt.widget.QTAbstractWindow;

import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QFileDialog.FileMode;

public class QTDirectoryChooser implements UIDirectoryChooser {

	private QTAbstractWindow<?> window;
	private String text;
	private File defaultPath;
	
	public QTDirectoryChooser(QTAbstractWindow<?> window) {
		this.window = window;
	}
	
	public void choose(UIDirectoryChooserHandler selectionHandler) {
		File selection = null;
		
		QFileDialog dialog = new QFileDialog(this.window.getControl());
		
		if( this.text != null ) {
			dialog.setWindowTitle(this.text);
		}
		
		if( this.defaultPath != null ) {
			dialog.selectFile(this.defaultPath.getAbsolutePath());
		}
		
		dialog.setFileMode(FileMode.DirectoryOnly);
		
		if( dialog.exec() == QDialog.DialogCode.Accepted.value() ) {
			List<String> selectedFiles = dialog.selectedFiles();
			if( selectedFiles != null && !selectedFiles.isEmpty() ) {
				selection = new File(selectedFiles.get(0));
			}
		}
		
		selectionHandler.onSelectDirectory(selection); 
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public void setDefaultPath(File defaultPath) {
		this.defaultPath = defaultPath;
	}
}
