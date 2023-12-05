package org.herac.tuxguitar.io.abc;

import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.io.base.TGSongWriterHandle;

public class ABCSongWriter extends ABCFileFormat implements TGSongWriter {
	
	public ABCSongWriter() {
		super();
	}
	
	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		try{
			ABCSettings settings = handle.getContext().getAttribute(ABCSettings.class.getName());
			if( settings == null ) {
				settings = ABCSettings.getDefaults();
			}
			
			new ABCOutputStream(handle.getOutputStream(), settings).writeSong(handle.getSong());
		}catch(Throwable throwable){
			throw new TGFileFormatException(throwable);
		}
	}
}