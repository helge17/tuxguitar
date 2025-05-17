package app.tuxguitar.io.lilypond;

import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.base.TGSongWriterHandle;

public class LilypondSongWriter implements TGSongWriter {

	public static final TGFileFormat FILE_FORMAT = new TGFileFormat("LilyPond", "text/x-lilypond", new String[]{"ly"});

	public LilypondSongWriter() {
		super();
	}

	public TGFileFormat getFileFormat() {
		return FILE_FORMAT;
	}

	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		try{
			LilypondSettings settings = handle.getContext().getAttribute(LilypondSettings.class.getName());
			if( settings == null ) {
				settings = LilypondSettings.getDefaults();
			}

			new LilypondOutputStream(handle.getOutputStream(), settings).writeSong(handle.getSong());
		}catch(Throwable throwable){
			throw new TGFileFormatException(throwable);
		}
	}
}
