package org.herac.tuxguitar.io.abc;

import java.io.OutputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

public class ABCSongExporter implements TGLocalFileExporter{
	
	private OutputStream stream;
	private ABCSettings settings;
	
	public String getExportName() {
		return "Abc";
	}
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat("Abc","*.abc");
	}
	
	public boolean configure(boolean setDefaults) {
		this.settings = (setDefaults ? ABCSettings.getDefaults() : new ABCExportSettingsDialog().open());
		return (this.settings != null);
	}
	
	public void init(TGFactory factory,OutputStream stream){
		this.stream = stream;
	}
	
	public void exportSong(TGSong song) {
		if(this.stream != null && this.settings != null){
			new ABCOutputStream(this.stream,this.settings).writeSong(song);
		}
	}
}