package org.herac.tuxguitar.app.actions.note;

import org.herac.tuxguitar.app.actions.ActionData;

public class SetNoteFretNumberAction extends ChangeNoteAction {
	
	public static final String NAME_PREFIX = "action.note.set.fret-";
	
	private int number;
	
	public SetNoteFretNumberAction(int number){
		super(getActionName(number), AUTO_LOCK | AUTO_UNLOCK | DISABLE_ON_PLAYING);
		this.number = number;
	}
	
	protected int execute(ActionData actionData){
		actionData.put(PROPERTY_FRET_NUMBER, new Integer(this.number));
		
		return super.execute(actionData);
	}
	
	public static final String getActionName(int number){
		return (NAME_PREFIX + number);
	}
}
