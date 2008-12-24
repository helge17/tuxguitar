/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.system.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

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
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.ActionLock;
import org.herac.tuxguitar.gui.editors.TablatureEditor;
import org.herac.tuxguitar.gui.system.config.items.LanguageOption;
import org.herac.tuxguitar.gui.system.config.items.MainOption;
import org.herac.tuxguitar.gui.system.config.items.Option;
import org.herac.tuxguitar.gui.system.config.items.SkinOption;
import org.herac.tuxguitar.gui.system.config.items.SoundOption;
import org.herac.tuxguitar.gui.system.config.items.StylesOption;
import org.herac.tuxguitar.gui.system.config.items.ToolBarsOption;
import org.herac.tuxguitar.gui.util.ConfirmDialog;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TGConfigEditor{
	
	protected Shell dialog;
	protected TGConfigManager config;
	protected List options;
	protected Properties defaults;
	protected boolean accepted;
	
	protected List runnables;
	
	public TGConfigEditor() {
		this.config = TuxGuitar.instance().getConfig();
	}
	
	public void showDialog(Shell shell) {
		this.accepted = false;
		
		this.dialog = DialogUtils.newDialog(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
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
			public void widgetSelected(SelectionEvent arg0) {
				TGConfigEditor.this.accepted = true;
				TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
				dispose();
				setDefaults();
				ConfirmDialog confirm = new ConfirmDialog(TuxGuitar.getProperty("settings.config.apply-changes-question"));
				confirm.setDefaultStatus( ConfirmDialog.STATUS_NO );
				if(confirm.confirm(ConfirmDialog.BUTTON_YES | ConfirmDialog.BUTTON_NO, ConfirmDialog.BUTTON_YES) == ConfirmDialog.STATUS_NO){
					TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
					ActionLock.unlock();
					return;
				}
				applyConfig(true);
			}
		});
		
		Button buttonOK = new Button(buttonComposite, SWT.PUSH);
		buttonOK.setLayoutData(getButtonData());
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				TGConfigEditor.this.accepted = true;
				TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
				updateOptions();
				dispose();
				ConfirmDialog confirm = new ConfirmDialog(TuxGuitar.getProperty("settings.config.apply-changes-question"));
				confirm.setDefaultStatus( ConfirmDialog.STATUS_NO );
				if(confirm.confirm(ConfirmDialog.BUTTON_YES | ConfirmDialog.BUTTON_NO, ConfirmDialog.BUTTON_YES) == ConfirmDialog.STATUS_NO){
					TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
					ActionLock.unlock();
					return;
				}
				applyConfig(false);
			}
		});
		
		Button buttonCancel = new Button(buttonComposite, SWT.PUSH);
		buttonCancel.setLayoutData(getButtonData()); 
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
				dispose();
				TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
			}
		});
		
		this.dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(this.dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
		
		if(!this.accepted){
			ActionLock.unlock();
		}
	}
	
	private void createComposites(Composite parent) {
		ToolBar toolBar = new ToolBar(parent, SWT.VERTICAL | SWT.FLAT | SWT.WRAP);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true , true));
		
		Composite option = new Composite(parent,SWT.NONE);
		option.setLayout(new FormLayout());
		
		initOptions(toolBar,option);
		
		Point optionSize = computeOptionsSize( 0 , toolBar.computeSize(SWT.DEFAULT,SWT.DEFAULT).y );
		option.setLayoutData(new GridData(optionSize.x,optionSize.y));
		
		if(this.options.size() > 0){
			select((Option)this.options.get(0));
		}
	}
	
	private void initOptions(ToolBar toolBar,Composite parent){
		this.options = new ArrayList();
		this.options.add(new MainOption(this,toolBar,parent));
		this.options.add(new StylesOption(this,toolBar,parent));
		this.options.add(new LanguageOption(this,toolBar,parent));
		this.options.add(new ToolBarsOption(this,toolBar,parent));
		this.options.add(new SkinOption(this,toolBar,parent));
		this.options.add(new SoundOption(this,toolBar,parent));
		
		Iterator it = this.options.iterator();
		while(it.hasNext()){
			Option option = (Option)it.next();
			option.createOption();
		}
	}
	
	private Point computeOptionsSize(int minimumWidth, int minimumHeight){
		int width = minimumWidth;
		int height = minimumHeight;
		
		Iterator it = this.options.iterator();
		while(it.hasNext()){
			Option option = (Option)it.next();
			Point size = option.computeSize();
			if(size.x > width){
				width = size.x;
			}
			if(size.y > height){
				height = size.y;
			}
		}
		return new Point(width, height);
	}
	
	public void pack(){
		this.dialog.pack();
	}
	
	protected GridData getButtonData(){
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
		//this.dialog.layout();
		this.dialog.redraw();
	}
	
	private void hideAll(){
		Iterator it = this.options.iterator();
		while(it.hasNext()){
			Option option = (Option)it.next();
			option.setVisible(false);
		}
	}
	
	protected void updateOptions(){
		Iterator it = this.options.iterator();
		while(it.hasNext()){
			Option option = (Option)it.next();
			option.updateConfig();
		}
		this.config.save();
	}
	
	protected void setDefaults(){
		Iterator it = this.options.iterator();
		while(it.hasNext()){
			Option option = (Option)it.next();
			option.updateDefaults();
		}
		this.config.save();
	}
	
	protected void applyConfig(final boolean force){
		TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
		new Thread(new Runnable() {
			public void run() {
				TGConfigEditor.this.runnables = new ArrayList();
				
				Iterator it = TGConfigEditor.this.options.iterator();
				while(it.hasNext()){
					Option option = (Option)it.next();
					option.applyConfig(force);
				}
				try {
					TGSynchronizer.instance().runLater(new TGSynchronizer.TGRunnable() {
						public void run() throws Throwable {
							Iterator it = TGConfigEditor.this.runnables.iterator();
							while(it.hasNext()){
								Runnable current = (Runnable)it.next();
								current.run();
							}
							new Thread(new Runnable() {
								public void run() {
									TuxGuitar.instance().fireUpdate();
									TuxGuitar.instance().updateCache(true);
									TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
									ActionLock.unlock();
								}
							}).start();
						}
					});
				} catch (Throwable throwable) {
					TuxGuitar.instance().fireUpdate();
					TuxGuitar.instance().updateCache(true);
					TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
					ActionLock.unlock();
					throwable.printStackTrace();
				}
			}
		}).start();
	}
	
	protected void dispose(){
		Iterator it = this.options.iterator();
		while(it.hasNext()){
			Option option = (Option)it.next();
			option.dispose();
		}
		getDialog().dispose();
	}
	
	public Properties getDefaults(){
		if(this.defaults == null){
			this.defaults = new TGConfigDefaults().getProperties();
		}
		return this.defaults;
	}
	
	public TGConfigManager getConfig(){
		return this.config;
	}
	
	public TablatureEditor getEditor(){
		return TuxGuitar.instance().getTablatureEditor();
	}
	
	public Shell getDialog(){
		return this.dialog;
	}
	
	public void addSyncThread(Runnable runnable){
		this.runnables.add( runnable );
	}
}
