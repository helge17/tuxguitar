package org.herac.tuxguitar.ui.event;
import org.herac.tuxguitar.ui.UIComponent;

public class UILinkEvent extends UIEvent {
	
	private String link;
	
	public UILinkEvent(UIComponent control, String link) {
		super(control);
		
		this.link = link;
	}

	public String getLink() {
		return link;
	}
}
