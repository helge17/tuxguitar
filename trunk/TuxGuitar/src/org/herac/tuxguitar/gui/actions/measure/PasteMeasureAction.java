/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.measure;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.clipboard.CannotInsertTransferException;
import org.herac.tuxguitar.gui.clipboard.MeasureTransferable;
import org.herac.tuxguitar.gui.clipboard.Transferable;
import org.herac.tuxguitar.gui.editors.tab.TGMeasureImpl;
import org.herac.tuxguitar.gui.util.DialogUtils;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PasteMeasureAction extends Action{
	public static final String NAME = "action.measure.paste";
	protected int pasteMode;
	
	public PasteMeasureAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		this.pasteMode = MeasureTransferable.TRANSFER_TYPE_REPLACE;
		showDialog(getEditor().getTablature().getShell());
		return 0;
	}
	
	public void showDialog(Shell shell) {
		TGMeasureImpl measure = getEditor().getTablature().getCaret().getMeasure();
		if (measure != null) {
			final Shell dialog = DialogUtils.newDialog(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			dialog.setLayout(new GridLayout());
			dialog.setText(TuxGuitar.getProperty("edit.paste"));
			
			//----------------------------------------------------------------------
			Group radios = new Group(dialog,SWT.SHADOW_ETCHED_IN);
			radios.setLayout(new GridLayout());
			radios.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			radios.setText(TuxGuitar.getProperty("edit.paste"));
			
			final Button replace = new Button(radios,SWT.RADIO);
			replace.setText(TuxGuitar.getProperty("edit.paste.replace-mode"));
			
			final Button insert = new Button(radios,SWT.RADIO);
			insert.setText(TuxGuitar.getProperty("edit.paste.insert-mode"));
			
			replace.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					if(replace.getSelection()){
						PasteMeasureAction.this.pasteMode = MeasureTransferable.TRANSFER_TYPE_REPLACE;
					}
				}
			});
			insert.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					if(insert.getSelection()){
						PasteMeasureAction.this.pasteMode = MeasureTransferable.TRANSFER_TYPE_INSERT;
					}
				}
			});
			replace.setSelection(true);
			//------------------BUTTONS--------------------------
			Composite buttons = new Composite(dialog, SWT.NONE);
			buttons.setLayout(new GridLayout(2,false));
			buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
			
			final Button buttonOK = new Button(buttons, SWT.PUSH);
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.setLayoutData(getButtonData());
			buttonOK.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					pasteMeasures();
					dialog.dispose();
				}
			});
			
			Button buttonCancel = new Button(buttons, SWT.PUSH);
			buttonCancel.setText(TuxGuitar.getProperty("cancel"));
			buttonCancel.setLayoutData(getButtonData());
			buttonCancel.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					dialog.dispose();
				}
			});
			
			dialog.setDefaultButton( buttonOK );
			
			DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
		}
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	protected void pasteMeasures(){
		try {
			Transferable transferable = getEditor().getClipBoard().getTransferable();
			if(transferable instanceof MeasureTransferable){
				((MeasureTransferable)transferable).setTransferType(this.pasteMode);
				transferable.insertTransfer();
			
				updateTablature();
			}
		} catch (CannotInsertTransferException ex) {
			ex.printStackTrace();
		}
	}
}
