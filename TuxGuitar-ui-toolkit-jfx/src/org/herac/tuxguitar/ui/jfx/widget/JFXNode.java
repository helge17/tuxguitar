package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

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
import org.herac.tuxguitar.ui.jfx.event.JFXDisposeListenerManager;
import org.herac.tuxguitar.ui.jfx.event.JFXFocusListenerManager;
import org.herac.tuxguitar.ui.jfx.event.JFXKeyPressedListenerManager;
import org.herac.tuxguitar.ui.jfx.event.JFXKeyReleasedListenerManager;
import org.herac.tuxguitar.ui.jfx.event.JFXMouseDoubleClickListenerManager;
import org.herac.tuxguitar.ui.jfx.event.JFXMouseDownListenerManager;
import org.herac.tuxguitar.ui.jfx.event.JFXMouseDragListenerManager;
import org.herac.tuxguitar.ui.jfx.event.JFXMouseEnterListenerManager;
import org.herac.tuxguitar.ui.jfx.event.JFXMouseExitListenerManager;
import org.herac.tuxguitar.ui.jfx.event.JFXMouseMoveListenerManager;
import org.herac.tuxguitar.ui.jfx.event.JFXMouseUpListenerManager;
import org.herac.tuxguitar.ui.jfx.event.JFXMouseWheelListenerManager;
import org.herac.tuxguitar.ui.jfx.resource.JFXCursor;
import org.herac.tuxguitar.ui.menu.UIPopupMenu;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UICursor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIControl;

public abstract class JFXNode<T extends Node> extends JFXEventReceiver<T> implements UIControl {
	
	private JFXContainer<? extends Region> parent;
	
	private JFXDisposeListenerManager disposeListener;
	private JFXFocusListenerManager focusListener;
	private JFXKeyPressedListenerManager keyPressedListener;
	private JFXKeyReleasedListenerManager keyReleasedListener;
	private JFXMouseUpListenerManager mouseUpListener;
	private JFXMouseDownListenerManager mouseDownListener;
	private JFXMouseDoubleClickListenerManager mouseDoubleClickListener;
	private JFXMouseMoveListenerManager mouseMoveListener;
	private JFXMouseDragListenerManager mouseDragListener;
	private JFXMouseEnterListenerManager mouseEnterListener;
	private JFXMouseExitListenerManager mouseExitListener;
	private JFXMouseWheelListenerManager mouseWheelListener;
	
	private String toolTipText;
	private UISize packedSize;
	private UIColor bgColor;
	private UIColor fgColor;
	private UIFont font;
	private UICursor cursor;
	private UIPopupMenu popupMenu;
	
	public JFXNode(T control, JFXContainer<? extends Region> parent) {
		super(control);
		
		this.parent = parent;
		if( this.parent != null ) {
			this.parent.addChild(this);
		}
		this.packedSize = new UISize();
		this.disposeListener = new JFXDisposeListenerManager(this);
		this.focusListener = new JFXFocusListenerManager(this);
		this.keyPressedListener = new JFXKeyPressedListenerManager(this);
		this.keyReleasedListener = new JFXKeyReleasedListenerManager(this);
		this.mouseUpListener = new JFXMouseUpListenerManager(this);
		this.mouseDownListener = new JFXMouseDownListenerManager(this);
		this.mouseDoubleClickListener = new JFXMouseDoubleClickListenerManager(this);
		this.mouseMoveListener = new JFXMouseMoveListenerManager(this);
		this.mouseWheelListener = new JFXMouseWheelListenerManager(this);
		this.mouseDragListener = new JFXMouseDragListenerManager(this);
		this.mouseEnterListener = new JFXMouseEnterListenerManager(this);
		this.mouseExitListener = new JFXMouseExitListenerManager(this);
		
		this.getControl().setManaged(false);
		this.getControl().getStyleClass().add(this.getClass().getSimpleName());
		this.getControl().applyCss();
	}
	
	public UIControl getParent() {
		return this.parent;
	}
	
	public void setPackedSize(UISize packedSize) {
		this.packedSize.setWidth(packedSize.getWidth());
		this.packedSize.setHeight(packedSize.getHeight());
	}

	public UISize getPackedSize() {
		return new UISize(this.packedSize.getWidth(), this.packedSize.getHeight());
	}

	public void computePackedSize(float wHint, float hHint) {
		this.packedSize.setWidth((float) this.getControl().prefWidth(hHint));
		this.packedSize.setHeight((float) this.getControl().prefHeight(wHint));
	}
	
