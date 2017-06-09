package org.herac.tuxguitar.midi.synth;

public class TGAudioBuffer {
	
	public static final int CHANNELS = 2;
	public static final int BUFFER_SIZE = 1024;
	public static final float SAMPLE_RATE = 44100f;
	public static final boolean BIGENDIAN = false;
	
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
		int index = 0;
		short sampleValue = 0;
		for (int i = 0; i < ( BUFFER_SIZE / 2 ); i++) {
			for (int j = 0; j < CHANNELS; j++) {
				sampleValue = (short) (sample[(j % sample.length)][i] * Short.MAX_VALUE);
				this.buffer[index++] = (byte) (BIGENDIAN ? ((sampleValue & 0xFF00) >> 8) : ((sampleValue & 0x00FF) >> 0) ) ;
				this.buffer[index++] = (byte) (BIGENDIAN ? ((sampleValue & 0x00FF) >> 0) : ((sampleValue & 0xFF00) >> 8) ) ;
			}
		}
	}
	
	public void read(float[][] bufferToFill) {
		int index = 0;
		short bufferValue = 0;
		for (int i = 0; i < ( BUFFER_SIZE / 2 ); i++) {
			for(int j = 0; j < CHANNELS; j++) {
				bufferValue = 0;
				for( int n = 0; n < 2; n ++){
					bufferValue |= ((this.buffer[index++] & 0xff ) << ( 8 * ( BIGENDIAN ? 1 - n : n )));
				}
				bufferToFill[(j % bufferToFill.length)][i] = ((float) bufferValue / (float) Short.MAX_VALUE);
			}
		}
	}
	
	public void mix(TGAudioBuffer buffer) {
		this.mix(buffer.getBuffer());
	}
	
	public void mix(byte[] sample) {
		if( this.buffer.length == sample.length ) {
			this.buffers ++;
			
			int index = 0;
			short s1Value = 0;
			short s2Value = 0;
			short mixedValue = 0;
			float mixAmp = (1f / this.buffers);
			
			for (int i = 0; i < ( BUFFER_SIZE / 2 ); i++) {
				for (int j = 0; j < CHANNELS; j++) {
					s1Value = 0;
					s2Value = 0;
					for( int n = 0; n < 2; n ++){
						s1Value |= ((this.buffer[(index + n)] & 0xff) << ( 8 * (BIGENDIAN ? 1 - n : n)));
						s2Value |= ((sample[(index + n)] & 0xff) << ( 8 * (BIGENDIAN ? 1 - n : n)));
					}
					
					float f1 = (((float) s1Value / Short.MAX_VALUE) * (1f - mixAmp));
					float f2 = (((float) s2Value / Short.MAX_VALUE) * mixAmp);					
					
					mixedValue = (short) ((f1 + f2) * Short.MAX_VALUE);
					
					this.buffer[index++] = (byte) ((BIGENDIAN ? ((mixedValue & 0xFF00) >> 8) : ((mixedValue & 0x00FF) >> 0) ) & 0xFF);
					this.buffer[index++] = (byte) ((BIGENDIAN ? ((mixedValue & 0x00FF) >> 0) : ((mixedValue & 0xFF00) >> 8) ) & 0xFF);
				}
			}
		}
	}
	
	public void clip() {
		if( this.buffers > 1 ) {
			int index = 0;
			
			short sampleValue = 0;
			short ampValue = 0;
			short maxValue = Short.MAX_VALUE;
			short minValue = Short.MIN_VALUE;
			
			for (int i = 0; i < ( BUFFER_SIZE / 2 ); i++) {
				for (int j = 0; j < CHANNELS; j++) {
					sampleValue = 0;
					for( int n = 0; n < 2; n ++){
						sampleValue |= ((this.buffer[(index + n)] & 0xff) << ( 8 * (BIGENDIAN ? 1 - n : n)));
					}
					ampValue = (short) (Math.max(Math.min(sampleValue * this.buffers, maxValue), minValue) & 0xFFFF);
					
					this.buffer[index++] = (byte) ((BIGENDIAN ? ((ampValue & 0xFF00) >> 8) : ((ampValue & 0x00FF) >> 0) ) & 0xFF);
					this.buffer[index++] = (byte) ((BIGENDIAN ? ((ampValue & 0x00FF) >> 0) : ((ampValue & 0xFF00) >> 8) ) & 0xFF);
				}
			}
		}
	}
}
