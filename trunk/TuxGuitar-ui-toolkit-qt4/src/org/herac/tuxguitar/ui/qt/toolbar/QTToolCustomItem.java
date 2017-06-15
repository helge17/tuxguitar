package org.herac.tuxguitar.ui.qt.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UIFocusGainedListener;
import org.herac.tuxguitar.ui.event.UIFocusLostListener;
import org.herac.tuxguitar.ui.event.UIKeyPressedListener;
import org.herac.tuxguitar.ui.event.UIKeyReleasedListener;
import org.herac.tuxguitar.ui.event.UIMouseDoubleClickListener;
import org.herac.tuxguitar.ui.event.UIMouseDownListener;
import org.herac.tuxguitar.ui.event.UIMouseDragListener;
import org.herac.tuxguitar.ui.event.UIMouseEnterListener;
import org.herac.tuxguitar.ui.event.UIMouseExitListener;
import org.herac.tuxguitar.ui.event.UIMouseMoveListener;
import org.herac.tuxguitar.ui.event.UIMouseUpListener;
import org.herac.tuxguitar.ui.event.UIMouseWheelListener;
import org.herac.tuxguitar.ui.event.UIResizeListener;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.ui.qt.widget.QTContainer;
import org.herac.tuxguitar.ui.qt.widget.QTWidget;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UICursor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.toolbar.UIToolCustomItem;
import org.herac.tuxguitar.ui.widget.UIControl;

import com.trolltech.qt.gui.QWidget;

public class QTToolCustomItem extends QTToolItem<QWidget> implements QTContainer, UIToolCustomItem {
	
	private QTWidget<? extends QWidget> control;
	
	public QTToolCustomItem(QTToolBar parent) {
		super(new QWidget(), parent);
		
		this.setLayoutAttribute(QTToolBar.MANAGED, true);
	}
	
	public QWidget getContainerControl() {
		return this.getControl();
	}
	
	public <T extends Object> void setLayoutAttribute(String key, T value) {
		this.getToolBar().set(this, key, value);
	}
	
	public <T extends Object> T getLayoutAttribute(String key){
		return this.getToolBar().get(this, key);
	}
	
	public void addChild(QTWidget<? extends QWidget> uiControl) {
		if( this.control != null ) {
			this.removeChild(this.control);
		}
		this.control = uiControl;
	}
	
	public void removeChild(QTWidget<? extends QWidget> uiControl) {
		this.control = null;
	}
	
	public List<UIControl> getChildren() {
		List<UIControl> children = new ArrayList<UIControl>();
		if( this.control != null ) {
			children.add(this.control);
		}
		return children;
	}
	
	public void computePackedSize() {
		UISize packedSize = new UISize();
		if( this.control != null ) {
			this.control.computePackedSize();
			
			packedSize.setWidth(this.control.getPackedSize().getWidth());
			packedSize.setHeight(this.control.getPackedSize().getHeight());
		}
		
		Float packedWidth = this.getLayoutAttribute(PACKED_WIDTH);
		Float minimumPackedWidth = this.getLayoutAttribute(MINIMUM_PACKED_WIDTH);
		Float maximumPackedWidth = this.getLayoutAttribute(MAXIMUM_PACKED_WIDTH);
		
		if( packedWidth != null ) {
			packedSize.setWidth(packedWidth);
		}
		if( minimumPackedWidth != null && minimumPackedWidth > packedSize.getWidth()) {
			packedSize.setWidth(minimumPackedWidth);
		}
		if( maximumPackedWidth != null && maximumPackedWidth < packedSize.getWidth()) {
			packedSize.setWidth(maximumPackedWidth);
		}
		this.setPackedSize(packedSize);
	}
	
	public void setBounds(UIRectangle bounds) {
		super.setBounds(bounds);
		
		if( this.control != null ) {
			this.control.setBounds(new UIRectangle(bounds.getSize()));
		}
	}

	public UIColor getBgColor() {
		if( this.control != null ) {
			return this.control.getBgColor();
		}
		return null;
	}

	public void setBgColor(UIColor color) {
		if( this.control != null ) {
			this.control.setBgColor(color);
		}
	}

	public UIColor getFgColor() {
		if( this.control != null ) {
			return this.control.getFgColor();
		}
		return null;
	}

	public void setFgColor(UIColor color) {
		if( this.control != null ) {
			this.control.setFgColor(color);
		}
	}
	
	public UIFont getFont() {
		if( this.control != null ) {
			return this.control.getFont();
		}
		return null;
	}
	
	public void setFont(UIFont font) {
		if( this.control != null ) {
			this.control.setFont(font);
		}
	}
	
	public UICursor getCursor() {
		if( this.control != null ) {
			return this.control.getCursor();
		}
		return null;
	}
	
	public void setCursor(UICursor cursor) {
		if( this.control != null ) {
			this.control.setCursor(cursor);
		}
	}
	
	public void setFocus() {
		if( this.control != null ) {
			this.control.setFocus();
		}
	}
	
	public void redraw() {
		if( this.control != null ) {
			this.control.redraw();
		}
	}

	public UIPopupMenu getPopupMenu() {
		if( this.control != null ) {
			return this.control.getPopupMenu();
		}
		return null;
	}

