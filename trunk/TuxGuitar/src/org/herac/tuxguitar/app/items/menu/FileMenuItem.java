/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.items.menu;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessor;
import org.herac.tuxguitar.app.action.impl.file.ExitAction;
import org.herac.tuxguitar.app.action.impl.file.ExportSongAction;
import org.herac.tuxguitar.app.action.impl.file.ImportSongAction;
import org.herac.tuxguitar.app.action.impl.file.NewFileAction;
import org.herac.tuxguitar.app.action.impl.file.OpenFileAction;
import org.herac.tuxguitar.app.action.impl.file.OpenURLAction;
import org.herac.tuxguitar.app.action.impl.file.PrintAction;
import org.herac.tuxguitar.app.action.impl.file.PrintPreviewAction;
import org.herac.tuxguitar.app.action.impl.file.SaveAsFileAction;
import org.herac.tuxguitar.app.action.impl.file.SaveFileAction;
import org.herac.tuxguitar.app.items.MenuItems;
import org.herac.tuxguitar.app.tools.template.TGTemplate;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGLocalFileImporter;
import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.base.TGRawImporter;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class FileMenuItem extends MenuItems {
	
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
	private MenuItem importItem;
	private MenuItem exportItem;
	private MenuItem printPreview;
	private MenuItem print;
	private MenuItem historyItem;
	private MenuItem[] historyFiles;
	private MenuItem exit;
	
	private List importItems;
	private List exportItems;
	
	public FileMenuItem(Shell shell,Menu parent, int style) {
		this.fileMenuItem = new MenuItem(parent, style);
		this.menu = new Menu(shell, SWT.DROP_DOWN);
		this.importItems = new ArrayList();
		this.exportItems = new ArrayList();
	}
	
	public void showItems(){
		//---------------------------------------------------
		//--NEW--
		this.newSong = new MenuItem(this.menu, SWT.CASCADE);
		this.newSongMenu = new Menu(this.menu.getShell(), SWT.DROP_DOWN);
		this.newSongDefault = new MenuItem(this.newSongMenu, SWT.PUSH);
		this.newSongDefault.addSelectionListener(new TGActionProcessor(NewFileAction.NAME));
		
		this.addNewSongTemplates();
		
		//--OPEN--
		this.open = new MenuItem(this.menu, SWT.PUSH);
		this.open.addSelectionListener(new TGActionProcessor(OpenFileAction.NAME));
		//--OPEN--
		this.openURL = new MenuItem(this.menu, SWT.PUSH);
		this.openURL.addSelectionListener(new TGActionProcessor(OpenURLAction.NAME));
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		//--SAVE--
		this.save = new MenuItem(this.menu, SWT.PUSH);
		this.save.addSelectionListener(new TGActionProcessor(SaveFileAction.NAME));
		//--SAVE AS--
		this.saveAs = new MenuItem(this.menu, SWT.PUSH);
		this.saveAs.addSelectionListener(new TGActionProcessor(SaveAsFileAction.NAME));
		
		//-- IMPORT | EXPORT --
		int countImporters = TGFileFormatManager.instance().countImporters();
		int countExporters = TGFileFormatManager.instance().countExporters();
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
		this.printPreview.addSelectionListener(new TGActionProcessor(PrintPreviewAction.NAME));
		//--PRINT--
		this.print = new MenuItem(this.menu, SWT.PUSH);
		this.print.addSelectionListener(new TGActionProcessor(PrintAction.NAME));
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
		this.exit.addSelectionListener(new TGActionProcessor(ExitAction.NAME));
		
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
	
	private void addNewSongTemplates(){		
		if( TuxGuitar.instance().getTemplateManager().countTemplates() > 0 ){
			//--SEPARATOR--
			new MenuItem(this.newSongMenu, SWT.SEPARATOR);
			
			Iterator it = TuxGuitar.instance().getTemplateManager().getTemplates();
			while( it.hasNext() ){
				TGTemplate tgTemplate = (TGTemplate)it.next();
				
				Map actionData = new HashMap();
				actionData.put(NewFileAction.PROPERTY_TEMPLATE, tgTemplate);
				
				MenuItem menuItem = new MenuItem(this.newSongMenu, SWT.PUSH);
				menuItem.setText(tgTemplate.getName());
				menuItem.setData(actionData);
				menuItem.addSelectionListener(new TGActionProcessor(NewFileAction.NAME));
			}
		}
	}
	
	private void addImporters(){
		List importersRaw = new ArrayList();
		List importersFile = new ArrayList();
		
		Iterator importers = TGFileFormatManager.instance().getImporters();
		while(importers.hasNext()){
			TGRawImporter importer = (TGRawImporter)importers.next();
			if( importer instanceof TGLocalFileImporter ){
				importersFile.add( importer );
			}else{
				importersRaw.add( importer );
			}
		}
		
		for( int i = 0 ; i < importersFile.size() ; i ++ ){
			Map actionData = new HashMap();
			actionData.put(ImportSongAction.PROPERTY_IMPORTER, importersFile.get( i ));
			
			MenuItem item = new MenuItem(this.importMenu, SWT.PUSH);
			item.setData(actionData);
			item.addSelectionListener(new TGActionProcessor(ImportSongAction.NAME));
			this.importItems.add( item );
		}
		
		//--SEPARATOR--
		if( !importersFile.isEmpty() && !importersRaw.isEmpty() ){
			new MenuItem(this.importMenu, SWT.SEPARATOR);
		}
		
		for( int i = 0 ; i < importersRaw.size() ; i ++ ){
			Map actionData = new HashMap();
			actionData.put(ImportSongAction.PROPERTY_IMPORTER, importersRaw.get( i ));
			
			MenuItem item = new MenuItem(this.importMenu, SWT.PUSH);
			item.setData(actionData);
			item.addSelectionListener(new TGActionProcessor(ImportSongAction.NAME));
			this.importItems.add( item );
		}
	}
	
	private void addExporters(){
		List exportersRaw = new ArrayList();
		List exportersFile = new ArrayList();
		
		Iterator exporters = TGFileFormatManager.instance().getExporters();
		while(exporters.hasNext()){
			TGRawExporter exporter = (TGRawExporter)exporters.next();
			if( exporter instanceof TGLocalFileExporter ){
				exportersFile.add( exporter );
			}else{
				exportersRaw.add( exporter );
			}
		}
		
		for( int i = 0 ; i < exportersFile.size() ; i ++ ){
			Map actionData = new HashMap();
			actionData.put(ExportSongAction.PROPERTY_EXPORTER, exportersFile.get( i ));
			
			MenuItem item = new MenuItem(this.exportMenu, SWT.PUSH);
			item.setData(actionData);
			item.addSelectionListener(new TGActionProcessor(ExportSongAction.NAME));
			this.exportItems.add( item );
		}
		
		//--SEPARATOR--
		if( !exportersFile.isEmpty() && !exportersRaw.isEmpty() ){
			new MenuItem(this.exportMenu, SWT.SEPARATOR);
		}
		
		for( int i = 0 ; i < exportersRaw.size() ; i ++ ){
			Map actionData = new HashMap();
			actionData.put(ExportSongAction.PROPERTY_EXPORTER, exportersRaw.get( i ));
			
			MenuItem item = new MenuItem(this.exportMenu, SWT.PUSH);
			item.setData(actionData);
			item.addSelectionListener(new TGActionProcessor(ExportSongAction.NAME));
			this.exportItems.add( item );
		}
	}
	
	private void disposeHistoryFiles(){
		for(int i = 0;i < this.historyFiles.length; i++){
			this.historyFiles[i].dispose();
		}
	}
	
	private void updateHistoryFiles(){
		List urls = TuxGuitar.instance().getFileHistory().getURLs();
		this.historyFiles = new MenuItem[urls.size()];
		for(int i = 0;i < this.historyFiles.length; i++){
			URL url = (URL)urls.get(i);
			Map actionData = new HashMap();
			actionData.put(OpenFileAction.PROPERTY_URL, url);
			this.historyFiles[i] = new MenuItem(this.historyMenu, SWT.PUSH);
			this.historyFiles[i].setText(decode(url.toString()));
			this.historyFiles[i].setData(actionData);
			this.historyFiles[i].addSelectionListener(new TGActionProcessor(OpenFileAction.NAME));
		}
		this.historyItem.setEnabled(this.historyFiles.length > 0);
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
		if(TuxGuitar.instance().getFileHistory().isChanged()){
			disposeHistoryFiles();
			updateHistoryFiles();
			TuxGuitar.instance().getFileHistory().setChanged(false);
		}
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.fileMenuItem, "file", null);
		setMenuItemTextAndAccelerator(this.newSong, "file.new", null);
		setMenuItemTextAndAccelerator(this.newSongDefault, "file.new-song.default-template", NewFileAction.NAME);
		setMenuItemTextAndAccelerator(this.open, "file.open", OpenFileAction.NAME);
		setMenuItemTextAndAccelerator(this.openURL, "file.open-url", OpenURLAction.NAME);
		setMenuItemTextAndAccelerator(this.save, "file.save", SaveFileAction.NAME);
		setMenuItemTextAndAccelerator(this.saveAs, "file.save-as", SaveAsFileAction.NAME);
		setMenuItemTextAndAccelerator(this.printPreview, "file.print-preview", PrintPreviewAction.NAME);
		setMenuItemTextAndAccelerator(this.print, "file.print", PrintAction.NAME);
		setMenuItemTextAndAccelerator(this.historyItem, "file.history", null);
		setMenuItemTextAndAccelerator(this.exit, "file.exit", ExitAction.NAME);
		
		if( this.importItem != null ){
			setMenuItemTextAndAccelerator(this.importItem, "file.import", ImportSongAction.NAME);
			
			Iterator importItems = this.importItems.iterator();
			while(importItems.hasNext()){
				MenuItem item = (MenuItem)importItems.next();
				
				Object itemImporter = ((Map)item.getData()).get(ImportSongAction.PROPERTY_IMPORTER);
				if( itemImporter instanceof TGLocalFileImporter ){
					item.setText(TuxGuitar.getProperty("file.import") + " " + ((TGRawImporter)itemImporter).getImportName());
				}else if( itemImporter instanceof TGRawImporter ){
					item.setText(((TGRawImporter)itemImporter).getImportName());
				}
			}
		}
		if( this.exportItem != null ){
			setMenuItemTextAndAccelerator(this.exportItem, "file.export", ExportSongAction.NAME);
			
			Iterator exportItems = this.exportItems.iterator();
			while(exportItems.hasNext()){
				MenuItem item = (MenuItem)exportItems.next();
				
				Object itemExporter = ((Map)item.getData()).get(ExportSongAction.PROPERTY_EXPORTER);
				if( itemExporter instanceof TGLocalFileExporter ){
					item.setText(TuxGuitar.getProperty("file.export") + " " + ((TGRawExporter)itemExporter).getExportName());
				}else if( itemExporter instanceof TGRawExporter ){
					item.setText(((TGRawExporter)itemExporter).getExportName());
				}
			}
		}
	}
	
	public void loadIcons(){
		this.newSong.setImage(TuxGuitar.instance().getIconManager().getFileNew());
		this.open.setImage(TuxGuitar.instance().getIconManager().getFileOpen());
		this.save.setImage(TuxGuitar.instance().getIconManager().getFileSave());
		this.saveAs.setImage(TuxGuitar.instance().getIconManager().getFileSaveAs());
		this.printPreview.setImage(TuxGuitar.instance().getIconManager().getFilePrintPreview());
		this.print.setImage(TuxGuitar.instance().getIconManager().getFilePrint());
	}
}