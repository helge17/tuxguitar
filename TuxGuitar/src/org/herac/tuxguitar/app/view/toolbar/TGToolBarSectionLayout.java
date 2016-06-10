package org.herac.tuxguitar.app.view.toolbar;

import org.herac.tuxguitar.app.action.impl.layout.TGSetCompactViewAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetLinearLayoutAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetMultitrackViewAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetPageLayoutAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetScoreEnabledAction;
import org.herac.tuxguitar.graphics.control.TGLayout;
import org.herac.tuxguitar.graphics.control.TGLayoutHorizontal;
import org.herac.tuxguitar.graphics.control.TGLayoutVertical;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolMenuItem;

public class TGToolBarSectionLayout implements TGToolBarSection {
	
	private UIToolMenuItem menuItem;
	
	private UIMenuActionItem pageLayout;
	private UIMenuActionItem linearLayout;
	private UIMenuActionItem multitrack;
	private UIMenuActionItem scoreEnabled;
	private UIMenuActionItem compact;
	
	public void createSection(final TGToolBar toolBar) {
		this.menuItem = toolBar.getControl().createMenuItem();
		
		this.pageLayout = this.menuItem.getMenu().createActionItem();
		this.pageLayout.addSelectionListener(toolBar.createActionProcessor(TGSetPageLayoutAction.NAME));
		
		this.linearLayout = this.menuItem.getMenu().createActionItem();
		this.linearLayout.addSelectionListener(toolBar.createActionProcessor(TGSetLinearLayoutAction.NAME));
		
		this.multitrack = this.menuItem.getMenu().createActionItem();
		this.multitrack.addSelectionListener(toolBar.createActionProcessor(TGSetMultitrackViewAction.NAME));
		
		this.scoreEnabled = this.menuItem.getMenu().createActionItem();
		this.scoreEnabled.addSelectionListener(toolBar.createActionProcessor(TGSetScoreEnabledAction.NAME));
		
		this.compact = this.menuItem.getMenu().createActionItem();
		this.compact.addSelectionListener(toolBar.createActionProcessor(TGSetCompactViewAction.NAME));
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void loadProperties(TGToolBar toolBar) {
		TGLayout layout = toolBar.getTablature().getViewLayout();
		int style = layout.getStyle();
		
		this.menuItem.setToolTipText(toolBar.getText("view.layout"));
		this.pageLayout.setText(toolBar.getText("view.layout.page", (layout instanceof TGLayoutVertical)));
		this.linearLayout.setText(toolBar.getText("view.layout.linear", (layout instanceof TGLayoutHorizontal)));
		this.multitrack.setText(toolBar.getText("view.layout.multitrack", ( (style & TGLayout.DISPLAY_MULTITRACK) != 0 )));
		this.scoreEnabled.setText(toolBar.getText("view.layout.score-enabled", ( (style & TGLayout.DISPLAY_SCORE) != 0 )));
		this.compact.setText(toolBar.getText("view.layout.compact", ( (style & TGLayout.DISPLAY_COMPACT) != 0 )));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.menuItem.setImage(toolBar.getIconManager().getLayoutScore());
		this.pageLayout.setImage(toolBar.getIconManager().getLayoutPage());
		this.linearLayout.setImage(toolBar.getIconManager().getLayoutLinear());
		this.multitrack.setImage(toolBar.getIconManager().getLayoutMultitrack());
		this.scoreEnabled.setImage(toolBar.getIconManager().getLayoutScore());
		this.compact.setImage(toolBar.getIconManager().getLayoutCompact());
	}
	
	public void updateItems(TGToolBar toolBar){
		TGLayout layout = toolBar.getTablature().getViewLayout();
		int style = layout.getStyle();
		
		this.pageLayout.setText(toolBar.getText("view.layout.page", (layout instanceof TGLayoutVertical)));
		this.linearLayout.setText(toolBar.getText("view.layout.linear", (layout instanceof TGLayoutHorizontal)));
		this.multitrack.setText(toolBar.getText("view.layout.multitrack", ( (style & TGLayout.DISPLAY_MULTITRACK) != 0 )));
		this.scoreEnabled.setText(toolBar.getText("view.layout.score-enabled", ( (style & TGLayout.DISPLAY_SCORE) != 0 )));
		this.compact.setText(toolBar.getText("view.layout.compact", ( (style & TGLayout.DISPLAY_COMPACT) != 0 )));
		this.compact.setEnabled((style & TGLayout.DISPLAY_MULTITRACK) == 0 || toolBar.getSong().countTracks() == 1);
	}
}
