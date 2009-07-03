package org.herac.tuxguitar.io.base;

import java.io.InputStream;

import org.herac.tuxguitar.song.factory.TGFactory;

public interface TGLocalFileImporter extends TGRawImporter {
	
	public TGFileFormat getFileFormat();
	
	public boolean configure(boolean setDefaults);
	
	public void init(TGFactory factory,InputStream stream);
	
}
