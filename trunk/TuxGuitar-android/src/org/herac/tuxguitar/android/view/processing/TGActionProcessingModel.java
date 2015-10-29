package org.herac.tuxguitar.android.view.processing;

public class TGActionProcessingModel {
	
	private boolean processing;
	private long processingTime;
	
	public TGActionProcessingModel(){
		super();
	}
	
	public void update(boolean processing) {
		this.processing = processing;
		this.processingTime = System.currentTimeMillis();
	}

	public boolean isProcessing() {
		return processing;
	}

	public long getProcessingTime() {
		return processingTime;
	}
}
