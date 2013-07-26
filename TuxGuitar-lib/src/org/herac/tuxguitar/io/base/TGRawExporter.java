package org.herac.tuxguitar.io.base;

import org.herac.tuxguitar.song.models.TGSong;

public interface TGRawExporter {
	
	public String getExportName();
	
	public void exportSong(TGSong song) throws TGFileFormatException;
}
