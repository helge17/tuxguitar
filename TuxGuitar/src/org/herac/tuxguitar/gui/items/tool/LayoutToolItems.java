/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.items.tool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.layout.SetCompactViewAction;
import org.herac.tuxguitar.gui.actions.layout.SetLinearLayoutAction;
import org.herac.tuxguitar.gui.actions.layout.SetMultitrackViewAction;
import org.herac.tuxguitar.gui.actions.layout.SetPageLayoutAction;
import org.herac.tuxguitar.gui.actions.layout.SetScoreEnabledAction;
import org.herac.tuxguitar.gui.editors.tab.layout.LinearViewLayout;
import org.herac.tuxguitar.gui.editors.tab.layout.PageViewLayout;
import org.herac.tuxguitar.gui.editors.tab.layout.ViewLayout;
import org.herac.tuxguitar.gui.items.ToolItems;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LayoutToolItems extends ToolItems{
	public static final String NAME = "layout.items";
	
	private ToolItem pageLayout;
	private ToolItem linearLayout;
	private ToolItem multitrack;
	private ToolItem scoreEnabled;
	private ToolItem compact;
	
	public LayoutToolItems(){
		super(NAME);
	}
	
	public void showItems(ToolBar toolBar){
		this.pageLayout = new ToolItem(toolBar, SWT.RADIO);
		this.pageLayout.addSelectionListener(TuxGuitar.instance().getAction(SetPageLayoutAction.NAME));
		
		this.linearLayout = new ToolItem(toolBar, SWT.RADIO);
		this.linearLayout.addSelectionListener(TuxGuitar.instance().getAction(SetLinearLayoutAction.NAME));
		
		this.multitrack = new ToolItem(toolBar, SWT.CHECK);
		this.multitrack.addSelectionListener(TuxGuitar.instance().getAction(SetMultitrackViewAction.NAME));
		
		this.scoreEnabled = new ToolItem(toolBar, SWT.CHECK);
		this.scoreEnabled.addSelectionListener(TuxGuitar.instance().getAction(SetScoreEnabledAction.NAME));
		
		this.compact = new ToolItem(toolBar, SWT.CHECK);
		this.compact.addSelectionListener(TuxGuitar.instance().getAction(SetCompactViewAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		this.pageLayout.setToolTipText(TuxGuitar.getProperty("view.layout.page"));
		this.linearLayout.setToolTipText(TuxGuitar.getProperty("view.layout.linear"));
		this.multitrack.setToolTipText(TuxGuitar.getProperty("view.layout.multitrack"));
		this.scoreEnabled.setToolTipText(TuxGuitar.getProperty("view.layout.score-enabled"));
		this.compact.setToolTipText(TuxGuitar.getProperty("view.layout.compact"));
	}
	
	public void loadIcons(){
		this.pageLayout.setImage(TuxGuitar.instance().getIconManager().getLayoutPage());
		this.linearLayout.setImage(TuxGuitar.instance().getIconManager().getLayoutLinear());
		this.multitrack.setImage(TuxGuitar.instance().getIconManager().getLayoutMultitrack());
		this.scoreEnabled.setImage(TuxGuitar.instance().getIconManager().getLayoutScore());
		this.compact.setImage(TuxGuitar.instance().getIconManager().getLayoutCompact());
	}
	
	public void update(){
		ViewLayout layout = getEditor().getTablature().getViewLayout();
		int style = layout.getStyle();
		this.pageLayout.setSelection(layout instanceof PageViewLayout);
		this.linearLayout.setSelection(layout instanceof LinearViewLayout);
		this.multitrack.setSelection( (style & ViewLayout.DISPLAY_MULTITRACK) != 0 );
		this.scoreEnabled.setSelection( (style & ViewLayout.DISPLAY_SCORE) != 0 );
		this.compact.setSelection( (style & ViewLayout.DISPLAY_COMPACT) != 0 );
		this.compact.setEnabled((style & ViewLayout.DISPLAY_MULTITRACK) == 0 || getEditor().getTablature().getSongManager().getSong().countTracks() == 1);
	}
}
