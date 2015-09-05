package org.herac.tuxguitar.app.view.dialog.keybindings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadLanguageAction;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadSettingsAction;
import org.herac.tuxguitar.app.system.keybindings.KeyBinding;
import org.herac.tuxguitar.app.system.keybindings.KeyBindingAction;
import org.herac.tuxguitar.app.system.keybindings.KeyBindingActionDefaults;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.editor.action.TGActionProcessor;

public class TGKeyBindingEditor {
	
	private static final int ACTION_WIDTH = 400;
	private static final int SHORTCUT_WIDTH = 100;
	
	private TGViewContext context;
	private Shell dialog;
	private Table table;
	private List<TableItem> items;
	
	public TGKeyBindingEditor(TGViewContext context){
		this.context = context;
		this.items = new ArrayList<TableItem>();
	}
	
	public void show() {
		Shell parent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		
		this.dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.dialog.setText(TuxGuitar.getProperty("key-bindings-editor"));
		this.dialog.setLayout(new GridLayout());
		
		Composite composite = new Composite(this.dialog,SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL,SWT.NONE,true,true));
		
		this.table = new Table(composite, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		this.table.setLayoutData(new GridData((ACTION_WIDTH + SHORTCUT_WIDTH) ,250));
		this.table.setHeaderVisible(true);
		this.table.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				final TableItem item = getSelectedItem();
				if( item != null ){
					final KeyBindingAction itemData = (KeyBindingAction)item.getData();
					TGKeyBindingSelector keyBindingSelector = new TGKeyBindingSelector(TGKeyBindingEditor.this, itemData, new TGKeyBindingSelectorHandler() {
						public void handleSelection(KeyBinding kb) {
							removeKeyBindingAction(kb);
							itemData.setKeyBinding(kb);
							loadTableItemLabel(item);
						}
					});
					keyBindingSelector.select(TGKeyBindingEditor.this.dialog.getShell());
				}
			}
		});
		
		TableColumn actionColumn = new TableColumn(this.table, SWT.LEFT);
		actionColumn.setText(TuxGuitar.getProperty("key-bindings-editor-action-column"));
		
		TableColumn shortcutColumn = new TableColumn(this.table, SWT.LEFT);
		shortcutColumn.setText(TuxGuitar.getProperty("key-bindings-editor-shortcut-column"));
		
		loadAvailableActionKeyBindings();
		loadEnableActionKeyBindings(TuxGuitar.getInstance().getKeyBindingManager().getKeyBindingActions());
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(this.dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
		
		Button defaults = new Button(buttons,SWT.PUSH);
		defaults.setText(TuxGuitar.getProperty("defaults"));
		defaults.setLayoutData(getButtonData());
		defaults.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				loadEnableActionKeyBindings(KeyBindingActionDefaults.getDefaultKeyBindings(getContext().getContext()));
			}
		});
		
		Button close = new Button(buttons,SWT.PUSH);
		close.setText(TuxGuitar.getProperty("close"));
		close.setLayoutData(getButtonData());
		close.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TGKeyBindingEditor.this.dialog.dispose();
			}
		});
		
		this.dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				save();
			}
		});
		
		this.table.setLayoutData(new GridData( (adjustWidth(actionColumn,ACTION_WIDTH) + adjustWidth(shortcutColumn,SHORTCUT_WIDTH)) ,250) );
		
		this.dialog.setDefaultButton( close );
		
		DialogUtils.openDialog(this.dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	protected int adjustWidth(TableColumn column, int defaultWidth){
		column.pack();
		int width = column.getWidth();
		if( width < defaultWidth ){
			width = defaultWidth;
			column.setWidth( width );
		}
		return width;
	}
	
	protected GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	protected void loadTableItemLabel(TableItem item){
		if(item.getData() instanceof KeyBindingAction){
			KeyBindingAction actionkeyBinding = (KeyBindingAction)item.getData();
			String action = actionkeyBinding.getAction();
			String shortcut = (actionkeyBinding.getKeyBinding() != null)?actionkeyBinding.getKeyBinding().toString():"";
			item.setText(new String[] { TuxGuitar.getProperty(action),shortcut});
		}
	}
	
	protected void loadAvailableActionKeyBindings(){
		List<String> list = TuxGuitar.getInstance().getActionAdapterManager().getKeyBindingActionIds().getActionIds();
		Collections.sort(list);
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			String action = (String) it.next();
			TableItem item = new TableItem(this.table, SWT.NONE);
			item.setData(new KeyBindingAction(action,null));
			this.items.add(item);
		}
	}
	
	protected void loadEnableActionKeyBindings(List<KeyBindingAction> list){
		Iterator<TableItem> items = this.items.iterator();
		while (items.hasNext()) {
			TableItem item = (TableItem) items.next();
			if(item.getData() instanceof KeyBindingAction){
				KeyBindingAction itemData = (KeyBindingAction)item.getData();
				KeyBinding keyBinding = null;
				Iterator<KeyBindingAction> it = list.iterator();
				while (it.hasNext()) {
					KeyBindingAction keyBindingAction = (KeyBindingAction) it.next();
					if(keyBindingAction.getAction().equals(itemData.getAction())){
						keyBinding = (KeyBinding) keyBindingAction.getKeyBinding().clone();
						break;
					}
				}
				itemData.setKeyBinding(keyBinding);
				loadTableItemLabel(item);
			}
		}
	}
	
	protected void removeKeyBindingAction(KeyBinding kb){
		if(kb != null){
			Iterator<TableItem> it = this.items.iterator();
			while(it.hasNext()){
				TableItem item = (TableItem) it.next();
				if(item.getData() instanceof KeyBindingAction){
					KeyBindingAction itemData = (KeyBindingAction)item.getData();
					if(kb.isSameAs(itemData.getKeyBinding())){
						itemData.setKeyBinding(null);
						loadTableItemLabel(item);
					}
				}
			}
		}
	}
	
	protected TableItem getSelectedItem(){
		TableItem item = null;
		int itemSelected = this.table.getSelectionIndex();
		if(itemSelected >= 0){
			item = this.table.getItem(itemSelected);
		}
		return item;
	}
	
	public boolean exists(KeyBinding kb){
		Iterator<TableItem> it = this.items.iterator();
		while(it.hasNext()){
			TableItem item = (TableItem) it.next();
			if(item.getData() instanceof KeyBindingAction){
				KeyBindingAction itemData = (KeyBindingAction)item.getData();
				if(itemData.getKeyBinding() != null && kb.isSameAs(itemData.getKeyBinding())){
					return true;
				}
			}
		}
		return false;
	}
	
	protected void save(){
		List<KeyBindingAction> list = new ArrayList<KeyBindingAction>();
		Iterator<TableItem> it = this.items.iterator();
		while (it.hasNext()) {
			TableItem item = (TableItem) it.next();
			if(item.getData() instanceof KeyBindingAction){
				KeyBindingAction keyBindingAction = (KeyBindingAction)item.getData();
				if(keyBindingAction.getAction() != null && keyBindingAction.getKeyBinding() != null){
					list.add(keyBindingAction);
				}
			}
		}
		TuxGuitar.getInstance().getKeyBindingManager().reset(list);
		TuxGuitar.getInstance().getKeyBindingManager().saveKeyBindings();
//		TuxGuitar.getInstance().loadLanguage();
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGReloadLanguageAction.NAME);
		tgActionProcessor.setAttribute(TGReloadSettingsAction.ATTRIBUTE_FORCE, true);
		tgActionProcessor.process();
	}
	
	public TGViewContext getContext() {
		return this.context;
	}
	
	public Shell getDialog(){
		return this.dialog;
	}
	
	public boolean isDisposed(){
		return (this.dialog == null || this.dialog.isDisposed() );
	}
}
