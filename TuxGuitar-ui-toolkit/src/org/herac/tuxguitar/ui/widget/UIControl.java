package org.herac.tuxguitar.ui.widget;

import org.herac.tuxguitar.ui.UIComponent;
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
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UICursor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;

public interface UIControl extends UIComponent {
	
	UIControl getParent();
	
	boolean isEnabled();

	void setEnabled(boolean enabled);

	boolean isVisible();

	void setVisible(boolean visible);

	void computePackedSize();
	
	UISize getPackedSize();
	
	void setPackedSize(UISize size);
	
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
}
