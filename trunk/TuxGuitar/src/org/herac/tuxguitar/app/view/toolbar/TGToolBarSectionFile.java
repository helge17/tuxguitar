package org.herac.tuxguitar.app.view.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenSongInfoDialogAction;
import org.herac.tuxguitar.app.action.impl.file.TGOpenFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGPrintAction;
import org.herac.tuxguitar.app.action.impl.file.TGPrintPreviewAction;
import org.herac.tuxguitar.app.action.impl.file.TGSaveAsFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGSaveFileAction;
import org.herac.tuxguitar.editor.action.file.TGLoadTemplateAction;

public class TGToolBarSectionFile implements TGToolBarSection {
	
	private ToolItem menuItem;
	private Menu menu;
	private MenuItem newSong;
	private MenuItem openSong;
	private MenuItem saveSong;
	private MenuItem saveAsSong;
	private MenuItem printSong;
	private MenuItem printPreviewSong;
	private MenuItem properties;
	
	public void createSection(final TGToolBar toolBar) {
		this.menuItem = new ToolItem(toolBar.getControl(), SWT.PUSH);
		this.menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				displayMenu();
			}
		});
		
		this.menu = new Menu(this.menuItem.getParent().getShell());
		
		this.newSong = new MenuItem(this.menu, SWT.PUSH);
		this.newSong.addSelectionListener(toolBar.createActionProcessor(TGLoadTemplateAction.NAME));
		
		this.openSong = new MenuItem(this.menu, SWT.PUSH);
		this.openSong.addSelectionListener(toolBar.createActionProcessor(TGOpenFileAction.NAME));
		
		this.saveSong = new MenuItem(this.menu, SWT.PUSH);
		this.saveSong.addSelectionListener(toolBar.createActionProcessor(TGSaveFileAction.NAME));
		
		this.saveAsSong = new MenuItem(this.menu, SWT.PUSH);
		this.saveAsSong.addSelectionListener(toolBar.createActionProcessor(TGSaveAsFileAction.NAME));
		
		this.printSong = new MenuItem(this.menu, SWT.PUSH);
		this.printSong.addSelectionListener(toolBar.createActionProcessor(TGPrintAction.NAME));
		
		this.printPreviewSong = new MenuItem(this.menu, SWT.PUSH);
		this.printPreviewSong.addSelectionListener(toolBar.createActionProcessor(TGPrintPreviewAction.NAME));
		
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		this.properties = new MenuItem(this.menu, SWT.PUSH);
		this.properties.addSelectionListener(toolBar.createActionProcessor(TGOpenSongInfoDialogAction.NAME));
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void loadProperties(TGToolBar toolBar){
		this.menuItem.setToolTipText(toolBar.getText("file"));
		this.newSong.setText(toolBar.getText("file.new"));
		this.openSong.setText(toolBar.getText("file.open"));
		this.saveSong.setText(toolBar.getText("file.save"));
		this.saveAsSong.setText(toolBar.getText("file.save-as"));
		this.printSong.setText(toolBar.getText("file.print"));
		this.printPreviewSong.setText(toolBar.getText("file.print-preview"));
		this.properties.setText(toolBar.getText("composition.properties"));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.menuItem.setImage(toolBar.getIconManager().getFileNew());
		this.newSong.setImage(toolBar.getIconManager().getFileNew());
		this.openSong.setImage(toolBar.getIconManager().getFileOpen());
		this.saveSong.setImage(toolBar.getIconManager().getFileSave());
		this.saveAsSong.setImage(toolBar.getIconManager().getFileSaveAs());
		this.printSong.setImage(toolBar.getIconManager().getFilePrint());
		this.printPreviewSong.setImage(toolBar.getIconManager().getFilePrintPreview());
		this.properties.setImage(toolBar.getIconManager().getSongProperties());
	}
	
	public void updateItems(TGToolBar toolBar){
		//Nothing to do
	}
	
	public void displayMenu() {
		Rectangle rect = this.menuItem.getBounds();
		Point pt = this.menuItem.getParent().toDisplay(new Point(rect.x, rect.y));
		
		this.menu.setLocation(pt.x, pt.y + rect.height);
		this.menu.setVisible(true);
	}
}
