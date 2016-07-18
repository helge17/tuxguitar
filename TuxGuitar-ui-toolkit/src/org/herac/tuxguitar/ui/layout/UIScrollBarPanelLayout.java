package org.herac.tuxguitar.ui.layout;

import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UIScrollBar;
import org.herac.tuxguitar.ui.widget.UIScrollBarPanel;

public class UIScrollBarPanelLayout extends UIAbstractLayout {

	public static final String H_SCROLL = "hScroll";
	public static final String V_SCROLL = "vScroll";
	public static final String FILL_WIDTH = "fillWidth";
	public static final String FILL_HEIGHT = "fillHeight";
	public static final String DYNAMIC_PACKED_WIDTH = "dynamicPackedWidth";
	public static final String DYNAMIC_PACKED_HEIGHT = "dynamicPackedHeight";
	
	public UIScrollBarPanelLayout(boolean hScroll, boolean vScroll, boolean fillWidth, boolean fillHeight, boolean dynamicPackedWidth, boolean dynamicPackedHeight) {
		this.set(H_SCROLL, hScroll);
		this.set(V_SCROLL, vScroll);
		this.set(FILL_WIDTH, fillWidth);
		this.set(FILL_HEIGHT, fillHeight);
		this.set(DYNAMIC_PACKED_WIDTH, dynamicPackedWidth);
		this.set(DYNAMIC_PACKED_HEIGHT, dynamicPackedHeight);
	}
	
	public UISize computePackedSize(UILayoutContainer container) {
		UISize packedSize = new UISize();
		UISize packedContentSize = this.getPackedContentSize(container);
		if(!Boolean.TRUE.equals(this.get(DYNAMIC_PACKED_WIDTH))) {
			packedSize.setWidth(packedContentSize.getWidth());
		}
		if(!Boolean.TRUE.equals(this.get(DYNAMIC_PACKED_HEIGHT))) {
			packedSize.setHeight(packedContentSize.getHeight());
		}
		return packedSize;
	}
	
	public void setBounds(UILayoutContainer container, UIRectangle bounds) {
		int hValue = 0;
		int vValue = 0;
		
		UISize packedContentSize = this.getPackedContentSize(container);
		if( Boolean.TRUE.equals(this.get(H_SCROLL))) {
			UIScrollBar uiScrollBar = ((UIScrollBarPanel) container).getHScroll();
			uiScrollBar.setMaximum(Math.max(Math.round(packedContentSize.getWidth() - bounds.getWidth()), 0));
			uiScrollBar.setThumb(Math.round(Math.min(packedContentSize.getWidth(), bounds.getWidth())));
			hValue = uiScrollBar.getValue();
		}
		if( Boolean.TRUE.equals(this.get(V_SCROLL))) {
			UIScrollBar uiScrollBar = ((UIScrollBarPanel) container).getVScroll();
			uiScrollBar.setMaximum(Math.max(Math.round(packedContentSize.getHeight() - bounds.getHeight()), 0));
			uiScrollBar.setThumb(Math.round(Math.min(packedContentSize.getHeight(), bounds.getHeight())));
			vValue = uiScrollBar.getValue();
		}
		
		for(UIControl child : container.getChildren()) {
			UIRectangle childBounds = new UIRectangle();
			childBounds.getPosition().setX(bounds.getX() - hValue);
			childBounds.getPosition().setY(bounds.getY() - vValue);
			childBounds.getSize().setWidth(Boolean.TRUE.equals(this.get(FILL_WIDTH)) ? (bounds.getWidth() + hValue) : child.getPackedSize().getWidth());
			childBounds.getSize().setHeight(Boolean.TRUE.equals(this.get(FILL_HEIGHT)) ? (bounds.getHeight() + vValue) : child.getPackedSize().getHeight());
			
			child.setBounds(childBounds);
		}
	}
	
	public UISize getPackedContentSize(UILayoutContainer container) {
		UISize packedSize = new UISize();
		for(UIControl child : container.getChildren()) {
			packedSize.setWidth(Math.max(packedSize.getWidth(), child.getPackedSize().getWidth()));
			packedSize.setHeight(Math.max(packedSize.getHeight(), child.getPackedSize().getHeight()));
		}
		return packedSize;
	}
}
