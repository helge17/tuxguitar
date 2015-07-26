package org.herac.tuxguitar.app.system.keybindings;

import org.eclipse.swt.SWT;

public class KeyBindingUtil {
	
	public static final int F1 = SWT.F1;
	public static final int F2 = SWT.F2;
	public static final int F3 = SWT.F3;
	public static final int F4 = SWT.F4;
	public static final int F5 = SWT.F5;
	public static final int F6 = SWT.F6;
	public static final int F7 = SWT.F7;
	public static final int F8 = SWT.F8;
	public static final int F9 = SWT.F9;
	public static final int F10 = SWT.F10;
	public static final int F11 = SWT.F11;
	public static final int F12 = SWT.F12;
	public static final int ESC = SWT.ESC;
	public static final int PAUSE = SWT.PAUSE;
	public static final int PRINT_SCREEN = SWT.PRINT_SCREEN;
	public static final int INSERT = SWT.INSERT;
	public static final int DELETE = SWT.DEL;
	public static final int HOME = SWT.HOME;
	public static final int PAGE_UP = SWT.PAGE_UP;
	public static final int PAGE_DOWN = SWT.PAGE_DOWN;
	public static final int END = SWT.END;
	public static final int ALT = SWT.ALT;
	public static final int CONTROL = SWT.CONTROL;
	public static final int COMMAND = SWT.COMMAND;
	public static final int SHIFT = SWT.SHIFT;
	public static final int TAB = SWT.TAB;
	public static final int BACKSPACE = SWT.BS;
	public static final int SPACE = 32;
	public static final int ENTER = 13;
	public static final int UP = 16777217;
	public static final int DOWN = 16777218;
	public static final int LEFT = 16777219;
	public static final int RIGHT = 16777220;
	public static final int NUMBER_0 = 48;
	public static final int NUMBER_1 = 49;
	public static final int NUMBER_2 = 50;
	public static final int NUMBER_3 = 51;
	public static final int NUMBER_4 = 52;
	public static final int NUMBER_5 = 53;
	public static final int NUMBER_6 = 54;
	public static final int NUMBER_7 = 55;
	public static final int NUMBER_8 = 56;
	public static final int NUMBER_9 = 57;
	public static final int MULTIPLY = SWT.KEYPAD_MULTIPLY;
	public static final int DIVIDE = SWT.KEYPAD_DIVIDE;
	public static final int DECIMAL = SWT.KEYPAD_DECIMAL;
	
	public static final String MASK_SEPARATOR = "+";
	
	public static final KeyCode[] MASK_KEYS = new KeyCode[]{
		new KeyCode(CONTROL, "Control"),
		new KeyCode(SHIFT, "Shift"),
		new KeyCode(ALT,"Alt"),
		new KeyCode(COMMAND,"\u2318"),
	};
	
	public static final KeyCode[] SPECIAL_KEYS = new KeyCode[]{
		new KeyCode(F1, "F1"),
		new KeyCode(F2, "F2"),
		new KeyCode(F3,"F3"),
		new KeyCode(F4,"F4"),
		new KeyCode(F5,"F5"),
		new KeyCode(F6,"F6"),
		new KeyCode(F7,"F7"),
		new KeyCode(F8,"F8"),
		new KeyCode(F9,"F9"),
		new KeyCode(F10,"F10"),
		new KeyCode(F11,"F11"),
		new KeyCode(F12,"F12"),
		new KeyCode(ESC,"Esc"),
		new KeyCode(PAUSE,"Pause"),
		new KeyCode(PRINT_SCREEN,"Print"),
		new KeyCode(INSERT,"Ins"),
		new KeyCode(DELETE,"Del"),
		new KeyCode(HOME,"Home"),
		new KeyCode(PAGE_UP,"PgUp"),
		new KeyCode(PAGE_DOWN,"PgDn"),
		new KeyCode(END,"End"),
		new KeyCode(UP,"Up"),
		new KeyCode(DOWN,"Down"),
		new KeyCode(LEFT,"Left"),
		new KeyCode(RIGHT,"Right"),
		new KeyCode(TAB,"Tab"),
		new KeyCode(SPACE,"Space"),
		new KeyCode(ENTER,"Enter"),
	};
	
	public static boolean isSpecialKey( int code ){
		for(int i = 0; i < KeyBindingUtil.SPECIAL_KEYS.length; i++){
			if (code == KeyBindingUtil.SPECIAL_KEYS[i].getCode()){
				return true;
			}
		}
		return false;
	}
	
	public static String getConversionKey( int code ){
		for(int i = 0; i < KeyBindingUtil.SPECIAL_KEYS.length; i++){
			if (code == KeyBindingUtil.SPECIAL_KEYS[i].getCode()){
				return KeyBindingUtil.SPECIAL_KEYS[i].getKey();
			}
		}
		return null;
	}
	
	public static String getConversionMask( int mask ){
		String fullMask = new String();
		for(int i = 0; i < KeyBindingUtil.MASK_KEYS.length; i++){
			if ( (mask & KeyBindingUtil.MASK_KEYS[i].getCode()) == KeyBindingUtil.MASK_KEYS[i].getCode()){
				fullMask += KeyBindingUtil.MASK_KEYS[i].getKey() + MASK_SEPARATOR;
			}
		}
		return fullMask;
	}
}
