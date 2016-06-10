package org.herac.tuxguitar.app.view.menu.impl;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.file.TGCloseAllDocumentsAction;
import org.herac.tuxguitar.app.action.impl.file.TGCloseCurrentDocumentAction;
import org.herac.tuxguitar.app.action.impl.file.TGCloseOtherDocumentsAction;
import org.herac.tuxguitar.app.action.impl.file.TGExitAction;
import org.herac.tuxguitar.app.action.impl.file.TGExportFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGExportSongAction;
import org.herac.tuxguitar.app.action.impl.file.TGImportFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGImportSongAction;
import org.herac.tuxguitar.app.action.impl.file.TGOpenFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGOpenURLAction;
import org.herac.tuxguitar.app.action.impl.file.TGPrintAction;
import org.herac.tuxguitar.app.action.impl.file.TGPrintPreviewAction;
import org.herac.tuxguitar.app.action.impl.file.TGReadURLAction;
import org.herac.tuxguitar.app.action.impl.file.TGSaveAsFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGSaveFileAction;
import org.herac.tuxguitar.app.helper.TGFileHistory;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.editor.action.file.TGLoadTemplateAction;
import org.herac.tuxguitar.editor.template.TGTemplate;
import org.herac.tuxguitar.editor.template.TGTemplateManager;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGLocalFileImporter;
import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.base.TGRawImporter;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class FileMenuItem extends TGMenuItem {
	
	private UIMenuSubMenuItem fileMenuItem;
	private UIMenuSubMenuItem newSong;
	private UIMenuActionItem newSongDefault;
	private UIMenuActionItem open;
	private UIMenuActionItem openURL;
	private UIMenuActionItem save;
	private UIMenuActionItem saveAs;
	private UIMenuActionItem close;
	private UIMenuActionItem closeOthers;
	private UIMenuActionItem closeAll;
	private UIMenuSubMenuItem importItem;
	private UIMenuSubMenuItem exportItem;
	private UIMenuActionItem printPreview;
	private UIMenuActionItem print;
	private UIMenuSubMenuItem historyItem;
	private UIMenuActionItem[] historyFiles;
	private UIMenuActionItem exit;
	
	private List<UIMenuActionItem> importItems;
	private List<UIMenuActionItem> exportItems;
	
	public FileMenuItem(UIMenu parent) {
		this.fileMenuItem = parent.createSubMenuItem();
		this.importItems = new ArrayList<UIMenuActionItem>();
		this.exportItems = new ArrayList<UIMenuActionItem>();
	}
	
	public void showItems(){
		//---------------------------------------------------
		//--NEW--
		this.newSong = this.fileMenuItem.getMenu().createSubMenuItem();
		this.newSongDefault = this.newSong.getMenu().createActionItem();
		this.newSongDefault.addSelectionListener(this.createNewSongFromTemplateActionProcessor(null));
		
		this.addNewSongTemplates();
		
		//--OPEN--
		this.open = this.fileMenuItem.getMenu().createActionItem();
		this.open.addSelectionListener(this.createActionProcessor(TGOpenFileAction.NAME));
		//--OPEN--
		this.openURL = this.fileMenuItem.getMenu().createActionItem();
		this.openURL.addSelectionListener(this.createActionProcessor(TGOpenURLAction.NAME));
		//--SEPARATOR--
		this.fileMenuItem.getMenu().createSeparator();
		//--CLOSE--
		this.close = this.fileMenuItem.getMenu().createActionItem();
		this.close.addSelectionListener(this.createActionProcessor(TGCloseCurrentDocumentAction.NAME));
		//--CLOSE OTHERS--
		this.closeOthers = this.fileMenuItem.getMenu().createActionItem();
		this.closeOthers.addSelectionListener(this.createActionProcessor(TGCloseOtherDocumentsAction.NAME));
		//--CLOSE ALL--
		this.closeAll = this.fileMenuItem.getMenu().createActionItem();
		this.closeAll.addSelectionListener(this.createActionProcessor(TGCloseAllDocumentsAction.NAME));
		//--SEPARATOR--
		this.fileMenuItem.getMenu().createSeparator();
		//--SAVE--
		this.save = this.fileMenuItem.getMenu().createActionItem();
		this.save.addSelectionListener(this.createActionProcessor(TGSaveFileAction.NAME));
		//--SAVE AS--
		this.saveAs = this.fileMenuItem.getMenu().createActionItem();
		this.saveAs.addSelectionListener(this.createActionProcessor(TGSaveAsFileAction.NAME));
		
		//-- IMPORT | EXPORT --
		int countImporters = TuxGuitar.getInstance().getFileFormatManager().countImporters();
		int countExporters = TuxGuitar.getInstance().getFileFormatManager().countExporters();
		if( ( countImporters + countExporters ) > 0 ){
			//--SEPARATOR--
			this.fileMenuItem.getMenu().createSeparator();
			
			//--IMPORT--
			this.importItems.clear();
			if( countImporters > 0 ){
				this.importItem = this.fileMenuItem.getMenu().createSubMenuItem();
				this.addImporters();
			}
			
			//--EXPORT--
			this.exportItems.clear();
			if( countExporters > 0 ){
				this.exportItem = this.fileMenuItem.getMenu().createSubMenuItem();
				this.addExporters();
			}
		}
		
		//--SEPARATOR--
		this.fileMenuItem.getMenu().createSeparator();
		
		//--PRINT PREVIEW--
		this.printPreview = this.fileMenuItem.getMenu().createActionItem();
		this.printPreview.addSelectionListener(this.createActionProcessor(TGPrintPreviewAction.NAME));
		//--PRINT--
		this.print = this.fileMenuItem.getMenu().createActionItem();
		this.print.addSelectionListener(this.createActionProcessor(TGPrintAction.NAME));
		//--SEPARATOR--
		this.fileMenuItem.getMenu().createSeparator();
		
		//--HISTORY--
		this.historyItem = this.fileMenuItem.getMenu().createSubMenuItem();
		this.updateHistoryFiles();
		//--SEPARATOR--
		this.fileMenuItem.getMenu().createSeparator();
		//--EXIT--
		this.exit = this.fileMenuItem.getMenu().createActionItem();
		this.exit.addSelectionListener(this.createActionProcessor(TGExitAction.NAME));
		
		//---------------------------------------------------
		
		this.loadIcons();
		this.loadProperties();
	}
	
	private void addNewSongTemplates() {
		TGTemplateManager templateManager = TGTemplateManager.getInstance(this.findContext());
		if( templateManager.countTemplates() > 0 ){
			//--SEPARATOR--
			this.newSong.getMenu().createSeparator();
			
			Iterator<TGTemplate> it = templateManager.getTemplates();
			while( it.hasNext() ){
				TGTemplate tgTemplate = (TGTemplate)it.next();
				
				UIMenuActionItem uiMenuItem = this.newSong.getMenu().createActionItem();
				uiMenuItem.setText(tgTemplate.getName());
				uiMenuItem.addSelectionListener(this.createNewSongFromTemplateActionProcessor(tgTemplate));
			}
		}
	}
	
	private void addImporters(){
		List<TGRawImporter> importersRaw = new ArrayList<TGRawImporter>();
		List<TGLocalFileImporter> importersFile = new ArrayList<TGLocalFileImporter>();
		
		Iterator<TGRawImporter> importers = TuxGuitar.getInstance().getFileFormatManager().getImporters();
		while(importers.hasNext()){
			TGRawImporter importer = (TGRawImporter)importers.next();
			if( importer instanceof TGLocalFileImporter ){
				importersFile.add( (TGLocalFileImporter) importer );
			}else{
				importersRaw.add( importer );
			}
		}
		
		for( int i = 0 ; i < importersFile.size() ; i ++ ){
			TGLocalFileImporter importer = importersFile.get( i );
			UIMenuActionItem uiMenuItem = this.importItem.getMenu().createActionItem();
			uiMenuItem.setData(TGRawImporter.class.getName(), importer);
			uiMenuItem.addSelectionListener(this.createImportFileActionProcessor(importer));
			this.importItems.add( uiMenuItem );
		}
		
		//--SEPARATOR--
		if( !importersFile.isEmpty() && !importersRaw.isEmpty() ){
			this.importItem.getMenu().createSeparator();
		}
		
		for( int i = 0 ; i < importersRaw.size() ; i ++ ){
			TGRawImporter importer = importersRaw.get( i );
			UIMenuActionItem uiMenuItem = this.importItem.getMenu().createActionItem();
			uiMenuItem.setData(TGRawImporter.class.getName(), importer);
			uiMenuItem.addSelectionListener(this.createImportSongActionProcessor(importer));
			this.importItems.add( uiMenuItem );
		}
	}
	
	private void addExporters(){
		List<TGRawExporter> exportersRaw = new ArrayList<TGRawExporter>();
		List<TGLocalFileExporter> exportersFile = new ArrayList<TGLocalFileExporter>();
		
		Iterator<TGRawExporter> exporters = TuxGuitar.getInstance().getFileFormatManager().getExporters();
		while(exporters.hasNext()){
			TGRawExporter exporter = (TGRawExporter)exporters.next();
			if( exporter instanceof TGLocalFileExporter ){
				exportersFile.add( (TGLocalFileExporter) exporter );
			}else{
				exportersRaw.add( exporter );
			}
		}
		
		for( int i = 0 ; i < exportersFile.size() ; i ++ ){
			TGLocalFileExporter exporter = exportersFile.get( i );
			UIMenuActionItem uiMenuItem = this.exportItem.getMenu().createActionItem();
			uiMenuItem.setData(TGRawExporter.class.getName(), exporter);
			uiMenuItem.addSelectionListener(this.createExportFileActionProcessor(exporter));
			this.exportItems.add( uiMenuItem );
		}
		
		//--SEPARATOR--
		if( !exportersFile.isEmpty() && !exportersRaw.isEmpty() ){
			this.exportItem.getMenu().createSeparator();
		}
		
		for( int i = 0 ; i < exportersRaw.size() ; i ++ ){
			TGRawExporter exporter = exportersRaw.get( i );
			UIMenuActionItem uiMenuItem = this.exportItem.getMenu().createActionItem();
			uiMenuItem.setData(TGRawExporter.class.getName(), exporter);
			uiMenuItem.addSelectionListener(this.createExportSongActionProcessor(exporter));
			this.exportItems.add( uiMenuItem );
		}
	}
	
	private void disposeHistoryFiles(){
		for(int i = 0;i < this.historyFiles.length; i++){
			this.historyFiles[i].dispose();
		}
	}
	
	private void updateHistoryFiles(){
		List<URL> urls = TGFileHistory.getInstance(this.findContext()).getURLs();
		this.historyFiles = new UIMenuActionItem[urls.size()];
		for(int i = 0;i < this.historyFiles.length; i++){
			URL url = (URL)urls.get(i);
			this.historyFiles[i] = this.historyItem.getMenu().createActionItem();
			this.historyFiles[i].setText(decode(url.toString()));
			this.historyFiles[i].addSelectionListener(this.createOpenUrlActionProcessor(url));
		}
		this.historyItem.setEnabled(this.historyFiles.length > 0);
	}
	
	public TGActionProcessorListener createOpenUrlActionProcessor(URL url) {
		TGActionProcessorListener tgActionProcessorListener = this.createActionProcessor(TGReadURLAction.NAME);
		tgActionProcessorListener.setAttribute(TGReadURLAction.ATTRIBUTE_URL, url);
		return tgActionProcessorListener;
	}
	
	public TGActionProcessorListener createNewSongFromTemplateActionProcessor(TGTemplate template) {
		TGActionProcessorListener tgActionProcessorListener = this.createActionProcessor(TGLoadTemplateAction.NAME);
		tgActionProcessorListener.setAttribute(TGLoadTemplateAction.ATTRIBUTE_TEMPLATE, template);
		return tgActionProcessorListener;
	}
	
	public TGActionProcessorListener createImportFileActionProcessor(TGLocalFileImporter importer) {
		TGActionProcessorListener tgActionProcessorListener = this.createActionProcessor(TGImportFileAction.NAME);
		tgActionProcessorListener.setAttribute(TGImportFileAction.ATTRIBUTE_PROVIDER, importer);
		return tgActionProcessorListener;
	}
	
	public TGActionProcessorListener createImportSongActionProcessor(TGRawImporter importer) {
		TGActionProcessorListener tgActionProcessorListener = this.createActionProcessor(TGImportSongAction.NAME);
		tgActionProcessorListener.setAttribute(TGImportSongAction.ATTRIBUTE_PROVIDER, importer);
		return tgActionProcessorListener;
	}
	
	public TGActionProcessorListener createExportFileActionProcessor(TGLocalFileExporter exporter) {
		TGActionProcessorListener tgActionProcessorListener = this.createActionProcessor(TGExportFileAction.NAME);
		tgActionProcessorListener.setAttribute(TGExportFileAction.ATTRIBUTE_PROVIDER, exporter);
		return tgActionProcessorListener;
	}
	
	public TGActionProcessorListener createExportSongActionProcessor(TGRawExporter exporter) {
		TGActionProcessorListener tgActionProcessorListener = this.createActionProcessor(TGExportSongAction.NAME);
		tgActionProcessorListener.setAttribute(TGExportSongAction.ATTRIBUTE_PROVIDER, exporter);
		return tgActionProcessorListener;
	}
	
	private String decode(String url){
		try {
			return URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	public void update(){
		TGFileHistory fileHistory = TGFileHistory.getInstance(this.findContext());
		if( fileHistory.isChanged()){
			disposeHistoryFiles();
			updateHistoryFiles();
			fileHistory.setChanged(false);
		}
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.fileMenuItem, "file", null);
		setMenuItemTextAndAccelerator(this.newSong, "file.new", null);
		setMenuItemTextAndAccelerator(this.newSongDefault, "file.new-song.default-template", TGLoadTemplateAction.NAME);
		setMenuItemTextAndAccelerator(this.open, "file.open", TGOpenFileAction.NAME);
		setMenuItemTextAndAccelerator(this.openURL, "file.open-url", TGOpenURLAction.NAME);
		setMenuItemTextAndAccelerator(this.close, "file.close", TGCloseCurrentDocumentAction.NAME);
		setMenuItemTextAndAccelerator(this.closeOthers, "file.close-others", TGCloseOtherDocumentsAction.NAME);
		setMenuItemTextAndAccelerator(this.closeAll, "file.close-all", TGCloseAllDocumentsAction.NAME);
		setMenuItemTextAndAccelerator(this.save, "file.save", TGSaveFileAction.NAME);
		setMenuItemTextAndAccelerator(this.saveAs, "file.save-as", TGSaveAsFileAction.NAME);
		setMenuItemTextAndAccelerator(this.printPreview, "file.print-preview", TGPrintPreviewAction.NAME);
		setMenuItemTextAndAccelerator(this.print, "file.print", TGPrintAction.NAME);
		setMenuItemTextAndAccelerator(this.historyItem, "file.history", null);
		setMenuItemTextAndAccelerator(this.exit, "file.exit", TGExitAction.NAME);
		
		if( this.importItem != null ){
			setMenuItemTextAndAccelerator(this.importItem, "file.import", TGImportFileAction.NAME);
			
			for(UIMenuActionItem item : this.importItems) {
				TGRawImporter itemImporter = item.getData(TGRawImporter.class.getName());
				if( itemImporter instanceof TGLocalFileImporter ){
					item.setText(TuxGuitar.getProperty("file.import") + " " + ((TGRawImporter)itemImporter).getImportName());
				} else {
					item.setText(((TGRawImporter)itemImporter).getImportName());
				}
			}
		}
		if( this.exportItem != null ){
			setMenuItemTextAndAccelerator(this.exportItem, "file.export", TGExportFileAction.NAME);
			
			for(UIMenuActionItem item : this.exportItems) {
				TGRawExporter itemExporter = item.getData(TGRawExporter.class.getName());
				if( itemExporter instanceof TGLocalFileExporter ){
					item.setText(TuxGuitar.getProperty("file.export") + " " + ((TGRawExporter)itemExporter).getExportName());
				} else {
					item.setText(((TGRawExporter)itemExporter).getExportName());
				}
			}
		}
	}
	
	public void loadIcons(){
		this.newSong.setImage(TuxGuitar.getInstance().getIconManager().getFileNew());
		this.open.setImage(TuxGuitar.getInstance().getIconManager().getFileOpen());
		this.save.setImage(TuxGuitar.getInstance().getIconManager().getFileSave());
		this.saveAs.setImage(TuxGuitar.getInstance().getIconManager().getFileSaveAs());
		this.printPreview.setImage(TuxGuitar.getInstance().getIconManager().getFilePrintPreview());
		this.print.setImage(TuxGuitar.getInstance().getIconManager().getFilePrint());
	}
}