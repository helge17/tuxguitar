package org.herac.tuxguitar.app.system.keybindings;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.actions.caret.GoDownAction;
import org.herac.tuxguitar.app.actions.caret.GoLeftAction;
import org.herac.tuxguitar.app.actions.caret.GoRightAction;
import org.herac.tuxguitar.app.actions.caret.GoUpAction;
import org.herac.tuxguitar.app.actions.duration.DecrementDurationAction;
import org.herac.tuxguitar.app.actions.duration.IncrementDurationAction;
import org.herac.tuxguitar.app.actions.note.ChangeNoteAction;
import org.herac.tuxguitar.app.actions.note.InsertNoteAction;
import org.herac.tuxguitar.app.actions.note.RemoveNoteAction;

public class KeyBindingReserveds {
	
	private static KeyBindingAction[] reserveds = new KeyBindingAction[]{
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.NUMBER_0,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.NUMBER_1,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.NUMBER_2,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.NUMBER_3,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.NUMBER_4,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.NUMBER_5,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.NUMBER_6,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.NUMBER_7,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.NUMBER_8,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.NUMBER_9,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.KEYPAD_0,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.KEYPAD_1,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.KEYPAD_2,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.KEYPAD_3,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.KEYPAD_4,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.KEYPAD_5,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.KEYPAD_6,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.KEYPAD_7,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.KEYPAD_8,0)),
		new KeyBindingAction(ChangeNoteAction.NAME,new KeyBinding(KeyBindingConstants.KEYPAD_9,0)),
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
			if(kb.isSameAs(reserveds[i].getKeyBinding())){
				return true;
			}
		}
		return false;
	}
	
	public static Action getActionForKeyBinding(KeyBinding kb){
		for(int i = 0;i < reserveds.length;i++){
			if(kb.isSameAs(reserveds[i].getKeyBinding())){
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
