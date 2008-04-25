/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.insert;

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
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGText;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class InsertTextAction extends Action {
	public static final String NAME = "action.insert.text";
	
	public InsertTextAction() {
		super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
	}
	
	protected int execute(TypedEvent e){
		final TGBeat beat = getEditor().getTablature().getCaret().getSelectedBeat();
		
		showInsertDialog(beat,(beat.getText() == null?new String():beat.getText().getValue()));
		
		return 0;
	}
	
	public void updateTablature() {
		fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
	}
	
	public void showInsertDialog(final TGBeat beat,String value) {
		final Shell dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("text.editor"));
		
		Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText(TuxGuitar.getProperty("text.insert"));
		
		Composite composite = new Composite(group, SWT.NONE);
		composite.setLayout(new GridLayout(2,false));
		composite.setLayoutData(getMainData());  
		
		final Label label = new Label(composite,SWT.LEFT);
		label.setText(TuxGuitar.getProperty("text.text") + ":");
		label.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true));
		
		final Text text = new Text(composite,SWT.BORDER | SWT.SINGLE);
		text.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		text.setText(value);
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(3,false));
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				insertText(beat,text.getText());
				dialog.dispose();
			}
		});
		
		final Button buttonClean = new Button(buttons, SWT.PUSH);
		buttonClean.setText(TuxGuitar.getProperty("clean"));
		buttonClean.setLayoutData(getButtonData());
		buttonClean.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				removeText(beat);
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
	
	private GridData getMainData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 300;
		return data;
	}
	
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	protected void insertText(TGBeat beat,String value) {
		//comienza el undoable
		UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
		
		TGText text = getSongManager().getFactory().newText();
		text.setValue(value);
		getSongManager().getMeasureManager().addText(beat, text);
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		updateTablature();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo());
	}
	
	protected void removeText(TGBeat beat) {
		//comienza el undoable
		UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();
		
		getSongManager().getMeasureManager().removeText(beat);
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
		updateTablature();
		
		//termia el undoable
		addUndoableEdit(undoable.endUndo());
	}
}