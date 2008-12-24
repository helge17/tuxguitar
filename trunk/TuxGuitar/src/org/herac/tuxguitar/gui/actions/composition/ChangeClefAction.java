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
import org.herac.tuxguitar.gui.undo.undoables.custom.UndoableChangeClef;
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
public class ChangeClefAction extends Action{
	public static final String NAME = "action.composition.change-clef";
	
	public ChangeClefAction() {
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
			dialog.setText(TuxGuitar.getProperty("composition.clef"));
			
			//-------clef-------------------------------------
			Group clef = new Group(dialog,SWT.SHADOW_ETCHED_IN);
			clef.setLayout(new GridLayout(2,false));
			clef.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			clef.setText(TuxGuitar.getProperty("composition.clef"));
			
			Label numeratorLabel = new Label(clef, SWT.NULL);
			numeratorLabel.setText(TuxGuitar.getProperty("composition.clef") + ":");
			
			final Combo clefs = new Combo(clef, SWT.DROP_DOWN | SWT.READ_ONLY);
			
			clefs.add(TuxGuitar.getProperty("composition.clef.treble"));
			clefs.add(TuxGuitar.getProperty("composition.clef.bass"));
			clefs.add(TuxGuitar.getProperty("composition.clef.tenor"));
			clefs.add(TuxGuitar.getProperty("composition.clef.alto"));
			clefs.select(measure.getClef() - 1);
			clefs.setLayoutData(getComboData());
			
			//--------------------To End Checkbox-------------------------------
			Group check = new Group(dialog,SWT.SHADOW_ETCHED_IN);
			check.setLayout(new GridLayout());
			check.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			check.setText(TuxGuitar.getProperty("options"));
			
			final Button toEnd = new Button(check, SWT.CHECK);
			toEnd.setText(TuxGuitar.getProperty("composition.clef.to-the-end"));
			toEnd.setSelection(true);
			//------------------BUTTONS--------------------------
			Composite buttons = new Composite(dialog, SWT.NONE);
			buttons.setLayout(new GridLayout(2,false));
			buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
			
			final Button buttonOK = new Button(buttons, SWT.PUSH);
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.setLayoutData(getButtonData());
			buttonOK.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					final boolean toEndValue = toEnd.getSelection();
					final int clef = (clefs.getSelectionIndex() + 1);
					
					dialog.dispose();
					try {
						TGSynchronizer.instance().runLater(new TGSynchronizer.TGRunnable() {
							public void run() throws Throwable {
								ActionLock.lock();
								TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
								setClef(clef,toEndValue);
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
	
	private GridData getComboData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 150;
		return data;
	}
	
	protected GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	protected void setClef(int clef,boolean toEnd){
		//comienza el undoable
		UndoableChangeClef undoable = UndoableChangeClef.startUndo();
		
		TGMeasure measure = getEditor().getTablature().getCaret().getMeasure();
		TGTrack track = getEditor().getTablature().getCaret().getTrack();
		getSongManager().getTrackManager().changeClef(track,measure.getStart(),clef,toEnd);
		
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		
		//actualizo la tablatura
		updateTablature();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo(clef,toEnd));
	}
}
