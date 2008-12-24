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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.actions.ActionLock;
import org.herac.tuxguitar.gui.editors.tab.TGMeasureImpl;
import org.herac.tuxguitar.gui.undo.undoables.custom.UndoableChangeTempo;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.gui.util.MessageDialog;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGTempo;
import org.herac.tuxguitar.util.TGSynchronizer;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChangeTempoAction extends Action{
	public static final String NAME = "action.composition.change-tempo";
	private static final int MIN_TEMPO = 30;
	private static final int MAX_TEMPO = 320;
	
	protected static final int[] DEFAULT_PERCENTS = new int[]{25,50,75,100,125,150,175,200};
	
	public ChangeTempoAction() {
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
			dialog.setText(TuxGuitar.getProperty("composition.tempo"));
			
			//-----------------TEMPO------------------------
			Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
			group.setLayout(new GridLayout(2,false));
			group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			group.setText(TuxGuitar.getProperty("composition.tempo"));
			
			TGTempo currentTempo = measure.getTempo();
			Label tempoLabel = new Label(group, SWT.NULL);
			tempoLabel.setText(TuxGuitar.getProperty("composition.tempo"));
			
			final Spinner tempo = new Spinner(group, SWT.BORDER);
			tempo.setLayoutData(getSpinnerData());
			tempo.setMinimum(MIN_TEMPO);
			tempo.setMaximum(MAX_TEMPO);
			tempo.setSelection(currentTempo.getValue());
			
			//------------------OPTIONS--------------------------
			Group options = new Group(dialog,SWT.SHADOW_ETCHED_IN);
			options.setLayout(new GridLayout());
			options.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			options.setText(TuxGuitar.getProperty("options"));
			
			final Button applyToAllMeasures = new Button(options, SWT.RADIO);
			applyToAllMeasures.setText(TuxGuitar.getProperty("composition.tempo.start-to-end"));
			
			final Button applyToEnd = new Button(options, SWT.RADIO);
			applyToEnd.setText(TuxGuitar.getProperty("composition.tempo.position-to-end"));
			
			final Button applyToNext = new Button(options, SWT.RADIO);
			applyToNext.setText(TuxGuitar.getProperty("composition.tempo.position-to-next"));
			
			applyToAllMeasures.setSelection(true);
			//------------------BUTTONS--------------------------
			Composite buttons = new Composite(dialog, SWT.NONE);
			buttons.setLayout(new GridLayout(2,false));
			buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
			
			final Button buttonOK = new Button(buttons, SWT.PUSH);
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.setLayoutData(getButtonData());
			buttonOK.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					final int tempoValue = tempo.getSelection();
					final boolean applyToEndValue = applyToEnd.getSelection();
					final boolean applyToAllMeasuresValue = applyToAllMeasures.getSelection();
					
					dialog.dispose();
					try {
						TGSynchronizer.instance().runLater(new TGSynchronizer.TGRunnable() {
							public void run() throws Throwable {
								ActionLock.lock();
								TuxGuitar.instance().loadCursor(SWT.CURSOR_WAIT);
								setTempo(tempoValue, applyToAllMeasuresValue, applyToEndValue);
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
	
	private GridData getSpinnerData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 150;
		return data;
	}
	
	protected void setTempo(int tempoValue,boolean applyToAllMeasures,boolean applyToEnd){
		if(tempoValue >= MIN_TEMPO && MAX_TEMPO <= 320){
			TGTempo tempo = getSongManager().getFactory().newTempo();
			tempo.setValue(tempoValue);
			
			long start = (applyToAllMeasures ? TGDuration.QUARTER_TIME : getEditor().getTablature().getCaret().getMeasure().getStart());
			boolean toEnd = (applyToAllMeasures || applyToEnd);
			
			//comienza el undoable
			UndoableChangeTempo undoable = UndoableChangeTempo.startUndo();
			
			getSongManager().changeTempos(start,tempo,toEnd);
			
			TuxGuitar.instance().getFileHistory().setUnsavedFile();
			
			//actualizo la tablatura
			updateTablature();
			
			//termia el undoable
			addUndoableEdit(undoable.endUndo());
		}
	}
	
	public TGSongManager getSongManager(){
		return super.getSongManager();
	}
}
