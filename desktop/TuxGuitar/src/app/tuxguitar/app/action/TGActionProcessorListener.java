package app.tuxguitar.app.action;

import java.util.HashMap;
import java.util.Map;

import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.ui.UIComponent;
import app.tuxguitar.ui.event.UICloseEvent;
import app.tuxguitar.ui.event.UICloseListener;
import app.tuxguitar.ui.event.UIEvent;
import app.tuxguitar.ui.event.UIMouseDoubleClickListener;
import app.tuxguitar.ui.event.UIMouseDownListener;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.event.UIMouseUpListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.widget.UIRadioButton;
import app.tuxguitar.util.TGContext;

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