	public void computePackedSize() {
		this.computePackedSize((float) Region.USE_COMPUTED_SIZE, (float) Region.USE_COMPUTED_SIZE);
	}

	public void dispose() {
		if( this.parent != null ) {
			this.parent.removeChild(this);
		}
		this.disposeListener.fireEvent();
		
		super.dispose();
	}
	
	public boolean isEnabled() {
		return !this.getControl().isDisable();
	}

	public void setEnabled(boolean enabled) {
		this.getControl().setDisable(!enabled);
	}

	public boolean isVisible() {
		return this.getControl().isVisible();
	}

	public void setVisible(boolean visible) {
		this.getControl().setVisible(visible);
	}
	
	public String getToolTipText() {
		return this.toolTipText;
	}

	public void setToolTipText(String toolTipText) {
		this.toolTipText = toolTipText;
	}
	
	public UIColor getBgColor() {
		return this.bgColor;
	}
	
	public void setBgColor(final UIColor color) {
		this.bgColor = color;
	}

	public UIColor getFgColor() {
		return this.fgColor;
	}
	
	public void setFgColor(UIColor color) {
		this.fgColor = color;
	}
	
	public UIFont getFont() {
		return this.font;
	}
	
	public void setFont(UIFont font) {
		this.font = font;
	}
	
	public UICursor getCursor() {
		return (this.cursor != null ? this.cursor : UICursor.NORMAL);
	}
	
	public void setCursor(UICursor cursor) {
		this.cursor = cursor;
		this.getControl().setCursor(JFXCursor.getCursor(this.getCursor()));
	}
	
	public UIPopupMenu getPopupMenu() {
		return this.popupMenu;
	}
	
	public void setPopupMenu(UIPopupMenu popupMenu) {
		this.popupMenu = popupMenu;
	}

	public void setFocus() {
		this.getControl().requestFocus();
	}

	public void updateClippingArea(UIRectangle childArea) {
		UIRectangle bounds = this.getBounds();
		UIRectangle area = new UIRectangle();
		
		area.getPosition().setX(childArea.getX() - bounds.getX());
		area.getPosition().setY(childArea.getY() - bounds.getY());
		area.getSize().setWidth(childArea.getWidth());
		area.getSize().setHeight(childArea.getHeight());
		
		this.getControl().setClip(new Rectangle(area.getX(), area.getY(), area.getWidth(), area.getHeight()));
	}
	
	public void addDisposeListener(UIDisposeListener listener) {
		this.disposeListener.addListener(listener);
	}
	
	public void removeDisposeListener(UIDisposeListener listener) {
		this.disposeListener.removeListener(listener);
	}
	
	public void addMouseUpListener(UIMouseUpListener listener) {
		if( this.mouseUpListener.isEmpty() ) {
			this.getControl().setOnMouseReleased(this.mouseUpListener);
		}
		this.mouseUpListener.addListener(listener);
	}

	public void removeMouseUpListener(UIMouseUpListener listener) {
		this.mouseUpListener.removeListener(listener);
		if( this.mouseUpListener.isEmpty() ) {
			this.getControl().setOnMouseReleased(null);
		}
	}

	public void addMouseDownListener(UIMouseDownListener listener) {
		if( this.mouseDownListener.isEmpty() ) {
			this.getControl().setOnMousePressed(this.mouseDownListener);
		}
		this.mouseDownListener.addListener(listener);
	}

	public void removeMouseDownListener(UIMouseDownListener listener) {
		this.mouseDownListener.removeListener(listener);
		if( this.mouseDownListener.isEmpty() ) {
			this.getControl().setOnMousePressed(null);
		}
	}

	public void addMouseDoubleClickListener(UIMouseDoubleClickListener listener) {
		if( this.mouseDoubleClickListener.isEmpty() ) {
			this.getControl().setOnMouseClicked(this.mouseDoubleClickListener);
		}
		this.mouseDoubleClickListener.addListener(listener);
	}

	public void removeMouseDoubleClickListener(UIMouseDoubleClickListener listener) {
		this.mouseDoubleClickListener.removeListener(listener);
		if( this.mouseDoubleClickListener.isEmpty() ) {
			this.getControl().setOnMouseClicked(null);
		}
	}

	public void addMouseMoveListener(UIMouseMoveListener listener) {
		if( this.mouseMoveListener.isEmpty() ) {
			this.getControl().setOnMouseMoved(this.mouseMoveListener);
		}
		this.mouseMoveListener.addListener(listener);
	}