	public void setPopupMenu(UIPopupMenu menu) {
		if( this.control != null ) {
			this.control.setPopupMenu(menu);
		}
	}
	
	public <Data> Data getData(String key) {
		if( this.control != null ) {
			this.control.getData(key);
		}
		return null;
	}
	
	public <Data> void setData(String key, Data data) {
		if( this.control != null ) {
			this.control.setData(key, data);
		}
	}
	
	public void dispose() {
		if( this.control != null ) {
			this.control.dispose();
		}
		
		super.dispose();
	}
	
	public void addDisposeListener(UIDisposeListener listener) {
		if( this.control != null ) {
			this.control.addDisposeListener(listener);
		}
	}

	public void removeDisposeListener(UIDisposeListener listener) {
		if( this.control != null ) {
			this.control.removeDisposeListener(listener);
		}
	}
	
	public void addResizeListener(UIResizeListener listener) {
		if( this.control != null ) {
			this.control.addResizeListener(listener);
		}
	}

	public void removeResizeListener(UIResizeListener listener) {
		if( this.control != null ) {
			this.control.removeResizeListener(listener);
		}
	}
	
	public void addMouseUpListener(UIMouseUpListener listener) {
		if( this.control != null ) {
			this.control.addMouseUpListener(listener);
		}
	}

	public void removeMouseUpListener(UIMouseUpListener listener) {
		if( this.control != null ) {
			this.control.removeMouseUpListener(listener);
		}
	}

	public void addMouseDownListener(UIMouseDownListener listener) {
		if( this.control != null ) {
			this.control.addMouseDownListener(listener);
		}
	}

	public void removeMouseDownListener(UIMouseDownListener listener) {
		if( this.control != null ) {
			this.control.removeMouseDownListener(listener);
		}
	}

	public void addMouseMoveListener(UIMouseMoveListener listener) {
		if( this.control != null ) {
			this.control.addMouseMoveListener(listener);
		}
	}

	public void removeMouseMoveListener(UIMouseMoveListener listener) {
		if( this.control != null ) {
			this.control.removeMouseMoveListener(listener);
		}
	}

	public void addMouseDoubleClickListener(UIMouseDoubleClickListener listener) {
		if( this.control != null ) {
			this.control.addMouseDoubleClickListener(listener);
		}
	}

	public void removeMouseDoubleClickListener(UIMouseDoubleClickListener listener) {
		if( this.control != null ) {
			this.control.removeMouseDoubleClickListener(listener);
		}
	}

	public void addMouseDragListener(UIMouseDragListener listener) {
		if( this.control != null ) {
			this.control.addMouseDragListener(listener);
		}
	}

	public void removeMouseDragListener(UIMouseDragListener listener) {
		if( this.control != null ) {
			this.control.removeMouseDragListener(listener);
		}
	}
	
	public void addMouseWheelListener(UIMouseWheelListener listener) {
		if( this.control != null ) {
			this.control.addMouseWheelListener(listener);
		}
	}

	public void removeMouseWheelListener(UIMouseWheelListener listener) {
		if( this.control != null ) {
			this.control.removeMouseWheelListener(listener);
		}
	}
	
	public void addMouseEnterListener(UIMouseEnterListener listener) {
		if( this.control != null ) {
			this.control.addMouseEnterListener(listener);
		}
	}

	public void removeMouseEnterListener(UIMouseEnterListener listener) {
		if( this.control != null ) {
			this.control.removeMouseEnterListener(listener);
		}
	}
	
	public void addMouseExitListener(UIMouseExitListener listener) {
		if( this.control != null ) {
			this.control.addMouseExitListener(listener);
		}
	}

	public void removeMouseExitListener(UIMouseExitListener listener) {
		if( this.control != null ) {
			this.control.removeMouseExitListener(listener);
		}
	}
	
	public void addKeyPressedListener(UIKeyPressedListener listener) {
		if( this.control != null ) {
			this.control.addKeyPressedListener(listener);
		}
	}

	public void removeKeyPressedListener(UIKeyPressedListener listener) {
		if( this.control != null ) {
			this.control.removeKeyPressedListener(listener);
		}
	}
	
	public void addKeyReleasedListener(UIKeyReleasedListener listener) {
		if( this.control != null ) {
			this.control.addKeyReleasedListener(listener);
		}
	}

	public void removeKeyReleasedListener(UIKeyReleasedListener listener) {
		if( this.control != null ) {
			this.control.removeKeyReleasedListener(listener);
		}
	}
	
	public void addFocusGainedListener(UIFocusGainedListener listener) {
		if( this.control != null ) {
			this.control.addFocusGainedListener(listener);
		}
	}

	public void removeFocusGainedListener(UIFocusGainedListener listener) {
		if( this.control != null ) {
			this.control.removeFocusGainedListener(listener);
		}
	}
	
	public void addFocusLostListener(UIFocusLostListener listener) {
		if( this.control != null ) {
			this.control.addFocusLostListener(listener);
		}
	}

	public void removeFocusLostListener(UIFocusLostListener listener) {
		if( this.control != null ) {
			this.control.removeFocusLostListener(listener);
		}
	}
}
