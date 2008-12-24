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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.actions.ActionLock;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.editors.tab.TGMeasureImpl;
import org.herac.tuxguitar.gui.undo.undoables.custom.UndoableChangeTripletFeel;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.gui.util.MessageDialog;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeTripletFeelAction extends Action{
	public static final String NAME = "action.composition.change-triplet-feel";
	
	public ChangeTripletFeelAction() {
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
			dialog.setText(TuxGuitar.getProperty("composition.tripletfeel"));
			dialog.setMinimumSize(300,0);
			
			//-------------TIME SIGNATURE-----------------------------------------------
			Group tripletFeel = new Group(dialog,SWT.SHADOW_ETCHED_IN);
			tripletFeel.setLayout(new GridLayout());
			tripletFeel.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			tripletFeel.setText(TuxGuitar.getProperty("composition.tripletfeel"));
			
			//none
			final Button tripletFeelNone = new Button(tripletFeel, SWT.RADIO);
			tripletFeelNone.setText(TuxGuitar.getProperty("composition.tripletfeel.none"));
			tripletFeelNone.setSelection(measure.getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_NONE);
			
			final Button tripletFeelEighth = new Button(tripletFeel, SWT.RADIO);
			tripletFeelEighth.setText(TuxGuitar.getProperty("composition.tripletfeel.eighth"));
			tripletFeelEighth.setSelection(measure.getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_EIGHTH);
			
			final Button tripletFeelSixteenth = new Button(tripletFeel, SWT.RADIO);
			tripletFeelSixteenth.setText(TuxGuitar.getProperty("composition.tripletfeel.sixteenth"));
			tripletFeelSixteenth.setSelection(measure.getTripletFeel() == TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH);
			
			//--------------------To End Checkbox-------------------------------
			Group check = new Group(dialog,SWT.SHADOW_ETCHED_IN);
			check.setLayout(new GridLayout());
			check.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			check.setText(TuxGuitar.getProperty("options"));
			
			final Button toEnd = new Button(check, SWT.CHECK);
			toEnd.setText(TuxGuitar.getProperty("composition.tripletfeel.to-the-end"));
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
					final int tripletFeel = getSelectedTripletFeel(tripletFeelNone, tripletFeelEighth, tripletFeelSixteenth);
					
					dialog.dispose();
					try {
						TGSynchronizer.instance().runLater(new TGSynchronizer.TGRunnable() {
							public void run() throws Throwable {
								ActionLock.lock();
								TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
								setTripletFeel(tripletFeel,toEndValue);
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
	
	protected int getSelectedTripletFeel(Button tripletFeelNone,Button tripletFeelEighth, Button tripletFeelSixteenth){
		if(tripletFeelNone.getSelection()){
			return TGMeasureHeader.TRIPLET_FEEL_NONE;
		}else if(tripletFeelEighth.getSelection()){
			return TGMeasureHeader.TRIPLET_FEEL_EIGHTH;
		}else if(tripletFeelSixteenth.getSelection()){
			return TGMeasureHeader.TRIPLET_FEEL_SIXTEENTH;
		}
		return TGMeasureHeader.TRIPLET_FEEL_NONE;
	}
	
	protected void setTripletFeel(int tripletFeel,boolean toEnd){
		//comienza el undoable
		UndoableChangeTripletFeel undoable = UndoableChangeTripletFeel.startUndo();
		
		Caret caret = getEditor().getTablature().getCaret();
		TGMeasureImpl measure = caret.getMeasure();
		
		getSongManager().changeTripletFeel(measure.getStart(),tripletFeel,toEnd);
		
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		
		//actualizo la tablatura
		updateTablature();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo(tripletFeel,toEnd));
	}
}
