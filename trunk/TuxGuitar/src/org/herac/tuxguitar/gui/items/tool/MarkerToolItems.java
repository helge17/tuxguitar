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
import org.herac.tuxguitar.gui.actions.marker.AddMarkerAction;
import org.herac.tuxguitar.gui.actions.marker.GoFirstMarkerAction;
import org.herac.tuxguitar.gui.actions.marker.GoLastMarkerAction;
import org.herac.tuxguitar.gui.actions.marker.GoNextMarkerAction;
import org.herac.tuxguitar.gui.actions.marker.GoPreviousMarkerAction;
import org.herac.tuxguitar.gui.actions.marker.ListMarkersAction;
import org.herac.tuxguitar.gui.items.ToolItems;
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
		this.add.addSelectionListener(TuxGuitar.instance().getAction(AddMarkerAction.NAME));
		
		//--LIST--
		this.list = new ToolItem(toolBar, SWT.PUSH);
		this.list.addSelectionListener(TuxGuitar.instance().getAction(ListMarkersAction.NAME));
		
		//--SEPARATOR--
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		//--FIRST--
		this.first = new ToolItem(toolBar, SWT.PUSH);
		this.first.addSelectionListener(TuxGuitar.instance().getAction(GoFirstMarkerAction.NAME));
		
		//--PREVIOUS--
		this.previous = new ToolItem(toolBar, SWT.PUSH);
		this.previous.addSelectionListener(TuxGuitar.instance().getAction(GoPreviousMarkerAction.NAME));
		
		//--PREVIOUS--
		this.next = new ToolItem(toolBar, SWT.PUSH);
		this.next.addSelectionListener(TuxGuitar.instance().getAction(GoNextMarkerAction.NAME));
		
		//--LAST--
		this.last = new ToolItem(toolBar, SWT.PUSH);
		this.last.addSelectionListener(TuxGuitar.instance().getAction(GoLastMarkerAction.NAME));
		
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
		this.add.setImage(TuxGuitar.instance().getIconManager().getMarkerAdd());
		this.list.setImage(TuxGuitar.instance().getIconManager().getMarkerList());
		this.first.setImage(TuxGuitar.instance().getIconManager().getMarkerFirst());
		this.previous.setImage(TuxGuitar.instance().getIconManager().getMarkerPrevious());
		this.next.setImage(TuxGuitar.instance().getIconManager().getMarkerNext());
		this.last.setImage(TuxGuitar.instance().getIconManager().getMarkerLast());
	}
}
