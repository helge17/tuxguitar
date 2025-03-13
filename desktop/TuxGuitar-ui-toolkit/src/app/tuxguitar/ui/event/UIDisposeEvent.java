package app.tuxguitar.ui.event;
import app.tuxguitar.ui.UIComponent;

public class UIDisposeEvent extends UIEvent {

	public UIDisposeEvent(UIComponent control) {
		super(control);
	}
}
