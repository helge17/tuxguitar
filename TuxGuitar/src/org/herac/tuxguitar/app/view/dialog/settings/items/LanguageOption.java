package org.herac.tuxguitar.app.view.dialog.settings.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.view.dialog.settings.TGSettingsEditor;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UITable;
import org.herac.tuxguitar.ui.widget.UITableItem;
import org.herac.tuxguitar.util.TGSynchronizer;

public class LanguageOption extends TGSettingsOption {
	
	private static final float PACKED_HEIGHT = 10f;
	
	private boolean initialized;
	private UITable<String> table;
	
	public LanguageOption(TGSettingsEditor configEditor, UIToolBar toolBar, UILayoutContainer parent){
		super(configEditor, toolBar, parent, TuxGuitar.getProperty("settings.config.language"), UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL);
		this.initialized = false;
	}
	
	public void createOption() {
		UIFactory uiFactory = this.getUIFactory();
		
		getToolItem().setText(TuxGuitar.getProperty("settings.config.language"));
		getToolItem().setImage(TuxGuitar.getInstance().getIconManager().getOptionLanguage());
		getToolItem().addSelectionListener(this);
		
		showLabel(getPanel(), TuxGuitar.getProperty("settings.config.language.choose"), true, 1, 1);
		
		UITableLayout compositeLayout = new UITableLayout();
		UIPanel composite = uiFactory.createPanel(getPanel(), false);
		composite.setLayout(compositeLayout);
		this.indent(composite, 2, 1);
		
		this.table = uiFactory.createTable(composite, true);
		this.table.setColumns(1);
		this.table.setColumnName(0, TuxGuitar.getProperty("settings.config.language.choose"));
		compositeLayout.set(this.table, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		compositeLayout.set(this.table, UITableLayout.PACKED_HEIGHT, PACKED_HEIGHT);
		
		this.loadConfig();
	}
	
	protected void loadTableItem(String text, String data, boolean selected){
		UITableItem<String> uiTableItem = new UITableItem<String>(data);
		uiTableItem.setText(0, text);
		
		this.table.addItem(uiTableItem);
		if( selected ){
			this.table.setSelectedItem(uiTableItem);
		}
	}
	
	protected List<LanguageItem> getLanguageItems(String[] languages){
		List<LanguageItem> list = new ArrayList<LanguageItem>();
		if( languages != null ){
			for(int i = 0;i < languages.length; i ++){
				list.add( new LanguageItem(languages[i],TuxGuitar.getProperty("locale." + languages[i] ) ) );
			}
			Collections.sort(list, new Comparator<LanguageItem>() {
				public int compare(LanguageItem l1, LanguageItem l2) {
					if( l1 != null && l2 != null ) {
						return l1.getValue().compareTo( l2.getValue() );
					}
					return 0;
				}
			} );
		}
		return list;
	}
	
	protected void loadConfig(){
		new Thread(new Runnable() {
			public void run() {
				final String language = getConfig().getStringValue(TGConfigKeys.LANGUAGE);
				final List<LanguageItem> languages = getLanguageItems( TuxGuitar.getInstance().getLanguageManager().getLanguages() );
				TGSynchronizer.getInstance(getViewContext().getContext()).executeLater(new Runnable() {
					public void run() {
						if(!isDisposed()){
							// Load default item
							loadTableItem(TuxGuitar.getProperty("locale.default"), new String(), true);
							
							for(int i = 0;i < languages.size(); i ++){
								LanguageItem item = (LanguageItem)languages.get( i );
								loadTableItem(item.getValue(),item.getKey(),(language != null && item.getKey().equals( language )));
							}
							
							LanguageOption.this.initialized = true;
							LanguageOption.this.pack();
						}
					}
				});
			}
		}).start();
	}
	
	public void updateConfig(){
		if( this.initialized ){
			String language = (this.table != null && !this.table.isDisposed() ? this.table.getSelectedValue() : null);
			
			getConfig().setValue(TGConfigKeys.LANGUAGE, language );
		}
	}
	
	public void updateDefaults(){
		if(this.initialized){
			getConfig().setValue(TGConfigKeys.LANGUAGE, getDefaults().getValue(TGConfigKeys.LANGUAGE));
		}
	}
	
	private class LanguageItem {
		
		private String key;
		private String value;
		
		public LanguageItem(String key, String value){
			this.key = key;
			this.value = value;
		}
		
		public String getKey(){
			return this.key;
		}
		
		public String getValue(){
			return this.value;
		}
	}
}