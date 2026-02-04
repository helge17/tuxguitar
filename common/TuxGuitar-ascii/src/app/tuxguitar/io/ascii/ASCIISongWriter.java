package app.tuxguitar.io.ascii;

import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.base.TGSongWriterHandle;
import app.tuxguitar.util.TGContext;

public class ASCIISongWriter implements TGSongWriter {

	private TGContext context;

	public ASCIISongWriter(TGContext context) {
		this.context = context;
	}

	public TGFileFormat getFileFormat() {
		return new TGFileFormat("ASCII", "text/x-tab", new String[]{"tab"});
	}

	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		try{
			new ASCIITabOutputStream(this.context, handle.getOutputStream()).writeSong(handle.getSong());
		}catch(Throwable throwable){
			throw new TGFileFormatException(throwable);
		}
	}
}