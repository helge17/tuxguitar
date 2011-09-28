package org.herac.tuxguitar.app.actions.note;

public class SetNoteFretNumber3Action extends SetNoteFretNumberAction {
	
	private static final int NUMBER = 3;
	
	public SetNoteFretNumber3Action(){
		super(NUMBER);
	}
	
	public static String getActionName(){
		return SetNoteFretNumberAction.getActionName(NUMBER);
	}
}
