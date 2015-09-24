package org.herac.tuxguitar.android.navigation;

import org.herac.tuxguitar.event.TGEvent;

public class TGNavigationEvent extends TGEvent {
	
	public static final String EVENT_TYPE = "ui-navigation";
	public static final String PROPERTY_LOADED_FRAGMENT = "loadedFragment";
	public static final String PROPERTY_BACK_FROM = "backFrom";
	
	public TGNavigationEvent(TGNavigationFragment fragment, TGNavigationFragment backFrom) {
		super(EVENT_TYPE);
		
		this.setAttribute(PROPERTY_LOADED_FRAGMENT, fragment);
		this.setAttribute(PROPERTY_BACK_FROM, backFrom);
	}
}
