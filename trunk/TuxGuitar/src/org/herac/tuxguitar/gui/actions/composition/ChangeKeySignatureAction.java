/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.composition;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.actions.ActionLock;
import org.herac.tuxguitar.gui.editors.tab.TGMeasureImpl;
import org.herac.tuxguitar.gui.undo.undoables.custom.UndoableChangeKeySignature;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.gui.util.MessageDialog;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGTrack;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeKeySignatureAction extends Action{
	public static final String NAME = "action.composition.change-key-signature";
	
	public ChangeKeySignatureAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		showDialog(getEditor().getTablature().getShell());
		return 0;
	}
	
	public void showDialog(Shell shell) {
		TGMeasureImpl measure = getEditor().getTablature().getCaret().getMeasure();
		if (measure != null) {
			final Shell dialog = DialogUtils.newDialog(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			
			dialog.setLayout(new GridLayout());
			dialog.setText(TuxGuitar.getProperty("composition.keysignature"));
			
			//-------key Signature-------------------------------------
			Group keySignature = new Group(dialog,SWT.SHADOW_ETCHED_IN);
			keySignature.setLayout(new GridLayout(2,false));
			keySignature.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			keySignature.setText(TuxGuitar.getProperty("composition.keysignature"));
			
			Label numeratorLabel = new Label(keySignature, SWT.NULL);
			numeratorLabel.setText(TuxGuitar.getProperty("composition.keysignature") + ":");
			
			final Combo keySignatures = new Combo(keySignature, SWT.DROP_DOWN | SWT.READ_ONLY);
			
			keySignatures.add(TuxGuitar.getProperty("composition.keysignature.natural"));
			keySignatures.add(TuxGuitar.getProperty("composition.keysignature.sharp-1"));
			keySignatures.add(TuxGuitar.getProperty("composition.keysignature.sharp-2"));
			keySignatures.add(TuxGuitar.getProperty("composition.keysignature.sharp-3"));
			keySignatures.add(TuxGuitar.getProperty("composition.keysignature.sharp-4"));
			keySignatures.add(TuxGuitar.getProperty("composition.keysignature.sharp-5"));
			keySignatures.add(TuxGuitar.getProperty("composition.keysignature.sharp-6"));
			keySignatures.add(TuxGuitar.getProperty("composition.keysignature.sharp-7"));
			keySignatures.add(TuxGuitar.getProperty("composition.keysignature.flat-1"));
			keySignatures.add(TuxGuitar.getProperty("composition.keysignature.flat-2"));
			keySignatures.add(TuxGuitar.getProperty("composition.keysignature.flat-3"));
			keySignatures.add(TuxGuitar.getProperty("composition.keysignature.flat-4"));
			keySignatures.add(TuxGuitar.getProperty("composition.keysignature.flat-5"));
			keySignatures.add(TuxGuitar.getProperty("composition.keysignature.flat-6"));
			keySignatures.add(TuxGuitar.getProperty("composition.keysignature.flat-7"));
			keySignatures.select(measure.getKeySignature());
			keySignatures.setLayoutData(getComboData());
			//--------------------To End Checkbox-------------------------------
			Group check = new Group(dialog,SWT.SHADOW_ETCHED_IN);
			check.setLayout(new GridLayout());
			check.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			check.setText(TuxGuitar.getProperty("options"));
			
			final Button toEnd = new Button(check, SWT.CHECK);
			toEnd.setText(TuxGuitar.getProperty("composition.keysignature.to-the-end"));
			toEnd.setSelection(true);
			//------------------BUTTONS--------------------------
			Composite buttons = new Composite(dialog, SWT.NONE);
			buttons.setLayout(new GridLayout(2,false));
			buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
			
			final Button buttonOK = new Button(buttons, SWT.PUSH);
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.setLayoutData(getButtonData());
			buttonOK.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					final boolean toEndValue = toEnd.getSelection();
					final int keySignature = keySignatures.getSelectionIndex();
					
					dialog.dispose();
					try {
						TGSynchronizer.instance().runLater(new TGSynchronizer.TGRunnable() {
							public void run() throws Throwable {
								ActionLock.lock();
								TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
								setKeySignature(keySignature,toEndValue);
								TuxGuitar.instance().updateCache( true );
								TuxGuitar.instance().loadCursor(SWT.CURSOR_ARROW);
								ActionLock.unlock();
							}
						});
					} catch (Throwable throwable) {
						MessageDialog.errorMessage(throwable);
					}
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
	
	private GridData getComboData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 150;
		return data;
	}
	
	protected void setKeySignature(int keySignature,boolean toEnd){
		//comienza el undoable
		UndoableChangeKeySignature undoable = UndoableChangeKeySignature.startUndo();
		
		TGMeasure measure = getEditor().getTablature().getCaret().getMeasure();
		TGTrack track = getEditor().getTablature().getCaret().getTrack();
		getSongManager().getTrackManager().changeKeySignature(track,measure.getStart(),keySignature,toEnd);
		
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		
		//actualizo la tablatura
		updateTablature();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo(keySignature,toEnd));
		
	}
}
