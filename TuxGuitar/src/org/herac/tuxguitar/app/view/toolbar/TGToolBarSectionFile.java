package org.herac.tuxguitar.app.view.toolbar;

import org.herac.tuxguitar.app.action.impl.composition.TGOpenSongInfoDialogAction;
import org.herac.tuxguitar.app.action.impl.file.TGOpenFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGPrintAction;
import org.herac.tuxguitar.app.action.impl.file.TGPrintPreviewAction;
import org.herac.tuxguitar.app.action.impl.file.TGSaveAsFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGSaveFileAction;
import org.herac.tuxguitar.editor.action.file.TGLoadTemplateAction;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolMenuItem;

public class TGToolBarSectionFile implements TGToolBarSection {
	
	private UIToolMenuItem menuItem;
	private UIMenuActionItem newSong;
	private UIMenuActionItem openSong;
	private UIMenuActionItem saveSong;
	private UIMenuActionItem saveAsSong;
	private UIMenuActionItem printSong;
	private UIMenuActionItem printPreviewSong;
	private UIMenuActionItem properties;
	
	public void createSection(final TGToolBar toolBar) {
		this.menuItem = toolBar.getControl().createMenuItem();
		
		this.newSong = this.menuItem.getMenu().createActionItem();
		this.newSong.addSelectionListener(toolBar.createActionProcessor(TGLoadTemplateAction.NAME));
		
		this.openSong = this.menuItem.getMenu().createActionItem();
		this.openSong.addSelectionListener(toolBar.createActionProcessor(TGOpenFileAction.NAME));
		
		this.saveSong = this.menuItem.getMenu().createActionItem();
		this.saveSong.addSelectionListener(toolBar.createActionProcessor(TGSaveFileAction.NAME));
		
		this.saveAsSong = this.menuItem.getMenu().createActionItem();
		this.saveAsSong.addSelectionListener(toolBar.createActionProcessor(TGSaveAsFileAction.NAME));
		
		this.printSong = this.menuItem.getMenu().createActionItem();
		this.printSong.addSelectionListener(toolBar.createActionProcessor(TGPrintAction.NAME));
		
		this.printPreviewSong = this.menuItem.getMenu().createActionItem();
		this.printPreviewSong.addSelectionListener(toolBar.createActionProcessor(TGPrintPreviewAction.NAME));
		
		this.menuItem.getMenu().createSeparator();
		
		this.properties = this.menuItem.getMenu().createActionItem();
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
}
