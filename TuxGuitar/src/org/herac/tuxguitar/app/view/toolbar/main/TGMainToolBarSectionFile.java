package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.action.impl.file.TGOpenFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGPrintAction;
import org.herac.tuxguitar.app.action.impl.file.TGPrintPreviewAction;
import org.herac.tuxguitar.app.action.impl.file.TGSaveAsFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGSaveFileAction;
import org.herac.tuxguitar.editor.action.file.TGLoadTemplateAction;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;

public class TGMainToolBarSectionFile extends TGMainToolBarSection {
	
	private UIToolActionItem newSong;
	private UIToolActionItem openSong;
	private UIToolActionItem saveSong;
	private UIToolActionItem saveAsSong;
	private UIToolActionItem printSong;
	private UIToolActionItem printPreviewSong;
	
	public TGMainToolBarSectionFile(TGMainToolBar toolBar) {
		super(toolBar);
	}
	
	public void createSection() {
		this.newSong = this.getToolBar().getControl().createActionItem();
		this.newSong.addSelectionListener(this.createActionProcessor(TGLoadTemplateAction.NAME));
		
		this.openSong = this.getToolBar().getControl().createActionItem();
		this.openSong.addSelectionListener(this.createActionProcessor(TGOpenFileAction.NAME));
		
		this.saveSong = this.getToolBar().getControl().createActionItem();
		this.saveSong.addSelectionListener(this.createActionProcessor(TGSaveFileAction.NAME));
		
		this.saveAsSong = this.getToolBar().getControl().createActionItem();
		this.saveAsSong.addSelectionListener(this.createActionProcessor(TGSaveAsFileAction.NAME));
		
		this.printSong = this.getToolBar().getControl().createActionItem();
		this.printSong.addSelectionListener(this.createActionProcessor(TGPrintAction.NAME));
		
		this.printPreviewSong = this.getToolBar().getControl().createActionItem();
		this.printPreviewSong.addSelectionListener(this.createActionProcessor(TGPrintPreviewAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		this.newSong.setToolTipText(this.getText("file.new"));
		this.openSong.setToolTipText(this.getText("file.open"));
		this.saveSong.setToolTipText(this.getText("file.save"));
		this.saveAsSong.setToolTipText(this.getText("file.save-as"));
		this.printSong.setToolTipText(this.getText("file.print"));
		this.printPreviewSong.setToolTipText(this.getText("file.print-preview"));
	}
	
	public void loadIcons(){
		this.newSong.setImage(this.getIconManager().getFileNew());
		this.openSong.setImage(this.getIconManager().getFileOpen());
		this.saveSong.setImage(this.getIconManager().getFileSave());
		this.saveAsSong.setImage(this.getIconManager().getFileSaveAs());
		this.printSong.setImage(this.getIconManager().getFilePrint());
		this.printPreviewSong.setImage(this.getIconManager().getFilePrintPreview());
	}
	
	public void updateItems(){
		//Nothing to do
	}
}
