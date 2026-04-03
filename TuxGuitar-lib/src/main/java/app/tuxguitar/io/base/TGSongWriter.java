package app.tuxguitar.io.base;

public interface TGSongWriter extends TGSongPersistenceHandler {

	void write(TGSongWriterHandle handle) throws TGFileFormatException;
}
