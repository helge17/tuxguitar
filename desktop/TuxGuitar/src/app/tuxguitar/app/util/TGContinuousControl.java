package org.herac.tuxguitar.app.util;

import org.herac.tuxguitar.util.TGContext;

public interface TGContinuousControl {

	// action to be done when continuous control is stable
	public void doActionWhenStable();

	public TGContext getContext();
}
