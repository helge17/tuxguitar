package org.herac.tuxguitar.midi.synth.ui;

import org.herac.tuxguitar.ui.widget.UIWindow;

public interface TGAudioProcessorUI {
	
	String getLabel();
	
	boolean isOpen();
	
	void open(UIWindow parent);
	
	void close();
}
