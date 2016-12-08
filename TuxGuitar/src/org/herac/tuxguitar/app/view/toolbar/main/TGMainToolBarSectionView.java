package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleEditToolbarAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleFretBoardEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleTransportDialogAction;
import org.herac.tuxguitar.app.view.dialog.channel.TGChannelManagerDialog;
import org.herac.tuxguitar.app.view.dialog.fretboard.TGFretBoardEditor;
import org.herac.tuxguitar.app.view.dialog.transport.TGTransportDialog;
import org.herac.tuxguitar.app.view.toolbar.edit.TGEditToolBar;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolCheckableItem;
import org.herac.tuxguitar.ui.toolbar.UIToolMenuItem;

public class TGMainToolBarSectionView extends TGMainToolBarSection {
	
	private UIToolCheckableItem showEditToolBar;
	private UIToolMenuItem menuItem;
	
	private UIMenuActionItem showFretBoard;
	private UIMenuActionItem showInstruments;
	private UIMenuActionItem showTransport;
	
	public TGMainToolBarSectionView(TGMainToolBar toolBar) {
		super(toolBar);
	}
	
	public void createSection() {
		this.showEditToolBar = this.getToolBar().getControl().createCheckItem();
		this.showEditToolBar.addSelectionListener(this.createActionProcessor(TGToggleEditToolbarAction.NAME));
		
		this.menuItem = this.getToolBar().getControl().createMenuItem();
		
		//--FRETBOARD--
		this.showFretBoard = this.menuItem.getMenu().createActionItem();
		this.showFretBoard.addSelectionListener(this.createActionProcessor(TGToggleFretBoardEditorAction.NAME));
		
		//--INSTRUMENTS--
		this.showInstruments = this.menuItem.getMenu().createActionItem();
		this.showInstruments.addSelectionListener(this.createActionProcessor(TGToggleChannelsDialogAction.NAME));
		
		//--TRANSPORT--
		this.showTransport = this.menuItem.getMenu().createActionItem();
		this.showTransport.addSelectionListener(this.createActionProcessor(TGToggleTransportDialogAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		this.showEditToolBar.setToolTipText(this.getText("view.show-edit-toolbar"));
		this.menuItem.setToolTipText(this.getText("view"));
		this.showFretBoard.setText(this.getText("view.show-fretboard", TGFretBoardEditor.getInstance(this.getToolBar().getContext()).isVisible()));
		this.showInstruments.setText(this.getText("view.show-instruments", (!TGChannelManagerDialog.getInstance(this.getToolBar().getContext()).isDisposed())));
		this.showTransport.setText(this.getText("view.show-transport", (!TGTransportDialog.getInstance(this.getToolBar().getContext()).isDisposed())));
	}
	
	public void loadIcons(){
		this.showEditToolBar.setImage(this.getIconManager().getToolbarEdit());
		this.menuItem.setImage(this.getIconManager().getFretboard());
		this.showFretBoard.setImage(this.getIconManager().getFretboard());
		this.showInstruments.setImage(this.getIconManager().getInstruments());
		this.showTransport.setImage(this.getIconManager().getTransport());
	}
	
	public void updateItems(){
		this.showEditToolBar.setChecked(TGEditToolBar.getInstance(this.getToolBar().getContext()).isVisible());
		this.showFretBoard.setText(this.getText("view.show-fretboard", TGFretBoardEditor.getInstance(this.getToolBar().getContext()).isVisible()));
		this.showInstruments.setText(this.getText("view.show-instruments", (!TGChannelManagerDialog.getInstance(this.getToolBar().getContext()).isDisposed())));
		this.showTransport.setText(this.getText("view.show-transport", (!TGTransportDialog.getInstance(this.getToolBar().getContext()).isDisposed())));
	}
}
