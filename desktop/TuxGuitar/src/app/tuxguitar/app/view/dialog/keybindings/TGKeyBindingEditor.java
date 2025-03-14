package app.tuxguitar.app.view.dialog.keybindings;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.settings.TGReloadLanguageAction;
import app.tuxguitar.app.action.impl.settings.TGReloadSettingsAction;
import app.tuxguitar.app.system.keybindings.KeyBindingAction;
import app.tuxguitar.app.system.keybindings.KeyBindingActionDefaults;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UIModifyEvent;
import app.tuxguitar.ui.event.UIModifyListener;
import app.tuxguitar.ui.event.UIMouseDoubleClickListener;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIKeyCombination;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UITable;
import app.tuxguitar.ui.widget.UITableItem;
import app.tuxguitar.ui.widget.UITextField;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGKeyBindFormatter;

public class TGKeyBindingEditor {

	private TGViewContext context;
	private UITextField filterText;
	private UIWindow dialog;
	private UITable<KeyBindingAction> table;
	private List<KeyBindingAction> kbActions;

	public TGKeyBindingEditor(TGViewContext context){
		this.context = context;
		this.kbActions = new ArrayList<KeyBindingAction>();
	}

	public void show() {
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();

		this.dialog = uiFactory.createWindow(uiParent, true, false);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(TuxGuitar.getProperty("key-bindings-editor"));

		this.filterText = uiFactory.createTextField(this.dialog);
		this.filterText.addModifyListener(new UIModifyListener() {
			@Override
			public void onModify(UIModifyEvent event) {
				TGKeyBindingEditor.this.updateTableItems();
			}
		});
		dialogLayout.set(filterText, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.table = uiFactory.createTable(this.dialog, true);
		this.table.setColumns(2);
		this.table.setColumnName(0, TuxGuitar.getProperty("key-bindings-editor-action-column"));
		this.table.setColumnName(1, TuxGuitar.getProperty("key-bindings-editor-shortcut-column"));
		this.table.addMouseDoubleClickListener(new UIMouseDoubleClickListener() {
			public void onMouseDoubleClick(UIMouseEvent event) {
				final KeyBindingAction kbAction = TGKeyBindingEditor.this.table.getSelectedValue();
				if( kbAction != null ){
					TGKeyBindingSelector keyBindingSelector = new TGKeyBindingSelector(TGKeyBindingEditor.this, kbAction, new TGKeyBindingSelectorHandler() {
						public void handleSelection(UIKeyCombination kb) {
							TGKeyBindingEditor.this.removeKeyBindingAction(kb);
							kbAction.setCombination(kb);
							TGKeyBindingEditor.this.updateTableItems();
						}
					});
					keyBindingSelector.select(TGKeyBindingEditor.this.dialog);
				}
			}
		});
		dialogLayout.set(this.table, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		dialogLayout.set(this.table, UITableLayout.MAXIMUM_PACKED_WIDTH, 500f);
		dialogLayout.set(this.table, UITableLayout.PACKED_HEIGHT, 250f);

		this.loadCurrentKeyBindingActions();

		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(this.dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		UIButton defaults = uiFactory.createButton(buttons);
		defaults.setText(TuxGuitar.getProperty("defaults"));
		defaults.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGKeyBindingEditor.this.loadDefaultKeyBindingActions();
			}
		});
		buttonsLayout.set(defaults, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

		UIButton close = uiFactory.createButton(buttons);
		close.setText(TuxGuitar.getProperty("close"));
		close.setDefaultButton();
		close.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGKeyBindingEditor.this.dialog.dispose();
			}
		});
		buttonsLayout.set(close, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(close, UITableLayout.MARGIN_RIGHT, 0f);

		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				save();
			}
		});

		TGDialogUtil.openDialog(this.dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	public void createKeyBindingActions(List<KeyBindingAction> keyBindingActions) {
		this.kbActions.clear();

		List<String> actionIds = TuxGuitar.getInstance().getActionAdapterManager().getKeyBindingActionIds().getActionIds();
		for(String actionId : actionIds) {
			this.kbActions.add(new KeyBindingAction(actionId, this.findKeyBinding(keyBindingActions, actionId)));
		}
	}

	public UIKeyCombination findKeyBinding(List<KeyBindingAction> keyBindingActions, String actionId) {
		for(KeyBindingAction keyBindingAction : keyBindingActions) {
			if( keyBindingAction.getAction().equals(actionId)){
				return (UIKeyCombination) keyBindingAction.getCombination().clone();
			}
		}
		return null;
	}

	public void loadCurrentKeyBindingActions() {
		this.createKeyBindingActions(TuxGuitar.getInstance().getKeyBindingManager().getKeyBindingActions());
		this.updateTableItems();
	}

	public void loadDefaultKeyBindingActions() {
		this.createKeyBindingActions(KeyBindingActionDefaults.getDefaultKeyBindings(getContext().getContext()));
		this.updateTableItems();
	}

	public void updateTableItems() {
		KeyBindingAction selection = this.table.getSelectedValue();

		this.table.removeItems();
		TGKeyBindFormatter formatter = TGKeyBindFormatter.getInstance();
		for(KeyBindingAction kbAction : this.kbActions) {
			UITableItem<KeyBindingAction> item = new UITableItem<KeyBindingAction>(kbAction);
			item.setText(0, TuxGuitar.getProperty(kbAction.getAction()));
			item.setText(1, (kbAction.getCombination() != null ? formatter.format(kbAction.getCombination().getKeyStrings()) : ""));

			if (item.getText(0).toLowerCase().contains(this.filterText.getText().toLowerCase())) {
				this.table.addItem(item);
			}
		}
		this.table.setSelectedValue(selection);
	}

	public KeyBindingAction findKeyBindingAction(UIKeyCombination kb){
		if( kb != null ){
			for(KeyBindingAction kbAction : this.kbActions){
				if( kb.equals(kbAction.getCombination())){
					return kbAction;
				}
			}
		}
		return null;
	}

	public void removeKeyBindingAction(UIKeyCombination kb){
		KeyBindingAction kbAction = this.findKeyBindingAction(kb);
		if( kbAction != null ){
			kbAction.setCombination(null);
		}
	}

	public boolean exists(UIKeyCombination kb){
		KeyBindingAction kbAction = this.findKeyBindingAction(kb);

		return (kbAction != null);
	}

	public void save(){
		List<KeyBindingAction> list = new ArrayList<KeyBindingAction>();
		for(KeyBindingAction kbAction : this.kbActions){
			if( kbAction.getAction() != null && kbAction.getCombination() != null){
				list.add(kbAction);
			}
		}

		TuxGuitar.getInstance().getKeyBindingManager().reset(list);
		TuxGuitar.getInstance().getKeyBindingManager().saveKeyBindings();

		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGReloadLanguageAction.NAME);
		tgActionProcessor.setAttribute(TGReloadSettingsAction.ATTRIBUTE_FORCE, true);
		tgActionProcessor.process();
	}

	public TGViewContext getContext() {
		return this.context;
	}

	public boolean isDisposed(){
		return (this.dialog == null || this.dialog.isDisposed());
	}
}
