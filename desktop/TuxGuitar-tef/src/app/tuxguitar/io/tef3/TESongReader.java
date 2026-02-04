package app.tuxguitar.io.tef3;

import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongReaderHandle;
import app.tuxguitar.io.tef3.base.TESong;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TESongReader implements TGSongReader {

	private TGContext context;

	public TESongReader(TGContext context){
		this.context = context;
	}

	public static final TGFileFormat FILE_FORMAT = new TGFileFormat("TablEdit v3", "application/x-tef", new String[] {"tef"});

	public TGFileFormat getFileFormat() {
		return FILE_FORMAT;
	}

	public void read(TGSongReaderHandle handle) throws TGFileFormatException {
		try {
			TESong teSong = new TEInputStream(handle.getInputStream()).readSong();
			TGSong tgSong = new TESongParser(this.context, handle.getFactory()).parseSong(teSong);

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
