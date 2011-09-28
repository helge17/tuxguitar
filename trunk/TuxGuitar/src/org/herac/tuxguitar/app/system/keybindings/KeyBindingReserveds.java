package org.herac.tuxguitar.app.system.keybindings;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.actions.caret.GoDownAction;
import org.herac.tuxguitar.app.actions.caret.GoLeftAction;
import org.herac.tuxguitar.app.actions.caret.GoRightAction;
import org.herac.tuxguitar.app.actions.caret.GoUpAction;
import org.herac.tuxguitar.app.actions.duration.DecrementDurationAction;
import org.herac.tuxguitar.app.actions.duration.IncrementDurationAction;
import org.herac.tuxguitar.app.actions.note.InsertNoteAction;
import org.herac.tuxguitar.app.actions.note.RemoveNoteAction;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber0Action;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber1Action;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber2Action;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber3Action;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber4Action;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber5Action;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber6Action;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber7Action;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber8Action;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber9Action;

public class KeyBindingReserveds {
	
	private static KeyBindingAction[] reserveds = new KeyBindingAction[]{
		new KeyBindingAction(SetNoteFretNumber0Action.getActionName(),new KeyBinding(KeyBindingUtil.NUMBER_0,0)),
		new KeyBindingAction(SetNoteFretNumber1Action.getActionName(),new KeyBinding(KeyBindingUtil.NUMBER_1,0)),
		new KeyBindingAction(SetNoteFretNumber2Action.getActionName(),new KeyBinding(KeyBindingUtil.NUMBER_2,0)),
		new KeyBindingAction(SetNoteFretNumber3Action.getActionName(),new KeyBinding(KeyBindingUtil.NUMBER_3,0)),
		new KeyBindingAction(SetNoteFretNumber4Action.getActionName(),new KeyBinding(KeyBindingUtil.NUMBER_4,0)),
		new KeyBindingAction(SetNoteFretNumber5Action.getActionName(),new KeyBinding(KeyBindingUtil.NUMBER_5,0)),
		new KeyBindingAction(SetNoteFretNumber6Action.getActionName(),new KeyBinding(KeyBindingUtil.NUMBER_6,0)),
		new KeyBindingAction(SetNoteFretNumber7Action.getActionName(),new KeyBinding(KeyBindingUtil.NUMBER_7,0)),
		new KeyBindingAction(SetNoteFretNumber8Action.getActionName(),new KeyBinding(KeyBindingUtil.NUMBER_8,0)),
		new KeyBindingAction(SetNoteFretNumber9Action.getActionName(),new KeyBinding(KeyBindingUtil.NUMBER_9,0)),
		new KeyBindingAction(RemoveNoteAction.NAME,new KeyBinding(KeyBindingUtil.BACKSPACE,0)),
		new KeyBindingAction(RemoveNoteAction.NAME,new KeyBinding(KeyBindingUtil.DELETE,0)),
		new KeyBindingAction(InsertNoteAction.NAME,new KeyBinding(KeyBindingUtil.INSERT,0)),
		new KeyBindingAction(IncrementDurationAction.NAME,new KeyBinding(16777259,0)),
		new KeyBindingAction(DecrementDurationAction.NAME,new KeyBinding(16777261,0)),
		new KeyBindingAction(GoUpAction.NAME,new KeyBinding(KeyBindingUtil.UP,0)),
		new KeyBindingAction(GoDownAction.NAME,new KeyBinding(KeyBindingUtil.DOWN,0)),
		new KeyBindingAction(GoLeftAction.NAME,new KeyBinding(KeyBindingUtil.LEFT,0)),
		new KeyBindingAction(GoRightAction.NAME,new KeyBinding(KeyBindingUtil.RIGHT,0)),
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
