package org.herac.tuxguitar.gui.help.doc;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.icons.IconLoader;
import org.herac.tuxguitar.gui.system.language.LanguageLoader;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.gui.util.TGFileUtils;

public class DocDialog implements LanguageLoader,IconLoader{
	
	private static final String URL_NOT_FOUND = ("<html><body><h2 align=\"center\">URL Not Found!</h2></body></html>");
	
	private Shell dialog;
	private SashForm form;
	private Browser browser;
	private Tree tree;
	
	public void show(Shell shell) throws DocException{
		try{
			this.dialog = DialogUtils.newDialog(shell,SWT.SHELL_TRIM);
			this.dialog.setLayout(new FillLayout());
			this.form = new SashForm(this.dialog,SWT.HORIZONTAL | SWT.SMOOTH) ;
			this.tree = new Tree(this.form, SWT.BORDER | SWT.SINGLE );
			this.browser = new Browser(this.form, SWT.BORDER);
			this.form.setWeights(new int[]{1,4});
			this.initTreeStyles();
			this.initTreeItems();
			this.initTreeListener();
			this.initBrowserListener();
			this.initFirstItem();
			this.initResources();
			
			DialogUtils.openDialog(this.dialog,DialogUtils.OPEN_STYLE_MAXIMIZED);
		}catch (Throwable trowable) {
			throw new DocException(trowable);
		}
	}
	
	protected Tree getTree(){
		return this.tree;
	}
	
	protected void showUrl(String url){
		try{
			String parsedUrl = parseURL(url);
			if(parsedUrl != null){
				this.browser.setUrl(parsedUrl);
			}else{
				this.browser.setText(URL_NOT_FOUND);
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
	}
	
	protected void initResources(){
		TuxGuitar.instance().getIconManager().addLoader( this );
		TuxGuitar.instance().getLanguageManager().addLoader( this );
		this.dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				TuxGuitar.instance().getIconManager().removeLoader( DocDialog.this );
				TuxGuitar.instance().getLanguageManager().removeLoader( DocDialog.this );
			}
		});
		this.loadIcons();
		this.loadProperties();
	}
	
	protected void initBrowserListener(){
		this.browser.addLocationListener(new LocationAdapter() {
			public void changed(LocationEvent event) {
				findSelectedItem();
			}
		});
	}
	
	protected void initTreeListener(){
		this.tree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TreeItem item = (TreeItem) e.item;
				Object data = item.getData();
				if(data instanceof DocItem){
					DocItem docItem = (DocItem)data;
					if( docItem.getUrl() != null && docItem.getUrl().length() > 0){
						showUrl(((DocItem)data).getUrl());
					}
				}
			}
		});
		this.tree.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				TreeItem item = getTree().getItem(new Point(e.x,e.y));
				if(item != null){
					getTree().setCursor(getTree().getDisplay().getSystemCursor(SWT.CURSOR_HAND));
				}else{
					getTree().setCursor(getTree().getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
				}
			}
		});
	}
	
	protected void initTreeStyles(){
		final Color color = new Color(this.tree.getDisplay(), new RGB(0xfa,0xfa,0xfa) );
		this.tree.setBackground(color);
		this.tree.addDisposeListener( new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				color.dispose();
			}
		} );
	}
	
	protected void initTreeItems(){
		InputStream stream = TGFileUtils.getResourceAsStream("help/contents.xml");
		if( stream != null ){
			List items = new ArrayList();
			new DocItemReader().loadHelpItems(items,stream);
			Iterator it = items.iterator();
			while(it.hasNext()){
				DocItem item = (DocItem)it.next();
				initSubItems(item,rootItem(item));
			}
		}
	}
	
	protected void initSubItems(DocItem item,TreeItem parent){
		Iterator it = item.getChildren().iterator();
		while(it.hasNext()){
			DocItem child = (DocItem)it.next();
			initSubItems(child,subItem(parent ,child));
		}
	}
	
	private void initFirstItem(){
		this.initFirstItem( this.tree.getItems() );
	}
	
	private boolean initFirstItem(TreeItem[] items){
		for( int i = 0 ; i < items.length; i ++ ){
			if(items[i].getData() instanceof DocItem){
				DocItem item = (DocItem)items[i].getData();
				if(item.getName() != null && item.getName().length() > 0 &&  item.getUrl() != null && item.getUrl().length() > 0){
					this.showUrl( item.getUrl() );
					this.tree.setSelection( items[i] );
					return true;
				}
				else if( initFirstItem( items[i].getItems() )){
					return true;
				}
			}
		}
		return false;
	}
	
	protected TreeItem rootItem(DocItem item){
		TreeItem treeItem = new TreeItem(this.tree,SWT.NONE);
		treeItem.setText(item.getName());
		treeItem.setData(item);
		return treeItem;
	}
	
	protected TreeItem subItem(TreeItem parent,DocItem item){
		TreeItem treeItem = new TreeItem(parent,SWT.NONE);
		treeItem.setText(item.getName());
		treeItem.setData(item);
		return treeItem;
	}
	
	protected void findSelectedItem(){
		try {
			if(!this.browser.isDisposed() && this.browser.isVisible()){
				String url = this.browser.getUrl();
				if(url != null && url.length() > 0){
					boolean selected = false;
					String browserPath = new URL(url).getFile();
					TreeItem[] selection = this.tree.getSelection();
					if(selection != null && selection.length > 0 && selection[0].getData() instanceof DocItem){
						DocItem item = (DocItem)selection[0].getData();
						if( item.getUrl() != null ){
							String path = parseURL( item.getUrl());
							if(path != null && browserPath.equals( path )){
								selected = true;
							}
						}
					}
					if(!selected){
						TreeItem item = findSelectedItem(this.tree.getItems(), browserPath);
						if(item != null){
							this.tree.setSelection(item);
						}
					}
				}
			}
		}catch (Throwable trowable) {
			new DocException(trowable).printStackTrace();
		}
	}
	
	protected TreeItem findSelectedItem(TreeItem[] items, String browserPath){
		for( int i = 0 ; i < items.length; i ++ ){
			if(items[i].getData() instanceof DocItem){
				DocItem item = (DocItem)items[ i ].getData();
				if( item.getUrl() != null ){
					String path = parseURL( item.getUrl());
					if(path != null && browserPath.equals( path )){
						return items[ i ];
					}
				}
				TreeItem child = findSelectedItem( items[ i ].getItems() , browserPath );
				if(child != null){
					return child;
				}
			}
		}
		return null;
	}
	
	private String parseURL(String url){
		URL resourceUrl = TGFileUtils.getResourceUrl("help/" + url);
		return ( resourceUrl != null ? resourceUrl.toExternalForm() : null );
	}
	
	public boolean isDisposed(){
		return (this.dialog == null || this.dialog.isDisposed() );
	}
	
	public void loadProperties() {
		if(!isDisposed()){
			this.dialog.setText(TuxGuitar.getProperty("help.doc"));
		}
	}
	
	public void loadIcons() {
		if(!isDisposed()){
			this.dialog.setImage( TuxGuitar.instance().getIconManager().getAppIcon() );
		}
	}
}