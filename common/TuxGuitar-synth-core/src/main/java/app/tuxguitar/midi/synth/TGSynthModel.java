package app.tuxguitar.midi.synth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import app.tuxguitar.midi.synth.impl.GervillProcessorFactory;
import app.tuxguitar.player.base.MidiChannel;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiReceiver;
import app.tuxguitar.player.base.MidiSampleClock;
import app.tuxguitar.player.base.MidiSynthesizer;
import app.tuxguitar.player.base.MidiTransport;
import app.tuxguitar.util.TGContext;

/**
 * The TuxGuitar Synthesizer's channels and programs. It is a sample clock: whoever renders it a block at a time runs
 * beforeBlock first, so the sequencer playing on it sends the block's events on their frames. Its channels are opened
 * and closed by the player while the audio thread renders them, so each reader reads the array of open channels as it
 * was, and the audio thread walks it without allocating.
 */
public class TGSynthModel implements MidiSynthesizer, MidiSampleClock{

	public static final int BANKS_LENGTH = 129;
	public static final int PROGRAMS_LENGTH = 128;

	private TGContext context;
	private float sampleRate;
	private TGProgram[][] programs;
	private volatile TGSynthChannel[] channels;
	private volatile Listener listener;
	private final MidiTransport transport = new MidiTransport();
	private volatile int outputLatency;
	private final List<TGSynthChannelListener> channelListeners = new CopyOnWriteArrayList<TGSynthChannelListener>();

	public TGSynthModel(TGContext context){
		this(context, TGAudioBuffer.SAMPLE_RATE);
	}

	// a synthesizer whose processors render at the sample rate (the audio device's)
	public TGSynthModel(TGContext context, float sampleRate){
		this.context = context;
		this.sampleRate = sampleRate;
		this.channels = new TGSynthChannel[0];
		this.programs = new TGProgram[BANKS_LENGTH][PROGRAMS_LENGTH];
		for( int b = 0; b < BANKS_LENGTH ; b ++ ){
			for( int p = 0; p < PROGRAMS_LENGTH ; p ++ ) {
				this.programs[ b ][ p ] = new TGProgram();
			}
		}
	}

	// the channel DRUMS is the synthesizer's own drums: Gervill's General MIDI drum kit (bank 128, program 0), outside
	// every sound chain
	public MidiChannel openChannel(int channelId) throws MidiPlayerException{
		TGSynthChannel tgChannel = new TGSynthChannel(this, channelId);
		if( channelId == DRUMS ) {
			tgChannel.loadProgram(drums());
		}

		this.add(tgChannel);

		return tgChannel;
	}

	public boolean hasDrums() {
		return true;
	}

	private static TGProgram drums() {
		TGProgramElement kit = new TGProgramElement();
		kit.setId("drums");
		kit.setType(GervillProcessorFactory.TYPE);
		kit.setParameter("gervil.program", "128:0");
		TGProgram drums = new TGProgram();
		drums.setReceiver(kit);
		return drums;
	}

	public void closeChannel(MidiChannel midiChannel) throws MidiPlayerException{
		TGSynthChannel tgChannel = ((TGSynthChannel) midiChannel);
		tgChannel.closeProcessor();

		this.remove(tgChannel);
	}

	// one writer at a time replaces the array; readers keep the one they read
	private synchronized void add(TGSynthChannel channel){
		TGSynthChannel[] open = Arrays.copyOf(this.channels, this.channels.length + 1);
		open[open.length - 1] = channel;
		this.channels = open;
	}

	private synchronized void remove(TGSynthChannel channel){
		List<TGSynthChannel> open = new ArrayList<TGSynthChannel>(Arrays.asList(this.channels));
		open.remove(channel);
		this.channels = open.toArray(new TGSynthChannel[0]);
	}

	// hears each change of a channel's processors, from now on
	public void addChannelListener(TGSynthChannelListener listener){
		this.channelListeners.add(listener);
	}

	public void removeChannelListener(TGSynthChannelListener listener){
		this.channelListeners.remove(listener);
	}

	// the channel's processors changed
	void chainChanged(TGSynthChannel channel){
		this.channelListeners.forEach(listener -> listener.chainChanged(channel));
	}

	public boolean isChannelOpen(MidiChannel midiChannel) throws MidiPlayerException{
		return true;
	}

	public boolean isBusy() {
		for(TGSynthChannel channel : this.channels) {
			if( channel.isBusy() ) {
				return true;
			}
		}
		return false;
	}

	public void closeChannels() throws MidiPlayerException{
		while( countChannels() > 0 ){
			closeChannel( getChannel(0) );
		}
	}

	public TGSynthChannel getChannel( int index ){
		TGSynthChannel[] open = this.channels;
		return (index >= 0 && index < open.length ? open[index] : null);
	}

	public TGSynthChannel getChannelById( int channelId ){
		for(TGSynthChannel channel : this.channels) {
			if( channel.getId() == channelId ) {
				return channel;
			}
		}
		return null;
	}

	// the open channels, as they are when read
	Iterable<TGSynthChannel> channels(){
		return Collections.unmodifiableList(Arrays.asList(this.channels));
	}

	// how many frames later than its events the output sounds them: the slowest channel's chain, to which plugin delay
	// compensation delays every channel
	public int getLatency(){
		int latency = 0;
		for(TGSynthChannel channel : this.channels) {
			latency = Math.max(latency, channel.getLatency());
		}
		return latency;
	}

	// the audio device's output latency, in frames: how long after it renders a frame the device sounds it. Seam for
	// the desktop: its synthesizer output sets it from the device's reported latency plus the frames its line buffers
	// (not done yet); 0 until then
	public void setOutputLatency(int frames){
		this.outputLatency = frames;
	}

	// how many frames after an event's frame the output sounds it: the chains' latency, then the output latency. The
	// sequencer playing on this clock reports the position sounding, that many frames behind the one it schedules
	public int getSoundingDelay(){
		return this.getLatency() + this.outputLatency;
	}

	public int countChannels(){
		return this.channels.length;
	}

	public TGProgram getProgram(int bank, int program){
		if( bank >= 0 && bank < this.programs.length ){
			if( program >= 0 && program < this.programs[bank].length ){
				return this.programs[bank][program];
			}
		}
		return null;
	}

	public float getSampleRate() {
		return this.sampleRate;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public MidiReceiver at(int frame) {
		return new TGSynthFrameReceiver(this, frame);
	}

	// the listener sends the events of the next frames, and tells the transport what the song does over them (a block
	// it tells nothing about is stopped); one that fails does not stop the synthesizer from rendering
	public void beforeBlock(int frames) {
		this.transport.stop();
		Listener listener = this.listener;
		if( listener != null ) {
			try {
				listener.beforeBlock(this, frames);
			} catch (MidiPlayerException | RuntimeException exception) {
				exception.printStackTrace();
			}
		}
	}

	// what the song does over the next block, as the listener tells it before the block
	public MidiTransport getTransport() {
		return this.transport;
	}

	public TGContext getContext() {
		return context;
	}
}
