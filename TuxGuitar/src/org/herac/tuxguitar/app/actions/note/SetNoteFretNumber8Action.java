package org.herac.tuxguitar.app.actions.note;

public class SetNoteFretNumber8Action extends SetNoteFretNumberAction {
	
	private static final int NUMBER = 8;
	
	public SetNoteFretNumber8Action(){
		super(NUMBER);
	}
	
	public static String getActionName(){
		return SetNoteFretNumberAction.getActionName(NUMBER);
	}
}
