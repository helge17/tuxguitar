package app.tuxguitar.io.gpx.v7;

import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongReader;
import app.tuxguitar.io.base.TGSongReaderHandle;
import app.tuxguitar.io.gpx.GPXDocumentParser;
import app.tuxguitar.io.gpx.GPXDocumentReader;
import app.tuxguitar.util.TGContext;

public class GPXInputStream implements TGSongReader {

	public static final TGFileFormat FILE_FORMAT = new TGFileFormat("Guitar Pro 7", "application/x-gtp", new String[]{"gp"});

	private TGContext context;

	public GPXInputStream(TGContext context) {
		this.context = context;
	}

	public TGFileFormat getFileFormat() {
		return FILE_FORMAT;
	}

	public void read(TGSongReaderHandle handle) throws TGFileFormatException {
		try {
			GPXFileSystem gpxFileSystem = new GPXFileSystem();
			gpxFileSystem.load(handle.getInputStream());

			GPXDocumentReader gpxReader = new GPXDocumentReader(gpxFileSystem.getFileContentsAsStream(GPXFileSystem.RESOURCE_SCORE), GPXDocumentReader.GP7);
			GPXDocumentParser gpxParser = new GPXDocumentParser(this.context, handle.getFactory(), gpxReader.read());

			handle.setSong(gpxParser.parse());
		} catch (Throwable throwable) {
			throw new TGFileFormatException( throwable );
		}
	}
}
