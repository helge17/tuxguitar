package org.herac.tuxguitar.io.base;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;

import org.herac.tuxguitar.util.TGContext;

public class TGSongReaderHelper extends TGSongPersistenceHelper {
	
	public TGSongReaderHelper(TGContext context){
		super(context);
	}
	
	public void read(TGSongReaderHandle handle) throws TGFileFormatException {
		try{
			boolean success = false;
			
			handle.setInputStream(new BufferedInputStream(handle.getInputStream()));
			if( handle.getFormat() == null ) {
				handle.setFormat(this.detectFileFormat(handle));
			}
			
			if( handle.getFormat() != null ) {
				TGSongReader reader = TGFileFormatManager.getInstance(this.getContext()).findSongReader(handle.getFormat());
				if( reader != null ){
					reader.read(handle);
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
				handle.getInputStream().close();
			} catch (IOException e) {
				throw new TGFileFormatException(e);
			}
		}
	}
	
	public TGFileFormat detectFileFormat(TGSongReaderHandle handle) throws IOException {
		handle.getInputStream().mark(1);
		
		TGFileFormatManager fileFormatManager = TGFileFormatManager.getInstance(this.getContext());
		List<TGFileFormatDetector> detectors = fileFormatManager.getFileFormatDetectors();
		for(TGFileFormatDetector detector : detectors){
			TGFileFormat fileFormat = detector.getFileFormat(handle.getInputStream());
			
			handle.getInputStream().reset();
			if( fileFormat != null ) {
				return fileFormat;
			}
		}
		
		String mimeType = handle.getContext().getAttribute(ATTRIBUTE_MIME_TYPE);
		if( mimeType != null ) {
			TGFileFormat fileFormat = fileFormatManager.findReaderFileFormatByMimeType(mimeType);
			if( fileFormat != null ) {
				return fileFormat;
			}
		}
		
		String formatCode = handle.getContext().getAttribute(ATTRIBUTE_FORMAT_CODE);
		if( formatCode != null ) {
			TGFileFormat fileFormat = fileFormatManager.findReaderFileFormatByCode(formatCode);
			if( fileFormat != null ) {
				return fileFormat;
			}
		}
		return null;
	}
}