package app.tuxguitar.midi.synth;

/**
 * Delays the blocks of a stream by a number of frames: each block comes out as the stream was that many frames earlier,
 * silence before the stream began. When the delay grows, silence fills the gap; when it shrinks, the oldest frames held
 * are dropped.
 */
public final class TGDelayLine {

	private static final int FRAME_BYTES = TGAudioBuffer.CHANNELS * 2;

	// the last frames that came in and have not gone out yet, oldest first
	private byte[] held = new byte[0];

	public void delay(TGAudioBuffer buffer, int frames) {
		int delay = frames * FRAME_BYTES;
		if( delay == 0 && this.held.length == 0 ) {
			return;
		}
		byte[] block = buffer.getBuffer();
		byte[] stream = new byte[delay + block.length];
		int kept = Math.min(this.held.length, delay);
		System.arraycopy(this.held, this.held.length - kept, stream, delay - kept, kept);
		System.arraycopy(block, 0, stream, delay, block.length);
		System.arraycopy(stream, 0, block, 0, block.length);
		this.held = java.util.Arrays.copyOfRange(stream, block.length, stream.length);
	}
}
