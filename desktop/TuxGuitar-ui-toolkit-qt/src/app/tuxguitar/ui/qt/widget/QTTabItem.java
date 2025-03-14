package app.tuxguitar.ui.qt.widget;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.ui.event.UIResizeEvent;
import app.tuxguitar.ui.event.UIResizeListener;
import app.tuxguitar.ui.menu.UIPopupMenu;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIPosition;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.resource.UISize;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.ui.widget.UITabItem;
import io.qt.widgets.QWidget;

public class QTTabItem extends QTWidget<QWidget> implements QTContainer, UITabItem {

	private QTTabFolder parent;
	private UIControl control;

	public QTTabItem(QTTabFolder parent) {
		super(new QWidget(), null);

		this.parent = parent;
		this.parent.addTab(this);

		this.addResizeListener(new UIResizeListener() {
			public void onResize(UIResizeEvent event) {
				QTTabItem.this.onResize();
			}
		});
	}

	public QWidget getContainerControl() {
		return this.getControl();
	}

	public void addChild(QTWidget<? extends QWidget> control) {
		this.control = control;
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

	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		UISize packedSize = new UISize();
		if( this.control != null ) {
			this.control.computePackedSize(fixedWidth, fixedHeight);

			packedSize.setWidth(this.control.getPackedSize().getWidth());
			packedSize.setHeight(this.control.getPackedSize().getHeight());
		}
		if( fixedWidth != null ) {
			packedSize.setWidth(fixedWidth);
		}
		if( fixedHeight != null ) {
			packedSize.setHeight(fixedHeight);
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
		this.computePackedSize(null, null);
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

		super.dispose();
	}

	public String getText() {
		return this.parent.getControl().tabText(this.parent.getTabIndex(this));
	}

	public void setText(String text) {
		this.parent.getControl().setTabText(this.parent.getTabIndex(this), text);
	}
}
