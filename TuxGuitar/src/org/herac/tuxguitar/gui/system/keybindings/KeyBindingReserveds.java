package org.herac.tuxguitar.gui.system.keybindings;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.actions.caret.GoDownAction;
import org.herac.tuxguitar.gui.actions.caret.GoLeftAction;
import org.herac.tuxguitar.gui.actions.caret.GoRightAction;
import org.herac.tuxguitar.gui.actions.caret.GoUpAction;
import org.herac.tuxguitar.gui.actions.duration.DecrementDurationAction;
import org.herac.tuxguitar.gui.actions.duration.IncrementDurationAction;
import org.herac.tuxguitar.gui.actions.note.ChangeNoteAction;
import org.herac.tuxguitar.gui.actions.note.InsertNoteAction;
import org.herac.tuxguitar.gui.actions.note.RemoveNoteAction;

public class KeyBindingReserveds {

	private static KeyBindingAction[] reserveds = new KeyBindingAction[]{
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(48,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(49,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(50,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(51,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(52,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(53,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(54,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(55,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(56,0)),
        new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(57,0)),
        new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(16777264,0)),
        new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(16777265,0)),
        new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(16777266,0)),
        new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(16777267,0)),
        new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(16777268,0)),
        new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(16777269,0)),
        new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(16777270,0)),
        new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(16777271,0)),
        new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(16777272,0)),
        new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(16777273,0)),
        new KeyBindingAction(RemoveNoteAction.NAME,new KeyBinding(KeyBindingConstants.BACKSPACE,0)),
        new KeyBindingAction(RemoveNoteAction.NAME,new KeyBinding(KeyBindingConstants.DELETE,0)),
        new KeyBindingAction(InsertNoteAction.NAME,new KeyBinding(KeyBindingConstants.INSERT,0)),
        new KeyBindingAction(IncrementDurationAction.NAME,new KeyBinding(16777259,0)),
        new KeyBindingAction(DecrementDurationAction.NAME,new KeyBinding(16777261,0)),
        new KeyBindingAction(GoUpAction.NAME,new KeyBinding(KeyBindingConstants.UP,0)),
        new KeyBindingAction(GoDownAction.NAME,new KeyBinding(KeyBindingConstants.DOWN,0)),
        new KeyBindingAction(GoLeftAction.NAME,new KeyBinding(KeyBindingConstants.LEFT,0)),
        new KeyBindingAction(GoRightAction.NAME,new KeyBinding(KeyBindingConstants.RIGHT,0)),
	};
	
	public static boolean isReserved(KeyBinding kb){
		for(int i = 0;i < reserveds.length;i++){
			if(kb.equals(reserveds[i].getKeyBinding())){
				return true;
			}
		}
		return false;
	}
	
    public static Action getActionForKeyBinding(KeyBinding kb){
    	for(int i = 0;i < reserveds.length;i++){
    		if(kb.equals(reserveds[i].getKeyBinding())){
    			return TuxGuitar.instance().getAction(reserveds[i].getAction());
    		}
    	}
    	return null;
    }
    
    public static KeyBinding getKeyBindingForAction(String action){
    	for(int i = 0;i < reserveds.length;i++){
    		if(action.equals(reserveds[i].getAction())){
    			return reserveds[i].getKeyBinding();
    		}
    	}
    	return null;
    }    
}
