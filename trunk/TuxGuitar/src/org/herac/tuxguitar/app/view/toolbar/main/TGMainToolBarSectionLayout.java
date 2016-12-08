package org.herac.tuxguitar.app.view.toolbar.main;

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

public class TGMainToolBarSectionLayout extends TGMainToolBarSection {
	
	private UIToolMenuItem menuItem;
	
	private UIMenuActionItem pageLayout;
	private UIMenuActionItem linearLayout;
	private UIMenuActionItem multitrack;
	private UIMenuActionItem scoreEnabled;
	private UIMenuActionItem compact;
	
	public TGMainToolBarSectionLayout(TGMainToolBar toolBar) {
		super(toolBar);
	}
	
	public void createSection() {
		this.menuItem = this.getToolBar().getControl().createMenuItem();
		
		this.pageLayout = this.menuItem.getMenu().createActionItem();
		this.pageLayout.addSelectionListener(this.createActionProcessor(TGSetPageLayoutAction.NAME));
		
		this.linearLayout = this.menuItem.getMenu().createActionItem();
		this.linearLayout.addSelectionListener(this.createActionProcessor(TGSetLinearLayoutAction.NAME));
		
		this.multitrack = this.menuItem.getMenu().createActionItem();
		this.multitrack.addSelectionListener(this.createActionProcessor(TGSetMultitrackViewAction.NAME));
		
		this.scoreEnabled = this.menuItem.getMenu().createActionItem();
		this.scoreEnabled.addSelectionListener(this.createActionProcessor(TGSetScoreEnabledAction.NAME));
		
		this.compact = this.menuItem.getMenu().createActionItem();
		this.compact.addSelectionListener(this.createActionProcessor(TGSetCompactViewAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties() {
		TGLayout layout = this.getTablature().getViewLayout();
		int style = layout.getStyle();
		
		this.menuItem.setToolTipText(this.getText("view.layout"));
		this.pageLayout.setText(this.getText("view.layout.page", (layout instanceof TGLayoutVertical)));
		this.linearLayout.setText(this.getText("view.layout.linear", (layout instanceof TGLayoutHorizontal)));
		this.multitrack.setText(this.getText("view.layout.multitrack", ( (style & TGLayout.DISPLAY_MULTITRACK) != 0 )));
		this.scoreEnabled.setText(this.getText("view.layout.score-enabled", ( (style & TGLayout.DISPLAY_SCORE) != 0 )));
		this.compact.setText(this.getText("view.layout.compact", ( (style & TGLayout.DISPLAY_COMPACT) != 0 )));
	}
	
	public void loadIcons(){
		this.menuItem.setImage(this.getIconManager().getLayoutScore());
		this.pageLayout.setImage(this.getIconManager().getLayoutPage());
		this.linearLayout.setImage(this.getIconManager().getLayoutLinear());
		this.multitrack.setImage(this.getIconManager().getLayoutMultitrack());
		this.scoreEnabled.setImage(this.getIconManager().getLayoutScore());
		this.compact.setImage(this.getIconManager().getLayoutCompact());
	}
	
	public void updateItems(){
		TGLayout layout = this.getTablature().getViewLayout();
		int style = layout.getStyle();
		
		this.pageLayout.setText(this.getText("view.layout.page", (layout instanceof TGLayoutVertical)));
		this.linearLayout.setText(this.getText("view.layout.linear", (layout instanceof TGLayoutHorizontal)));
		this.multitrack.setText(this.getText("view.layout.multitrack", ( (style & TGLayout.DISPLAY_MULTITRACK) != 0 )));
		this.scoreEnabled.setText(this.getText("view.layout.score-enabled", ( (style & TGLayout.DISPLAY_SCORE) != 0 )));
		this.compact.setText(this.getText("view.layout.compact", ( (style & TGLayout.DISPLAY_COMPACT) != 0 )));
		this.compact.setEnabled((style & TGLayout.DISPLAY_MULTITRACK) == 0 || this.getSong().countTracks() == 1);
	}
}
