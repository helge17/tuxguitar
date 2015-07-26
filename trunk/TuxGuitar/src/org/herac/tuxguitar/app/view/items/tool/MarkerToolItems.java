/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.view.items.tool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.marker.TGOpenMarkerEditorAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoFirstMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoLastMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoNextMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoPreviousMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGToggleMarkerListAction;
import org.herac.tuxguitar.app.view.items.ToolItems;
/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MarkerToolItems  extends ToolItems{
	public static final String NAME = "marker.items";
	
	private ToolItem add;
	private ToolItem list;
	private ToolItem first;
	private ToolItem last;
	private ToolItem previous;
	private ToolItem next;
	
	public MarkerToolItems(){
		super(NAME);
	}
	
	public void showItems(ToolBar toolBar){
		//--ADD--
		this.add = new ToolItem(toolBar, SWT.PUSH);
		this.add.addSelectionListener(this.createActionProcessor(TGOpenMarkerEditorAction.NAME));
		
		//--LIST--
		this.list = new ToolItem(toolBar, SWT.PUSH);
		this.list.addSelectionListener(this.createActionProcessor(TGToggleMarkerListAction.NAME));
		
		//--SEPARATOR--
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		//--FIRST--
		this.first = new ToolItem(toolBar, SWT.PUSH);
		this.first.addSelectionListener(this.createActionProcessor(TGGoFirstMarkerAction.NAME));
		
		//--PREVIOUS--
		this.previous = new ToolItem(toolBar, SWT.PUSH);
		this.previous.addSelectionListener(this.createActionProcessor(TGGoPreviousMarkerAction.NAME));
		
		//--PREVIOUS--
		this.next = new ToolItem(toolBar, SWT.PUSH);
		this.next.addSelectionListener(this.createActionProcessor(TGGoNextMarkerAction.NAME));
		
		//--LAST--
		this.last = new ToolItem(toolBar, SWT.PUSH);
		this.last.addSelectionListener(this.createActionProcessor(TGGoLastMarkerAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		//Nothing to do
	}
	
	public void loadProperties(){
		this.add.setToolTipText(TuxGuitar.getProperty("marker.add"));
		this.list.setToolTipText(TuxGuitar.getProperty("marker.list"));
		this.first.setToolTipText(TuxGuitar.getProperty("marker.first"));
		this.last.setToolTipText(TuxGuitar.getProperty("marker.last"));
		this.previous.setToolTipText(TuxGuitar.getProperty("marker.previous"));
		this.next.setToolTipText(TuxGuitar.getProperty("marker.next"));
	}
	
	public void loadIcons(){
		this.add.setImage(TuxGuitar.getInstance().getIconManager().getMarkerAdd());
		this.list.setImage(TuxGuitar.getInstance().getIconManager().getMarkerList());
		this.first.setImage(TuxGuitar.getInstance().getIconManager().getMarkerFirst());
		this.previous.setImage(TuxGuitar.getInstance().getIconManager().getMarkerPrevious());
		this.next.setImage(TuxGuitar.getInstance().getIconManager().getMarkerNext());
		this.last.setImage(TuxGuitar.getInstance().getIconManager().getMarkerLast());
	}
}
