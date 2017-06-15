package org.herac.tuxguitar.ui.qt.toolbar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.layout.UILayoutAttributes;
import org.herac.tuxguitar.ui.qt.event.QTEventHandler;
import org.herac.tuxguitar.ui.qt.widget.QTAbstractContainer;
import org.herac.tuxguitar.ui.qt.widget.QTContainer;
import org.herac.tuxguitar.ui.qt.widget.QTWidget;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolActionMenuItem;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.toolbar.UIToolCheckableItem;
import org.herac.tuxguitar.ui.toolbar.UIToolCustomItem;
import org.herac.tuxguitar.ui.toolbar.UIToolMenuItem;
import org.herac.tuxguitar.ui.widget.UIControl;

import com.trolltech.qt.core.QEvent;
import com.trolltech.qt.core.QEvent.Type;
import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.Qt.Orientation;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QToolBar;
import com.trolltech.qt.gui.QWidget;

public class QTToolBar extends QTAbstractContainer<QToolBar> implements QTContainer, UIToolBar {
	
	public static final int ITEM_MARGIN = 1;
	public static final String MANAGED = "managed";
	
	private Map<UIControl, UILayoutAttributes> controlAttributes;
	
	public QTToolBar(QTContainer container, Orientation orientation) {
		super(new QToolBar(container.getContainerControl()), container);
		
		this.controlAttributes = new HashMap<UIControl, UILayoutAttributes>();
		
		this.getControl().setOrientation(orientation);
		this.getControl().setMovable(false);
		this.getControl().setFloatable(false);
		
		this.getEventFilter().connect(Type.LayoutRequest, new QTEventHandler() {
			public void handle(QEvent event) {
				QTToolBar.this.layout();
			}
		});
	}
	
	public QWidget getContainerControl() {
		return this.getControl();
	}
	
	public UIComponent createSeparator() {
		return new QTToolSeparatorItem(this);
	}
	
	public UIToolActionItem createActionItem() {
		return new QTToolActionItem(this);
	}
	
	public UIToolCheckableItem createCheckItem() {
		return new QTToolCheckableItem(this);
	}
	
	public UIToolMenuItem createMenuItem() {
		return new QTToolMenuItem(this);
	}
	
	public UIToolActionMenuItem createActionMenuItem() {
		return new QTToolActionMenuItem(this);
	}
	
	public UIToolCustomItem createCustomItem() {
		return new QTToolCustomItem(this);
	}
	
	public boolean hasWidget(QWidget widget) {
		for(QAction action : this.getControl().actions()) {
			QWidget widgetForAction = this.getControl().widgetForAction(action);
			if( widgetForAction != null && widgetForAction.equals(widget) ) {
				return true;
			}
		}
		return false;
	}
	
	public void addChild(QTWidget<? extends QWidget> uiControl) {
		super.addChild(uiControl);
		
		if(!this.hasWidget(uiControl.getControl())) {
			this.getControl().addWidget(uiControl.getControl());
		}
	}
	
	public UILayoutAttributes getControlAttributes(UIControl control) {
		if( this.controlAttributes.containsKey(control) ) {
			return this.controlAttributes.get(control);
		}
		this.controlAttributes.put(control, new UILayoutAttributes());
		
		return this.getControlAttributes(control);
	}
	
	public <T extends Object> void set(UIControl control, String key, T value){
		this.getControlAttributes(control).set(key, value);
	}
	
	public <T extends Object> T get(UIControl control, String key){
		return this.getControlAttributes(control).get(key);
	}
	
	public <T extends Object> T get(UIControl control, String key, T defaultValue){
		T value = this.get(control, key);
		return (value != null ? value : defaultValue);
	}
	
	public void layout() {
		this.layout(this.getBounds());
	}

	public void layout(UIRectangle bounds) {
		this.computePackedSize();
		this.setBounds(bounds);
	}
	
	public void computePackedSize() {
		super.computePackedSize();
		
		for(UIControl uiControl : this.getChildren()) {
			uiControl.computePackedSize();
		}
	}

	public void setBounds(UIRectangle bounds) {
		super.setBounds(bounds);
		
		List<UIControl> children = this.getChildren();
		
		int itemsToFill = 0;
		float availableWidth = (this.findSize(this.getControl().contentsRect()) - (ITEM_MARGIN * (children.size() - 1)));
		float itemsWidth = 0;
		float itemsHeight = 0;
		for(UIControl control : this.getChildren()) {
			UISize packedSize = control.getPackedSize();
			
			itemsWidth += this.findSize(packedSize);
			itemsHeight = Math.max(itemsHeight, this.findHeight(packedSize));
			
			if( Boolean.TRUE.equals(this.get(control, MANAGED)) && Boolean.TRUE.equals(this.get(control, UIToolCustomItem.FILL))) {
				itemsToFill ++;
			}
		}
		
		float position = 0;
		float widthToFill = (itemsToFill > 0 && availableWidth > itemsWidth ? ((availableWidth - itemsWidth) / itemsToFill) : 0);
		for(UIControl control : this.getChildren()) {
			UIRectangle controlBounds = control.getBounds();
			
			if( Boolean.TRUE.equals(this.get(control, MANAGED))) {
				float controlWidth = this.findSize(control.getPackedSize());
				
				if( Boolean.TRUE.equals(this.get(control, UIToolCustomItem.FILL))) {
					controlWidth += widthToFill;
				}
				
				controlBounds.getSize().setWidth(this.isHorizontal() ? Math.round(controlWidth) : itemsHeight);
				controlBounds.getSize().setHeight(this.isHorizontal() ? itemsHeight : Math.round(controlWidth));
				
				control.setBounds(controlBounds);
			}
			
			controlBounds.getPosition().setX(this.isHorizontal() ? position : controlBounds.getPosition().getX());
			controlBounds.getPosition().setY(this.isHorizontal() ? controlBounds.getPosition().getY() : position);
			control.setBounds(controlBounds);
			
			position += (this.findSize(controlBounds.getSize()) + ITEM_MARGIN);
		}
	}
	
	public boolean isHorizontal() {
		return Orientation.Horizontal.equals(this.getControl().orientation());
	}
	
	private int findSize(QRect r) {
		return (this.isHorizontal() ? r.width() : r.height());
	}
	
	private float findSize(UISize r) {
		return (this.isHorizontal() ? r.getWidth() : r.getHeight());
	}
	
	private float findHeight(UISize r) {
		return (this.isHorizontal() ? r.getHeight() : r.getWidth());
	}
}
