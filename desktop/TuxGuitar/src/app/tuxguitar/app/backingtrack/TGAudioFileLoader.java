package app.tuxguitar.app.backingtrack;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

public class TGAudioFileLoader {

	public static final long MAX_PCM_BYTES = 512L * 1024L * 1024L;

	public static AudioData load(File file) throws Exception {
		if( file == null || !file.isFile() || !file.canRead() ) {
			throw new IOException("file not found or not readable");
		}
		String name = file.getName().toLowerCase();
		if( name.endsWith(".mp3") ) {
			return loadMp3(file);
		}
		return loadSystem(file);
	}

	private static AudioData loadMp3(File file) throws Exception {
		Bitstream bitstream = new Bitstream(new BufferedInputStream(new FileInputStream(file), 64 * 1024));
		try {
			Header firstFrame = bitstream.readFrame();
			if( firstFrame == null ) {
				throw new IOException("no mpeg frame found");
			}
			bitstream.unreadFrame();

			int frequency = firstFrame.frequency();
			int channels = (firstFrame.mode() == Header.SINGLE_CHANNEL) ? 1 : 2;
			Decoder decoder = new Decoder();
			SampleBuffer sampleBuffer = new SampleBuffer(frequency, channels);
			decoder.setOutputBuffer(sampleBuffer);

			ByteArrayOutputStream out = new ByteArrayOutputStream(1024 * 1024);
			byte[] chunk = new byte[4096];
			Header frame;
			while( (frame = bitstream.readFrame()) != null ) {
				decoder.decodeFrame(frame, bitstream);
				int length = sampleBuffer.getBufferLength();
				short[] buffer = sampleBuffer.getBuffer();
				if( (length * 2) > chunk.length ) {
					chunk = new byte[length * 2];
				}
				if( (out.size() + (length * 2)) > MAX_PCM_BYTES ) {
					throw new IOException("audio file is too long");
				}
				for( int i = 0; i < length; i++ ) {
					short sample = buffer[i];
					chunk[(i * 2)] = (byte)(sample & 0xFF);
					chunk[(i * 2) + 1] = (byte)((sample >> 8) & 0xFF);
				}
				out.write(chunk, 0, length * 2);
				sampleBuffer.clear_buffer();
				bitstream.closeFrame();
			}
			byte[] pcm = out.toByteArray();
			if( pcm.length == 0 ) {
				throw new IOException("no audio data found");
			}
			return new AudioData(new AudioFormat(frequency, 16, channels, true, false), pcm);
		} finally {
			try {
				bitstream.close();
			} catch (Exception e) {
			}
		}
	}

	private static AudioData loadSystem(File file) throws Exception {
		AudioInputStream source = AudioSystem.getAudioInputStream(file);
		AudioInputStream stream = source;
		try {
			AudioFormat sourceFormat = source.getFormat();
			AudioFormat targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sourceFormat.getSampleRate(), 16, sourceFormat.getChannels(), sourceFormat.getChannels() * 2, sourceFormat.getSampleRate(), false);
			boolean alreadyPcm = AudioFormat.Encoding.PCM_SIGNED.equals(sourceFormat.getEncoding())
					&& sourceFormat.getSampleSizeInBits() == 16
					&& !sourceFormat.isBigEndian()
					&& sourceFormat.getFrameSize() == (sourceFormat.getChannels() * 2);
			if( !alreadyPcm ) {
				if( !AudioSystem.isConversionSupported(targetFormat, sourceFormat) ) {
					throw new IOException("unsupported audio format");
				}
				stream = AudioSystem.getAudioInputStream(targetFormat, source);
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream(1024 * 1024);
			byte[] buffer = new byte[64 * 1024];
			int read;
			while( (read = stream.read(buffer)) > 0 ) {
				if( (out.size() + read) > MAX_PCM_BYTES ) {
					throw new IOException("audio file is too long");
				}
				out.write(buffer, 0, read);
			}
			byte[] pcm = out.toByteArray();
			if( pcm.length == 0 ) {
				throw new IOException("no audio data found");
			}
			return new AudioData(stream.getFormat(), pcm);
		} finally {
			try {
				stream.close();
			} catch (Exception e) {
			}
			if( stream != source ) {
				try {
					source.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public static class AudioData {

		private AudioFormat format;
		private byte[] pcm;

		public AudioData(AudioFormat format, byte[] pcm) {
			this.format = format;
			this.pcm = pcm;
		}

		public AudioFormat getFormat() {
			return this.format;
		}

		public byte[] getPcm() {
			return this.pcm;
		}
	}
}
