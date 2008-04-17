package org.herac.tuxguitar.io.base;

import java.io.InputStream;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

public interface TGSongImporter {
	
	public String getImportName();
	
	public TGFileFormat getFileFormat();
	
	public boolean configure(boolean setDefaults);
	
	public TGSong importSong(TGFactory factory,InputStream stream) throws TGFileFormatException;
	
}
