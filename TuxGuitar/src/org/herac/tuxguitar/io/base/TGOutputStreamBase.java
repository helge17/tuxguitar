package org.herac.tuxguitar.io.base;

import java.io.IOException;
import java.io.OutputStream;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

public interface TGOutputStreamBase {
	
	public void init(TGFactory factory,OutputStream stream);
	
	public boolean isSupportedExtension(String extension);
	
	public TGFileFormat getFileFormat();
	
	public void writeSong(TGSong song) throws TGFileFormatException,IOException;
}
