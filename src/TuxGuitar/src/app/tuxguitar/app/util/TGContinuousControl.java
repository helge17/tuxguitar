package app.tuxguitar.app.util;

import app.tuxguitar.util.TGContext;

public interface TGContinuousControl {

	// action to be done when continuous control is stable
	public void doActionWhenStable();

	public TGContext getContext();
}
