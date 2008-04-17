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
import org.herac.tuxguitar.gui.helper.SyncThread;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableAddMeasure;
import org.herac.tuxguitar.gui.util.DialogUtils;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddMeasureAction extends Action{
    public static final String NAME = "action.measure.add";

    protected int selectedNumber;
    
    public AddMeasureAction() {
    	super(NAME, AUTO_LOCK | AUTO_UNLOCK | DISABLE_ON_PLAYING | KEY_BINDING_AVAILABLE);
    }

    protected int execute(TypedEvent e){
        final int number = showDialog();
        if(number > 0 && number <=  (getSongManager().getSong().countMeasureHeaders() + 1)){
        	new Thread(new Runnable() {
				public void run() {						
					new SyncThread(new Runnable() {
						public void run() {
				            //comienza el undoable
				        	UndoableAddMeasure undoable = UndoableAddMeasure.startUndo(number);    	        	
				        	TuxGuitar.instance().getFileHistory().setUnsavedFile();
				        	
				        	getSongManager().addNewMeasure(number);                
				        	updateTablature();
				        	
				        	int trackNumber = getEditor().getTablature().getCaret().getTrack().getNumber();
				        	int stringNumber = getEditor().getTablature().getCaret().getStringNumber();
				        	long start = getSongManager().getMeasureHeader(number).getStart();
				        	getEditor().getTablature().getCaret().update(trackNumber,start,stringNumber);
				        	
				            //termia el undoable
				        	addUndoableEdit(undoable.endUndo());

				        	TuxGuitar.instance().updateCache(true);
						}
					}).start();
				}
			}).start();        	
        }
	    return 0;
    }
    
    public int showDialog() {
    	this.selectedNumber = -1;
        if (getEditor().getTablature().getCaret().getMeasure() != null) {
            final Shell dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);  
            dialog.setLayout(new GridLayout());
            dialog.setText(TuxGuitar.getProperty("measure.add"));
            

            //----------------------------------------------------------------------
            Group radios = new Group(dialog,SWT.SHADOW_ETCHED_IN);
            radios.setLayout(new GridLayout());     
            radios.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
            radios.setText(TuxGuitar.getProperty("measure.add"));
            
            final Button beforePosition = new Button(radios,SWT.RADIO);
            beforePosition.setText(TuxGuitar.getProperty("measure.add-before-current-position"));
            
            final Button afterPosition = new Button(radios,SWT.RADIO);
            afterPosition.setText(TuxGuitar.getProperty("measure.add-after-current-position"));
            
            
            final Button atEnd = new Button(radios,SWT.RADIO);
            atEnd.setText(TuxGuitar.getProperty("measure.add-at-end"));                                   
            atEnd.setSelection(true);
            //------------------BUTTONS--------------------------            
            Composite buttons = new Composite(dialog, SWT.NONE);
            buttons.setLayout(new GridLayout(2,false));
            buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));    	
            
            final Button buttonOK = new Button(buttons, SWT.PUSH);
            buttonOK.setText(TuxGuitar.getProperty("ok"));
            buttonOK.setLayoutData(getButtonData());
            buttonOK.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent arg0) {   
                	if(beforePosition.getSelection()){
                		AddMeasureAction.this.selectedNumber = (getEditor().getTablature().getCaret().getMeasure().getNumber());
                	}else if(afterPosition.getSelection()){
                		AddMeasureAction.this.selectedNumber = (getEditor().getTablature().getCaret().getMeasure().getNumber() + 1);
                	}else if(atEnd.getSelection()){
                		AddMeasureAction.this.selectedNumber = (getSongManager().getSong().countMeasureHeaders() + 1);
                	}
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
        return this.selectedNumber;
    }    
    
	private GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
}
