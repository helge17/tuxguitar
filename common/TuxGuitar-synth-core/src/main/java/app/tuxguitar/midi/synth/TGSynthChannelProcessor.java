package app.tuxguitar.midi.synth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntFunction;

import app.tuxguitar.player.base.MidiTransport;
import app.tuxguitar.util.TGContext;

public class TGSynthChannelProcessor {

	private TGAudioBuffer buffer;
	// the chain's block: 32-bit float from the source to the channel's 16-bit write, so it has room above full scale
	private float[][] block;
	private TGDelayLine delayLine;
	private TGMidiProcessor processor;
	private List<TGAudioProcessor> outputs;
	private Map<TGProgramElement, TGAudioProcessor> processorMap;
	private Object lock;

	private float sampleRate;
	private MidiTransport transport;

	// its processors render at the sample rate and are told the song's state over each block in the transport
	public TGSynthChannelProcessor(float sampleRate, MidiTransport transport) {
		this.sampleRate = sampleRate;
		this.transport = transport;
		this.lock = new Object();
		this.buffer = new TGAudioBuffer();
		this.block = new float[TGAudioBuffer.CHANNELS][TGAudioBuffer.BUFFER_SIZE / 2];
		this.delayLine = new TGDelayLine();
		this.outputs = new ArrayList<TGAudioProcessor>();
		this.processorMap = new HashMap<TGProgramElement, TGAudioProcessor>();
	}

	public void open(TGContext context, TGProgram program) {
		synchronized (this.lock) {
			this.processor = null;
			this.outputs.clear();
			this.closeOrphanProcessors(program);
			this.openReceiver(context, program.getReceiver());
			this.openOutputs(context, program);
		}
	}

	public void openReceiver(TGContext context, TGProgramElement receiver) {
		synchronized (this.lock) {
			TGMidiProcessor processor = (TGMidiProcessor) this.getAudioProcessor(receiver);
			if( processor == null || !processor.isOpen()) {
				TGSynthManager synthManager = TGSynthManager.getInstance(context);
				TGMidiProcessorFactory factory = synthManager.findMidiProcessorFactory(receiver.getType());
				if( factory != null ) {
					processor = factory.createProcessor();
					processor.setSampleRate(this.sampleRate);
					processor.setTransport(this.transport);
				}
			}
			if( processor != null ) {
				this.setMidiProcessor(receiver, processor);
			}
		}
	}

	public void openOutputs(TGContext context, TGProgram program) {
		synchronized (this.lock) {
			for( int i = 0 ; i < program.countOutputs() ;i ++){
				this.openOutput(context, program.getOutput(i));
			}
		}
	}

	public void openOutput(TGContext context, TGProgramElement output) {
		synchronized (this.lock) {
			TGAudioProcessor processor = this.getAudioProcessor(output);
			if( processor == null || !processor.isOpen()) {
				TGSynthManager synthManager = TGSynthManager.getInstance(context);
				TGAudioProcessorFactory factory = synthManager.findAudioProcessorFactory(output.getType());
				if( factory != null ) {
					processor = factory.createProcessor();
					processor.setSampleRate(this.sampleRate);
					processor.setTransport(this.transport);
				}
			}
			if( processor != null ) {
				this.addOutputProcessor(output, processor);
			}
		}
	}

	public void setMidiProcessor(TGProgramElement element, TGMidiProcessor processor) {
		this.processor = processor;
		this.addProcessor(element, processor);
	}

	public void addOutputProcessor(TGProgramElement element, TGAudioProcessor processor) {
		this.addOutput(processor);
		this.addProcessor(element, processor);
	}

	public void addProcessor(TGProgramElement element, TGAudioProcessor processor) {
		if( this.processorMap.containsKey(element) ) {
			this.processorMap.remove(element);
		}
		this.processorMap.put(element, processor);
		this.restoreParameters(element, processor);
	}

	public void restoreParameters(TGProgramElement element, TGAudioProcessor processor) {
		if( element.getParameters() != null ) {
			processor.restoreParameters(element.getParameters());
		}
	}

	public void close(){
		synchronized (this.lock) {
			this.closeOrphanProcessors(null);
		}
	}

	public void closeOrphanProcessors(TGProgram program) {
		synchronized (this.lock) {
			Map<TGProgramElement, TGAudioProcessor> mappedProcessors = new HashMap<TGProgramElement, TGAudioProcessor>(this.processorMap);
			for(TGProgramElement element : mappedProcessors.keySet()) {
				if( shouldCloseProcessor(program, element)) {
					closeProcessor(element);
				}
			}
		}
	}

	public void closeProcessor(TGProgramElement element) {
		synchronized (this.lock) {
			TGAudioProcessor mappedProcessor = this.getAudioProcessor(element);
			if( mappedProcessor != null ) {
				mappedProcessor.close();
				mappedProcessor.finalize();

				this.processorMap.remove(element);
			}
		}
	}

