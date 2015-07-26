/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.view.items.tool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.file.TGPrintAction;
import org.herac.tuxguitar.app.action.impl.file.TGPrintPreviewAction;
import org.herac.tuxguitar.app.action.impl.file.TGLoadTemplateAction;
import org.herac.tuxguitar.app.action.impl.file.TGOpenFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGSaveAsFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGSaveFileAction;
import org.herac.tuxguitar.app.view.items.ToolItems;
/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FileToolItems extends ToolItems {
	public static final String NAME = "file.items";
	private ToolItem newSong;
	private ToolItem openSong;
	private ToolItem saveSong;
	private ToolItem saveAsSong;
	private ToolItem printSong;
	private ToolItem printPreviewSong;
	
	public FileToolItems(){
		super(NAME);
	}
	
	public void showItems(ToolBar toolBar){
		this.newSong = new ToolItem(toolBar, SWT.PUSH);
		this.newSong.addSelectionListener(this.createActionProcessor(TGLoadTemplateAction.NAME));
		
		this.openSong = new ToolItem(toolBar, SWT.PUSH);
		this.openSong.addSelectionListener(this.createActionProcessor(TGOpenFileAction.NAME));
		
		this.saveSong = new ToolItem(toolBar, SWT.PUSH);
		this.saveSong.addSelectionListener(this.createActionProcessor(TGSaveFileAction.NAME));
		
		this.saveAsSong = new ToolItem(toolBar, SWT.PUSH);
		this.saveAsSong.addSelectionListener(this.createActionProcessor(TGSaveAsFileAction.NAME));
		
		this.printSong = new ToolItem(toolBar, SWT.PUSH);
		this.printSong.addSelectionListener(this.createActionProcessor(TGPrintAction.NAME));
		
		this.printPreviewSong = new ToolItem(toolBar, SWT.PUSH);
		this.printPreviewSong.addSelectionListener(this.createActionProcessor(TGPrintPreviewAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		//Nothing to do
	}
	
	public void loadProperties(){
		this.newSong.setToolTipText(TuxGuitar.getProperty("file.new"));
		this.openSong.setToolTipText(TuxGuitar.getProperty("file.open"));
		this.saveSong.setToolTipText(TuxGuitar.getProperty("file.save"));
		this.saveAsSong.setToolTipText(TuxGuitar.getProperty("file.save-as"));
		this.printSong.setToolTipText(TuxGuitar.getProperty("file.print"));
		this.printPreviewSong.setToolTipText(TuxGuitar.getProperty("file.print-preview"));
	}
	
	public void loadIcons(){
		this.newSong.setImage(TuxGuitar.getInstance().getIconManager().getFileNew());
		this.openSong.setImage(TuxGuitar.getInstance().getIconManager().getFileOpen());
		this.saveSong.setImage(TuxGuitar.getInstance().getIconManager().getFileSave());
		this.saveAsSong.setImage(TuxGuitar.getInstance().getIconManager().getFileSaveAs());
		this.printSong.setImage(TuxGuitar.getInstance().getIconManager().getFilePrint());
		this.printPreviewSong.setImage(TuxGuitar.getInstance().getIconManager().getFilePrintPreview());
	}
}
