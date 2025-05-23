package app.tuxguitar.android.sound;

import android.media.AudioManager;
import android.media.AudioTrack;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class TGSourceDataLine extends TGAbstractLine implements SourceDataLine {

	private static final int BUFFER_SIZE = 4096;

	private TGAudioFormat format;
	private AudioTrack audioTrack;

	public TGSourceDataLine(Line.Info info) {
		super(info);
	}

	@Override
	public void flush() {
		if( this.audioTrack != null ) {
			this.audioTrack.flush();
		}
	}

	@Override
	public void start() {
		if( this.audioTrack != null ) {
			this.audioTrack.play();
		}
	}

	@Override
	public void stop() {
		if( this.audioTrack != null ) {
			this.audioTrack.stop();
		}
	}

	@Override
	public boolean isRunning() {
		return (this.audioTrack != null && this.audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING);
	}

	@Override
	public boolean isActive() {
		return (this.audioTrack != null && this.audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING);
	}

	@Override
	public AudioFormat getFormat() {
		return (this.format != null ? this.format.getSource() : null);
	}

	@Override
	public int getBufferSize() {
		return BUFFER_SIZE;
	}

	@Override
	public int available() {
		return this.getBufferSize();
	}

	@Override
	public void close() {
		this.audioTrack.release();

		super.close();
	}

	@Override
	public void open(AudioFormat format) throws LineUnavailableException {
		this.format = new TGAudioFormat(format);
		this.audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, this.format.getSampleRateInHz(), this.format.getChannelConfig(), this.format.getAudioFormat(), BUFFER_SIZE, AudioTrack.MODE_STREAM);

		super.open();
	}

	@Override
	public void open(AudioFormat format, int bufferSize) throws LineUnavailableException {
		this.open(format);
	}

	@Override
	public int write(byte[] b, int off, int len) {
		return this.audioTrack.write(b, off, len);
	}

	@Override
	public int getFramePosition() {
		return 0;
	}

	@Override
	public long getLongFramePosition() {
		return 0;
	}

	@Override
	public long getMicrosecondPosition() {
		return 0;
	}

	@Override
	public float getLevel() {
		return 0;
	}

	@Override
	public void drain() {
		// not implemented
	}
}
