package org.herac.tuxguitar.io.base;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class TGSongLoaderHelper {
	
	private TGContext context;
	
	public TGSongLoaderHelper(TGContext context){
		this.context = context;
	}
	
	public void load(TGSongLoaderHandle handle) throws TGFileFormatException {
		BufferedInputStream stream = null;
		try{
			stream = new BufferedInputStream(handle.getInputStream());
			
			stream.mark(1);
			TGFileFormatManager fileFormatManager = TGFileFormatManager.getInstance(this.context);
			Iterator<TGInputStreamBase> it = fileFormatManager.getInputStreams();
			while(it.hasNext() && handle.getSong() == null){
				TGInputStreamBase reader = (TGInputStreamBase)it.next();
				reader.init(handle.getFactory(),stream);
				if( reader.isSupportedVersion() ){
					handle.setSong(reader.readSong());
					handle.setFormat(reader.getFileFormat());
					return;
				}
				stream.reset();
			}
			
			if( handle.getFormat() != null ) {
				Iterator<TGRawImporter> importers = fileFormatManager.getImporters();
				while (importers.hasNext() ) {
					TGRawImporter rawImporter = importers.next();
					if( rawImporter instanceof TGLocalFileImporter ) {
						TGLocalFileImporter fileImporter = (TGLocalFileImporter) rawImporter;
						if( fileImporter.getFileFormat().getName().equals(handle.getFormat().getName()) ){							
							TGSongStreamContext tgStreamContext = new TGSongStreamContext();
							tgStreamContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER, new TGSongManager(handle.getFactory()));
							tgStreamContext.setAttribute(InputStream.class.getName(), stream);
							
							TGSongStream tgSongStream = fileImporter.openStream(tgStreamContext);
							tgSongStream.process();
							
							TGSong tgSong = tgStreamContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
							
							handle.setSong(tgSong);
							return;
						}
					}
				}
			}
		} catch(Throwable throwable) {
			throw new TGFileFormatException(throwable);
		} finally {
			try {
				if( stream != null ) {
					stream.close();
				}
			} catch (IOException e) {
				throw new TGFileFormatException(e);
			}
		}
		throw new TGFileFormatException("Unsupported file format");
	}
}