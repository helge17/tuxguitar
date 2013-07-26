package org.herac.tuxguitar.io.base;

import org.herac.tuxguitar.song.models.TGSong;

public interface TGRawImporter {
	
	public String getImportName();
	
	public TGSong importSong() throws TGFileFormatException;
	
}
