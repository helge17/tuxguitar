package app.tuxguitar.midi.synth;

import java.util.ArrayList;
import java.util.List;

public class TGProgram {

	private TGProgramElement receiver;
	private List<TGProgramElement> outputs;

	public TGProgram(){
		this.receiver = null;
		this.outputs = new ArrayList<TGProgramElement>();
	}

	public TGProgramElement getReceiver(){
		return this.receiver;
	}

	public void setReceiver(TGProgramElement receiver){
		this.receiver = receiver;
	}

	public void addOutput(TGProgramElement output){
		TGSynthLists.addPresent(this.outputs, output);
	}

	// the element given, or one with its id; null or an element not there changes nothing
	public void removeOutput(TGProgramElement output){
		this.outputs.remove(output);
	}

	public void moveOutputUp(TGProgramElement output){
		int index = this.outputs.indexOf(output);
		if( index >= 1 ){
			this.outputs.remove(output);
			this.outputs.add(index - 1, output);
		}
	}

	public void moveOutputDown(TGProgramElement output){
		int index = this.outputs.indexOf(output);
		if( index >= 0 && index < (this.outputs.size() - 1)){
			this.outputs.remove(output);
			this.outputs.add(index + 1, output);
		}
	}

	public TGProgramElement getOutput(int index){
		return TGSynthLists.get(this.outputs, index);
	}

	public int countOutputs(){
		return this.outputs.size();
	}

	public void copyFrom(TGProgram program) {
		this.receiver = null;
		this.outputs.clear();

		if( program.getReceiver() != null ) {
			this.receiver = new TGProgramElement();
			this.receiver.copyFrom(program.getReceiver());
		}

		int outputs = program.countOutputs();
		for(int i = 0 ; i < outputs; i ++) {
			TGProgramElement output = new TGProgramElement();
			output.copyFrom(program.getOutput(i));
			this.addOutput(output);
		}
	}
}
