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
import org.herac.tuxguitar.gui.actions.view.ShowFretBoardAction;
import org.herac.tuxguitar.gui.actions.view.ShowMixerAction;
import org.herac.tuxguitar.gui.actions.view.ShowTransportAction;
import org.herac.tuxguitar.gui.items.ToolItems;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ViewToolItems extends ToolItems{
	public static final String NAME = "view.items";
	
	private ToolItem showFretBoard;
	private ToolItem showMixer;
	private ToolItem showTransport;
	
	public ViewToolItems(){
		super(NAME);
	}
	
	public void showItems(ToolBar toolBar){
		//--FRETBOARD--
		this.showFretBoard = new ToolItem(toolBar, SWT.CHECK);
		this.showFretBoard.addSelectionListener(TuxGuitar.instance().getAction(ShowFretBoardAction.NAME));
		
		//--MIXER--
		this.showMixer = new ToolItem(toolBar, SWT.CHECK);
		this.showMixer.addSelectionListener(TuxGuitar.instance().getAction(ShowMixerAction.NAME));
		
		//--TRANSPORT--
		this.showTransport = new ToolItem(toolBar, SWT.CHECK);
		this.showTransport.addSelectionListener(TuxGuitar.instance().getAction(ShowTransportAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		this.showFretBoard.setSelection(TuxGuitar.instance().getFretBoardEditor().isVisible());
		this.showMixer.setSelection(!TuxGuitar.instance().getMixer().isDisposed());
		this.showTransport.setSelection(!TuxGuitar.instance().getTransport().isDisposed());
	}
	
	public void loadProperties(){
		this.showFretBoard.setToolTipText(TuxGuitar.getProperty("view.show-fretboard"));
		this.showMixer.setToolTipText(TuxGuitar.getProperty("view.show-mixer"));
		this.showTransport.setToolTipText(TuxGuitar.getProperty("view.show-transport"));
	}
	
	public void loadIcons(){
		this.showFretBoard.setImage(TuxGuitar.instance().getIconManager().getFretboard());
		this.showMixer.setImage(TuxGuitar.instance().getIconManager().getMixer());
		this.showTransport.setImage(TuxGuitar.instance().getIconManager().getTransport());
	}
}
