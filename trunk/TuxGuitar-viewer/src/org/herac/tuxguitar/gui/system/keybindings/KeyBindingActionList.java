package org.herac.tuxguitar.gui.system.keybindings;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.Action;
import org.herac.tuxguitar.gui.actions.measure.GoNextMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.GoPreviousMeasureAction;
import org.herac.tuxguitar.gui.actions.transport.TransportPlayAction;

public class KeyBindingActionList {
	
	private static KeyBindingAction[] KB_ACTIONS = new KeyBindingAction[]{
		new KeyBindingAction(GoPreviousMeasureAction.NAME,new KeyBinding(KeyBindingConstants.LEFT,0)),
		new KeyBindingAction(GoNextMeasureAction.NAME,new KeyBinding(KeyBindingConstants.RIGHT,0)),
		new KeyBindingAction(TransportPlayAction.NAME,new KeyBinding(KeyBindingConstants.SPACE,0)),

	};
	
	public static boolean isReserved(KeyBinding kb){
		for(int i = 0;i < KB_ACTIONS.length;i++){
			if(kb.isSameAs(KB_ACTIONS[i].getKeyBinding())){
				return true;
			}
		}
		return false;
	}
	
	public static Action getActionForKeyBinding(KeyBinding kb){
		for(int i = 0;i < KB_ACTIONS.length;i++){
			if(kb.isSameAs(KB_ACTIONS[i].getKeyBinding())){
				return TuxGuitar.instance().getAction(KB_ACTIONS[i].getAction());
			}
		}
		return null;
	}
	
	public static KeyBinding getKeyBindingForAction(String action){
		for(int i = 0;i < KB_ACTIONS.length;i++){
			if(action.equals(KB_ACTIONS[i].getAction())){
				return KB_ACTIONS[i].getKeyBinding();
			}
		}
		return null;
	}
}
