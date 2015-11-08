package org.herac.tuxguitar.app.view.dialog.settings.items;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.variables.TGVarAppName;
import org.herac.tuxguitar.app.system.variables.TGVarAppVersion;
import org.herac.tuxguitar.app.system.variables.TGVarFileName;
import org.herac.tuxguitar.app.system.variables.TGVarFilePath;
import org.herac.tuxguitar.app.system.variables.TGVarSongAlbum;
import org.herac.tuxguitar.app.system.variables.TGVarSongArtist;
import org.herac.tuxguitar.app.system.variables.TGVarSongAuthor;
import org.herac.tuxguitar.app.system.variables.TGVarSongName;
import org.herac.tuxguitar.app.view.dialog.settings.TGSettingsEditor;
import org.herac.tuxguitar.util.TGSynchronizer;

public class MainOption  extends Option{
	
	private static final String VAR_START = "${";
	private static final String VAR_END = "}";
	
	protected boolean initialized;
	protected Button showSplash;
	protected Button autoSizeTable;
	protected Text windowTitle;
	
	public MainOption(TGSettingsEditor configEditor,ToolBar toolBar,final Composite parent){
		super(configEditor,toolBar,parent,TuxGuitar.getProperty("settings.config.main"));
		this.initialized = false;
	}
	
	public void createOption(){
		getToolItem().setText(TuxGuitar.getProperty("settings.config.main"));
		getToolItem().setImage(TuxGuitar.getInstance().getIconManager().getOptionMain());
		getToolItem().addSelectionListener(this);
		
		showLabel(getComposite(),SWT.TOP | SWT.LEFT | SWT.WRAP,SWT.BOLD,0,TuxGuitar.getProperty("settings.config.main.window-title"));
		
		Composite windowTitleComposite = new Composite(getComposite(),SWT.NONE);
		windowTitleComposite.setLayout(new GridLayout());
		windowTitleComposite.setLayoutData(getTabbedData());
		
		this.windowTitle = new Text(windowTitleComposite,SWT.BORDER);
		this.windowTitle.setLayoutData(new GridData(SWT.FILL, SWT.FILL , true, true));
		this.windowTitle.setTextLimit(80);
		
		Composite infoHeader = new Composite(windowTitleComposite,SWT.NONE);
		infoHeader.setLayout(new GridLayout(2,false));
		infoHeader.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		showImageLabel(infoHeader,SWT.NONE,infoHeader.getDisplay().getSystemImage(SWT.ICON_INFORMATION));
		showLabel(infoHeader,SWT.TOP | SWT.LEFT | SWT.WRAP,SWT.NORMAL,0,TuxGuitar.getProperty("settings.config.main.window-title.help"));
		
		Composite infoBody = new Composite(windowTitleComposite,SWT.NONE);
		infoBody.setLayout(new GridLayout(2,false));
		infoBody.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		showLabel(infoBody, SWT.NONE,SWT.BOLD,-1, this.getVar(TGVarAppName.NAME));
		showLabel(infoBody,SWT.TOP | SWT.WRAP,SWT.NORMAL,0,TuxGuitar.getProperty("settings.config.main.window-title.var.description." + TGVarAppName.NAME ));
		
		showLabel(infoBody, SWT.NONE,SWT.BOLD,-1, this.getVar(TGVarAppVersion.NAME));
		showLabel(infoBody,SWT.TOP | SWT.WRAP,SWT.NORMAL,0,TuxGuitar.getProperty("settings.config.main.window-title.var.description." + TGVarAppVersion.NAME ));
		
		showLabel(infoBody, SWT.NONE,SWT.BOLD,-1, this.getVar(TGVarFileName.NAME));
		showLabel(infoBody,SWT.TOP | SWT.WRAP,SWT.NORMAL,0,TuxGuitar.getProperty("settings.config.main.window-title.var.description." + TGVarFileName.NAME ));
		
		showLabel(infoBody, SWT.NONE,SWT.BOLD,-1, this.getVar(TGVarFilePath.NAME));
		showLabel(infoBody,SWT.TOP | SWT.WRAP,SWT.NORMAL,0,TuxGuitar.getProperty("settings.config.main.window-title.var.description." + TGVarFilePath.NAME ));
		
		showLabel(infoBody, SWT.NONE,SWT.BOLD,-1, this.getVar(TGVarSongName.NAME));
		showLabel(infoBody,SWT.TOP | SWT.WRAP,SWT.NORMAL,0,TuxGuitar.getProperty("settings.config.main.window-title.var.description." + TGVarSongName.NAME ));
		
		showLabel(infoBody, SWT.NONE,SWT.BOLD,-1, this.getVar(TGVarSongAlbum.NAME));
		showLabel(infoBody,SWT.TOP | SWT.WRAP,SWT.NORMAL,0,TuxGuitar.getProperty("settings.config.main.window-title.var.description." + TGVarSongAlbum.NAME ));
		
		showLabel(infoBody, SWT.NONE,SWT.BOLD,-1, this.getVar(TGVarSongArtist.NAME));
		showLabel(infoBody,SWT.TOP | SWT.WRAP,SWT.NORMAL,0,TuxGuitar.getProperty("settings.config.main.window-title.var.description." + TGVarSongArtist.NAME ));
		
		showLabel(infoBody, SWT.NONE,SWT.BOLD,-1, this.getVar(TGVarSongAuthor.NAME));
		showLabel(infoBody,SWT.TOP | SWT.WRAP,SWT.NORMAL,0,TuxGuitar.getProperty("settings.config.main.window-title.var.description." + TGVarSongAuthor.NAME ));
		
		showLabel(getComposite(),SWT.BOTTOM | SWT.LEFT | SWT.WRAP,SWT.BOLD,0,TuxGuitar.getProperty("settings.config.main.options"));
		
		Composite options = new Composite(getComposite(),SWT.NONE);
		options.setLayout(new GridLayout());
		options.setLayoutData(getTabbedData());
		
		this.autoSizeTable = new Button(options,SWT.CHECK);
		this.autoSizeTable.setText(TuxGuitar.getProperty("settings.config.main.table.auto-size.enabled"));
		
		this.showSplash = new Button(options,SWT.CHECK);
		this.showSplash.setText(TuxGuitar.getProperty("settings.config.main.splash-enabled"));
		
		this.loadConfig();
	}
	
