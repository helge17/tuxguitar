package app.tuxguitar.app.view.toolbar.main;

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

	public void addToolBarItem(TGMainToolBarItem toolBarItem) {
		switch (toolBarItem.getType()) {
		case TGMainToolBarItem.ACTION_ITEM:
			if (!(toolBarItem instanceof TGMainToolBarItemButton)) {
				throw new IllegalArgumentException("invalid toolbar item type, action_item expected");
			}
			UIToolActionItem toolActionItem = this.toolBar.createActionItem();
			toolActionItem.addSelectionListener(this.createActionProcessor(toolBarItem.getActionName()));
			((TGMainToolBarItemButton) toolBarItem).setToolItem(toolActionItem);
			break;
		case TGMainToolBarItem.CHECKABLE_ITEM:
			if (!(toolBarItem instanceof TGMainToolBarItemButton)) {
				throw new IllegalArgumentException("invalid toolbar item type, checkable_item expected");
			}
			UIToolCheckableItem toolCheckableItem = this.toolBar.createCheckItem();
			toolCheckableItem.addSelectionListener(this.createActionProcessor(toolBarItem.getActionName()));
			((TGMainToolBarItemButton) toolBarItem).setToolItem(toolCheckableItem);
			break;
		case TGMainToolBarItem.SEPARATOR:
			this.toolBar.createSeparator();
			break;
		case TGMainToolBarItem.MENU:
			if (!(toolBarItem instanceof TGMainToolBarItemMenu)) {
				throw new IllegalArgumentException("invalid toolbar item type, menu expected");
			}
			((TGMainToolBarItemMenu) toolBarItem).createMenu(toolBar, this.getContext());
			break;
		default:
			throw new IllegalArgumentException("unmanaged type of mainToolBarItem");
		}
		this.toolBarItems.add(toolBarItem);
	}

}
