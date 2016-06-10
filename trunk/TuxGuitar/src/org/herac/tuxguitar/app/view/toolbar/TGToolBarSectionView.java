package org.herac.tuxguitar.app.view.toolbar;

import org.herac.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleFretBoardEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleTransportDialogAction;
import org.herac.tuxguitar.app.view.dialog.channel.TGChannelManagerDialog;
import org.herac.tuxguitar.app.view.dialog.fretboard.TGFretBoardEditor;
import org.herac.tuxguitar.app.view.dialog.transport.TGTransportDialog;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolMenuItem;

public class TGToolBarSectionView implements TGToolBarSection {
	
	private UIToolMenuItem menuItem;
	
	private UIMenuActionItem showFretBoard;
	private UIMenuActionItem showInstruments;
	private UIMenuActionItem showTransport;
	
	public void createSection(final TGToolBar toolBar) {
		this.menuItem = toolBar.getControl().createMenuItem();
		
		//--FRETBOARD--
		this.showFretBoard = this.menuItem.getMenu().createActionItem();
		this.showFretBoard.addSelectionListener(toolBar.createActionProcessor(TGToggleFretBoardEditorAction.NAME));
		
		//--INSTRUMENTS--
		this.showInstruments = this.menuItem.getMenu().createActionItem();
		this.showInstruments.addSelectionListener(toolBar.createActionProcessor(TGToggleChannelsDialogAction.NAME));
		
		//--TRANSPORT--
		this.showTransport = this.menuItem.getMenu().createActionItem();
		this.showTransport.addSelectionListener(toolBar.createActionProcessor(TGToggleTransportDialogAction.NAME));
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void loadProperties(TGToolBar toolBar){
		this.menuItem.setToolTipText(toolBar.getText("view"));
		this.showFretBoard.setText(toolBar.getText("view.show-fretboard", TGFretBoardEditor.getInstance(toolBar.getContext()).isVisible()));
		this.showInstruments.setText(toolBar.getText("view.show-instruments", (!TGChannelManagerDialog.getInstance(toolBar.getContext()).isDisposed())));
		this.showTransport.setText(toolBar.getText("view.show-transport", (!TGTransportDialog.getInstance(toolBar.getContext()).isDisposed())));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.menuItem.setImage(toolBar.getIconManager().getFretboard());
		this.showFretBoard.setImage(toolBar.getIconManager().getFretboard());
		this.showInstruments.setImage(toolBar.getIconManager().getInstruments());
		this.showTransport.setImage(toolBar.getIconManager().getTransport());
	}
	
	public void updateItems(TGToolBar toolBar){
		this.showFretBoard.setText(toolBar.getText("view.show-fretboard", TGFretBoardEditor.getInstance(toolBar.getContext()).isVisible()));
		this.showInstruments.setText(toolBar.getText("view.show-instruments", (!TGChannelManagerDialog.getInstance(toolBar.getContext()).isDisposed())));
		this.showTransport.setText(toolBar.getText("view.show-transport", (!TGTransportDialog.getInstance(toolBar.getContext()).isDisposed())));
	}
}
