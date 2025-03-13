package app.tuxguitar.ui.event;

import app.tuxguitar.ui.UIComponent;
import app.tuxguitar.ui.resource.UIKeyCombination;

public class UIKeyEvent extends UIEvent {

	private UIKeyCombination keyCombination;

	public UIKeyEvent(UIComponent control, UIKeyCombination keyCombination) {
		super(control);

		this.keyCombination = keyCombination;
	}

	public UIKeyCombination getKeyCombination() {
		return keyCombination;
	}
}
