/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions.note;

import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.editors.tab.Caret;
import org.herac.tuxguitar.gui.undo.undoables.measure.UndoableMeasureGeneric;
import org.herac.tuxguitar.song.models.TGBeat;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RemoveNoteAction extends Action{
    public static final String NAME = "action.note.general.remove";
    
    public RemoveNoteAction() {
    	super(NAME, AUTO_LOCK | AUTO_UNLOCK | AUTO_UPDATE | DISABLE_ON_PLAYING);
    }

    protected int execute(TypedEvent e){
        //comienza el undoable
        UndoableMeasureGeneric undoable = UndoableMeasureGeneric.startUndo();     
        TuxGuitar.instance().getFileHistory().setUnsavedFile();
        
        Caret caret = getEditor().getTablature().getCaret();
        TGBeat beat = caret.getSelectedBeat();
        if(beat.isRestBeat()){                               
            if(beat.isTextBeat()){
            	getSongManager().getMeasureManager().removeText(beat);
            }else{
            	getSongManager().getMeasureManager().removeBeat( beat ,true);
            }
        }else{
            int string = caret.getSelectedString().getNumber();       
            getSongManager().getMeasureManager().removeNote(caret.getMeasure(),beat.getStart(), string);
        }   
        
        //termia el undoable
        addUndoableEdit(undoable.endUndo());                    
        updateTablature();          
        
        return 0;
    }
    
    public void updateTablature() {
        fireUpdate(getEditor().getTablature().getCaret().getMeasure().getNumber());
    }    

}
