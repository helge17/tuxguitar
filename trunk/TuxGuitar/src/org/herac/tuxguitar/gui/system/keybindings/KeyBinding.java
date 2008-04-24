package org.herac.tuxguitar.gui.system.keybindings;

import java.util.StringTokenizer;

import org.eclipse.swt.SWT;

public class KeyBinding {
	public static final String ALT_STRING = "Alt";
	public static final String SHIFT_STRING = "Shift";
	public static final String CONTROL_STRING = "Control";
	public static final String MASK_SEPARATOR = "+";
	
	private int mask;
	private int key;
	
	public KeyBinding(int key,int mask){
		this.key = key;
		this.mask = mask;
	}
	
	public KeyBinding(){
		this(0,0);
	}
	
	public int getKey() {
		return this.key;
	}
	
	public void setKey(int key) {
		this.key = key;
	}
	
	public int getMask() {
		return this.mask;
	}
	
	public void setMask(int mask) {
		this.mask = mask;
	}
	
	public static KeyBinding parse(String keystring){
		String key=keystring;
		KeyBinding keybinding = new KeyBinding();
		int mask=0;
		
		// process mask
		if (keystring.indexOf(MASK_SEPARATOR)!= -1){
			StringTokenizer st = new StringTokenizer(keystring,MASK_SEPARATOR);
			while(st.hasMoreTokens()){
				String token = st.nextToken();
				
				// only process if this is not the last token
				if (st.hasMoreTokens()){
					// add the mask
					mask |= getMaskCode(token);
				}
				else {
					key = token;
				}
			}
		}
		
		keybinding.setMask(mask);
		
		// process key
		int keycode = getSpecialKeyCode(key);
		if (keycode == 0)
			keycode = getKeyCode(key);
		
		keybinding.setKey(keycode);
		
		return keybinding;
	}
	
	private static int getKeyCode(String key){
		return key.charAt(0);
	}
	
	private static int getSpecialKeyCode(String key){
		for(int i = 0; i < KeyConversion.relations.length; i++){
			if (key.equals(KeyConversion.relations[i].getKey())){
				return KeyConversion.relations[i].getCode();
			}
		}
		
		return 0;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof KeyBinding)){
			return false;
		}
		
		KeyBinding kb = (KeyBinding)obj;
		
		return (this.key == kb.key && this.mask == kb.mask);
	}
	
	public int hashCode() {
		return new Integer(this.key + this.mask).hashCode();
	}
	
	/**
	 * get the mask code for this string
	 * @param mask
	 * @return
	 */
	private static int getMaskCode(String mask){
		if (mask.equals(ALT_STRING)){
			return SWT.ALT;
		}
		else if (mask.equals(SHIFT_STRING)){
			return SWT.SHIFT;
		}
		else if (mask.equals(CONTROL_STRING)){
			return SWT.CTRL;
		}
		return 0;
	}
	
	private String getMaskString(){
		String maskstring = "";
		
		if ((this.mask & SWT.ALT) == SWT.ALT){
			maskstring += ALT_STRING+"+";
		}
		if ((this.mask & SWT.SHIFT) == SWT.SHIFT){
			maskstring += SHIFT_STRING+"+";
		}
		if ((this.mask & SWT.CTRL) == SWT.CTRL){
			maskstring += CONTROL_STRING+"+";
		}
		return maskstring;
	}
	
	public String toString(){
		String s = getMaskString();
		String sp = getSpecialKey();
		if (sp != null){
			s += sp;
		}else{
			s += (char)this.key;
		}
		return s;
	}
	
	private String getSpecialKey(){
		for(int i = 0; i < KeyConversion.relations.length; i++){
			if (this.key == KeyConversion.relations[i].getCode()){
				return KeyConversion.relations[i].getKey();
			}
		}
		
		return null;
	}
	
	public Object clone(){
		return new KeyBinding(getKey(),getMask());
	}
}

class KeyConversion {
	
	protected static final KeyConversion[] relations = new KeyConversion[]{
		new KeyConversion("F1",KeyBindingConstants.F1),
		new KeyConversion("F2",KeyBindingConstants.F2),
		new KeyConversion("F3",KeyBindingConstants.F3),
		new KeyConversion("F4",KeyBindingConstants.F4),
		new KeyConversion("F5",KeyBindingConstants.F5),
		new KeyConversion("F6",KeyBindingConstants.F6),
		new KeyConversion("F7",KeyBindingConstants.F7),
		new KeyConversion("F8",KeyBindingConstants.F8),
		new KeyConversion("F9",KeyBindingConstants.F9),
		new KeyConversion("F10",KeyBindingConstants.F10),
		new KeyConversion("F11",KeyBindingConstants.F11),
		new KeyConversion("F12",KeyBindingConstants.F12),
		new KeyConversion("Esc",KeyBindingConstants.ESC),
		new KeyConversion("Pause",KeyBindingConstants.PAUSE),
		new KeyConversion("Print",KeyBindingConstants.PRINT_SCREEN),
		new KeyConversion("Ins",KeyBindingConstants.INSERT),
		new KeyConversion("Del",KeyBindingConstants.DELETE),
		new KeyConversion("Home",KeyBindingConstants.HOME),
		new KeyConversion("PgUp",KeyBindingConstants.PAGE_UP),
		new KeyConversion("PgDn",KeyBindingConstants.PAGE_DOWN),
		new KeyConversion("End",KeyBindingConstants.END),
		new KeyConversion("Up",KeyBindingConstants.UP),
		new KeyConversion("Down",KeyBindingConstants.DOWN),
		new KeyConversion("Left",KeyBindingConstants.LEFT),
		new KeyConversion("Right",KeyBindingConstants.RIGHT),
		new KeyConversion("Alt",KeyBindingConstants.ALT),
		new KeyConversion("Control",KeyBindingConstants.CONTROL),
		new KeyConversion("Shift",KeyBindingConstants.SHIFT),
		new KeyConversion("Tab",KeyBindingConstants.TAB),
		new KeyConversion("Space",KeyBindingConstants.SPACE),
		new KeyConversion("Enter",KeyBindingConstants.ENTER),
		new KeyConversion("*",KeyBindingConstants.KEYPAD_MULTIPLY),
		new KeyConversion("/",KeyBindingConstants.KEYPAD_DIVIDE),
		new KeyConversion(".",KeyBindingConstants.KEYPAD_DECIMAL),
	};
	
	private String key;
	private int code;
	
	private KeyConversion(String key,int code){
		this.key = key;
		this.code = code;
	}
	
	public String getKey(){
		return this.key;
	}
	
	public int getCode(){
		return this.code;
	}
}