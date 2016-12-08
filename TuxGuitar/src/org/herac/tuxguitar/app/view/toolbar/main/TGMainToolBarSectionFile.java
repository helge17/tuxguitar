package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.action.impl.composition.TGOpenSongInfoDialogAction;
import org.herac.tuxguitar.app.action.impl.file.TGOpenFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGPrintAction;
import org.herac.tuxguitar.app.action.impl.file.TGPrintPreviewAction;
import org.herac.tuxguitar.app.action.impl.file.TGSaveAsFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGSaveFileAction;
import org.herac.tuxguitar.editor.action.file.TGLoadTemplateAction;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolMenuItem;

public class TGMainToolBarSectionFile extends TGMainToolBarSection {
	
	private UIToolMenuItem menuItem;
	private UIMenuActionItem newSong;
	private UIMenuActionItem openSong;
	private UIMenuActionItem saveSong;
	private UIMenuActionItem saveAsSong;
	private UIMenuActionItem printSong;
	private UIMenuActionItem printPreviewSong;
	private UIMenuActionItem properties;
	
	public TGMainToolBarSectionFile(TGMainToolBar toolBar) {
		super(toolBar);
	}
	
	public void createSection() {
		this.menuItem = this.getToolBar().getControl().createMenuItem();
		
		this.newSong = this.menuItem.getMenu().createActionItem();
		this.newSong.addSelectionListener(this.createActionProcessor(TGLoadTemplateAction.NAME));
		
		this.openSong = this.menuItem.getMenu().createActionItem();
		this.openSong.addSelectionListener(this.createActionProcessor(TGOpenFileAction.NAME));
		
		this.saveSong = this.menuItem.getMenu().createActionItem();
		this.saveSong.addSelectionListener(this.createActionProcessor(TGSaveFileAction.NAME));
		
		this.saveAsSong = this.menuItem.getMenu().createActionItem();
		this.saveAsSong.addSelectionListener(this.createActionProcessor(TGSaveAsFileAction.NAME));
		
		this.printSong = this.menuItem.getMenu().createActionItem();
		this.printSong.addSelectionListener(this.createActionProcessor(TGPrintAction.NAME));
		
		this.printPreviewSong = this.menuItem.getMenu().createActionItem();
		this.printPreviewSong.addSelectionListener(this.createActionProcessor(TGPrintPreviewAction.NAME));
		
		this.menuItem.getMenu().createSeparator();
		
		this.properties = this.menuItem.getMenu().createActionItem();
		this.properties.addSelectionListener(this.createActionProcessor(TGOpenSongInfoDialogAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		this.menuItem.setToolTipText(this.getText("file"));
		this.newSong.setText(this.getText("file.new"));
		this.openSong.setText(this.getText("file.open"));
		this.saveSong.setText(this.getText("file.save"));
		this.saveAsSong.setText(this.getText("file.save-as"));
		this.printSong.setText(this.getText("file.print"));
		this.printPreviewSong.setText(this.getText("file.print-preview"));
		this.properties.setText(this.getText("composition.properties"));
	}
	
	public void loadIcons(){
		this.menuItem.setImage(this.getIconManager().getFileNew());
		this.newSong.setImage(this.getIconManager().getFileNew());
		this.openSong.setImage(this.getIconManager().getFileOpen());
		this.saveSong.setImage(this.getIconManager().getFileSave());
		this.saveAsSong.setImage(this.getIconManager().getFileSaveAs());
		this.printSong.setImage(this.getIconManager().getFilePrint());
		this.printPreviewSong.setImage(this.getIconManager().getFilePrintPreview());
		this.properties.setImage(this.getIconManager().getSongProperties());
	}
	
	public void updateItems(){
		//Nothing to do
	}
}
