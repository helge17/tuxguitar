package org.herac.tuxguitar.android.action;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;

import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public class TGActionProcessorListener extends TGActionProcessor implements OnClickListener, OnLongClickListener, OnMenuItemClickListener{
	
	public static final String PROPERTY_EVENT_SOURCE = "eventSource";
	
	public TGActionProcessorListener(TGContext context, String actionName){
		super(context, actionName);
	}
	
	public void processEvent(Object eventSource, Map<String, Object> attributes) {
		this.processOnNewThread(this.processEventAttributes(eventSource, attributes));
	}
	
	public Map<String, Object> processEventAttributes(Object eventSource, Map<String, Object> attributes){
		Map<String, Object> eventAttributes = new HashMap<String, Object>();
		eventAttributes.put(PROPERTY_EVENT_SOURCE, eventSource);
		if( attributes != null ) {
			eventAttributes.putAll(attributes);
		}
		return eventAttributes;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void onClick(View view) {
		this.processEvent(view, ( view.getTag() instanceof Map ? (Map<String, Object>) view.getTag() : null ));
	}
	
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		this.processEvent(item, null);
		
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean onLongClick(View view) {
		this.processEvent(view, ( view.getTag() instanceof Map ? (Map<String, Object>) view.getTag() : null ));
		
		return true;
	}
}
