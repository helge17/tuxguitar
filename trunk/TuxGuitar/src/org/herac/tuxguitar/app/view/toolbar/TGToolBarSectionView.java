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
	
	public void createSection(final TGToolBar toolBar) {
		this.menuItem = new ToolItem(toolBar.getControl(), SWT.PUSH);
		this.menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				createMenu(toolBar, (ToolItem) event.widget);
			}
		});
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void loadProperties(TGToolBar toolBar){
		this.menuItem.setToolTipText(toolBar.getText("view"));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.menuItem.setImage(toolBar.getIconManager().getFretboard());
	}
	
	public void updateItems(TGToolBar toolBar){
		//Nothing to do
	}
	
	public void createMenu(TGToolBar toolBar, ToolItem item) {
		Menu menu = new Menu(item.getParent().getShell());
		
		//--FRETBOARD--
		MenuItem showFretBoard = new MenuItem(menu, SWT.PUSH);
		showFretBoard.addSelectionListener(toolBar.createActionProcessor(TGToggleFretBoardEditorAction.NAME));
		showFretBoard.setImage(toolBar.getIconManager().getFretboard());
		showFretBoard.setText(toolBar.getText("view.show-fretboard", TGFretBoardEditor.getInstance(toolBar.getContext()).isVisible()));

		//--INSTRUMENTS--
		MenuItem showInstruments = new MenuItem(menu, SWT.PUSH);
		showInstruments.addSelectionListener(toolBar.createActionProcessor(TGToggleChannelsDialogAction.NAME));
		showInstruments.setImage(toolBar.getIconManager().getInstruments());
		showInstruments.setText(toolBar.getText("view.show-instruments", (!TGChannelManagerDialog.getInstance(toolBar.getContext()).isDisposed())));

		//--TRANSPORT--
		MenuItem showTransport = new MenuItem(menu, SWT.PUSH);
		showTransport.addSelectionListener(toolBar.createActionProcessor(TGToggleTransportDialogAction.NAME));
		showTransport.setImage(toolBar.getIconManager().getTransport());
		showTransport.setText(toolBar.getText("view.show-transport", (!TGTransportDialog.getInstance(toolBar.getContext()).isDisposed())));
		
		Rectangle rect = item.getBounds();
		Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
		
		menu.setLocation(pt.x, pt.y + rect.height);
		menu.setVisible(true);
	}
}
