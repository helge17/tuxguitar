package org.herac.tuxguitar.app.view.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleFretBoardEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleTransportDialogAction;
import org.herac.tuxguitar.app.view.dialog.channel.TGChannelManagerDialog;
import org.herac.tuxguitar.app.view.dialog.fretboard.TGFretBoardEditor;
import org.herac.tuxguitar.app.view.dialog.transport.TGTransportDialog;

public class TGToolBarSectionView implements TGToolBarSection {
	
	private ToolItem menuItem;
	
	private Menu menu;
	private MenuItem showFretBoard;
	private MenuItem showInstruments;
	private MenuItem showTransport;
	
	public void createSection(final TGToolBar toolBar) {
		this.menuItem = new ToolItem(toolBar.getControl(), SWT.PUSH);
		this.menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				displayMenu();
			}
		});
		
		this.menu = new Menu(this.menuItem.getParent().getShell());
		
		//--FRETBOARD--
		this.showFretBoard = new MenuItem(this.menu, SWT.PUSH);
		this.showFretBoard.addSelectionListener(toolBar.createActionProcessor(TGToggleFretBoardEditorAction.NAME));
		
		//--INSTRUMENTS--
		this.showInstruments = new MenuItem(this.menu, SWT.PUSH);
		this.showInstruments.addSelectionListener(toolBar.createActionProcessor(TGToggleChannelsDialogAction.NAME));
		
		//--TRANSPORT--
		this.showTransport = new MenuItem(this.menu, SWT.PUSH);
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
	
	public void displayMenu() {		
		Rectangle rect = this.menuItem.getBounds();
		Point pt = this.menuItem.getParent().toDisplay(new Point(rect.x, rect.y));
		
		this.menu.setLocation(pt.x, pt.y + rect.height);
		this.menu.setVisible(true);
	}
}
