package org.herac.tuxguitar.io.gpx;

import java.io.IOException;
import java.io.InputStream;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

public class GPXInputStream implements TGInputStreamBase{
	
	private int gpxHeader;
	private InputStream gpxStream;
	private GPXFileSystem gpxFileSystem;
	private TGFactory factory;
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat("Guitar Pro 6","*.gpx");
	}
	
	public void init(TGFactory factory, InputStream stream) {
		this.factory = factory;
		this.gpxStream = stream;
		this.gpxHeader = 0;
		this.gpxFileSystem = new GPXFileSystem();
	}
	
	public boolean isSupportedVersion() {
		try {
			this.gpxHeader = this.gpxFileSystem.getHeader( this.gpxStream );
			
			return this.gpxFileSystem.isSupportedHeader(this.gpxHeader);
		} catch (Throwable throwable) {
			return false;
		}
	}
	
	public TGSong readSong() throws TGFileFormatException, IOException {
		try {
			this.gpxFileSystem.load(this.gpxHeader, this.gpxStream);
			
			GPXDocumentReader gpxReader = new GPXDocumentReader( this.gpxFileSystem.getFileContentsAsStream("score.gpif"));
			GPXDocumentParser gpxParser = new GPXDocumentParser( this.factory , gpxReader.read() );
			
			return gpxParser.parse();
		} catch (Throwable throwable) {
			throw new TGFileFormatException( throwable );
		}
	}
}
