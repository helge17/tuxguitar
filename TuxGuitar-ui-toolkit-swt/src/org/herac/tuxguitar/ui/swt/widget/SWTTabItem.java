package org.herac.tuxguitar.ui.swt.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UITabItem;

public class SWTTabItem extends SWTControl<CTabFolder> implements SWTContainer<CTabFolder>, UITabItem {
	
	private SWTTabFolder parent;
	private CTabItem item;
	private UIControl control;
	
	public SWTTabItem(CTabItem item, SWTTabFolder parent) {
		super(parent.getControl(), null);
		
		this.parent = parent;
		this.item = item;
	}
	
	public CTabItem getItem() {
		return item;
	}

	@SuppressWarnings("unchecked")
	public void addChild(UIControl control) {
		Control handle = ((SWTControl<? extends Control>) control).getControl();
		
		this.control = control;
		this.item.setControl(handle);
		
		handle.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				onResize();
			}
		});
		handle.getDisplay().asyncExec(new Runnable() {
			public void run() {
				onResize();
			}
		});
	}

	public void removeChild(UIControl uiControl) {
		this.control = null;
		this.item.setControl(null);
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
		this.setPackedSize(packedSize);
	}

	public boolean isEnabled() {
		if( this.control != null ) {
			return this.control.isEnabled();
		}
		return false;
	}

	public void setEnabled(boolean enabled) {
		if( this.control != null ) {
			this.control.setEnabled(enabled);
		}
	}

	public boolean isVisible() {
		if( this.control != null ) {
			return this.control.isVisible();
		}
		return false;
	}

	public void setVisible(boolean visible) {
		if( this.control != null ) {
			this.control.setVisible(visible);
		}
	}

	public UISize getPackedSize() {
		if( this.control != null ) {
			return this.control.getPackedSize();
		}
		return new UISize();
	}

	public void setPackedSize(UISize size) {
		if( this.control != null ) {
			this.control.setPackedSize(size);
		}
	}

	public UIRectangle getBounds() {
		if( this.control != null ) {
			return this.control.getBounds();
		}
		return new UIRectangle();
	}
	
	public void setBounds(UIRectangle bounds) {
		if( this.control != null ) {
			this.control.setBounds(bounds);
		}
	}
	
	public void layout(UIRectangle bounds) {
		this.computePackedSize();
		this.setBounds(bounds);
	}
	
	public void onSelect() {
		this.onResize();
	}
	
	public void onResize() {
		if( this.control != null ) {
			this.layout(this.getBounds());
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
	
	public void dispose() {
		this.parent.dispose(this);
	}
	
	public void disposeControl() {
		if(!this.item.isDisposed()) {
			this.item.dispose();
		}
		if( this.control != null && !this.control.isDisposed() ) {
			this.control.dispose();
		}
	}

	public boolean isControlDisposed() {
		return this.item.isDisposed();
	}

	public String getText() {
		return this.item.getText();
	}

	public void setText(String text) {
		this.item.setText(text);
	}
}
