package org.herac.tuxguitar.app.action;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.event.UICloseEvent;
import org.herac.tuxguitar.ui.event.UICloseListener;
import org.herac.tuxguitar.ui.event.UIEvent;
import org.herac.tuxguitar.ui.event.UIMouseDoubleClickListener;
import org.herac.tuxguitar.ui.event.UIMouseDownListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.widget.UIRadioButton;
import org.herac.tuxguitar.util.TGContext;

public class TGActionProcessorListener extends TGActionProcessor implements UISelectionListener, UIMouseUpListener, UIMouseDownListener, UIMouseDoubleClickListener, UICloseListener {
	
	public static final String PROPERTY_UI_EVENT = UIEvent.class.getName();
	public static final String PROPERTY_ACTION_ATTRIBUTES = "actionAttributes";
	
	public TGActionProcessorListener(TGContext context, String actionName){
		super(context, actionName);
	}
	
	public void processEvent(UIEvent event) {
		this.processOnNewThread(this.createWidgetAttributes(event));
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> createWidgetAttributes(UIEvent event){
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put(PROPERTY_UI_EVENT, event);
		
		Object widgetAttributes = (event.getComponent() != null ? event.getComponent().getData(PROPERTY_ACTION_ATTRIBUTES) : null);
		if( widgetAttributes instanceof Map ){
			attributes.putAll((Map<String, Object>) widgetAttributes);
		}
		return attributes;
	}
	
	public void onSelect(UISelectionEvent event) {
		UIComponent uiComponent = event.getComponent();
		if( uiComponent instanceof UIRadioButton && !((UIRadioButton) uiComponent).isSelected() ){
			return;
		}
		this.processEvent(event);
	}
	
	public void onMouseDoubleClick(UIMouseEvent event) {
		this.processEvent(event);
	}

	public void onMouseDown(UIMouseEvent event) {
		this.processEvent(event);
	}

	public void onMouseUp(UIMouseEvent event) {
		this.processEvent(event);
	}
	
	public void onClose(UICloseEvent event) {
		this.processEvent(event);
	}
}
