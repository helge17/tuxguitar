package app.tuxguitar.midi.synth;

import javax.sound.sampled.AudioFormat;

public class TGAudioBuffer {

	public static final int CHANNELS = 2;
	public static final int BUFFER_SIZE = 1024;
	public static final float SAMPLE_RATE = 44100f;
	// the samples are little-endian
	public static final boolean BIGENDIAN = false;
	// the format of the samples in a buffer: what the audio line plays and what Gervill renders
	public static final AudioFormat AUDIO_FORMAT = new AudioFormat(SAMPLE_RATE, 16, CHANNELS, true, BIGENDIAN);

	private int buffers;
	private byte[] buffer;

	public TGAudioBuffer() {
		this.buffer = new byte[CHANNELS * BUFFER_SIZE];
	}

	public byte[] getBuffer() {
		return this.buffer;
	}

	public int getLength() {
		return this.buffer.length;
	}

	public void clear() {
		this.buffers = 0;
		for( int i = 0 ; i < this.buffer.length ; i ++ ){
			this.buffer[i] = 0;
		}
	}

	public void write(TGAudioBuffer buffer) {
		this.write(buffer.getBuffer());
	}

	public void write(byte[] sample) {
		if( this.buffer.length == sample.length ) {
			for(int i = 0; i < this.buffer.length; i ++) {
				this.buffer[i] = sample[i];
			}
		}
	}

	public void write(float[][] sample) {
		for (int i = 0; i < ( BUFFER_SIZE / 2 ); i++) {
			for (int j = 0; j < CHANNELS; j++) {
				this.setSample(i, j, saturate(sample[(j % sample.length)][i] * Short.MAX_VALUE));
			}
		}
	}

	public void read(float[][] bufferToFill) {
		for (int i = 0; i < ( BUFFER_SIZE / 2 ); i++) {
			for(int j = 0; j < CHANNELS; j++) {
				bufferToFill[(j % bufferToFill.length)][i] = ((float) this.getSample(i, j) / (float) Short.MAX_VALUE);
			}
		}
	}

	public void mix(TGAudioBuffer buffer) {
		this.mix(buffer.getBuffer());
	}

	public void mix(byte[] sample) {
		if( this.buffer.length == sample.length ) {
			this.buffers ++;

			float mixAmp = (1f / this.buffers);

			for (int i = 0; i < ( BUFFER_SIZE / 2 ); i++) {
				for (int j = 0; j < CHANNELS; j++) {
					float f1 = (((float) this.getSample(i, j) / Short.MAX_VALUE) * (1f - mixAmp));
					float f2 = (((float) sampleAt(sample, index(i, j)) / Short.MAX_VALUE) * mixAmp);
					this.setSample(i, j, (short) ((f1 + f2) * Short.MAX_VALUE));
				}
			}
		}
	}

	public void clip() {
		if( this.buffers > 1 ) {
			for (int i = 0; i < ( BUFFER_SIZE / 2 ); i++) {
				for (int j = 0; j < CHANNELS; j++) {
					this.setSample(i, j, saturate(this.getSample(i, j) * this.buffers));
				}
			}
		}
	}

	// odd, pinned: with fewer volumes than sides, the samples scaled are the first ones of the buffer, one per volume
	// and frame, whatever their side
	public void balance(float[] volumes) {
		int index = 0;
		for (int i = 0; i < ( BUFFER_SIZE / 2 ); i++) {
			for(int c = 0; c < CHANNELS; c++) {
				if( c < volumes.length ) {
					short sample = sampleAt(this.buffer, index);
					writeSample(this.buffer, index, (short) Math.round(Math.max(Math.min(sample * volumes[c], Short.MAX_VALUE), Short.MIN_VALUE)));
					index += 2;
				}
			}
		}
	}

	// a 16-bit sample, what goes past full scale held there (truncated toward zero inside it)
	private static short saturate(float value) {
		return (short) Math.max(Math.min(value, Short.MAX_VALUE), Short.MIN_VALUE);
	}

	// where the sample of the frame and side starts: the samples are 16 bits, little-endian, sides interleaved
	private static int index(int frame, int side) {
		return (frame * CHANNELS + side) * 2;
	}

	private short getSample(int frame, int side) {
		return sampleAt(this.buffer, index(frame, side));
	}

	private void setSample(int frame, int side, short value) {
		writeSample(this.buffer, index(frame, side), value);
	}

	private static short sampleAt(byte[] bytes, int index) {
		return (short) ((bytes[index] & 0xff) | ((bytes[index + 1] & 0xff) << 8));
	}

	private static void writeSample(byte[] bytes, int index, short value) {
		bytes[index] = (byte) (value & 0xFF);
		bytes[index + 1] = (byte) ((value >> 8) & 0xFF);
	}
}