	protected void loadConfig(){
		new Thread(new Runnable() {
			public void run() {
				final String windowTitle = getConfig().getStringValue(TGConfigKeys.WINDOW_TITLE);
				final boolean showSplash = getConfig().getBooleanValue(TGConfigKeys.SHOW_SPLASH);
				final boolean autoSizeTable = getConfig().getBooleanValue(TGConfigKeys.TABLE_AUTO_SIZE);
				TGSynchronizer.getInstance(getViewContext().getContext()).executeLater(new Runnable() {
					public void run() {
						if(!isDisposed()){
							MainOption.this.windowTitle.setText(windowTitle);
							MainOption.this.showSplash.setSelection(showSplash);
							MainOption.this.autoSizeTable.setSelection(autoSizeTable);
							MainOption.this.initialized = true;
							MainOption.this.pack();
						}
					}
				});
			}
		}).start();
	}
	
	public void updateConfig(){
		if(this.initialized){
			getConfig().setValue(TGConfigKeys.WINDOW_TITLE,this.windowTitle.getText());
			getConfig().setValue(TGConfigKeys.SHOW_SPLASH,this.showSplash.getSelection());
			getConfig().setValue(TGConfigKeys.TABLE_AUTO_SIZE,this.autoSizeTable.getSelection());
		}
	}
	
	public void updateDefaults(){
		if(this.initialized){
			getConfig().setValue(TGConfigKeys.WINDOW_TITLE, getDefaults().getValue(TGConfigKeys.WINDOW_TITLE));
			getConfig().setValue(TGConfigKeys.SHOW_SPLASH, getDefaults().getValue(TGConfigKeys.SHOW_SPLASH));
			getConfig().setValue(TGConfigKeys.TABLE_AUTO_SIZE, getDefaults().getValue(TGConfigKeys.TABLE_AUTO_SIZE));
		}
	}
	
	public String getVar(String varName){
		return (VAR_START + varName + VAR_END);
	}
}
