/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.insert;

import java.util.Iterator;

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
import org.herac.tuxguitar.gui.undo.undoables.UndoableJoined;
import org.herac.tuxguitar.gui.undo.undoables.custom.UndoableChangeAlternativeRepeat;
import org.herac.tuxguitar.gui.undo.undoables.custom.UndoableChangeCloseRepeat;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGMeasureHeader;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RepeatAlternativeAction extends Action{
	public static final String NAME = "action.insert.repeat-alternative";
	
	public RepeatAlternativeAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		TGMeasure measure = getEditor().getTablature().getCaret().getMeasure();
		showCloseRepeatDialog(getEditor().getTablature().getShell(), measure);
		return 0;
	}
	
	public void showCloseRepeatDialog(Shell shell, final TGMeasure measure) {
		if (measure != null) {
			int existentEndings = getExistentEndings(measure);
			int selectedEndings = (measure.getHeader().getRepeatAlternative() > 0)?measure.getHeader().getRepeatAlternative():getDefaultEndings(existentEndings);
			
			final Shell dialog = DialogUtils.newDialog(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			
			dialog.setLayout(new GridLayout());
			dialog.setText(TuxGuitar.getProperty("repeat.alternative.editor"));
			
			Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
			group.setLayout(new GridLayout(4,true));
			group.setLayoutData(getMainData());
			group.setText(TuxGuitar.getProperty("repeat.alternative"));
			
			final Button[] selections = new Button[8];
			for(int i = 0; i < selections.length; i ++){
				boolean enabled = ((existentEndings & (1 << i)) == 0);
				selections[i] = new Button(group,SWT.CHECK);
				selections[i].setText(Integer.toString( i + 1 ));
				selections[i].setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
				selections[i].setEnabled(enabled);
				selections[i].setSelection(enabled && ((selectedEndings & (1 << i)) != 0)  );
			}
			
			//----------------------BUTTONS--------------------------------
			Composite buttons = new Composite(dialog, SWT.NONE);
			buttons.setLayout(new GridLayout(3,false));
			buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
			
			final Button buttonOK = new Button(buttons, SWT.PUSH);
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.setLayoutData(getButtonData());
			buttonOK.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					int values = 0;
					for(int i = 0; i < selections.length; i ++){
						values |=  (  (selections[i].getSelection()) ? (1 << i) : 0  );
					}
					update(measure,values);
					dialog.dispose();
				}
			});
			Button buttonClean = new Button(buttons, SWT.PUSH);
			buttonClean.setText(TuxGuitar.getProperty("clean"));
			buttonClean.setLayoutData(getButtonData());
			buttonClean.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					update(measure,0);
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
	
	private GridData getMainData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 350;
		return data;
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	protected int getExistentEndings(TGMeasure measure){
		int existentEndings = 0;
		Iterator it = getSongManager().getSong().getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = (TGMeasureHeader)it.next();
			if(header.getNumber() == measure.getNumber()){
				break;
			}
			if(header.isRepeatOpen()){
				existentEndings = 0;
			}
			existentEndings |= header.getRepeatAlternative();
		}
		return existentEndings;
	}
	
	protected int getDefaultEndings(int existentEndings){
		for(int i = 0; i < 8; i ++){
			if((existentEndings & (1 << i)) == 0){
				return (1 << i);
			}
		}
		return -1;
	}
	
	protected void update(TGMeasure measure, int value) {
		//Solo si hubieron cambios
		if(value != measure.getHeader().getRepeatAlternative()){
			//Si no estoy editando, y la alternativa no contiene el primer final,
			//por defecto se cierra la repeticion del compas anterior
			boolean previousRepeatClose = (measure.getHeader().getRepeatAlternative() == 0 && ((value & (1 << 0)) == 0)) ;
			
			//comienza el undoable
			UndoableJoined undoable = new UndoableJoined();
			TuxGuitar.instance().getFileHistory().setUnsavedFile();
			
			//Guardo la repeticion alternativa
			UndoableChangeAlternativeRepeat u1 = UndoableChangeAlternativeRepeat.startUndo();
			getSongManager().changeAlternativeRepeat(measure.getStart(), value);
			fireUpdate(measure.getNumber());
			undoable.addUndoableEdit(u1.endUndo(value));
			
			if(previousRepeatClose){
				//Agrego un cierre de repeticion al compaz anterior
				TGMeasureHeader previous = getSongManager().getMeasureHeader( measure.getNumber() - 1);
				if(previous != null && previous.getRepeatClose() == 0){
					UndoableChangeCloseRepeat u2 = UndoableChangeCloseRepeat.startUndo(previous.getStart(),previous.getRepeatClose());
					getSongManager().changeCloseRepeat(previous.getStart(), 1);
					fireUpdate(previous.getNumber());
					undoable.addUndoableEdit(u2.endUndo(1));
				}
			}
			//termia el undoable
			addUndoableEdit(undoable.endUndo());
		}
	}
}
