package org.herac.tuxguitar.io.base;

import java.io.OutputStream;

import org.herac.tuxguitar.song.models.TGSong;

public interface TGSongExporter {
	
	public String getExportName();
	
	public TGFileFormat getFileFormat();
	
	public boolean configure(boolean setDefaults);
	
	public void exportSong(OutputStream stream,TGSong song) throws TGFileFormatException;
}
