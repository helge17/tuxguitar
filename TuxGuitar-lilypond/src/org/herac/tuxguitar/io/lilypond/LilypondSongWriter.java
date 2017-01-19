package org.herac.tuxguitar.io.lilypond;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.io.base.TGSongWriterHandle;

public class LilypondSongWriter implements TGSongWriter {
	
	public static final TGFileFormat FILE_FORMAT = new TGFileFormat("Lilypond", "text/x-lilypond", new String[]{"ly"});
	
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