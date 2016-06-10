package org.herac.tuxguitar.app.view.dialog.settings.items;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.app.view.dialog.settings.TGSettingsEditor;
import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIPaintEvent;
import org.herac.tuxguitar.ui.event.UIPaintListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UICursor;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.util.TGSynchronizer;

public class SkinOption extends TGSettingsOption{
	
	private static final float PREVIEW_WIDTH = 450f;
	private static final float PREVIEW_HEIGHT = 324f;
	
	private boolean initialized;
	private List<SkinInfo> skins;
	private UIDropDownSelect<SkinInfo> combo;
	private UILabel nameLabel;
	private UILabel authorLabel;
	private UILabel versionLabel;
	private UILabel descriptionLabel;
	private UIImage preview;
	private UICanvas previewArea;
	
	public SkinOption(TGSettingsEditor configEditor, UIToolBar toolBar, UILayoutContainer parent){
		super(configEditor, toolBar, parent,TuxGuitar.getProperty("settings.config.skin"), UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL);
		this.initialized = false;
	}
	
	public void createOption() {
		UIFactory uiFactory = this.getUIFactory();
		
		getToolItem().setText(TuxGuitar.getProperty("settings.config.skin"));
		getToolItem().setImage(TuxGuitar.getInstance().getIconManager().getOptionSkin());
		getToolItem().addSelectionListener(this);
		
		showLabel(getPanel(), TuxGuitar.getProperty("settings.config.skin.choose"), true, 1, 1);
		
		UITableLayout compositeLayout = new UITableLayout();
		UIPanel composite = uiFactory.createPanel(getPanel(), false);
		composite.setLayout(compositeLayout);
		this.indent(composite, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		
		this.combo = uiFactory.createDropDownSelect(composite);
		compositeLayout.set(this.combo, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UITableLayout skinInfoLayout = new UITableLayout();
		UIPanel skinInfoComposite = uiFactory.createPanel(getPanel(), false);
		skinInfoComposite.setLayout(skinInfoLayout);
		this.indent(skinInfoComposite, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		
		showLabel(skinInfoComposite, TuxGuitar.getProperty("name") + ": ", true, 1, 1);
		this.nameLabel = showLabel(skinInfoComposite, "" + ": ", false, 1, 2);
		
		showLabel(skinInfoComposite, TuxGuitar.getProperty("author") + ": ", true, 2, 1);
		this.authorLabel = showLabel(skinInfoComposite, "" + ": ", false, 2, 2);
		
		showLabel(skinInfoComposite, TuxGuitar.getProperty("version") + ": ", true, 3, 1);
		this.versionLabel = showLabel(skinInfoComposite, "" + ": ", false, 3, 2);
		
		showLabel(skinInfoComposite, TuxGuitar.getProperty("description") + ": ", true, 4, 1);
		this.descriptionLabel = showLabel(skinInfoComposite, "" + ": ", false, 4, 2);
		
		UITableLayout skinPreviewLayout = new UITableLayout();
		UIPanel skinPreviewComposite = uiFactory.createPanel(getPanel(), false);
		skinPreviewComposite.setLayout(skinPreviewLayout);
		this.indent(skinPreviewComposite, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		
		this.previewArea = uiFactory.createCanvas(skinPreviewComposite, false);
		this.previewArea.addPaintListener(new UIPaintListener() {
			public void onPaint(UIPaintEvent event) {
				if( SkinOption.this.preview != null && !SkinOption.this.preview.isDisposed() ){
					event.getPainter().drawImage(SkinOption.this.preview, 0, 0);
				}
			}
		});
		skinPreviewLayout.set(this.previewArea, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
		skinPreviewLayout.set(this.previewArea, UITableLayout.PACKED_WIDTH, PREVIEW_WIDTH);
		skinPreviewLayout.set(this.previewArea, UITableLayout.PACKED_HEIGHT, PREVIEW_HEIGHT);
		
		this.loadConfig();
	}
	
	public void loadConfig(){
		new Thread(new Runnable() {
			public void run() {
				SkinOption.this.skins = new ArrayList<SkinInfo>();
				String[] skinNames = TGFileUtils.getFileNames(getViewContext().getContext(), "skins");
				if( skinNames != null ){
					for(int i = 0;i < skinNames.length;i++){
						Properties properties = new Properties();
						try {
							InputStream skinInfo = TGResourceManager.getInstance(getViewContext().getContext()).getResourceAsStream("skins/" + skinNames[i] + "/skin.properties");
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
				TGSynchronizer.getInstance(getViewContext().getContext()).executeLater(new Runnable() {
					public void run() {
						if(!isDisposed()){
							for(SkinInfo info : SkinOption.this.skins) {
								UISelectItem<SkinInfo> item = new UISelectItem<SkinOption.SkinInfo>(info.getName(), info);
								SkinOption.this.combo.addItem(item);
								if( info.getSkin().equals(getConfig().getStringValue(TGConfigKeys.SKIN))){
									SkinOption.this.combo.setSelectedItem(item);
								}
							}
							SkinOption.this.combo.addSelectionListener(new UISelectionListener() {
								public void onSelect(UISelectionEvent event) {
									SkinOption.this.showSkinInfo();
								}
							});
							
							SkinOption.this.showSkinInfo();
							SkinOption.this.initialized = true;
							SkinOption.this.pack();
						}
					}
				});
			}
		}).start();
	}
	
	public void showSkinInfo() {
		SkinInfo skinInfo = SkinOption.this.combo.getSelectedValue();
		if( skinInfo != null ){
			this.showSkinInfo(skinInfo);
		}
	}
	
	public void showSkinInfo(final SkinInfo info){
		loadCursor(UICursor.WAIT);
		TGSynchronizer.getInstance(getViewContext().getContext()).executeLater(new Runnable() {
			public void run() {
				if(!isDisposed()){
					disposePreview();
					SkinOption.this.nameLabel.setText(info.getName());
					SkinOption.this.authorLabel.setText(info.getAuthor());
					SkinOption.this.descriptionLabel.setText(info.getDescription());
					SkinOption.this.versionLabel.setText((info.getDate() == null)?info.getVersion():info.getVersion() + " (" + info.getDate() + ")");
					if( info.getPreview() != null){
						SkinOption.this.preview = TGFileUtils.loadImage2(getViewContext().getContext(), info.getSkin(), info.getPreview());
					}
					SkinOption.this.previewArea.redraw();
					
					loadCursor(UICursor.NORMAL);
				}
			}
		});
	}
	
	public void updateConfig() {
		if(this.initialized){
			SkinInfo skinInfo = this.combo.getSelectedValue();
			if( skinInfo != null ){
				getConfig().setValue(TGConfigKeys.SKIN, skinInfo.getSkin());
			}
		}
	}
	
	public void updateDefaults(){
		if(this.initialized){
			getConfig().setValue(TGConfigKeys.SKIN, getDefaults().getValue(TGConfigKeys.SKIN));
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
