package app.tuxguitar.app.view.toolbar.main;

import java.util.HashMap;
import java.util.Map;

/*
 * A template for a toolBar item
 */
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.util.TGContext;

public class TGMainToolBarItem {

	public static final int SEPARATOR = 0;
	public static final int ACTION_ITEM = 1;
	public static final int CHECKABLE_ITEM = 2;
	public static final int MENU = 3;
	public static final int TIME_COUNTER = 4;
	public static final int TEMPO_INDICATOR = 5;

	protected String groupName;
	protected int type;
	protected String actionName;
	protected String iconFileName;
	protected String text;
	protected TGMainToolBarItemUpdater updater;
	protected Map<String,Object> attributes;	// parameters to be passed to the action processor

	protected TGMainToolBarItem(String groupName, String text, int type, String actionName, String iconFileName,
			TGMainToolBarItemUpdater updater) {
		this.groupName = groupName;
		this.type = type;
		this.actionName = actionName;
		this.iconFileName = iconFileName;
		this.text = text;
		this.updater = updater;
		this.attributes = new HashMap<String, Object>();
	}

	public String getGroupName() {
		return this.groupName;
	}

	public String getText() {
		return this.text;
	}

	public int getType() {
		return this.type;
	}

	protected TGMainToolBarItemUpdater getUpdater() {
		return this.updater;
	}

	public int getSectionType() {
		switch (this.type) {
		case SEPARATOR:
		case ACTION_ITEM:
		case CHECKABLE_ITEM:
		case MENU:
			return TGMainToolBarSection.TYPE_TOOLITEMS;
		case TIME_COUNTER:
			return TGMainToolBarSection.TYPE_TIME_COUNTER;
		case TEMPO_INDICATOR:
			return TGMainToolBarSection.TYPE_TEMPO;
		default:
			// does not fit in any section
			throw new IllegalAccessError(
					"Cannot include this item type in a toolBar section: " + String.valueOf(this.type));
		}
	}

	protected void setAttribute(String name, Object value) {
		this.attributes.put(name, value);
	}

	protected String getActionName() {
		return this.actionName;
	}

	public String getIconFileName() {
		return this.iconFileName;
	}

	public void update(TGContext context, boolean running) {
		// default: do nothing, override if needed
	}

	public void loadProperties() {
		// default: do nothing, override if needed
	}

	public void loadIcons(TGIconManager iconManager) {
		// default: do nothing, override if needed
	}

}
