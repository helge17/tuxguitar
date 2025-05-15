package app.tuxguitar.app.view.toolbar.main;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.ui.toolbar.UIToolActionItem;
import app.tuxguitar.ui.toolbar.UIToolCheckableItem;
import app.tuxguitar.ui.toolbar.UIToolItem;
import app.tuxguitar.util.TGContext;

// basic button, checkable button
public class TGMainToolBarItemButton extends TGMainToolBarItem {

	protected UIToolItem toolItem;
	private boolean checked;

	public TGMainToolBarItemButton(String groupName, String text, boolean checkable, String actionName, String iconFileName,
			TGMainToolBarItemUpdater updater) {
		super(groupName, text, checkable ? CHECKABLE_ITEM : ACTION_ITEM, actionName, iconFileName, updater);
		this.toolItem = null;
	}

	// the most common type: action_item (not checkable)
	public TGMainToolBarItemButton(String groupName, String text, String actionName, String iconFileName,
			TGMainToolBarItemUpdater updater) {
		this(groupName, text, false, actionName, iconFileName, updater);
	}

	public void setToolItem(UIToolItem toolItem) {
		this.toolItem = toolItem;
	}

	protected UIToolItem getToolItem() {
		return this.toolItem;
	}

	@Override
	public void update(TGContext context, boolean running) {
		if (this.updater != null) {
			this.checked = updater.checked(context, running);
			if (this.toolItem != null) {
				((UIToolActionItem) toolItem).setEnabled(updater.enabled(context, running));
				if (toolItem instanceof UIToolCheckableItem) {
					((UIToolCheckableItem) toolItem).setChecked(this.checked);
				}
			}
		}
	}

	@Override
	public void loadProperties() {
		if (toolItem != null) {
			toolItem.setToolTipText(TuxGuitar.getProperty(this.text));
		}
	}

	@Override
	public void loadIcons(TGIconManager iconManager) {
		if (toolItem != null) {
			toolItem.setImage(iconManager.getImageByName(this.iconFileName));
		}
	}

	public TGMainToolBarItemButton clone() {
		return new TGMainToolBarItemButton(this.groupName, this.text, (this.type == CHECKABLE_ITEM), this.actionName, this.iconFileName, this.updater);
	}

}
