package org.herac.tuxguitar.android.fragment;

import android.app.Fragment;

import org.herac.tuxguitar.event.TGEvent;

public class TGFragmentEvent extends TGEvent {
	
	public static final String EVENT_TYPE = "ui-fragment";
	public static final String ATTRIBUTE_FRAGMENT = "fragment";
	public static final String ATTRIBUTE_ACTION = "action";
	
	public static final String ACTION_CREATED = "onCreate";
	public static final String ACTION_VIEW_CREATED = "onCreateView";
	public static final String ACTION_OPTIONS_MENU_CREATED = "onCreateOptionsMenu";
	
	public TGFragmentEvent(Fragment fragment, String action) {
		super(EVENT_TYPE);
		
		this.setAttribute(ATTRIBUTE_FRAGMENT, fragment);
		this.setAttribute(ATTRIBUTE_ACTION, action);
	}
}
