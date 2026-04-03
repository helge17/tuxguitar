package app.tuxguitar.ui.menu;

import java.util.List;

import app.tuxguitar.ui.UIComponent;

public interface UIMenu extends UIComponent {

	Integer getItemCount();

	UIMenuItem getItem(int index);

	List<UIMenuItem> getItems();

	UIMenuActionItem createActionItem();

	UIMenuCheckableItem createCheckItem();

	UIMenuCheckableItem createRadioItem();

	UIMenuSubMenuItem createSubMenuItem();

	UIComponent createSeparator();
}
