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
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.editors.tab.TGMeasureImpl;
import org.herac.tuxguitar.gui.undo.undoables.custom.UndoableChangeTimeSignature;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.gui.util.MessageDialog;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGTimeSignature;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeTimeSignatureAction extends Action{
	public static final String NAME = "action.composition.change-time-signature";
	
	public ChangeTimeSignatureAction() {
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
			dialog.setText(TuxGuitar.getProperty("composition.timesignature"));
			
			//-------------TIME SIGNATURE-----------------------------------------------
			Group timeSignature = new Group(dialog,SWT.SHADOW_ETCHED_IN);
			timeSignature.setLayout(new GridLayout(2,false));
			timeSignature.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			timeSignature.setText(TuxGuitar.getProperty("composition.timesignature"));
			
			TGTimeSignature currentTimeSignature = measure.getTimeSignature();
			//numerator
			Label numeratorLabel = new Label(timeSignature, SWT.NULL);
			numeratorLabel.setText(TuxGuitar.getProperty("composition.timesignature.Numerator"));
			final Combo numerator = new Combo(timeSignature, SWT.DROP_DOWN | SWT.READ_ONLY);
			for (int i = 1; i <= 32; i++) {
				numerator.add(Integer.toString(i));
			}
			numerator.setText(Integer.toString(currentTimeSignature.getNumerator()));
			numerator.setLayoutData(getComboData());
			//denominator
			Label denominatorLabel = new Label(timeSignature, SWT.NULL);
			denominatorLabel.setText(TuxGuitar.getProperty("composition.timesignature.denominator"));
			final Combo denominator = new Combo(timeSignature, SWT.DROP_DOWN | SWT.READ_ONLY);
			for (int i = 1; i <= 32; i = i * 2) {
				denominator.add(Integer.toString(i));
			}
			denominator.setText(Integer.toString(currentTimeSignature.getDenominator().getValue()));
			denominator.setLayoutData(getComboData());
			
			//--------------------To End Checkbox-------------------------------
			Group check = new Group(dialog,SWT.SHADOW_ETCHED_IN);
			check.setLayout(new GridLayout());
			check.setText(TuxGuitar.getProperty("options"));
			check.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			
			final Button toEnd = new Button(check, SWT.CHECK);
			toEnd.setText(TuxGuitar.getProperty("composition.timesignature.to-the-end"));
			toEnd.setSelection(true);
			//------------------BUTTONS--------------------------
			Composite buttons = new Composite(dialog, SWT.NONE);
			buttons.setLayout(new GridLayout(2,false));
			buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
			
			final Button buttonOk = new Button(buttons, SWT.PUSH);
			buttonOk.setText(TuxGuitar.getProperty("ok"));
			buttonOk.setLayoutData(getButtonData());
			buttonOk.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					final boolean toEndValue = toEnd.getSelection();
					final int numeratorValue = Integer.parseInt(numerator.getText());
					final int denominatorValue = Integer.parseInt(denominator.getText());
					
					dialog.dispose();
					try {
						TGSynchronizer.instance().runLater(new TGSynchronizer.TGRunnable() {
							public void run() throws Throwable {
								ActionLock.lock();
								TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
								TGTimeSignature timeSignature = getSongManager().getFactory().newTimeSignature();
								timeSignature.setNumerator(numeratorValue);
								timeSignature.getDenominator().setValue(denominatorValue);
								setTimeSignature(timeSignature,toEndValue);
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
			buttonCancel.setLayoutData(getButtonData());
			buttonCancel.setText(TuxGuitar.getProperty("cancel"));
			buttonCancel.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					dialog.dispose();
				}
			});
			
			dialog.setDefaultButton( buttonOk );
			
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
	
	protected void setTimeSignature(TGTimeSignature timeSignature,boolean toEnd){
		//comienza el undoable
		UndoableChangeTimeSignature undoable = UndoableChangeTimeSignature.startUndo();
		
		Caret caret = getEditor().getTablature().getCaret();
		TGMeasureImpl measure = caret.getMeasure();
		
		getSongManager().changeTimeSignature(measure.getStart(),timeSignature,toEnd);
		
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		
		//actualizo la tablatura
		updateTablature();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo(timeSignature,measure.getStart(),toEnd));
	}
	
	public TGSongManager getSongManager(){
		return super.getSongManager();
	}
}
