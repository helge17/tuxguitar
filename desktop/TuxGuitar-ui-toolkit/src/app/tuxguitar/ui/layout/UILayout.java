package app.tuxguitar.ui.layout;

import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.resource.UISize;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.ui.widget.UILayoutContainer;

public interface UILayout {

	<T extends Object> void set(String key, T value);

	<T extends Object> void set(UIControl control, String key, T value);

	<T extends Object> T get(String key);

	<T extends Object> T get(UIControl control, String key);

	UISize computePackedSize(UILayoutContainer container);

	void setBounds(UILayoutContainer container, UIRectangle bounds);
}
