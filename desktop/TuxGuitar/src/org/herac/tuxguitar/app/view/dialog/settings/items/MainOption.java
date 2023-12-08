package org.herac.tuxguitar.app.view.dialog.settings.items;

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
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.widget.UICheckBox;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UITextField;
import org.herac.tuxguitar.util.TGSynchronizer;

public class MainOption extends TGSettingsOption {
	
	private static final String VAR_START = "${";
	private static final String VAR_END = "}";
	
	private static final String[] VAR_NAMES = new String[] {
		TGVarAppName.NAME,TGVarAppVersion.NAME,TGVarFileName.NAME,TGVarFilePath.NAME,TGVarSongName.NAME,TGVarSongAlbum.NAME,TGVarSongArtist.NAME,TGVarSongAuthor.NAME
	};
	
	private boolean initialized;
	private UICheckBox showSplash;
	private UICheckBox autoSizeTable;
	private UITextField windowTitle;
	
	public MainOption(TGSettingsEditor configEditor, UIToolBar toolBar, UILayoutContainer parent){
		super(configEditor,toolBar,parent,TuxGuitar.getProperty("settings.config.main"));
		this.initialized = false;
	}
	
	public void createOption() {
		UIFactory uiFactory = this.getUIFactory();
		
		getToolItem().setText(TuxGuitar.getProperty("settings.config.main"));
		getToolItem().setImage(TuxGuitar.getInstance().getIconManager().getOptionMain());
		getToolItem().addSelectionListener(this);
		
		showLabel(getPanel(), TuxGuitar.getProperty("settings.config.main.window-title"), true, 1, 1);
		
		UITableLayout titleSectionLayout = new UITableLayout();
		UIPanel titleSection = uiFactory.createPanel(getPanel(), false);
		titleSection.setLayout(titleSectionLayout);
		this.indent(titleSection, 2, 1);
		
		UITableLayout windowTitleLayout = new UITableLayout();
		UIPanel windowTitleComposite = uiFactory.createPanel(titleSection, false);
		windowTitleComposite.setLayout(windowTitleLayout);
		titleSectionLayout.set(windowTitleComposite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.windowTitle = uiFactory.createTextField(windowTitleComposite);
		this.windowTitle.setTextLimit(80);
		windowTitleLayout.set(this.windowTitle, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UITableLayout infoHeaderLayout = new UITableLayout();
		UIPanel infoHeader = uiFactory.createPanel(titleSection, false);
		infoHeader.setLayout(infoHeaderLayout);
		titleSectionLayout.set(infoHeader, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		showImageLabel(infoHeader, TuxGuitar.getInstance().getIconManager().getStatusInfo(), 1, 1);
		showLabel(infoHeader, TuxGuitar.getProperty("settings.config.main.window-title.help"), false, 1, 2);
		
		UIPanel infoBody = uiFactory.createPanel(titleSection, false);
		infoBody.setLayout(new UITableLayout());
		titleSectionLayout.set(infoBody, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		for(int i = 0 ; i < VAR_NAMES.length; i ++) {
			showLabel(infoBody, this.getVar(VAR_NAMES[i]), true, (i + 1), 1);
			showLabel(infoBody, TuxGuitar.getProperty("settings.config.main.window-title.var.description." + VAR_NAMES[i] ), false, (i + 1), 2);
		}
		
		showLabel(getPanel(), TuxGuitar.getProperty("settings.config.main.options"), true, 3, 1);
		
		UITableLayout optionsLayout = new UITableLayout();
		UIPanel options = uiFactory.createPanel(getPanel(), false);
		options.setLayout(optionsLayout);
		this.indent(options, 4, 1);
		
		this.autoSizeTable = uiFactory.createCheckBox(options);
		this.autoSizeTable.setText(TuxGuitar.getProperty("settings.config.main.table.auto-size.enabled"));
		optionsLayout.set(this.autoSizeTable, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.showSplash = uiFactory.createCheckBox(options);
		this.showSplash.setText(TuxGuitar.getProperty("settings.config.main.splash-enabled"));
		optionsLayout.set(this.showSplash, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
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
							MainOption.this.showSplash.setSelected(showSplash);
							MainOption.this.autoSizeTable.setSelected(autoSizeTable);
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
			getConfig().setValue(TGConfigKeys.SHOW_SPLASH,this.showSplash.isSelected());
			getConfig().setValue(TGConfigKeys.TABLE_AUTO_SIZE,this.autoSizeTable.isSelected());
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
