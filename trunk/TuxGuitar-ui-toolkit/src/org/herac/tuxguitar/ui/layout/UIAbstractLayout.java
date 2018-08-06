package org.herac.tuxguitar.ui.layout;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;

public abstract class UIAbstractLayout implements UILayout {
	
	public static final String PACKED_WIDTH = "packed_width";
	public static final String PACKED_HEIGHT = "packed_height";
	public static final String MINIMUM_PACKED_WIDTH = "minimum_packed_width";
	public static final String MINIMUM_PACKED_HEIGHT = "minimum_packed_height";
	public static final String MAXIMUM_PACKED_WIDTH = "maximum_packed_width";
	public static final String MAXIMUM_PACKED_HEIGHT = "maximum_packed_height";
	
	private UILayoutAttributes attributes;
	private Map<UIControl, UILayoutAttributes> controlAttributes;
	
	public UIAbstractLayout() {
		this.attributes = new UILayoutAttributes();
		this.controlAttributes = new HashMap<UIControl, UILayoutAttributes>();
	}

	public UILayoutAttributes getAttributes() {
		return attributes;
	}

	public UILayoutAttributes getControlAttributes(UIControl control) {
		if( this.controlAttributes.containsKey(control) ) {
			return this.controlAttributes.get(control);
		}
		this.controlAttributes.put(control, new UILayoutAttributes());
		
		return this.getControlAttributes(control);
	}
	
	public <T extends Object> void set(String key, T value){
		this.getAttributes().set(key, value);
	}
	
	public <T extends Object> void set(UIControl control, String key, T value){
		this.getControlAttributes(control).set(key, value);
	}
	
	public <T extends Object> T get(String key){
		return this.getAttributes().get(key);
	}
	
	public <T extends Object> T get(UIControl control, String key){
		return this.getControlAttributes(control).get(key);
	}
	
	public <T extends Object> T get(String key, T defaultValue){
		T value = this.get(key);
		return (value != null ? value : defaultValue);
	}
	
	public <T extends Object> T get(UIControl control, String key, T defaultValue){
		T value = this.get(control, key);
		return (value != null ? value : defaultValue);
	}
	
	public UISize getPreferredControlSize(UIControl control) {
		Float packedWidth = this.get(control, PACKED_WIDTH);
		Float packedHeight = this.get(control, PACKED_HEIGHT);
		Float minimumPackedWidth = this.get(control, MINIMUM_PACKED_WIDTH);
		Float minimumPackedHeight = this.get(control, MINIMUM_PACKED_HEIGHT);
		Float maximumPackedWidth = this.get(control, MAXIMUM_PACKED_WIDTH);
		Float maximumPackedHeight = this.get(control, MAXIMUM_PACKED_HEIGHT);
		
		UISize packedSize = control.getPackedSize();
		UISize preferredSize = new UISize(packedSize.getWidth(), packedSize.getHeight());
		if( packedWidth != null ) {
			preferredSize.setWidth(packedWidth);
		}
		if( packedHeight != null ) {
			preferredSize.setHeight(packedHeight);
		}
		if( minimumPackedWidth != null && minimumPackedWidth > preferredSize.getWidth()) {
			preferredSize.setWidth(minimumPackedWidth);
		}
		if( minimumPackedHeight != null && minimumPackedHeight > preferredSize.getHeight()) {
			preferredSize.setHeight(minimumPackedHeight);
		}
		if( maximumPackedWidth != null && maximumPackedWidth < preferredSize.getWidth()) {
			preferredSize.setWidth(maximumPackedWidth);
		}
		if( maximumPackedHeight != null && maximumPackedHeight < preferredSize.getHeight()) {
			preferredSize.setHeight(maximumPackedHeight);
		}
		return preferredSize;
	}
	
	public void computeChildPackedSize(UIControl control) {
		Float fixedWidth = this.get(control, PACKED_WIDTH);
		Float fixedHeight = this.get(control, PACKED_HEIGHT);
		
		control.computePackedSize(fixedWidth, fixedHeight);
		
		int tries = 1;
		boolean done = false;
		while(!done && tries < 3) {
			UISize packedSize = control.getPackedSize();
			UISize preferredSize = this.getPreferredControlSize(control);
			
			boolean expectedWidth = (packedSize.getWidth() == preferredSize.getWidth());
			boolean expectedHeight = (packedSize.getHeight() == preferredSize.getHeight());
			
			if((tries ++) > 1 && (!expectedWidth || !expectedHeight)) {
				control.computePackedSize(preferredSize.getWidth(), preferredSize.getHeight());
			} else if(!expectedWidth && !expectedHeight) {
				control.computePackedSize(preferredSize.getWidth(), preferredSize.getHeight());
			} else if(!expectedWidth && expectedHeight) {
				control.computePackedSize(preferredSize.getWidth(), null);
			} else if(expectedWidth && !expectedHeight) {
				control.computePackedSize(null, preferredSize.getHeight());
			} else {
				done = true;
			}
		}
	}
	
	public void computeChildrenPackedSize(UILayoutContainer container) {
		for(UIControl control : container.getChildren()) {			
			this.computeChildPackedSize(control);
		}
	}
	
	public UISize computePackedSize(UILayoutContainer container) {
		this.computeChildrenPackedSize(container);
		
		return this.getComputedPackedSize(container);
	}
	
	public abstract UISize getComputedPackedSize(UILayoutContainer container);
}
