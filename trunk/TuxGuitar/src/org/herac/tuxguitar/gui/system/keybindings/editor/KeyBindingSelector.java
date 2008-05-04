package org.herac.tuxguitar.gui.system.keybindings.editor;

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
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.keybindings.KeyBinding;
import org.herac.tuxguitar.gui.system.keybindings.KeyBindingAction;
import org.herac.tuxguitar.gui.system.keybindings.KeyBindingReserveds;
import org.herac.tuxguitar.gui.util.ConfirmDialog;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.gui.util.MessageDialog;

public class KeyBindingSelector {
	
	protected Shell dialog;
	protected KeyBindingEditor editor;
	protected KeyBinding keyBinding;
	protected String action;
	
	public KeyBindingSelector(KeyBindingEditor editor,KeyBindingAction keyBindingAction){
		this.editor = editor;
		this.keyBinding = keyBindingAction.getKeyBinding();
		this.action = keyBindingAction.getAction();
	}
	
	public KeyBinding select(Shell parent){
		this.dialog = DialogUtils.newDialog(parent,SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		this.dialog.setLayout(new GridLayout());
		this.dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.dialog.setText(TuxGuitar.getProperty("key-bindings-editor"));
		
		Group group = new Group(this.dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText(TuxGuitar.getProperty(this.action));
		
		final Composite composite = new Composite(group,SWT.NONE);
		composite.setLayout(new GridLayout(2,false));
		composite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		composite.setFocus();
		composite.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				KeyBinding kb = new KeyBinding(e.keyCode,e.stateMask);
				if(kb.isSameAs(KeyBindingSelector.this.keyBinding) || isValid(kb)){
					if(KeyBindingSelector.this.keyBinding == null){
						KeyBindingSelector.this.keyBinding = new KeyBinding();
					}
					KeyBindingSelector.this.keyBinding.setKey(kb.getKey());
					KeyBindingSelector.this.keyBinding.setMask(kb.getMask());
				}
				KeyBindingSelector.this.dialog.dispose();
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
				KeyBindingSelector.this.keyBinding = null;
				KeyBindingSelector.this.dialog.dispose();
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
				KeyBindingSelector.this.dialog.dispose();
			}
		});
		
		DialogUtils.openDialog(this.dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
		
		return this.keyBinding;
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	protected boolean isValid(KeyBinding kb){
		if(KeyBindingReserveds.isReserved(kb)){
			if(!this.editor.isDisposed()){
				String title = TuxGuitar.getProperty("key-bindings-editor-reserved-title");
				String message = TuxGuitar.getProperty("key-bindings-editor-reserved-message");
				MessageDialog.infoMessage(this.editor.getDialog(),title,message);
			}
			return false;
		}
		if(this.editor.exists(kb)){
			ConfirmDialog confirm = new ConfirmDialog(TuxGuitar.getProperty("key-bindings-editor-override"));
			confirm.setDefaultStatus( ConfirmDialog.STATUS_NO );
			if(confirm.confirm(ConfirmDialog.BUTTON_YES | ConfirmDialog.BUTTON_NO, ConfirmDialog.BUTTON_NO) == ConfirmDialog.STATUS_NO){
				return false;
			}
		}
		return true;
	}
}
