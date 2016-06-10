package org.herac.tuxguitar.app.view.dialog.settings.items;

import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.dialog.settings.TGSettingsEditor;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UICursor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.toolbar.UIToolCheckableItem;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UIImageView;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.properties.TGProperties;

public abstract class TGSettingsOption implements UISelectionListener {
	
	public static final float DEFAULT_INDENT = 20f;
	
	private TGSettingsEditor configEditor;
	private UIToolBar toolBar;
	private UIToolCheckableItem toolItem;
	private UILegendPanel group;
	private UIPanel composite;
	
	public TGSettingsOption(TGSettingsEditor configEditor, UIToolBar toolBar, UILayoutContainer parent, String text, int horizontalAlignment,int verticalAlignment){
		UIFactory uiFactory = configEditor.getUIFactory();
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();
		UITableLayout groupLayout = new UITableLayout();
		
		this.configEditor = configEditor;
		this.toolBar = toolBar;
		this.toolItem = this.toolBar.createCheckItem();
		this.group = uiFactory.createLegendPanel(parent);
		this.group.setLayout(groupLayout);
		this.group.setText(text);
		this.composite = uiFactory.createPanel(this.group, false);
		this.composite.setLayout(new UITableLayout());
		
		groupLayout.set(this.composite, 1, 1, horizontalAlignment,verticalAlignment,true ,true);
		parentLayout.set(this.group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, null, null, null, null, 0f);
	}
	
	public TGSettingsOption(TGSettingsEditor configEditor, UIToolBar toolBar, UILayoutContainer parent, String text){
		this(configEditor, toolBar, parent, text, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP);
	}
	
	public abstract void createOption();
	
	public abstract void updateConfig();
	
	public abstract void updateDefaults();
	
	public void setVisible(boolean visible){
		this.toolItem.setChecked(visible);
		this.group.setVisible(visible);
		this.group.setFocus();
		
		if( visible ) {
			this.pack();
		}
	}
	
	public void dispose(){
		//Override me
	}
	
	protected UILabel showLabel(UILayoutContainer parent, String text, boolean bold, int row, int col){
		return showLabel(parent, text, bold, row, col, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER);
	}
	
	protected UILabel showLabel(UILayoutContainer parent, String text, boolean bold, int row, int col, int alignX, int alignY){
		return showLabel(parent, text, bold, row, col, alignX, alignY, true, false);
	}
	
	protected UILabel showLabel(UILayoutContainer parent, String text, boolean bold, int row, int col, int alignX, int alignY, boolean fillX, boolean fillY){
		UIFactory uiFactory = this.configEditor.getUIFactory();
		
		UILabel label = uiFactory.createLabel(parent);
		label.setText(text);
		
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();
		parentLayout.set(label, row, col, alignX, alignY, fillX, fillY);
		
		if( bold ) {
			UIFont defaultFont = label.getFont();
			if( defaultFont != null ) {
				final UIFont font = uiFactory.createFont(defaultFont.getName(), defaultFont.getHeight(), true, defaultFont.isItalic());
				
				label.setFont(font);
				label.addDisposeListener(new UIDisposeListener() {
					public void onDispose(UIDisposeEvent event) {
						font.dispose();
					}
				});
			}
		}
		return label;
	}
	
	protected UIImageView showImageLabel(UILayoutContainer parent, UIImage image, int row, int col) {
		return this.showImageLabel(parent, image, row, col, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, true);
	}
	
	protected UIImageView showImageLabel(UILayoutContainer parent, UIImage image, int row, int col, int alignX, int alignY, boolean fillX, boolean fillY) {
		UIFactory uiFactory = this.configEditor.getUIFactory();
		UIImageView uiImageView = uiFactory.createImageView(parent);
		uiImageView.setImage(image);
		
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();
		parentLayout.set(uiImageView, row, col, alignX, alignY, fillX, fillY);
		
		return uiImageView;
	}

	protected void indent(UIControl control, int row, int col){
		this.indent(control, row, col, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
	}
	
	protected void indent(UIControl control, int row, int col, int alignX, int alignY, boolean fillX, boolean fillY){
		this.indent(control, row, col, alignX, alignY, fillX, fillY, DEFAULT_INDENT);
	}
	
	protected void indent(UIControl control, int row, int col, int alignX, int alignY, boolean fillX, boolean fillY, float indent){
		UILayoutContainer parent = (UILayoutContainer) control.getParent();
		UITableLayout layout = (UITableLayout) parent.getLayout();
		
		layout.set(control, row, col, alignX, alignY, fillX, fillY);
		layout.set(control, UITableLayout.MARGIN_LEFT, indent);
	}
	
	public void onSelect(UISelectionEvent event) {
		this.configEditor.select(this);
	}
	
	public UIPanel getPanel(){
		return this.composite;
	}
	
	public UIToolCheckableItem getToolItem(){
		return this.toolItem;
	}
	
	public TGConfigManager getConfig(){
		return this.configEditor.getConfig();
	}
	
	public TGViewContext getViewContext(){
		return this.configEditor.getViewContext();
	}
	
	public TGProperties getDefaults(){
		return this.configEditor.getDefaults();
	}
	
	public TablatureEditor getEditor(){
		return this.configEditor.getEditor();
	}
	
	public UIWindow getWindow(){
		return this.configEditor.getWindow();
	}
	
	public UIFactory getUIFactory(){
		return this.configEditor.getUIFactory();
	}
	
	protected boolean isDisposed(){
		return (this.configEditor.isDisposed());
	}
	
	public void pack(){
		this.configEditor.pack();
	}
	
	public void loadCursor(UICursor cursor){
		this.configEditor.loadCursor(cursor);
	}
	
	protected void addSyncThread(Runnable runnable){
		this.configEditor.addSyncThread(runnable);
	}
}