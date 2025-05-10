package app.tuxguitar.app.view.toolbar.main;

/**
 * Main toolbar
 * 
 * content is fully defined by configuration. Main toolBar is made of 3 different areas:
 * left, center, right
 * 
 * Content of each area is defined by configuration. One configuration is a list of Strings
 * Each String refers to a specific TGMainToolBarItem instance
 * 
 * The content of the toolBar is built programmatically. Each area contains 0 to n instances
 * of TGMainToolBarSection.
 * There are different classes inheriting TGMainToolBarSection, because not all items can fit
 * in the same UI object.
 */

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.toolbar.model.TGToolBarModel;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGMainToolBar extends TGToolBarModel {

	private UIPanel panel;
	public static final Integer LEFT_AREA = 0;
	public static final Integer CENTER_AREA = 1;
	public static final Integer RIGHT_AREA = 2;
	public static final Integer[] AREAS = new Integer[] {LEFT_AREA, CENTER_AREA, RIGHT_AREA};
	public static final String[] AREA_NAMES = new String[] {"toolbar.main.left", "toolbar.main.center", "toolbar.main.right"};
	private TGMainToolBarConfig config;

	private TGMainToolBar(TGContext context) {
		super(context);
		String toolBarName = TGConfigManager.getInstance(context).getStringValue(TGConfigKeys.MAIN_TOOLBAR_NAME);
		TGMainToolBarConfigManager configMgr = new TGMainToolBarConfigManager();
		this.config = configMgr.getConfig(toolBarName);
		if (this.config == null) {
			this.config = configMgr.getDefaultConfig();
		}
	}

	public void createToolBar(UIWindow parentWindow, boolean visible) {
		UIFactory uiFactory = TGApplication.getInstance(this.getContext()).getFactory();
		this.panel = uiFactory.createPanel(parentWindow, false);
		this.panel.setVisible(visible);
		this.layout();
	}

	public void layout() {
		this.clearSections();
		for (UIControl control: this.panel.getChildren()) {
			control.dispose();
		}
		
		UIWindow parentWindow = (UIWindow)this.panel.getParent();
		UIFactory uiFactory = TGApplication.getInstance(this.getContext()).getFactory();
		UITableLayout mainToolBarLayout = new UITableLayout();
		this.panel.setLayout(mainToolBarLayout);
		List<UIControl> controls = new ArrayList<UIControl>();
		mainToolBarLayout.set(UITableLayout.MARGIN, 0f);
		int col = 1;
		TGMainToolBarConfigMap configMap = new TGMainToolBarConfigMap();
		
		for (int area : new int[] { LEFT_AREA, CENTER_AREA, RIGHT_AREA }) {
			TGMainToolBarSection section = null;
			TGMainToolBarItem lastAddedItem = null;
			controls.clear();

			for (String toolBarItemName : this.config.getAreaContent(area)) {
				TGMainToolBarItem toolBarItem = configMap.getToolBarItem(toolBarItemName);
				if (toolBarItem == null) {
					System.out.printf("toolBarItem name not found (ignored): " + toolBarItemName + "\n");
				}
				else {
					int sectionType = toolBarItem.getSectionType();
					// need to create a new section?
					if ((lastAddedItem == null) || (sectionType != lastAddedItem.getSectionType())) {
						// if a section was already in creation, add its controls to list, and
						// initialize the section
						if (section != null) {
							controls.addAll(section.getControls());
							this.createSection(mainToolBarLayout, section);
						}
						if (sectionType == TGMainToolBarSection.TYPE_TOOLITEMS) {
							UIToolBar toolBar = uiFactory.createHorizontalToolBar(this.panel);
							section = new TGMainToolBarSectionToolItems(getContext(), toolBar);
						} else if (toolBarItem.getType() == TGMainToolBarItem.TIME_COUNTER) {
							section = new TGMainToolBarSectionTimeCounter(getContext(), this.panel, parentWindow);
						} else if (toolBarItem.getType() == TGMainToolBarItem.TEMPO_INDICATOR) {
							section = new TGMainToolBarSectionTempo(getContext(), this.panel, parentWindow);
						}
					}
					section.addToolBarItem(toolBarItem);
					lastAddedItem = toolBarItem;
				}
			}
			if (section != null) {
				// if nothing in area
				controls.addAll(section.getControls());
				this.createSection(mainToolBarLayout, section);
				this.layoutControls(mainToolBarLayout, area, col, controls);
				col += controls.size();
			}
		}
		this.panel.layout();
	}

	public TGMainToolBarConfig getConfig() {
		return this.config;
	}

	public void setConfig(TGMainToolBarConfig config) {
		this.config = config;
	}

	private void createSection(UITableLayout layout, TGMainToolBarSection section) {
		section.loadIcons();
		section.loadProperties();
		section.setLayoutProperties(layout);
		this.addSection(section);
	}

	private void layoutControls(UITableLayout mainToolBarLayout, int area, int col, List<UIControl> controls) {
		for (int i = 0; i < controls.size(); i++) {
			UIControl control = controls.get(i);
			boolean isFirst = (i == 0);
			boolean isLast = (i == (controls.size() - 1));
			int alignX = UITableLayout.ALIGN_LEFT;
			boolean fillX = false;
			if (area == CENTER_AREA) {
				if (isFirst && !isLast) {
					alignX = UITableLayout.ALIGN_RIGHT;
					fillX = true;
				} else if (isFirst && isLast) {
					alignX = UITableLayout.ALIGN_CENTER;
					fillX = true;
				} else if (isLast) {
					fillX = true;
				}
			} else if (area == RIGHT_AREA) {
				alignX = UITableLayout.ALIGN_RIGHT;
			}
			mainToolBarLayout.set(control, 1, col, alignX, UITableLayout.ALIGN_CENTER, fillX, true);
			col++;
		}
	}

	public UIPanel getControl() {
		return this.panel;
	}

	public static TGMainToolBar getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGMainToolBar.class.getName(),
				new TGSingletonFactory<TGMainToolBar>() {
					public TGMainToolBar createInstance(TGContext context) {
						return new TGMainToolBar(context);
					}
				});
	}
}
