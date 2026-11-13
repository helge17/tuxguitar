package app.tuxguitar.audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * Writes left and right samples as an audio file of the format given, through the Java Sound file writers that
 * TuxGuitar's audio export uses today.
 */
public final class TGAudioFileWriter {

	private TGAudioFileWriter() {
	}

	public static void write(float[][] samples, TGExportFormat format, OutputStream out) throws IOException {
		byte[] data = format.encode(samples);
		AudioInputStream stream = new AudioInputStream(new ByteArrayInputStream(data), format.audioFormat(), data.length / format.audioFormat().getFrameSize());
		AudioSystem.write(stream, format.fileType(), out);
	}
}
