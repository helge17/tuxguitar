package org.herac.tuxguitar.io.base;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.Iterator;

import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.util.TGContext;

public class TGSongWriterHelper {
	
	private TGContext context;
	
	public TGSongWriterHelper(TGContext context){
		this.context = context;
	}
	
	public void write(TGSongWriterHandle handle) throws TGFileFormatException{
		try {
			TGFileFormatManager fileFormatManager = TGFileFormatManager.getInstance(this.context);
			Iterator<TGOutputStreamBase> it = fileFormatManager.getOutputStreams();
			while(it.hasNext()){
				TGOutputStreamBase writer = (TGOutputStreamBase)it.next();
				if( writer.getFileFormat().getName().equals(handle.getFormat().getName()) ){
					writer.init(handle.getFactory(), new BufferedOutputStream(handle.getOutputStream()));
					writer.writeSong(handle.getSong());
					return;
				}
			}
			
			Iterator<TGRawExporter> exporters = fileFormatManager.getExporters();
			while (exporters.hasNext() ) {
				TGRawExporter rawExporter = exporters.next();
				if( rawExporter instanceof TGLocalFileExporter ) {
					TGLocalFileExporter fileExporter = (TGLocalFileExporter) rawExporter;
					if( fileExporter.getFileFormat().getName().equals(handle.getFormat().getName()) ){							
						TGSongStreamContext tgStreamContext = new TGSongStreamContext();
						tgStreamContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, handle.getSong());
						tgStreamContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER, new TGSongManager(handle.getFactory()));
						tgStreamContext.setAttribute(OutputStream.class.getName(), new BufferedOutputStream(handle.getOutputStream()));
						
						TGSongStream tgSongStream = fileExporter.openStream(tgStreamContext);
						tgSongStream.process();
						return;
					}
				}
			}
		} catch (Throwable t) {
			throw new TGFileFormatException(t);
		}
		throw new TGFileFormatException("Unsupported file format");
	}
}
