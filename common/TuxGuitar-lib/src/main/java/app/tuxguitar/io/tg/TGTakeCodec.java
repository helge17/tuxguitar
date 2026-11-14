package app.tuxguitar.io.tg;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import app.tuxguitar.song.models.TGTake;

/**
 * How a TuxGuitar file of format 3.0 keeps the samples of a take: one entry of the file per take, "takes/<id>.<extension>".
 * TuxGuitar's audio support gives one to the file format manager; without one, TuxGuitar reads and writes format 2.0
 * only (songs without audio channels), as older versions and Android do.
 */
public interface TGTakeCodec {

	// the extension of a take's entry
	String getExtension();

	// the take's samples and sample rate, onto the stream, which stays open
	void write(TGTake take, OutputStream out) throws IOException;

	// a take's samples and sample rate from the stream, which stays open; its number is the file's to give (0 here)
	TGTake read(InputStream in) throws IOException;
}
