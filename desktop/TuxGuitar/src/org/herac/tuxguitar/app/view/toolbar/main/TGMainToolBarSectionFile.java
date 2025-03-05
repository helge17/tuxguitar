package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.app.action.impl.file.TGOpenFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGPrintAction;
import org.herac.tuxguitar.app.action.impl.file.TGPrintPreviewAction;
import org.herac.tuxguitar.app.action.impl.file.TGSaveAsFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGSaveFileAction;
import org.herac.tuxguitar.editor.action.file.TGLoadTemplateAction;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.util.TGContext;

public class TGMainToolBarSectionFile extends TGMainToolBarSection {
	
	private UIToolActionItem newSong;
	private UIToolActionItem openSong;
	private UIToolActionItem saveSong;
	private UIToolActionItem saveAsSong;
	private UIToolActionItem printSong;
	private UIToolActionItem printPreviewSong;
	
	public TGMainToolBarSectionFile(TGContext context, UIToolBar toolBar) {
		super(context, toolBar);
	}
	
	public void createSection() {
		this.newSong = this.getToolBar().createActionItem();
		this.newSong.addSelectionListener(this.createActionProcessor(TGLoadTemplateAction.NAME));
		
		this.openSong = this.getToolBar().createActionItem();
		this.openSong.addSelectionListener(this.createActionProcessor(TGOpenFileAction.NAME));
		
		this.saveSong = this.getToolBar().createActionItem();
		this.saveSong.addSelectionListener(this.createActionProcessor(TGSaveFileAction.NAME));
		
		this.saveAsSong = this.getToolBar().createActionItem();
		this.saveAsSong.addSelectionListener(this.createActionProcessor(TGSaveAsFileAction.NAME));
		
		this.printPreviewSong = this.getToolBar().createActionItem();
		this.printPreviewSong.addSelectionListener(this.createActionProcessor(TGPrintPreviewAction.NAME));
		
		this.printSong = this.getToolBar().createActionItem();
		this.printSong.addSelectionListener(this.createActionProcessor(TGPrintAction.NAME));

		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		this.newSong.setToolTipText(this.getText("file.new"));
		this.openSong.setToolTipText(this.getText("file.open"));
		this.saveSong.setToolTipText(this.getText("file.save"));
		this.saveAsSong.setToolTipText(this.getText("file.save-as"));
		this.printPreviewSong.setToolTipText(this.getText("file.print-preview"));
		this.printSong.setToolTipText(this.getText("file.print"));
	}
	
	public void loadIcons(){
		this.newSong.setImage(this.getIconManager().getFileNew());
		this.openSong.setImage(this.getIconManager().getFileOpen());
		this.saveSong.setImage(this.getIconManager().getFileSave());
		this.saveAsSong.setImage(this.getIconManager().getFileSaveAs());
		this.printPreviewSong.setImage(this.getIconManager().getFilePrintPreview());
		this.printSong.setImage(this.getIconManager().getFilePrint());
	}
	
	public void updateItems(){
		//Nothing to do
	}
}