	public void removeMouseMoveListener(UIMouseMoveListener listener) {
		this.mouseMoveListener.removeListener(listener);
		if( this.mouseMoveListener.isEmpty() ) {
			this.getControl().setOnMouseMoved(null);
		}
	}
	
	public void addMouseDragListener(UIMouseDragListener listener) {
		if( this.mouseDragListener.isEmpty() ) {
			this.addMouseUpListener(this.mouseDragListener);
			this.addMouseDownListener(this.mouseDragListener);
			this.getControl().setOnMouseDragged(this.mouseDragListener);
		}
		this.mouseDragListener.addListener(listener);
	}

	public void removeMouseDragListener(UIMouseDragListener listener) {
		this.mouseDragListener.removeListener(listener);
		if( this.mouseDragListener.isEmpty() ) {
			this.getControl().setOnMouseDragged(null);
			this.removeMouseUpListener(this.mouseDragListener);
			this.removeMouseDownListener(this.mouseDragListener);
		}
	}
	
	public void addMouseWheelListener(UIMouseWheelListener listener) {
		if( this.mouseWheelListener.isEmpty() ) {
			this.getControl().setOnScroll(this.mouseWheelListener);
		}
		this.mouseWheelListener.addListener(listener);
	}

	public void removeMouseWheelListener(UIMouseWheelListener listener) {
		this.mouseWheelListener.removeListener(listener);
		if( this.mouseWheelListener.isEmpty() ) {
			this.getControl().setOnScroll(null);
		}
	}
	
	public void addMouseEnterListener(UIMouseEnterListener listener) {
		if( this.mouseEnterListener.isEmpty() ) {
			this.getControl().setOnMouseEntered(this.mouseEnterListener);
		}
		this.mouseEnterListener.addListener(listener);
	}

	public void removeMouseEnterListener(UIMouseEnterListener listener) {
		this.mouseEnterListener.removeListener(listener);
		if( this.mouseEnterListener.isEmpty() ) {
			this.getControl().setOnMouseEntered(null);
		}
	}
	
	public void addMouseExitListener(UIMouseExitListener listener) {
		if( this.mouseExitListener.isEmpty() ) {
			this.getControl().setOnMouseExited(this.mouseExitListener);
		}
		this.mouseExitListener.addListener(listener);
	}

	public void removeMouseExitListener(UIMouseExitListener listener) {
		this.mouseExitListener.removeListener(listener);
		if( this.mouseExitListener.isEmpty() ) {
			this.getControl().setOnMouseExited(null);
		}
	}
	
	public void addKeyPressedListener(UIKeyPressedListener listener) {
		if( this.keyPressedListener.isEmpty() ) {
			this.getControl().setOnKeyPressed(this.keyPressedListener);
		}
		this.keyPressedListener.addListener(listener);
	}

	public void removeKeyPressedListener(UIKeyPressedListener listener) {
		this.keyPressedListener.removeListener(listener);
		if( this.keyPressedListener.isEmpty() ) {
			this.getControl().setOnKeyPressed(null);
		}
	}
	
	public void addKeyReleasedListener(UIKeyReleasedListener listener) {
		if( this.keyReleasedListener.isEmpty() ) {
			this.getControl().setOnKeyReleased(this.keyReleasedListener);
		}
		this.keyReleasedListener.addListener(listener);
	}

	public void removeKeyReleasedListener(UIKeyReleasedListener listener) {
		this.keyReleasedListener.removeListener(listener);
		if( this.keyReleasedListener.isEmpty() ) {
			this.getControl().setOnKeyReleased(null);
		}
	}
	
	public void addFocusGainedListener(UIFocusGainedListener listener) {
		if( this.focusListener.isEmpty() ) {
			this.getControl().focusedProperty().addListener(this.focusListener);
		}
		this.focusListener.addListener(listener);
	}

	public void removeFocusGainedListener(UIFocusGainedListener listener) {
		this.focusListener.removeListener(listener);
		if( this.focusListener.isEmpty() ) {
			this.getControl().focusedProperty().removeListener(this.focusListener);
		}
	}
	
	public void addFocusLostListener(UIFocusLostListener listener) {
		if( this.focusListener.isEmpty() ) {
			this.getControl().focusedProperty().addListener(this.focusListener);
		}
		this.focusListener.addListener(listener);
	}

	public void removeFocusLostListener(UIFocusLostListener listener) {
		this.focusListener.removeListener(listener);
		if( this.focusListener.isEmpty() ) {
			this.getControl().focusedProperty().removeListener(this.focusListener);
		}
	}
}