	public boolean shouldCloseProcessor(TGProgram program, TGProgramElement element) {
		synchronized (this.lock) {
			if( program != null ) {
				if( program.getReceiver() != null && program.getReceiver().equals(element)) {
					return false;
				}
				for( int i = 0 ; i < program.countOutputs() ;i ++){
					if( program.getOutput(i).equals(element)) {
						return false;
					}
				}
			}
			return true;
		}
	}

	public TGProgramElement getElement(TGAudioProcessor processor) {
		synchronized (this.lock) {
			Set<Map.Entry<TGProgramElement, TGAudioProcessor>> entries = this.processorMap.entrySet();
			for(Map.Entry<TGProgramElement, TGAudioProcessor> entry : entries) {
				if( entry.getValue() != null && entry.getValue().equals(processor)) {
					return entry.getKey();
				}
			}
			return null;
		}
	}

	public TGAudioProcessor getAudioProcessor(TGProgramElement element) {
		synchronized (this.lock) {
			if( this.processorMap.containsKey(element)) {
				return this.processorMap.get(element);
			}
			return null;
		}
	}

	public TGMidiProcessor getProcessor(){
		synchronized (this.lock) {
			return this.processor;
		}
	}

	public void addOutput(TGAudioProcessor effect){
		synchronized (this.lock) {
			TGSynthLists.addPresent(this.outputs, effect);
		}
	}

	public TGAudioProcessor getOutput( int index ){
		synchronized (this.lock) {
			return TGSynthLists.get(this.outputs, index);
		}
	}

	public int countOutputs(){
		synchronized (this.lock) {
			return this.outputs.size();
		}
	}

	// the chain delayed by the frames, at the volume and balance, written in 16 bits (clipped there) and mixed into the
	// buffer
	public void fillBuffer(TGAudioBuffer buffer, int volume, int balance, int delay) {
		synchronized (this.lock) {
			if( this.isOpen() ){
				this.fillChain(this.block);
				this.delayLine.delay(this.block, delay);
				float[] gains = gains(volume, balance);
				for( int side = 0 ; side < this.block.length ; side ++ ) {
					for( int i = 0 ; i < this.block[side].length ; i ++ ) {
						this.block[side][i] *= gains[side];
					}
				}
				this.buffer.write(this.block);
				buffer.mix(this.buffer);
			}
		}
	}

	// the receiver, then each output in processing order, from silence, in 32-bit float; silence when the receiver is
	// not open
	public void fillChain(float[][] block) {
		synchronized (this.lock) {
			for( float[] side : block ) {
				java.util.Arrays.fill(side, 0f);
			}
			if( this.isOpen() ){
				this.process(block, this.processor);
				for( TGAudioProcessor output : this.outputs ){
					this.process(block, output);
				}
			}
		}
	}

	// the latencies of the enabled processors along the chain; none when the receiver is not open
	public int getLatency() {
		return this.along(TGAudioProcessor::getLatency);
	}

	// the tails of the enabled processors along the chain; none when the receiver is not open
	public int getTail() {
		return this.along(TGAudioProcessor::getTail);
	}

	private int along(ToIntFunction<TGAudioProcessor> frames) {
		synchronized (this.lock) {
			int sum = 0;
			if( this.isOpen() ){
				sum = TGAudioProcessor.along(sum, this.of(this.processor, frames));
				for( TGAudioProcessor output : this.outputs ){
					sum = TGAudioProcessor.along(sum, this.of(output, frames));
				}
			}
			return sum;
		}
	}

	private int of(TGAudioProcessor processor, ToIntFunction<TGAudioProcessor> frames) {
		return (this.isEnabled(processor) ? frames.applyAsInt(processor) : 0);
	}

	private boolean isOpen() {
		return (this.processor != null && this.processor.isOpen());
	}

	private void process(float[][] block, TGAudioProcessor processor) {
		if( this.isEnabled(processor) ) {
			processor.process(block);
		}
	}

	// the left and right gains of a Volume and Balance (0 to 127). Constant-power panning law: use sine/cosine so that
	// the total power (left² + right²) remains constant across all pan positions. At center (balance=63.5) both
	// channels get cos(π/4) ≈ 0.707, whereas a linear law would give 0.5 — a 3 dB centre-hole.
	public static float[] gains(int volume, int balance) {
		float pan = (balance / 127f) * ((float) Math.PI / 2f);
		float gain = volume / 127f;
		return new float[] {gain * (float) Math.cos(pan), gain * (float) Math.sin(pan)};
	}

	public boolean isBusy() {
		synchronized (this.lock) {
			if( this.processor != null && this.processor.isBusy()) {
				return true;
			}
			for(TGAudioProcessor processor : this.outputs) {
				if( processor.isBusy() ) {
					return true;
				}
			}
			return false;
		}
	}

	public boolean isEnabled(TGAudioProcessor processor) {
		synchronized (this.lock) {
			TGProgramElement element = this.getElement(processor);

			return (element != null && element.isEnabled());
		}
	}
}
