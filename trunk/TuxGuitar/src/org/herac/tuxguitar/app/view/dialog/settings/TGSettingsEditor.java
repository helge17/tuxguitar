package org.herac.tuxguitar.app.view.dialog.settings;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadSettingsAction;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.system.config.TGConfigDefaults;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialog;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.app.view.dialog.settings.items.LanguageOption;
import org.herac.tuxguitar.app.view.dialog.settings.items.MainOption;
import org.herac.tuxguitar.app.view.dialog.settings.items.SkinOption;
import org.herac.tuxguitar.app.view.dialog.settings.items.SoundOption;
import org.herac.tuxguitar.app.view.dialog.settings.items.StylesOption;
import org.herac.tuxguitar.app.view.dialog.settings.items.TGSettingsOption;
import org.herac.tuxguitar.app.view.util.TGCursorController;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UICursor;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.properties.TGProperties;

public class TGSettingsEditor{
	
	private TGViewContext context;
	private TGCursorController cursorController;
	private TGConfigManager config;
	private TGProperties defaults;
	private UIWindow dialog;
	private List<TGSettingsOption> options;
	
	private List<Runnable> runnables;
	
	public TGSettingsEditor(TGViewContext context) {
		this.context = context;
		this.config = TGConfigManager.getInstance(this.context.getContext());
	}
	
	public void show() {
		final UIFactory uiFactory = this.getUIFactory();
		final UIWindow uiParent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT2);
		final UITableLayout dialogLayout = new UITableLayout();
		
		this.dialog = uiFactory.createWindow(uiParent, true, false);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(TuxGuitar.getProperty("settings.config"));
		
		//-------main-------------------------------------
		UIPanel mainComposite = uiFactory.createPanel(this.dialog, false);
		mainComposite.setLayout(new UITableLayout());
		dialogLayout.set(mainComposite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		this.createComposites(mainComposite);
		
		//-------buttons-------------------------------------
		UITableLayout buttonsLayout = new UITableLayout();
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		UIButton buttonDefaults = uiFactory.createButton(buttons); 
		buttonDefaults.setText(TuxGuitar.getProperty("defaults"));
		buttonDefaults.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dispose();
				setDefaults();
				applyConfigWithConfirmation(true);
			}
		});
		buttonsLayout.set(buttonDefaults, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setDefaultButton();
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				updateOptions();
				dispose();
				applyConfigWithConfirmation(false);
			}
		});
		buttonsLayout.set(buttonOK, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		TGDialogUtil.openDialog(this.dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	private void createComposites(UILayoutContainer parent) {
		UIFactory uiFactory = this.getUIFactory();
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();
		
		UIToolBar toolBar = uiFactory.createVerticalToolBar(parent);
		parentLayout.set(toolBar, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UIPanel option = uiFactory.createPanel(parent, false);
		option.setLayout(new UITableLayout(0f));
		parentLayout.set(option, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		initOptions(toolBar, option);
		
		if( this.options.size() > 0 ){
			select(this.options.get(0));
		}
	}
	
	private void initOptions(UIToolBar toolBar, UILayoutContainer parent){
		
		this.options = new ArrayList<TGSettingsOption>();
		this.options.add(new MainOption(this, toolBar, parent));
		this.options.add(new StylesOption(this, toolBar, parent));
		this.options.add(new LanguageOption(this, toolBar, parent));
		this.options.add(new SkinOption(this, toolBar, parent));
		this.options.add(new SoundOption(this, toolBar, parent));
		
		for(TGSettingsOption option : this.options) {
			option.createOption();
		}
	}
	
	public void loadCursor(UICursor cursor) {
		if(!this.isDisposed()) {
			if( this.cursorController == null || !this.cursorController.isControlling(this.dialog) ) {
				this.cursorController = new TGCursorController(this.context.getContext(), this.dialog);
			}
			this.cursorController.loadCursor(cursor);
		}
	}
	
	public void pack(){
		this.dialog.pack();
	}
	
	public void select(TGSettingsOption option){
		hideAll();
		option.setVisible(true);
		this.dialog.redraw();
	}
	
	private void hideAll(){
		for(TGSettingsOption option : this.options) {
			option.setVisible(false);
		}
	}
	
	protected void updateOptions(){
		for(TGSettingsOption option : this.options) {
			option.updateConfig();
		}
		this.config.save();
	}
	
	protected void setDefaults(){
		for(TGSettingsOption option : this.options) {
			option.updateDefaults();
		}
		this.config.save();
	}
	
	protected void applyConfigWithConfirmation(final boolean force) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGOpenViewAction.NAME);
		tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGConfirmDialogController());
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_MESSAGE, TuxGuitar.getProperty("settings.config.apply-changes-question"));
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_STYLE, TGConfirmDialog.BUTTON_YES | TGConfirmDialog.BUTTON_NO);
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_DEFAULT_BUTTON, TGConfirmDialog.BUTTON_NO);
		tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_RUNNABLE_YES, new Runnable() {
			public void run() {
				applyConfig(force);
			}
		});
		tgActionProcessor.process();
	}
	
	protected void applyConfig(final boolean force) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGReloadSettingsAction.NAME);
		tgActionProcessor.setAttribute(TGReloadSettingsAction.ATTRIBUTE_FORCE, force);
		tgActionProcessor.process();
	}
	
	protected void dispose(){
		for(TGSettingsOption option : this.options) {
			option.dispose();
		}
		getWindow().dispose();
	}
	
	public TGProperties getDefaults(){
		if( this.defaults == null ){
			this.defaults = TGConfigDefaults.createDefaults();
		}
		return this.defaults;
	}
	
	public TGConfigManager getConfig(){
		return this.config;
	}
	
	public TablatureEditor getEditor(){
		return TuxGuitar.getInstance().getTablatureEditor();
	}
	
	public TGViewContext getViewContext() {
		return this.context;
	}
	
	public UIWindow getWindow(){
		return this.dialog;
	}
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context.getContext()).getFactory();
	}
	
	public void addSyncThread(Runnable runnable){
		this.runnables.add( runnable );
	}
	
	public boolean isDisposed() {
		return (this.dialog == null || this.dialog.isDisposed());
	}
}
