package app.tuxguitar.ui.widget;

import app.tuxguitar.ui.UIComponent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UIFocusGainedListener;
import app.tuxguitar.ui.event.UIFocusLostListener;
import app.tuxguitar.ui.event.UIKeyPressedListener;
import app.tuxguitar.ui.event.UIKeyReleasedListener;
import app.tuxguitar.ui.event.UIMouseDoubleClickListener;
import app.tuxguitar.ui.event.UIMouseDownListener;
import app.tuxguitar.ui.event.UIMouseDragListener;
import app.tuxguitar.ui.event.UIMouseEnterListener;
import app.tuxguitar.ui.event.UIMouseExitListener;
import app.tuxguitar.ui.event.UIMouseMoveListener;
import app.tuxguitar.ui.event.UIMouseUpListener;
import app.tuxguitar.ui.event.UIMouseWheelListener;
import app.tuxguitar.ui.event.UIResizeListener;
import app.tuxguitar.ui.event.UIZoomListener;
import app.tuxguitar.ui.menu.UIPopupMenu;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UICursor;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.resource.UISize;

public interface UIControl extends UIComponent {

	UIControl getParent();

	boolean isEnabled();

	void setEnabled(boolean enabled);

	boolean isVisible();

	void setVisible(boolean visible);

	void computePackedSize(Float fixedWidth, Float fixedHeight);

	UISize getPackedSize();

	UIRectangle getBounds();

	void setBounds(UIRectangle bounds);

	UIColor getBgColor();

	void setBgColor(UIColor color);

	UIColor getFgColor();

	void setFgColor(UIColor color);

	UIFont getFont();

	void setFont(UIFont font);

	UICursor getCursor();

	void setCursor(UICursor cursor);

	void setFocus();

	void redraw();

	UIPopupMenu getPopupMenu();

	boolean isIgnoreEvents();

	void setIgnoreEvents(boolean ignoreEvents);

	void setPopupMenu(UIPopupMenu menu);

	void addDisposeListener(UIDisposeListener listener);

	void removeDisposeListener(UIDisposeListener listener);

	void addMouseUpListener(UIMouseUpListener listener);

	void removeMouseUpListener(UIMouseUpListener listener);

	void addMouseDownListener(UIMouseDownListener listener);

	void removeMouseDownListener(UIMouseDownListener listener);

	void addMouseMoveListener(UIMouseMoveListener listener);

	void removeMouseMoveListener(UIMouseMoveListener listener);

	void addMouseDoubleClickListener(UIMouseDoubleClickListener listener);

	void removeMouseDoubleClickListener(UIMouseDoubleClickListener listener);

	void addMouseDragListener(UIMouseDragListener listener);

	void removeMouseDragListener(UIMouseDragListener listener);

	void addMouseEnterListener(UIMouseEnterListener listener);

	void removeMouseEnterListener(UIMouseEnterListener listener);

	void addMouseExitListener(UIMouseExitListener listener);

	void removeMouseExitListener(UIMouseExitListener listener);

	void addMouseWheelListener(UIMouseWheelListener listener);

	void removeMouseWheelListener(UIMouseWheelListener listener);

	void addKeyPressedListener(UIKeyPressedListener listener);

	void removeKeyPressedListener(UIKeyPressedListener listener);

	void addKeyReleasedListener(UIKeyReleasedListener listener);

	void removeKeyReleasedListener(UIKeyReleasedListener listener);

	void addResizeListener(UIResizeListener listener);

	void removeResizeListener(UIResizeListener listener);

	void addFocusGainedListener(UIFocusGainedListener listener);

	void removeFocusGainedListener(UIFocusGainedListener listener);

	void addFocusLostListener(UIFocusLostListener listener);

	void removeFocusLostListener(UIFocusLostListener listener);

	void addZoomListener(UIZoomListener listener);

	void removeZoomListener(UIZoomListener listener);
}
