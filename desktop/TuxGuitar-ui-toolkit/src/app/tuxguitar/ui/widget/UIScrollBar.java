package app.tuxguitar.ui.widget;

import app.tuxguitar.ui.UIComponent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.resource.UISize;

public interface UIScrollBar extends UIComponent {

	void setValue(int value);

	int getValue();

	void setMaximum(int maximum);

	int getMaximum();

	void setMinimum(int minimum);

	int getMinimum();

	void setIncrement(int increment);

	int getIncrement();

	void setThumb(int thumb);

	int getThumb();

	UISize getSize();

	void addSelectionListener(UISelectionListener listener);

	void removeSelectionListener(UISelectionListener listener);

	void setVisible(boolean visible);
}
