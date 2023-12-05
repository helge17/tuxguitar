package org.herac.tuxguitar.midi.synth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.herac.tuxguitar.util.TGContext;

public class TGSynthChannelProcessor {
	
	private TGAudioBuffer buffer;
	private TGMidiProcessor processor;
	private List<TGAudioProcessor> outputs;
	private Map<TGProgramElement, TGAudioProcessor> processorMap;
	private Object lock;
	
	public TGSynthChannelProcessor() {
		this.lock = new Object();
		this.buffer = new TGAudioBuffer();
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
			if( effect != null ){
				this.outputs.add( effect );
			}
		}
	}
	
	public TGAudioProcessor getOutput( int index ){
		synchronized (this.lock) {
			if( index >= 0 && index < this.outputs.size() ){
				return (TGAudioProcessor)this.outputs.get( index );
			}
			return null;
		}
	}
	
	public int countOutputs(){
		synchronized (this.lock) {
			return this.outputs.size();
		}
	}
	
	public void fillBuffer(TGAudioBuffer buffer, int volume, int balance) {
		synchronized (this.lock) {
			if( this.processor != null && this.processor.isOpen() ){
				this.buffer.clear();
				this.fillBuffer(this.buffer, this.processor);
				for( int i = 0 ; i < this.countOutputs() ;i ++){
					this.fillBuffer(this.buffer, this.getOutput(i));
				}
				this.balance(this.buffer, volume, balance);
				
				buffer.mix(this.buffer);
			}
		}
	}
	
	public void fillBuffer(TGAudioBuffer buffer, TGAudioProcessor processor) {
		synchronized (this.lock) {
			if( this.isEnabled(processor) ) {
				processor.fillBuffer(buffer);
			}
		}
	}
	
	public void balance(TGAudioBuffer buffer, int volume, int balance) {
		synchronized (this.lock) {
			buffer.balance(new float[] {
				// left channel
				(volume / 127f) * ((127 - balance) / 127f),
				// right channel
				(volume / 127f) * ((balance) / 127f)
			});
		}
	}
	
	public boolean isBusy() {
		synchronized (this.lock) {
			if( this.processor != null && this.processor.isBusy()) {
				return true;
			}
			for(TGAudioProcessor procesor : this.outputs) {
				if( procesor.isBusy() ) {
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
