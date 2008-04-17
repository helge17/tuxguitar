/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.items.menu;

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
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.file.ExitAction;
import org.herac.tuxguitar.gui.actions.file.ExportSongAction;
import org.herac.tuxguitar.gui.actions.file.ImportSongAction;
import org.herac.tuxguitar.gui.actions.file.NewFileAction;
import org.herac.tuxguitar.gui.actions.file.OpenFileAction;
import org.herac.tuxguitar.gui.actions.file.OpenURLAction;
import org.herac.tuxguitar.gui.actions.file.PrintAction;
import org.herac.tuxguitar.gui.actions.file.PrintPreviewAction;
import org.herac.tuxguitar.gui.actions.file.SaveAsFileAction;
import org.herac.tuxguitar.gui.actions.file.SaveFileAction;
import org.herac.tuxguitar.gui.items.MenuItems;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGSongExporter;
import org.herac.tuxguitar.io.base.TGSongImporter;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class FileMenuItem implements MenuItems {
    private MenuItem fileMenuItem;
    private Menu menu;
    private Menu importMenu; 
    private Menu exportMenu; 
    private Menu historyMenu;
    private MenuItem newSong;
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
        this.newSong = new MenuItem(this.menu, SWT.PUSH);
        this.newSong.addSelectionListener(TuxGuitar.instance().getAction(NewFileAction.NAME));
        //--OPEN--
        this.open = new MenuItem(this.menu, SWT.PUSH);
        this.open.addSelectionListener(TuxGuitar.instance().getAction(OpenFileAction.NAME));
        //--OPEN--
        this.openURL = new MenuItem(this.menu, SWT.PUSH);
        this.openURL.addSelectionListener(TuxGuitar.instance().getAction(OpenURLAction.NAME));
        //--SEPARATOR--
        new MenuItem(this.menu, SWT.SEPARATOR);
        //--SAVE--
        this.save = new MenuItem(this.menu, SWT.PUSH);
        this.save.addSelectionListener(TuxGuitar.instance().getAction(SaveFileAction.NAME));       
        //--SAVE AS--
        this.saveAs = new MenuItem(this.menu, SWT.PUSH);
        this.saveAs.addSelectionListener(TuxGuitar.instance().getAction(SaveAsFileAction.NAME));             
        //--SEPARATOR--
        new MenuItem(this.menu, SWT.SEPARATOR);
        
        //--IMPORT--
        this.importItems.clear();
        this.importItem = new MenuItem(this.menu,SWT.CASCADE);
        this.importMenu = new Menu(this.menu.getShell(), SWT.DROP_DOWN);

        Iterator importers = TGFileFormatManager.instance().getImporters();
        while(importers.hasNext()){
        	TGSongImporter importer = (TGSongImporter)importers.next();
            MenuItem item = new MenuItem(this.importMenu, SWT.PUSH);
            item.setData(importer);
            item.addSelectionListener(TuxGuitar.instance().getAction(ImportSongAction.NAME));
            this.importItems.add( item );
        }        

        //--EXPORT--
        this.exportItems.clear();
        this.exportItem = new MenuItem(this.menu,SWT.CASCADE);
        this.exportMenu = new Menu(this.menu.getShell(), SWT.DROP_DOWN);
        
        Iterator exporters = TGFileFormatManager.instance().getExporters();
        while(exporters.hasNext()){
        	TGSongExporter exporter = (TGSongExporter)exporters.next();
            MenuItem item = new MenuItem(this.exportMenu, SWT.PUSH);
            item.setData(exporter);
            item.addSelectionListener(TuxGuitar.instance().getAction(ExportSongAction.NAME));
            this.exportItems.add( item );
        }

        //--SEPARATOR--
        new MenuItem(this.menu, SWT.SEPARATOR);
        //--PRINT PREVIEW--
        this.printPreview = new MenuItem(this.menu, SWT.PUSH);
        this.printPreview.addSelectionListener(TuxGuitar.instance().getAction(PrintPreviewAction.NAME));          
        //--PRINT--
        this.print = new MenuItem(this.menu, SWT.PUSH);
        this.print.addSelectionListener(TuxGuitar.instance().getAction(PrintAction.NAME));         
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
        this.exit.addSelectionListener(TuxGuitar.instance().getAction(ExitAction.NAME)); 
        
        //---------------------------------------------------
        this.importItem.setMenu(this.importMenu);
        this.exportItem.setMenu(this.exportMenu);
        this.historyItem.setMenu(this.historyMenu);
        this.fileMenuItem.setMenu(this.menu);
        
        this.loadIcons();
        this.loadProperties();
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
    		this.historyFiles[i] = new MenuItem(this.historyMenu, SWT.PUSH);
    		this.historyFiles[i].setText(decode(url.toString()));
    		this.historyFiles[i].setData(url);
    		this.historyFiles[i].addSelectionListener(TuxGuitar.instance().getAction(OpenFileAction.NAME));
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
        this.fileMenuItem.setText(TuxGuitar.getProperty("file"));  
        this.newSong.setText(TuxGuitar.getProperty("file.new"));  
        this.open.setText(TuxGuitar.getProperty("file.open"));
        this.openURL.setText(TuxGuitar.getProperty("file.open-url")); 
        this.save.setText(TuxGuitar.getProperty("file.save"));
        this.saveAs.setText(TuxGuitar.getProperty("file.save-as"));        
        this.importItem.setText(TuxGuitar.getProperty("file.import"));        
        this.exportItem.setText(TuxGuitar.getProperty("file.export"));
        this.printPreview.setText(TuxGuitar.getProperty("file.print-preview"));
        this.print.setText(TuxGuitar.getProperty("file.print"));
        this.historyItem.setText(TuxGuitar.getProperty("file.history"));
        this.exit.setText(TuxGuitar.getProperty("file.exit"));
        
        Iterator importItems = this.importItems.iterator();
        while(importItems.hasNext()){
        	MenuItem item = (MenuItem)importItems.next();
        	if( item.getData() instanceof TGSongImporter ){
        		item.setText(TuxGuitar.getProperty("file.import") + " " + ((TGSongImporter)item.getData()).getImportName());
        	}
        }
        
        Iterator exportItems = this.exportItems.iterator();
        while(exportItems.hasNext()){
        	MenuItem item = (MenuItem)exportItems.next();
        	if( item.getData() instanceof TGSongExporter ){
        		item.setText(TuxGuitar.getProperty("file.export") + " " + ((TGSongExporter)item.getData()).getExportName());
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