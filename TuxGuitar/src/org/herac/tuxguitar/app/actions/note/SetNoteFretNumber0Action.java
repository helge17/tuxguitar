package org.herac.tuxguitar.app.actions.note;

public class SetNoteFretNumber0Action extends SetNoteFretNumberAction {
	
	private static final int NUMBER = 0;
	
	public SetNoteFretNumber0Action(){
		super(NUMBER);
	}
	
	public static String getActionName(){
		return SetNoteFretNumberAction.getActionName(NUMBER);
	}
}
