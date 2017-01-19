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
import org.herac.tuxguitar.app.action.impl.file.TGExportSongAction;
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
import org.herac.tuxguitar.editor.action.file.TGReadSongAction;
import org.herac.tuxguitar.editor.template.TGTemplate;
import org.herac.tuxguitar.editor.template.TGTemplateManager;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGSongExporter;
import org.herac.tuxguitar.io.base.TGSongImporter;
import org.herac.tuxguitar.io.base.TGSongReader;
import org.herac.tuxguitar.io.base.TGSongWriter;
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
	
	private List<UIMenuActionItem> readerItems;
	private List<UIMenuActionItem> writerItems;
	private List<UIMenuActionItem> importItems;
	private List<UIMenuActionItem> exportItems;
	
	public FileMenuItem(UIMenu parent) {
		this.fileMenuItem = parent.createSubMenuItem();
		this.readerItems = new ArrayList<UIMenuActionItem>();
		this.writerItems = new ArrayList<UIMenuActionItem>();
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
		TGFileFormatManager fileFormatManager = TGFileFormatManager.getInstance(this.findContext());
		List<TGSongReader> readers = fileFormatManager.findSongReaders(false);
		List<TGSongWriter> writers = fileFormatManager.findSongWriters(false);
		List<TGSongImporter> importers = fileFormatManager.getImporters();
		List<TGSongExporter> exporters = fileFormatManager.getExporters();
		
		int countImporters = (readers.size() + importers.size());
		int countExporters = (writers.size() + exporters.size());
		if( ( countImporters + countExporters ) > 0 ){
			//--SEPARATOR--
			this.fileMenuItem.getMenu().createSeparator();
			
			//--IMPORT--
			this.readerItems.clear();
			this.importItems.clear();
			if( countImporters > 0 ){
				this.importItem = this.fileMenuItem.getMenu().createSubMenuItem();
				this.addImporters(readers, importers);
			}
			
			//--EXPORT--
			this.writerItems.clear();
			this.exportItems.clear();
			if( countExporters > 0 ){
				this.exportItem = this.fileMenuItem.getMenu().createSubMenuItem();
				this.addExporters(writers, exporters);
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
	
	private void addImporters(List<TGSongReader> readers, List<TGSongImporter> importers) {
		// readers 
		for(TGSongReader reader : readers) {
			UIMenuActionItem uiMenuItem = this.importItem.getMenu().createActionItem();
			uiMenuItem.setData(TGFileFormat.class.getName(), reader.getFileFormat());
			uiMenuItem.addSelectionListener(this.createOpenFileActionProcessor(reader.getFileFormat()));
			this.readerItems.add( uiMenuItem );
		}
		
		// separator
		if(!readers.isEmpty() && !importers.isEmpty() ){
			this.importItem.getMenu().createSeparator();
		}
		
		// importers
		for(TGSongImporter importer : importers) {
			UIMenuActionItem uiMenuItem = this.importItem.getMenu().createActionItem();
			uiMenuItem.setData(TGSongImporter.class.getName(), importer);
			uiMenuItem.addSelectionListener(this.createImportSongActionProcessor(importer));
			this.importItems.add( uiMenuItem );
		}
	}
	
	private void addExporters(List<TGSongWriter> writers, List<TGSongExporter> exporters) {
		// writers
		for(TGSongWriter writer : writers) {
			UIMenuActionItem uiMenuItem = this.exportItem.getMenu().createActionItem();
			uiMenuItem.setData(TGFileFormat.class.getName(), writer.getFileFormat());
			uiMenuItem.addSelectionListener(this.createSaveAsFileActionProcessor(writer.getFileFormat()));
			this.writerItems.add( uiMenuItem );
		}
		
		// separator
		if(!writers.isEmpty() && !exporters.isEmpty() ){
			this.exportItem.getMenu().createSeparator();
		}
		
		// exporters
		for(TGSongExporter exporter : exporters){
			UIMenuActionItem uiMenuItem = this.exportItem.getMenu().createActionItem();
			uiMenuItem.setData(TGSongExporter.class.getName(), exporter);
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
	
	public TGActionProcessorListener createOpenFileActionProcessor(TGFileFormat fileFormat) {
		TGActionProcessorListener tgActionProcessorListener = this.createActionProcessor(TGOpenFileAction.NAME);
		tgActionProcessorListener.setAttribute(TGReadSongAction.ATTRIBUTE_FORMAT, fileFormat);
		return tgActionProcessorListener;
	}
	
	public TGActionProcessorListener createSaveAsFileActionProcessor(TGFileFormat fileFormat) {
		TGActionProcessorListener tgActionProcessorListener = this.createActionProcessor(TGSaveAsFileAction.NAME);
		tgActionProcessorListener.setAttribute(TGReadSongAction.ATTRIBUTE_FORMAT, fileFormat);
		return tgActionProcessorListener;
	}
	
	public TGActionProcessorListener createImportSongActionProcessor(TGSongImporter importer) {
		TGActionProcessorListener tgActionProcessorListener = this.createActionProcessor(TGImportSongAction.NAME);
		tgActionProcessorListener.setAttribute(TGImportSongAction.ATTRIBUTE_PROVIDER, importer);
		return tgActionProcessorListener;
	}
	
	public TGActionProcessorListener createExportSongActionProcessor(TGSongExporter exporter) {
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
			setMenuItemTextAndAccelerator(this.importItem, "file.import", null);
			
			for(UIMenuActionItem item : this.readerItems) {
				TGFileFormat fileFormat = item.getData(TGFileFormat.class.getName());
				item.setText(TuxGuitar.getProperty("file.import") + " " + fileFormat.getName());
			}
			
			for(UIMenuActionItem item : this.importItems) {
				TGSongImporter itemImporter = item.getData(TGSongImporter.class.getName());
				item.setText(itemImporter.getImportName());
			}
		}
		if( this.exportItem != null ){
			setMenuItemTextAndAccelerator(this.exportItem, "file.export", null);
			
			for(UIMenuActionItem item : this.writerItems) {
				TGFileFormat fileFormat = item.getData(TGFileFormat.class.getName());
				item.setText(TuxGuitar.getProperty("file.export") + " " + fileFormat.getName());
			}
			
			for(UIMenuActionItem item : this.exportItems) {
				TGSongExporter itemExporter = item.getData(TGSongExporter.class.getName());
				item.setText(itemExporter.getExportName());
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