package app.tuxguitar.app.view.toolbar.main;

/**
 * A toolBar section to host checkable/non-checkable button(s), menu(s), or separator(s)
 * it hosts one single UIToolBar objects, which itself hosts all items
 */

import java.util.ArrayList;
import app.tuxguitar.ui.toolbar.UIToolActionItem;
import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.ui.toolbar.UIToolCheckableItem;
import app.tuxguitar.util.TGContext;

public class TGMainToolBarSectionToolItems extends TGMainToolBarSection {

	private UIToolBar toolBar;

	public TGMainToolBarSectionToolItems(TGContext context, UIToolBar toolBar) {
		super(context);
		this.toolBar = toolBar;
		this.controls.add(toolBar);
		this.toolBarItems = new ArrayList<TGMainToolBarItem>();
	}

	@Override
	public void addToolBarItem(TGMainToolBarItemConfig toolBarItemConfig) {
		switch (toolBarItemConfig.getType()) {
		case TGMainToolBarItem.ACTION_ITEM:
			UIToolActionItem toolActionItem = this.toolBar.createActionItem();
			toolActionItem.addSelectionListener(this.createActionProcessor(toolBarItemConfig));
			TGMainToolBarItemButton button = new TGMainToolBarItemButton(toolBarItemConfig, toolActionItem);
			this.toolBarItems.add(button);
			break;
		case TGMainToolBarItem.CHECKABLE_ITEM:
			UIToolCheckableItem toolCheckableItem = this.toolBar.createCheckItem();
			toolCheckableItem.addSelectionListener(this.createActionProcessor(toolBarItemConfig));
			TGMainToolBarItemButton checkable = new TGMainToolBarItemButton(toolBarItemConfig, toolCheckableItem);
			this.toolBarItems.add(checkable);
			break;
		case TGMainToolBarItem.SEPARATOR:
			this.toolBar.createSeparator();
			break;
		case TGMainToolBarItem.MENU:
			TGMainToolBarItemMenu menu = new TGMainToolBarItemMenu(toolBarItemConfig);
			menu.createMenu(toolBar, this.getContext());
			this.toolBarItems.add(menu);
			break;
		default:
			throw new IllegalArgumentException("unmanaged type of mainToolBarItem");
		}
	}

}
