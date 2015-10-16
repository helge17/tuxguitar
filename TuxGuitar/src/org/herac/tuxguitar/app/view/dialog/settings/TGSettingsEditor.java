package org.herac.tuxguitar.app.view.dialog.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadSettingsAction;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.system.config.TGConfigDefaults;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialog;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.app.view.dialog.settings.items.LanguageOption;
import org.herac.tuxguitar.app.view.dialog.settings.items.MainOption;
import org.herac.tuxguitar.app.view.dialog.settings.items.Option;
import org.herac.tuxguitar.app.view.dialog.settings.items.SkinOption;
import org.herac.tuxguitar.app.view.dialog.settings.items.SoundOption;
import org.herac.tuxguitar.app.view.dialog.settings.items.StylesOption;
import org.herac.tuxguitar.app.view.util.TGCursorController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.properties.TGProperties;

public class TGSettingsEditor{
	
	private TGViewContext context;
	private TGCursorController cursorController;
	private Shell dialog;
	private TGConfigManager config;
	private List<Option> options;
	private TGProperties defaults;
	
	private List<Runnable> runnables;
	
	public TGSettingsEditor(TGViewContext context) {
		this.context = context;
		this.config = TGConfigManager.getInstance(this.context.getContext());
	}
	
	public void show() {
		Shell parent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		
		this.dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.dialog.setLayout(new GridLayout());
		this.dialog.setText(TuxGuitar.getProperty("settings.config"));
		
		//-------main-------------------------------------
		Composite mainComposite = new Composite(this.dialog,SWT.NONE);
		mainComposite.setLayout(new GridLayout(2,false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true , true));
		createComposites(mainComposite);
		
		//-------buttons-------------------------------------
		Composite buttonComposite = new Composite(this.dialog,SWT.NONE);
		buttonComposite.setLayout(new GridLayout(3,true));
		buttonComposite.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
		
		Button buttonDefaults = new Button(buttonComposite, SWT.PUSH);
		buttonDefaults.setLayoutData(getButtonData()); 
		buttonDefaults.setText(TuxGuitar.getProperty("defaults"));
		buttonDefaults.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dispose();
				setDefaults();
				applyConfigWithConfirmation(true);
			}
		});
		
		Button buttonOK = new Button(buttonComposite, SWT.PUSH);
		buttonOK.setLayoutData(getButtonData());
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateOptions();
				dispose();
				applyConfigWithConfirmation(false);
			}
		});
		
		Button buttonCancel = new Button(buttonComposite, SWT.PUSH);
		buttonCancel.setLayoutData(getButtonData()); 
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dispose();
			}
		});
		
		this.dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(this.dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	private void createComposites(Composite parent) {
		ToolBar toolBar = new ToolBar(parent, SWT.VERTICAL | SWT.FLAT | SWT.WRAP);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true , true));
		
		Composite option = new Composite(parent,SWT.NONE);
		option.setLayout(new FormLayout());
		
		initOptions(toolBar,option);
		
		Point optionSize = computeOptionsSize(0 , toolBar.computeSize(SWT.DEFAULT,SWT.DEFAULT).y );
		option.setLayoutData(new GridData(optionSize.x,optionSize.y));
		
		if( this.options.size() > 0 ){
			select((Option)this.options.get(0));
		}
	}
	
	private void initOptions(ToolBar toolBar,Composite parent){
		this.options = new ArrayList<Option>();
		this.options.add(new MainOption(this,toolBar,parent));
		this.options.add(new StylesOption(this,toolBar,parent));
		this.options.add(new LanguageOption(this,toolBar,parent));
		this.options.add(new SkinOption(this,toolBar,parent));
		this.options.add(new SoundOption(this,toolBar,parent));
		
		Iterator<Option> it = this.options.iterator();
		while(it.hasNext()){
			Option option = (Option)it.next();
			option.createOption();
		}
	}
	
	private Point computeOptionsSize(int minimumWidth, int minimumHeight){
		int width = minimumWidth;
		int height = minimumHeight;
		
		Iterator<Option> it = this.options.iterator();
		while(it.hasNext()){
			Option option = (Option)it.next();
			Point size = option.computeSize();
			if( size.x > width ){
				width = size.x;
			}
			if( size.y > height ){
				height = size.y;
			}
		}
		return new Point(width, height);
	}
	
	public void loadCursor(int cursorStyle) {
		if(!this.isDisposed()) {
			if( this.cursorController == null || !this.cursorController.isControlling(this.dialog) ) {
				this.cursorController = new TGCursorController(this.context.getContext(), this.dialog);
			}
			this.cursorController.loadCursor(cursorStyle);
		}
	}
	
	public void pack(){
		this.dialog.pack();
	}
	
	public GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	public GridData makeGridData(int with,int height,int minWith,int minHeight){
		GridData data = new GridData();
		data.minimumWidth = minWith;
		data.minimumHeight = minHeight;
		if(with > 0){
			data.widthHint = with;
		}else{
			data.horizontalAlignment = SWT.FILL;
			data.grabExcessHorizontalSpace = true;
		}
		if(height > 0){
			data.heightHint = with;
		}else{
			data.verticalAlignment = SWT.FILL;
			data.grabExcessVerticalSpace = true;
		}
		
		return data;
	}
	
	public void select(Option option){
		hideAll();
		option.setVisible(true);
		this.dialog.redraw();
	}
	
	private void hideAll(){
		Iterator<Option> it = this.options.iterator();
		while(it.hasNext()){
			Option option = (Option)it.next();
			option.setVisible(false);
		}
	}
	
	protected void updateOptions(){
		Iterator<Option> it = this.options.iterator();
		while(it.hasNext()){
			Option option = (Option)it.next();
			option.updateConfig();
		}
		this.config.save();
	}
	
	protected void setDefaults(){
		Iterator<Option> it = this.options.iterator();
		while(it.hasNext()){
			Option option = (Option)it.next();
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
		Iterator<Option> it = this.options.iterator();
		while(it.hasNext()){
			Option option = (Option)it.next();
			option.dispose();
		}
		getDialog().dispose();
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
	
	public Shell getDialog(){
		return this.dialog;
	}
	
	public TGViewContext getViewContext() {
		return this.context;
	}
	
	public void addSyncThread(Runnable runnable){
		this.runnables.add( runnable );
	}
	
	public boolean isDisposed() {
		return (this.dialog == null || this.dialog.isDisposed());
	}
}
