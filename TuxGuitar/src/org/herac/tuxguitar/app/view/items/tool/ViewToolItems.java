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
import org.herac.tuxguitar.app.action.impl.view.TGToggleFretBoardEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleTransportDialogAction;
import org.herac.tuxguitar.app.view.dialog.transport.TGTransportDialog;
import org.herac.tuxguitar.app.view.items.ToolItems;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ViewToolItems extends ToolItems{
	public static final String NAME = "view.items";
	
	private ToolItem showFretBoard;
	private ToolItem showInstruments;
	private ToolItem showTransport;
	
	public ViewToolItems(){
		super(NAME);
	}
	
	public void showItems(ToolBar toolBar){
		//--FRETBOARD--
		this.showFretBoard = new ToolItem(toolBar, SWT.CHECK);
		this.showFretBoard.addSelectionListener(this.createActionProcessor(TGToggleFretBoardEditorAction.NAME));
		
		//--INSTRUMENTS--
		this.showInstruments = new ToolItem(toolBar, SWT.CHECK);
		this.showInstruments.addSelectionListener(this.createActionProcessor(TGToggleChannelsDialogAction.NAME));
		
		//--TRANSPORT--
		this.showTransport = new ToolItem(toolBar, SWT.CHECK);
		this.showTransport.addSelectionListener(this.createActionProcessor(TGToggleTransportDialogAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		this.showFretBoard.setSelection(TuxGuitar.getInstance().getFretBoardEditor().isVisible());
		this.showInstruments.setSelection(!TuxGuitar.getInstance().getChannelManager().isDisposed());
		this.showTransport.setSelection(!TGTransportDialog.getInstance(this.findContext()).isDisposed());
	}
	
	public void loadProperties(){
		this.showFretBoard.setToolTipText(TuxGuitar.getProperty("view.show-fretboard"));
		this.showInstruments.setToolTipText(TuxGuitar.getProperty("view.show-instruments"));
		this.showTransport.setToolTipText(TuxGuitar.getProperty("view.show-transport"));
	}
	
	public void loadIcons(){
		this.showFretBoard.setImage(TuxGuitar.getInstance().getIconManager().getFretboard());
		this.showInstruments.setImage(TuxGuitar.getInstance().getIconManager().getInstruments());
		this.showTransport.setImage(TuxGuitar.getInstance().getIconManager().getTransport());
	}
}
