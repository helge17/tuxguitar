package org.herac.tuxguitar.io.base;

import java.io.IOException;

import org.herac.tuxguitar.util.TGContext;

public class TGSongWriterHelper extends TGSongPersistenceHelper {
	
	public TGSongWriterHelper(TGContext context){
		super(context);
	}
	
	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		try {
			boolean success = false;
			
			if( handle.getFormat() == null ) {
				handle.setFormat(this.detectFileFormat(handle));
			}
			
			if( handle.getFormat() != null ) {
				TGSongWriter writer = TGFileFormatManager.getInstance(this.getContext()).findSongWriter(handle.getFormat());
				if( writer != null ){
					writer.write(handle);
					success = true;
				}
			}
			
			if(!success) {
				throw new TGFileFormatException("Unsupported file format");
			}
		} catch(TGFileFormatException tgFileFormatException) {
			throw tgFileFormatException;
		} catch(Throwable throwable) {
			throw new TGFileFormatException(throwable);
		} finally {
			try {
				handle.getOutputStream().close();
			} catch (IOException e) {
				throw new TGFileFormatException(e);
			}
		}
	}
	
	public TGFileFormat detectFileFormat(TGSongWriterHandle handle) throws IOException {
		TGFileFormatManager fileFormatManager = TGFileFormatManager.getInstance(this.getContext());
		
		String mimeType = handle.getContext().getAttribute(ATTRIBUTE_MIME_TYPE);
		if( mimeType != null ) {
			TGFileFormat fileFormat = fileFormatManager.findWriterFileFormatByMimeType(mimeType);
			if( fileFormat != null ) {
				return fileFormat;
			}
		}
		
		String formatCode = handle.getContext().getAttribute(ATTRIBUTE_FORMAT_CODE);
		if( formatCode != null ) {
			TGFileFormat fileFormat = fileFormatManager.findWriterFileFormatByCode(formatCode);
			if( fileFormat != null ) {
				return fileFormat;
			}
		}
		return null;
	}
}
