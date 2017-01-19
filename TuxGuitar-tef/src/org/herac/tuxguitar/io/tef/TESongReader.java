package org.herac.tuxguitar.io.tef;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.io.base.TGSongReaderHandle;
import org.herac.tuxguitar.io.tef.base.TESong;
import org.herac.tuxguitar.song.models.TGSong;

public class TESongReader implements TGSongReader {
	
	public TESongReader(){
		super();
	}
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat("Tef", "audio/x-tef", new String[]{"tef"});
	}
	
	public void read(TGSongReaderHandle handle) throws TGFileFormatException {
		try {
			TESong teSong = new TEInputStream(handle.getInputStream()).readSong();
			TGSong tgSong = new TESongParser(handle.getFactory()).parseSong(teSong);
			
			handle.setSong(tgSong);
		} catch (Exception e) {
			throw new TGFileFormatException();
		}
	}
}