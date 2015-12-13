/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.view.menu.impl;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
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

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class FileMenuItem extends TGMenuItem {
	
	private MenuItem fileMenuItem;
	private Menu menu;
	private Menu newSongMenu;
	private Menu importMenu; 
	private Menu exportMenu; 
	private Menu historyMenu;
	private MenuItem newSong;
	private MenuItem newSongDefault;
	private MenuItem open;
	private MenuItem openURL;
	private MenuItem save;
	private MenuItem saveAs;
	private MenuItem close;
	private MenuItem closeOthers;
	private MenuItem closeAll;
	private MenuItem importItem;
	private MenuItem exportItem;
	private MenuItem printPreview;
	private MenuItem print;
	private MenuItem historyItem;
	private MenuItem[] historyFiles;
	private MenuItem exit;
	
	private List<MenuItem> importItems;
	private List<MenuItem> exportItems;
	
	public FileMenuItem(Shell shell,Menu parent, int style) {
		this.fileMenuItem = new MenuItem(parent, style);
		this.menu = new Menu(shell, SWT.DROP_DOWN);
		this.importItems = new ArrayList<MenuItem>();
		this.exportItems = new ArrayList<MenuItem>();
	}
	
	public void showItems(){
		//---------------------------------------------------
		//--NEW--
		this.newSong = new MenuItem(this.menu, SWT.CASCADE);
		this.newSongMenu = new Menu(this.menu.getShell(), SWT.DROP_DOWN);
		this.newSongDefault = new MenuItem(this.newSongMenu, SWT.PUSH);
		this.newSongDefault.addSelectionListener(this.createNewSongFromTemplateActionProcessor(null));
		
		this.addNewSongTemplates();
		
		//--OPEN--
		this.open = new MenuItem(this.menu, SWT.PUSH);
		this.open.addSelectionListener(this.createActionProcessor(TGOpenFileAction.NAME));
		//--OPEN--
		this.openURL = new MenuItem(this.menu, SWT.PUSH);
		this.openURL.addSelectionListener(this.createActionProcessor(TGOpenURLAction.NAME));
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		//--CLOSE--
		this.close = new MenuItem(this.menu, SWT.PUSH);
		this.close.addSelectionListener(this.createActionProcessor(TGCloseCurrentDocumentAction.NAME));
		//--CLOSE OTHERS--
		this.closeOthers = new MenuItem(this.menu, SWT.PUSH);
		this.closeOthers.addSelectionListener(this.createActionProcessor(TGCloseOtherDocumentsAction.NAME));
		//--CLOSE ALL--
		this.closeAll = new MenuItem(this.menu, SWT.PUSH);
		this.closeAll.addSelectionListener(this.createActionProcessor(TGCloseAllDocumentsAction.NAME));
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		//--SAVE--
		this.save = new MenuItem(this.menu, SWT.PUSH);
		this.save.addSelectionListener(this.createActionProcessor(TGSaveFileAction.NAME));
		//--SAVE AS--
		this.saveAs = new MenuItem(this.menu, SWT.PUSH);
		this.saveAs.addSelectionListener(this.createActionProcessor(TGSaveAsFileAction.NAME));
		
		//-- IMPORT | EXPORT --
		int countImporters = TuxGuitar.getInstance().getFileFormatManager().countImporters();
		int countExporters = TuxGuitar.getInstance().getFileFormatManager().countExporters();
		if( ( countImporters + countExporters ) > 0 ){
			//--SEPARATOR--
			new MenuItem(this.menu, SWT.SEPARATOR);
			
			//--IMPORT--
			this.importItems.clear();
			if( countImporters > 0 ){
				this.importItem = new MenuItem(this.menu,SWT.CASCADE);
				this.importMenu = new Menu(this.menu.getShell(), SWT.DROP_DOWN);
				this.addImporters();
			}
			
			//--EXPORT--
			this.exportItems.clear();
			if( countExporters > 0 ){
				this.exportItem = new MenuItem(this.menu,SWT.CASCADE);
				this.exportMenu = new Menu(this.menu.getShell(), SWT.DROP_DOWN);
				this.addExporters();
			}
		}
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--PRINT PREVIEW--
		this.printPreview = new MenuItem(this.menu, SWT.PUSH);
		this.printPreview.addSelectionListener(this.createActionProcessor(TGPrintPreviewAction.NAME));
		//--PRINT--
		this.print = new MenuItem(this.menu, SWT.PUSH);
		this.print.addSelectionListener(this.createActionProcessor(TGPrintAction.NAME));
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--HISTORY--
		this.historyItem = new MenuItem(this.menu,SWT.CASCADE);
		this.historyMenu = new Menu(this.menu.getShell(), SWT.DROP_DOWN);
		this.updateHistoryFiles();
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		//--EXIT--
		this.exit = new MenuItem(this.menu, SWT.PUSH);
		this.exit.addSelectionListener(this.createActionProcessor(TGExitAction.NAME));
		
		//---------------------------------------------------
		if( this.importItem != null ){
			this.importItem.setMenu(this.importMenu);
		}
		if( this.exportItem != null ){
			this.exportItem.setMenu(this.exportMenu);
		}
		this.newSong.setMenu(this.newSongMenu);
		this.historyItem.setMenu(this.historyMenu);
		this.fileMenuItem.setMenu(this.menu);
		
		this.loadIcons();
		this.loadProperties();
	}
	
	private void addNewSongTemplates() {
		TGTemplateManager templateManager = TGTemplateManager.getInstance(this.findContext());
		if( templateManager.countTemplates() > 0 ){
			//--SEPARATOR--
			new MenuItem(this.newSongMenu, SWT.SEPARATOR);
			
			Iterator<TGTemplate> it = templateManager.getTemplates();
			while( it.hasNext() ){
				TGTemplate tgTemplate = (TGTemplate)it.next();
				
				MenuItem menuItem = new MenuItem(this.newSongMenu, SWT.PUSH);
				menuItem.setText(tgTemplate.getName());
				menuItem.addSelectionListener(this.createNewSongFromTemplateActionProcessor(tgTemplate));
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
			MenuItem item = new MenuItem(this.importMenu, SWT.PUSH);
			item.setData(importer);
			item.addSelectionListener(this.createImportFileActionProcessor(importer));
			this.importItems.add( item );
		}
		
		//--SEPARATOR--
		if( !importersFile.isEmpty() && !importersRaw.isEmpty() ){
			new MenuItem(this.importMenu, SWT.SEPARATOR);
		}
		
		for( int i = 0 ; i < importersRaw.size() ; i ++ ){
			TGRawImporter importer = importersRaw.get( i );
			MenuItem item = new MenuItem(this.importMenu, SWT.PUSH);
			item.setData(importer);
			item.addSelectionListener(this.createImportSongActionProcessor(importer));
			this.importItems.add( item );
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
			MenuItem item = new MenuItem(this.exportMenu, SWT.PUSH);
			item.setData(exporter);
			item.addSelectionListener(this.createExportFileActionProcessor(exporter));
			this.exportItems.add( item );
		}
		
		//--SEPARATOR--
		if( !exportersFile.isEmpty() && !exportersRaw.isEmpty() ){
			new MenuItem(this.exportMenu, SWT.SEPARATOR);
		}
		
		for( int i = 0 ; i < exportersRaw.size() ; i ++ ){
			TGRawExporter exporter = exportersRaw.get( i );
			MenuItem item = new MenuItem(this.exportMenu, SWT.PUSH);
			item.setData(exporter);
			item.addSelectionListener(this.createExportSongActionProcessor(exporter));
			this.exportItems.add( item );
		}
	}
	
	private void disposeHistoryFiles(){
		for(int i = 0;i < this.historyFiles.length; i++){
			this.historyFiles[i].dispose();
		}
	}
	
	private void updateHistoryFiles(){
		List<URL> urls = TGFileHistory.getInstance(this.findContext()).getURLs();
		this.historyFiles = new MenuItem[urls.size()];
		for(int i = 0;i < this.historyFiles.length; i++){
			URL url = (URL)urls.get(i);
			this.historyFiles[i] = new MenuItem(this.historyMenu, SWT.PUSH);
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
			
			Iterator<MenuItem> importItems = this.importItems.iterator();
			while(importItems.hasNext()){
				MenuItem item = (MenuItem)importItems.next();
				
				TGRawImporter itemImporter = (TGRawImporter) item.getData();
				if( itemImporter instanceof TGLocalFileImporter ){
					item.setText(TuxGuitar.getProperty("file.import") + " " + ((TGRawImporter)itemImporter).getImportName());
				} else {
					item.setText(((TGRawImporter)itemImporter).getImportName());
				}
			}
		}
		if( this.exportItem != null ){
			setMenuItemTextAndAccelerator(this.exportItem, "file.export", TGExportFileAction.NAME);
			
			Iterator<MenuItem> exportItems = this.exportItems.iterator();
			while(exportItems.hasNext()){
				MenuItem item = (MenuItem)exportItems.next();
				
				TGRawExporter itemExporter = (TGRawExporter) item.getData();
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