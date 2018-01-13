package org.herac.tuxguitar.io.base;

import java.io.ByteArrayInputStream;
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
			byte[] buffer = TGFileFormatUtils.getBytes(handle.getInputStream());
			
			if( handle.getFormat() == null ) {
				handle.setFormat(this.detectFileFormat(handle, buffer));
			}
			
			if( handle.getFormat() != null ) {
				handle.setInputStream(new ByteArrayInputStream(buffer));
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
	
	public TGFileFormat detectFileFormat(TGSongReaderHandle handle, byte[] buffer) throws IOException {
		TGFileFormatManager fileFormatManager = TGFileFormatManager.getInstance(this.getContext());
		List<TGFileFormatDetector> detectors = fileFormatManager.getFileFormatDetectors();
		for(TGFileFormatDetector detector : detectors) {
			handle.setInputStream(new ByteArrayInputStream(buffer));
			TGFileFormat fileFormat = detector.getFileFormat(handle.getInputStream());
			
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