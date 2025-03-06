package org.herac.tuxguitar.io.tef2;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.io.base.TGSongReaderHandle;
import org.herac.tuxguitar.io.tef2.base.TESong;
import org.herac.tuxguitar.song.models.TGSong;

public class TESongReader implements TGSongReader {

	public TESongReader(){
		super();
	}

	public static final TGFileFormat FILE_FORMAT = new TGFileFormat("TablEdit v2", "application/x-tef", new String[]{"tef"});

	public TGFileFormat getFileFormat() {
		return FILE_FORMAT;
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
