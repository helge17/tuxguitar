/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.items.tool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessor;
import org.herac.tuxguitar.app.action.impl.composition.ChangeInfoAction;
import org.herac.tuxguitar.app.items.ToolItems;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PropertiesToolItems extends ToolItems{
	public static final String NAME = "property.items";
	
	private ToolItem info;
	
	public PropertiesToolItems(){
		super(NAME);
	}
	
	public void showItems(ToolBar toolBar){
		this.info = new ToolItem(toolBar, SWT.PUSH);
		this.info.addSelectionListener(new TGActionProcessor(ChangeInfoAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		this.info.setToolTipText(TuxGuitar.getProperty("composition.properties"));
	}
	
	public void loadIcons(){
		this.info.setImage(TuxGuitar.getInstance().getIconManager().getSongProperties());
	}
	
	public void update(){
		//Nothing to do
	}
}
