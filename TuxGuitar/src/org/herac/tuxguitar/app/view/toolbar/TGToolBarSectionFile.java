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
		this.menuItem.setToolTipText(toolBar.getText("file"));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.menuItem.setImage(toolBar.getIconManager().getFileNew());
	}
	
	public void updateItems(TGToolBar toolBar){
		//Nothing to do
	}
	
	public void createMenu(TGToolBar toolBar, ToolItem item) {
		Rectangle rect = item.getBounds();
		Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
		
		Menu menu = new Menu(item.getParent().getShell());
		
		MenuItem newSong = new MenuItem(menu, SWT.PUSH);
		newSong.addSelectionListener(toolBar.createActionProcessor(TGLoadTemplateAction.NAME));
		newSong.setText(toolBar.getText("file.new"));
		newSong.setImage(toolBar.getIconManager().getFileNew());
		
		MenuItem openSong = new MenuItem(menu, SWT.PUSH);
		openSong.addSelectionListener(toolBar.createActionProcessor(TGOpenFileAction.NAME));
		openSong.setText(toolBar.getText("file.open"));
		openSong.setImage(toolBar.getIconManager().getFileOpen());
		
		MenuItem saveSong = new MenuItem(menu, SWT.PUSH);
		saveSong.addSelectionListener(toolBar.createActionProcessor(TGSaveFileAction.NAME));
		saveSong.setText(toolBar.getText("file.save"));
		saveSong.setImage(toolBar.getIconManager().getFileSave());
		
		MenuItem saveAsSong = new MenuItem(menu, SWT.PUSH);
		saveAsSong.addSelectionListener(toolBar.createActionProcessor(TGSaveAsFileAction.NAME));
		saveAsSong.setText(toolBar.getText("file.save-as"));
		saveAsSong.setImage(toolBar.getIconManager().getFileSaveAs());
		
		MenuItem printSong = new MenuItem(menu, SWT.PUSH);
		printSong.addSelectionListener(toolBar.createActionProcessor(TGPrintAction.NAME));
		printSong.setText(toolBar.getText("file.print"));
		printSong.setImage(toolBar.getIconManager().getFilePrint());
		
		MenuItem printPreviewSong = new MenuItem(menu, SWT.PUSH);
		printPreviewSong.addSelectionListener(toolBar.createActionProcessor(TGPrintPreviewAction.NAME));
		printPreviewSong.setText(toolBar.getText("file.print-preview"));
		printPreviewSong.setImage(toolBar.getIconManager().getFilePrintPreview());
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		MenuItem properties = new MenuItem(menu, SWT.PUSH);
		properties.addSelectionListener(toolBar.createActionProcessor(TGOpenSongInfoDialogAction.NAME));
		properties.setText(toolBar.getText("composition.properties"));
		properties.setImage(toolBar.getIconManager().getSongProperties());
		
		menu.setLocation(pt.x, pt.y + rect.height);
		menu.setVisible(true);
	}
}
