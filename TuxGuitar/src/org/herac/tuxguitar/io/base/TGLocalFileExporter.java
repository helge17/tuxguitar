package org.herac.tuxguitar.io.base;

import java.io.OutputStream;

import org.herac.tuxguitar.song.factory.TGFactory;

public interface TGLocalFileExporter extends TGRawExporter {
	
	public TGFileFormat getFileFormat();
	
	public boolean configure(boolean setDefaults);
	
	public void init(TGFactory factory,OutputStream stream);
	
}
