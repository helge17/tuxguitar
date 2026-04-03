package app.tuxguitar.app.view.toolbar.main;

/**
 * This class contains all required information (configuration) to instantiate toolBar items in a section
 */

import java.util.ArrayList;

/**
 * This class contains all required information (configuration) to instantiate toolBar items in a section
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TGMainToolBarItemConfig {

	public static final int SEPARATOR = 0;
	public static final int ACTION_ITEM = 1;
	public static final int CHECKABLE_ITEM = 2;
	public static final int MENU = 3;
	public static final int TIME_COUNTER = 4;
	public static final int TEMPO_INDICATOR = 5;

	private String groupName;
	private int type;
	private int sectionType;
	private String actionName;
	private String iconFileName;
	private String text;
	private TGMainToolBarItemUpdater updater;
	private Map<String,Object> attributes;
	private boolean displaySelectedItemIcon; // only relevant for menu
	private List<TGMainToolBarItemConfig> subItemConfigs;	// nested items, useful (at least) for menus

	public TGMainToolBarItemConfig(String groupName, String text, int type, int sectionType, String actionName, String iconFileName,
			TGMainToolBarItemUpdater updater, boolean displaySelectedItemIcon) {
		this.groupName = groupName;
		this.type = type;
		this.sectionType = sectionType;
		this.actionName = actionName;
		this.iconFileName = iconFileName;
		this.text = text;
		this.updater = updater;
		this.attributes = new HashMap<String, Object>();
		this.displaySelectedItemIcon = displaySelectedItemIcon;
		this.subItemConfigs = new ArrayList<TGMainToolBarItemConfig>();
	}

	public TGMainToolBarItemConfig(String groupName, String text, int type, int sectionType, String iconFileName) {
		this(groupName, text, type, sectionType, null, iconFileName, null, false);
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

	public int getSectionType() {
		return this.sectionType;
	}

	protected TGMainToolBarItemUpdater getUpdater() {
		return this.updater;
	}

	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	public void setAttribute(String name, Object value) {
		this.attributes.put(name, value);
	}

	public String getActionName() {
		return this.actionName;
	}

	public String getIconFileName() {
		return this.iconFileName;
	}
	
	public boolean displaySelectedItemIcon() {
		return this.displaySelectedItemIcon;
	}

	public void addSubItem(TGMainToolBarItemConfig config) {
		this.subItemConfigs.add(config);
	}

	public List<TGMainToolBarItemConfig> getSubItemConfigs() {
		return this.subItemConfigs;
	}

}
