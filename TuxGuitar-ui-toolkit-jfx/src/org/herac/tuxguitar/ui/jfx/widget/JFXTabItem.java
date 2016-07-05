package org.herac.tuxguitar.ui.jfx.widget;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;

import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIPosition;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UITabItem;

public class JFXTabItem extends JFXRegion<Pane> implements JFXContainer<Pane>, UITabItem {
	
	private JFXTabFolder parent;
	private Tab item;
	private UIControl control;
	
	public JFXTabItem(JFXTabFolder parent) {
		super(new Pane(), null);
		
		this.parent = parent;
		this.item = new Tab();
		this.item.setContent(this.getControl());
		this.getControl().setManaged(true);
		this.parent.addTab(this);
	}
	
	public Tab getItem() {
		return item;
	}

	public void addChild(JFXNode<? extends Node> control) {
		final Node handle = control.getControl();
		
		this.control = control;
		this.getControl().getChildren().add(handle);
		
		ChangeListener<Number> resizeListener = new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSize, Number newSize) {
				JFXTabItem.this.onResize();
			}
		};
		this.getControl().widthProperty().addListener(resizeListener);
		this.getControl().heightProperty().addListener(resizeListener);
	}

	public void removeChild(JFXNode<? extends Node> control) {
		this.getControl().getChildren().remove(control.getControl());
		this.control = null;
		this.item.setContent(null);
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
		this.layout(new UIRectangle(new UIPosition(), super.getBounds().getSize()));
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
	
	public void setFocus() {
		if( this.control != null ) {
			this.control.setFocus();
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
		this.parent.removeTab(this);
		
		if( this.control != null && !this.control.isDisposed() ) {
			this.control.dispose();
		}
		this.item = null;
		
		super.dispose();
	}

	public boolean isDisposed() {
		return (this.item == null || super.isDisposed());
	}

	public String getText() {
		return this.item.getText();
	}

	public void setText(String text) {
		this.item.setText(text);
	}
}
