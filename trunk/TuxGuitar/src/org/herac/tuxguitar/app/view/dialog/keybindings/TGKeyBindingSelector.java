package org.herac.tuxguitar.app.view.dialog.keybindings;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.system.keybindings.KeyBindingAction;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialog;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UIKeyEvent;
import org.herac.tuxguitar.ui.event.UIKeyReleasedListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIKeyConvination;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIImageView;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIWindow;

public class TGKeyBindingSelector {
	
	public static final String ATTRIBUTE_EDITOR = TGKeyBindingEditor.class.getName();
	public static final String ATTRIBUTE_KB_ACTION = KeyBindingAction.class.getName();
	
	private TGKeyBindingEditor editor;
	private KeyBindingAction keyBindingAction;
	private TGKeyBindingSelectorHandler handler;
	
	public TGKeyBindingSelector(TGKeyBindingEditor editor, KeyBindingAction keyBindingAction, TGKeyBindingSelectorHandler handler){
		this.editor = editor;
		this.keyBindingAction = keyBindingAction;
		this.handler = handler;
	}
	
	public void select(UIWindow parent){
		final UIFactory uiFactory = TGApplication.getInstance(this.editor.getContext().getContext()).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(parent, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("key-bindings-editor"));
		
		UITableLayout legendLayout = new UITableLayout();
		UILegendPanel legend = uiFactory.createLegendPanel(dialog);
		legend.setLayout(legendLayout);
		legend.setText(TuxGuitar.getProperty(this.keyBindingAction.getAction()));
		dialogLayout.set(legend, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UITableLayout panelLayout = new UITableLayout();
		final UIPanel panel = uiFactory.createPanel(legend, false);
		panel.setLayout(panelLayout);
		panel.setFocus();
		panel.addKeyReleasedListener(new UIKeyReleasedListener() {
			public void onKeyReleased(UIKeyEvent event) {
				TGKeyBindingSelector.this.handleSelection(event.getKeyConvination());
				
				dialog.dispose();
			}
		});
		legendLayout.set(panel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UIImageView iconLabel = uiFactory.createImageView(panel);
		iconLabel.setImage(TGIconManager.getInstance(this.editor.getContext().getContext()).getStatusInfo());
		panelLayout.set(iconLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		UILabel textLabel = uiFactory.createLabel(panel);
		textLabel.setText(TuxGuitar.getProperty("key-bindings-editor-push-a-key"));
		panelLayout.set(textLabel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, true);
		
		UIFont defaultFont = textLabel.getFont();
		if( defaultFont != null ) {
			final UIFont font = uiFactory.createFont(defaultFont.getName(), 14, true, false);
			textLabel.setFont(font);
			textLabel.addDisposeListener(new UIDisposeListener() {
				public void onDispose(UIDisposeEvent event) {
					font.dispose();
				}
			});
		}
		
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		final UIButton buttonClean = uiFactory.createButton(buttons);
		buttonClean.setText(TuxGuitar.getProperty("clean"));
		buttonClean.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGKeyBindingSelector.this.handleSelection(null);
				dialog.dispose();
			}
		});
		
		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		
		buttonsLayout.set(buttonClean, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
		
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void handleSelection(final UIKeyConvination kb) {
		if( kb == null || kb.equals(this.keyBindingAction.getConvination()) || !this.editor.exists(kb) ){
			this.handler.handleSelection(kb);
		}
		
		else {
			TGActionProcessor tgActionProcessor = new TGActionProcessor(this.editor.getContext().getContext(), TGOpenViewAction.NAME);
			tgActionProcessor.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGConfirmDialogController());
			tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_MESSAGE, TuxGuitar.getProperty("key-bindings-editor-override"));
			tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_STYLE, TGConfirmDialog.BUTTON_YES | TGConfirmDialog.BUTTON_NO);
			tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_DEFAULT_BUTTON, TGConfirmDialog.BUTTON_NO);
			tgActionProcessor.setAttribute(TGConfirmDialog.ATTRIBUTE_RUNNABLE_YES, new Runnable() {
				public void run() {
					TGKeyBindingSelector.this.handler.handleSelection(kb);
				}
			});
			tgActionProcessor.process();
		}
	}
}
