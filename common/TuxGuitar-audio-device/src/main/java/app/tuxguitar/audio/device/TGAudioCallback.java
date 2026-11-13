package app.tuxguitar.audio.device;

/**
 * What a stream runs each time its device asks for a block, on the device's own thread: it reads what each input
 * channel delivered and writes what each output channel is to sound, frames of each (one array per channel, at least
 * frames long, reused from block to block). It must not block and should not allocate.
 */
public interface TGAudioCallback {

	void process(float[][] input, float[][] output, int frames);
}
