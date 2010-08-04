package org.herac.tuxguitar.app.tools.custom.tuner;

public interface TGTunerListener {

	public void fireFrequency(double freq);
	
	public int[] getTuning();
	
	public void fireException(Exception ex);
	
	public void fireCurrentString(int string);
	
}
