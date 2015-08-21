package org.herac.tuxguitar.app.view.dialog.confirm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;

public class TGConfirmDialog {

	public static final String ATTRIBUTE_MESSAGE = "message";
	public static final String ATTRIBUTE_STYLE = "style";
	public static final String ATTRIBUTE_DEFAULT_BUTTON = "defaultButton";
	public static final String ATTRIBUTE_RUNNABLE_YES = "yesRunnable";
	public static final String ATTRIBUTE_RUNNABLE_NO = "noRunnable";
	public static final String ATTRIBUTE_RUNNABLE_CANCEL = "cancelRunnable";
	
	public static int BUTTON_CANCEL = 0x01;
	public static int BUTTON_YES = 0x02;
	public static int BUTTON_NO = 0x04;
	
	public void show(final TGViewContext context) {
		final String message = context.getAttribute(ATTRIBUTE_MESSAGE);
		final Integer style = context.getAttribute(ATTRIBUTE_STYLE);
		final Integer defaultButton = context.getAttribute(ATTRIBUTE_DEFAULT_BUTTON);
		final Runnable yesRunnable = context.getAttribute(ATTRIBUTE_RUNNABLE_YES);
		final Runnable noRunnable = context.getAttribute(ATTRIBUTE_RUNNABLE_NO);
		final Runnable cancelRunnable = context.getAttribute(ATTRIBUTE_RUNNABLE_CANCEL);
		
		final Shell parent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		dialog.setLayout(new GridLayout(1, true));
		
		//========================================================================
		Composite labelComposite = new Composite(dialog, SWT.NONE);
		labelComposite.setLayout(new GridLayout(2, false));
		labelComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		Label icon = new Label(labelComposite, SWT.NONE);
		icon.setImage(parent.getDisplay().getSystemImage(SWT.ICON_QUESTION));
		
		Label messageLabel = new Label(labelComposite, SWT.NONE);
		messageLabel.setText(message);
		
		//========================================================================
		GridLayout buttonsLayout = new GridLayout(0,false);
		Composite buttonsComposite = new Composite(dialog, SWT.NONE);
		buttonsComposite.setLayout(buttonsLayout);
		buttonsComposite.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		if((style & BUTTON_YES) != 0){
			addCloseButton(dialog, buttonsComposite, TuxGuitar.getProperty("yes"), yesRunnable, (defaultButton == BUTTON_YES));
			buttonsLayout.numColumns ++;
		}
		if((style & BUTTON_NO) != 0){
			addCloseButton(dialog, buttonsComposite, TuxGuitar.getProperty("no"), noRunnable, (defaultButton == BUTTON_NO));
			buttonsLayout.numColumns ++;
		}
		if((style & BUTTON_CANCEL) != 0){
			addCloseButton(dialog, buttonsComposite, TuxGuitar.getProperty("cancel"), cancelRunnable, (defaultButton == BUTTON_CANCEL));
			buttonsLayout.numColumns ++;
		}
		
		DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	private void addCloseButton(final Shell dialog, Composite parent, String text, final Runnable runnable, boolean defaultButton){
		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(getButtonData());
		button.setText(text);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
				if( runnable != null ) {
					runnable.run();
				}
			}
		});
		if( defaultButton ){
			dialog.setDefaultButton(button);
		}
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
}
