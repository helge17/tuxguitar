package app.tuxguitar.ui.toolbar;

import app.tuxguitar.ui.UIComponent;
import app.tuxguitar.ui.widget.UIControl;

public interface UIToolBar extends UIControl {

	UIToolActionItem createActionItem();

	UIToolCheckableItem createCheckItem();

	UIToolMenuItem createMenuItem();

	UIToolActionMenuItem createActionMenuItem();

	UIToolCustomItem createCustomItem();

	UIComponent createSeparator();
}
