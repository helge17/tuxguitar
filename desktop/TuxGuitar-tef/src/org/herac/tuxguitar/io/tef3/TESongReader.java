package org.herac.tuxguitar.io.tef3;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.io.base.TGSongReaderHandle;
import org.herac.tuxguitar.io.tef3.base.TESong;
import org.herac.tuxguitar.song.models.TGSong;

public class TESongReader implements TGSongReader {
	
	public TESongReader(){
		super();
	}

	// do not associate a file extension (.tef) here:
	// since tef v2 files can only be identified by extension and not by content, make sure ".tef" is only associated to tef v2
	// tef v3 should be identified by file content (thanks to TEFileFormatDetector)
	public static final TGFileFormat FILE_FORMAT = new TGFileFormat("TablEdit v3", "application/x-tef", new String[] {});
	
	public TGFileFormat getFileFormat() {
		return FILE_FORMAT;
	}
	
	public void read(TGSongReaderHandle handle) throws TGFileFormatException {
		try {
			TESong teSong = new TEInputStream(handle.getInputStream()).readSong();
			TGSong tgSong = new TESongParser(handle.getFactory()).parseSong(teSong);
			
			handle.setSong(tgSong);
		} catch (Exception e) {
			StackTraceElement[] stackTrace = e.getStackTrace();
			
			StringBuilder sb = new StringBuilder();
			
			sb.append(e.getMessage());
			sb.append("\n");
			
			for (StackTraceElement element : stackTrace) {
				String toAppend = element.toString() + "\n";
				sb.append(toAppend);
			}
			throw new TGFileFormatException(sb.toString(), e);
		}
	}
}
