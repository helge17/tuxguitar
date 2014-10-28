package org.herac.tuxguitar.io.base;

import java.io.OutputStream;

import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

public interface TGLocalFileExporter extends TGRawExporter {
	
	public TGFileFormat getFileFormat();
	
	public boolean configure(TGSong song, boolean setDefaults);
	
	public void init(TGFactory factory,OutputStream stream);
	
}
