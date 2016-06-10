package org.herac.tuxguitar.ui.event;
import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.resource.UIPosition;

public class UIMouseEvent extends UIEvent {
	
	private Integer button;
	private UIPosition position;
	
	public UIMouseEvent(UIComponent control, UIPosition position, Integer button) {
		super(control);
		
		this.button = button;
		this.position = position;
	}

	public UIPosition getPosition() {
		return position;
	}

	public Integer getButton() {
		return button;
	}
}
