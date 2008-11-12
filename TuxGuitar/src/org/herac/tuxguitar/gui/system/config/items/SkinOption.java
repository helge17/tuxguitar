package org.herac.tuxguitar.gui.system.config.items;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.helper.SyncThread;
import org.herac.tuxguitar.gui.system.config.TGConfigEditor;
import org.herac.tuxguitar.gui.system.config.TGConfigKeys;
import org.herac.tuxguitar.gui.util.TGFileUtils;

public class SkinOption extends Option{
	
	protected boolean initialized;
	protected List skins;
	protected Combo combo;
	protected Label nameLabel;
	protected Label authorLabel;
	protected Label versionLabel;
	protected Label descriptionLabel;
	protected Image preview;
	protected Composite previewArea;
	
	public SkinOption(TGConfigEditor configEditor,ToolBar toolBar,final Composite parent){
		super(configEditor,toolBar,parent,TuxGuitar.getProperty("settings.config.skin"), SWT.FILL,SWT.FILL);
		this.initialized = false;
	}
	
	public void createOption() {
		getToolItem().setText(TuxGuitar.getProperty("settings.config.skin"));
		getToolItem().setImage(TuxGuitar.instance().getIconManager().getOptionSkin());
		getToolItem().addSelectionListener(this);
		
		showLabel(getComposite(),SWT.FILL,SWT.TOP,true, false, SWT.TOP | SWT.LEFT | SWT.WRAP,SWT.BOLD,0,TuxGuitar.getProperty("settings.config.skin.choose"));
		
		Composite composite = new Composite(getComposite(),SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(getTabbedData(SWT.FILL, SWT.FILL, true, false));
		
		this.combo = new Combo(composite,SWT.DROP_DOWN | SWT.READ_ONLY);
		this.combo.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		Composite skinInfoComposite = new Composite(getComposite(),SWT.NONE);
		skinInfoComposite.setLayout(new GridLayout(2,false));
		skinInfoComposite.setLayoutData(getTabbedData(SWT.FILL, SWT.FILL, true, false));
		showLabel(skinInfoComposite,SWT.FILL,SWT.CENTER,false,true,SWT.TOP | SWT.LEFT | SWT.WRAP,SWT.BOLD,0,TuxGuitar.getProperty("name") + ": ");
		this.nameLabel = showLabel(skinInfoComposite,SWT.FILL,SWT.CENTER,SWT.TOP | SWT.LEFT | SWT.WRAP,SWT.NONE,0,"");
		showLabel(skinInfoComposite,SWT.FILL,SWT.CENTER,false,true,SWT.TOP | SWT.LEFT | SWT.WRAP,SWT.BOLD,0,TuxGuitar.getProperty("author")+": ");
		this.authorLabel = showLabel(skinInfoComposite,SWT.FILL,SWT.CENTER,SWT.TOP | SWT.LEFT | SWT.WRAP,SWT.NONE,0,"");
		showLabel(skinInfoComposite,SWT.FILL,SWT.CENTER,false,true,SWT.TOP | SWT.LEFT | SWT.WRAP,SWT.BOLD,0,TuxGuitar.getProperty("version")+": ");
		this.versionLabel = showLabel(skinInfoComposite,SWT.FILL,SWT.CENTER,SWT.TOP | SWT.LEFT | SWT.WRAP,SWT.NONE,0,"");
		showLabel(skinInfoComposite,SWT.FILL,SWT.CENTER,false,true,SWT.TOP | SWT.LEFT | SWT.WRAP,SWT.BOLD,0,TuxGuitar.getProperty("description")+": ");
		this.descriptionLabel = showLabel(skinInfoComposite,SWT.FILL,SWT.CENTER,SWT.TOP | SWT.LEFT | SWT.WRAP,SWT.NONE,0,"");
		
		Composite skinPreviewComposite = new Composite(getComposite(),SWT.NONE);
		skinPreviewComposite.setLayout(new GridLayout());
		skinPreviewComposite.setLayoutData(getTabbedData(SWT.FILL, SWT.FILL ,true, true));
		
		this.previewArea = new Composite(skinPreviewComposite,SWT.DOUBLE_BUFFERED);
		this.previewArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.previewArea.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if(SkinOption.this.preview != null && !SkinOption.this.preview.isDisposed()){
					e.gc.drawImage(SkinOption.this.preview, 0, 0);
				}
			}
		});
		
		this.loadConfig();
	}
	
	protected void loadConfig(){
		new Thread(new Runnable() {
			public void run() {
				SkinOption.this.skins = new ArrayList();
				String[] skinNames = TGFileUtils.getFileNames("skins");
				if( skinNames != null ){
					for(int i = 0;i < skinNames.length;i++){
						Properties properties = new Properties();
						try {
							InputStream skinInfo = TGFileUtils.getResourceAsStream("skins/" + skinNames[i] + "/skin.properties");
							if( skinInfo != null ){
								properties.load( skinInfo );
							}
						}catch (Throwable throwable) {
							throwable.printStackTrace();
						}
						SkinInfo info = new SkinInfo(skinNames[i]);
						info.setName(properties.getProperty("name",info.getSkin()));
						info.setAuthor(properties.getProperty("author","Not available."));
						info.setVersion(properties.getProperty("version","Not available."));
						info.setDescription(properties.getProperty("description","Not available."));
						info.setDate(properties.getProperty("date",null));
						info.setPreview(properties.getProperty("preview",null));
						SkinOption.this.skins.add(info);
					}
				}
				new SyncThread(new Runnable() {
					public void run() {
						if(!isDisposed()){
							for(int i = 0;i < SkinOption.this.skins.size();i++){
								SkinInfo info = (SkinInfo)SkinOption.this.skins.get(i);
								SkinOption.this.combo.add(info.getName());
								if(info.getSkin().equals(getConfig().getStringConfigValue(TGConfigKeys.SKIN))){
									SkinOption.this.combo.select(i);
								}
							}
							SkinOption.this.combo.addSelectionListener(new SelectionAdapter() {
								public void widgetSelected(SelectionEvent e) {
									int selection = SkinOption.this.combo.getSelectionIndex();
									if(selection >= 0 && selection < SkinOption.this.skins.size()){
										showSkinInfo((SkinInfo)SkinOption.this.skins.get(selection));
									}
								}
							});
							
							int selection = SkinOption.this.combo.getSelectionIndex();
							if(selection >= 0 && selection < SkinOption.this.skins.size()){
								showSkinInfo((SkinInfo)SkinOption.this.skins.get(selection));
							}
							SkinOption.this.initialized = true;
							SkinOption.this.pack();
						}
					}
				}).start();
			}
		}).start();
	}
	
	protected void showSkinInfo(final SkinInfo info){
		loadCursor(SWT.CURSOR_WAIT);
		new SyncThread(new Runnable() {
			public void run() {
				if(!isDisposed()){
					disposePreview();
					SkinOption.this.nameLabel.setText(info.getName());
					SkinOption.this.authorLabel.setText(info.getAuthor());
					SkinOption.this.descriptionLabel.setText(info.getDescription());
					SkinOption.this.versionLabel.setText((info.getDate() == null)?info.getVersion():info.getVersion() + " (" + info.getDate() + ")");
					if(info.getPreview() != null){
						SkinOption.this.preview = TGFileUtils.loadImage(info.getSkin(),info.getPreview());
					}
					SkinOption.this.previewArea.redraw();
					loadCursor(SWT.CURSOR_ARROW);
				}
			}
		}).start();
	}
	
	public void updateConfig() {
		if(this.initialized){
			int selection = this.combo.getSelectionIndex();
			if(selection >= 0 && selection < this.skins.size()){
				SkinInfo info = (SkinInfo)this.skins.get(selection);
				getConfig().setProperty(TGConfigKeys.SKIN,info.getSkin());
			}
		}
	}
	
	public void updateDefaults(){
		if(this.initialized){
			getConfig().setProperty(TGConfigKeys.SKIN,getDefaults().getProperty(TGConfigKeys.SKIN));
		}
	}
	
	public void applyConfig(boolean force){
		if(force || (this.initialized && TuxGuitar.instance().getIconManager().shouldReload())){
			addSyncThread(new Runnable() {
				public void run() {
					TuxGuitar.instance().loadSkin();
				}
			});
		}
	}
	
	public void dispose(){
		this.disposePreview();
	}
	
	public void disposePreview(){
		if(this.preview != null && !this.preview.isDisposed()){
			this.preview.dispose();
		}
	}
	
	private class SkinInfo{
		private String skin;
		private String name;
		private String date;
		private String author;
		private String version;
		private String description;
		private String preview;
		
		public SkinInfo(String skin){
			this.skin = skin;
		}
		
		public String getAuthor() {
			return this.author;
		}
		
		public void setAuthor(String author) {
			this.author = author;
		}
		
		public String getDate() {
			return this.date;
		}
		
		public void setDate(String date) {
			this.date = date;
		}
		
		public String getDescription() {
			return this.description;
		}
		
		public void setDescription(String description) {
			this.description = description;
		}
		
		public String getName() {
			return this.name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getVersion() {
			return this.version;
		}
		
		public void setVersion(String version) {
			this.version = version;
		}
		
		public String getSkin() {
			return this.skin;
		}
		
		public String getPreview() {
			return this.preview;
		}
		
		public void setPreview(String preview) {
			this.preview = preview;
		}
	}
}
