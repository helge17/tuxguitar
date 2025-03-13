package app.tuxguitar.midi.synth.ui;

import app.tuxguitar.ui.widget.UIWindow;

public interface TGAudioProcessorUI {

	String getLabel();

	boolean isOpen();

	void open(UIWindow parent);

	void close();

	void focus();
}
