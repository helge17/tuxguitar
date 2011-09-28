package org.herac.tuxguitar.app.actions.note;

public class SetNoteFretNumber9Action extends SetNoteFretNumberAction {
	
	private static final int NUMBER = 9;
	
	public SetNoteFretNumber9Action(){
		super(NUMBER);
	}
	
	public static String getActionName(){
		return SetNoteFretNumberAction.getActionName(NUMBER);
	}
}
