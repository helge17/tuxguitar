package org.herac.tuxguitar.app.view.dialog.keybindings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.system.keybindings.KeyBinding;
import org.herac.tuxguitar.app.system.keybindings.KeyBindingAction;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialog;
import org.herac.tuxguitar.app.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;

public class TGKeyBindingSelector {
	
	public static final String ATTRIBUTE_EDITOR = TGKeyBindingEditor.class.getName();
	public static final String ATTRIBUTE_KB_ACTION = KeyBindingAction.class.getName();
	
	private Shell dialog;
	private TGKeyBindingEditor editor;
	private KeyBindingAction keyBindingAction;
	private TGKeyBindingSelectorHandler handler;
	
	public TGKeyBindingSelector(TGKeyBindingEditor editor, KeyBindingAction keyBindingAction, TGKeyBindingSelectorHandler handler){
		this.editor = editor;
		this.keyBindingAction = keyBindingAction;
		this.handler = handler;
	}
	
	public void select(Shell parent){
		this.dialog = DialogUtils.newDialog(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		this.dialog.setLayout(new GridLayout());
		this.dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.dialog.setText(TuxGuitar.getProperty("key-bindings-editor"));
		
		Group group = new Group(this.dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText(TuxGuitar.getProperty(this.keyBindingAction.getAction()));
		
		final Composite composite = new Composite(group,SWT.NONE);
		composite.setLayout(new GridLayout(2,false));
		composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		composite.setFocus();
		composite.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				handleSelection( new KeyBinding(e.keyCode,e.stateMask));
				TGKeyBindingSelector.this.dialog.dispose();
			}
		});
		
		Label iconLabel = new Label(composite, SWT.CENTER );
		iconLabel.setImage(iconLabel.getDisplay().getSystemImage(SWT.ICON_INFORMATION));
		iconLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true));
		
		Label textLabel = new Label(composite,SWT.CENTER);
		textLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true));
		textLabel.setText(TuxGuitar.getProperty("key-bindings-editor-push-a-key"));
		
		FontData[] fd = textLabel.getFont().getFontData();
		if(fd != null && fd.length > 0){
			final Font font = new Font(textLabel.getDisplay(),new FontData( fd[0].getName(), 14 , SWT.BOLD) );
			textLabel.setFont(font);
			textLabel.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent arg0) {
					font.dispose();
				}
			});
		}
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(this.dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
		
		final Button buttonClean = new Button(buttons, SWT.PUSH);
		buttonClean.setText(TuxGuitar.getProperty("clean"));
		buttonClean.setLayoutData(getButtonData());
		buttonClean.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				composite.setFocus();
			}
		});
		buttonClean.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				TGKeyBindingSelector.this.dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonData());
		buttonCancel.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				composite.setFocus();
			}
		});
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				TGKeyBindingSelector.this.dialog.dispose();
			}
		});
		
		DialogUtils.openDialog(this.dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	public void handleSelection(final KeyBinding kb) {
		if( kb.isSameAs(this.keyBindingAction.getKeyBinding()) || !this.editor.exists(kb) ){
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
