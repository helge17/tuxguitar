package org.herac.tuxguitar.io.ascii;

import java.io.OutputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGSongExporter;
import org.herac.tuxguitar.song.models.TGSong;

public class ASCIISongExporter implements TGSongExporter{

	public String getExportName() {
		return "ASCII";
	}

	public TGFileFormat getFileFormat() {
		return new TGFileFormat("ASCII","*.tab");
	}	
	
	public boolean configure(boolean setDefaults) {
		return true;
	}	
	
	public void exportSong(OutputStream stream, TGSong song) {
		new ASCIITabOutputStream(stream).writeSong(song);
	}

}