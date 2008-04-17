package org.herac.tuxguitar.gui.tools.custom.converter;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGSongExporter;

public class DestinationFileTypeCombo {
	
	/** the combo box of available output formats */
	protected Combo comboBox = null;

	/** index of the last OutputStream plugin, the rest are exporters and treated differently */
	protected int lastOutputStreamIndex = -1;
	
	
	/** instantiates a combo */
	public DestinationFileTypeCombo(Composite parent) {

		this.comboBox = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.init();
		
	}
	
	
	/** initializes combo box */
	private void init() {
		
		// fill in the combo with input streams and exporters
		Iterator outputStreams = TGFileFormatManager.instance().getOutputFormats().iterator();
		while (outputStreams.hasNext()) {
			this.comboBox.add(((TGFileFormat)outputStreams.next()).getSupportedFormats());
			this.lastOutputStreamIndex++;
		}
		
		Iterator exporters = TGFileFormatManager.instance().getExporters();
		while (exporters.hasNext()) {
			TGSongExporter curExporter = (TGSongExporter)exporters.next();
			// curExporter.configure(true); -- still not supported by most plugins, and old tuxguitar
			this.comboBox.add(curExporter.getFileFormat().getSupportedFormats());
		}
		
		if (this.lastOutputStreamIndex!=-1) // first one is default
			this.comboBox.select(0);
	
	
	}

	
	/** Gets the destination file extension */
	String getExtension() {
		String extension = this.comboBox.getItem(this.comboBox.getSelectionIndex()).trim();
		// check if the extension is multiple
		int index = extension.indexOf(";");
		if (index == -1) 
			index = extension.length();
		
		// remove * and multiple extensions (first is good enough)
		extension = extension.substring(extension.indexOf("."),	index );
		return extension;
	}
	
	public void setLayoutData(Object layoutData){
		this.comboBox.setLayoutData( layoutData );
	}
	
}
