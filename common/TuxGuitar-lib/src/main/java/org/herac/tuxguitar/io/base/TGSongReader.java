package org.herac.tuxguitar.io.base;

public interface TGSongReader extends TGSongPersistenceHandler {
	
	void read(TGSongReaderHandle handle) throws TGFileFormatException;
}
