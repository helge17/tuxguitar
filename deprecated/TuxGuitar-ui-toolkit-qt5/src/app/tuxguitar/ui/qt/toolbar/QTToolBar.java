package app.tuxguitar.ui.qt.toolbar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.tuxguitar.ui.UIComponent;
import app.tuxguitar.ui.layout.UILayoutAttributes;
import app.tuxguitar.ui.qt.event.QTEventHandler;
import app.tuxguitar.ui.qt.widget.QTAbstractContainer;
import app.tuxguitar.ui.qt.widget.QTContainer;
import app.tuxguitar.ui.qt.widget.QTWidget;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.resource.UISize;
import app.tuxguitar.ui.toolbar.UIToolActionItem;
import app.tuxguitar.ui.toolbar.UIToolActionMenuItem;
import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.ui.toolbar.UIToolCheckableItem;
import app.tuxguitar.ui.toolbar.UIToolCustomItem;
import app.tuxguitar.ui.toolbar.UIToolItem;
import app.tuxguitar.ui.toolbar.UIToolMenuItem;
import app.tuxguitar.ui.widget.UIControl;
import org.qtjambi.qt.core.QEvent;
import org.qtjambi.qt.core.QEvent.Type;
import org.qtjambi.qt.core.QRect;
import org.qtjambi.qt.core.QSize;
import org.qtjambi.qt.core.Qt.Orientation;
import org.qtjambi.qt.widgets.QAction;
import org.qtjambi.qt.widgets.QToolBar;
import org.qtjambi.qt.widgets.QWidget;

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
			public boolean handle(QEvent event) {
				QTToolBar.this.layout();
				return true;
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
		this.computePackedSize(null, null);
		this.setBounds(bounds);
	}

	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		this.computeIconSize();

		for(UIControl uiControl : this.getChildren()) {
			uiControl.computePackedSize(null, null);
		}
		super.computePackedSize(fixedWidth, fixedHeight);
	}

	public void computeIconSize() {
		QSize iconSize = new QSize();
		for(UIControl control : this.getChildren()) {
			if( control instanceof UIToolItem ) {
				UIImage image = ((UIToolItem) control).getImage();
				if( image != null ) {
					iconSize.setWidth(Math.max(iconSize.width(), Math.round(image.getWidth())));
					iconSize.setHeight(Math.max(iconSize.height(), Math.round(image.getHeight())));
				}
			}
		}
		if(!this.getControl().iconSize().equals(iconSize)) {
			this.getControl().setIconSize(iconSize);
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
