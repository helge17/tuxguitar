package org.herac.tuxguitar.ui.resource;

public class UIKeyLabel {
	
	public static final int F1 = (0x100000a);
	public static final int F2 = (0x100000b);
	public static final int F3 = (0x100000c);
	public static final int F4 = (0x100000d);
	public static final int F5 = (0x100000e);
	public static final int F6 = (0x100000f);
	public static final int F7 = (0x1000010);
	public static final int F8 = (0x1000011);
	public static final int F9 = (0x1000012);
	public static final int F10 = (0x1000013);
	public static final int F11 = (0x1000014);
	public static final int F12 = (0x1000015);
	public static final int ESC = (0x1b);
	public static final int PAUSE = (0x1000055);
	public static final int PRINT_SCREEN = (0x1000057);
	public static final int INSERT = (0x1000009);
	public static final int DELETE = (0x7f);
	public static final int HOME = (0x1000007);
	public static final int PAGE_UP = (0x1000005);
	public static final int PAGE_DOWN = (0x1000006);
	public static final int END = (0x1000008);
	public static final int ALT = (0x10000);
	public static final int CONTROL = (0x40000);
	public static final int COMMAND = (0x400000);
	public static final int SHIFT = (0x20000);
	public static final int TAB = (0x9);
	public static final int BACKSPACE = (0x8);
	public static final int SPACE = (0x20);
	public static final int ENTER = (0xd);
	public static final int UP = (0x1000001);
	public static final int DOWN = (0x1000002);
	public static final int LEFT = (0x1000003);
	public static final int RIGHT = (0x1000004);
	
	public static final UIKeyLabel[] LABELS = new UIKeyLabel[] {
		new UIKeyLabel(F1, "F1"),
		new UIKeyLabel(F2, "F2"),
		new UIKeyLabel(F3,"F3"),
		new UIKeyLabel(F4,"F4"),
		new UIKeyLabel(F5,"F5"),
		new UIKeyLabel(F6,"F6"),
		new UIKeyLabel(F7,"F7"),
		new UIKeyLabel(F8,"F8"),
		new UIKeyLabel(F9,"F9"),
		new UIKeyLabel(F10,"F10"),
		new UIKeyLabel(F11,"F11"),
		new UIKeyLabel(F12,"F12"),
		new UIKeyLabel(ESC,"Esc"),
		new UIKeyLabel(PAUSE,"Pause"),
		new UIKeyLabel(PRINT_SCREEN,"Print"),
		new UIKeyLabel(INSERT,"Ins"),
		new UIKeyLabel(DELETE,"Del"),
		new UIKeyLabel(HOME,"Home"),
		new UIKeyLabel(PAGE_UP,"PgUp"),
		new UIKeyLabel(PAGE_DOWN,"PgDn"),
		new UIKeyLabel(END,"End"),
		new UIKeyLabel(UP,"Up"),
		new UIKeyLabel(DOWN,"Down"),
		new UIKeyLabel(LEFT,"Left"),
		new UIKeyLabel(RIGHT,"Right"),
		new UIKeyLabel(TAB,"Tab"),
		new UIKeyLabel(SPACE,"Space"),
		new UIKeyLabel(ENTER,"Enter"),
		new UIKeyLabel(BACKSPACE,"BackSpace"),
		new UIKeyLabel(CONTROL, "Control"),
		new UIKeyLabel(SHIFT, "Shift"),
		new UIKeyLabel(ALT,"Alt"),
		new UIKeyLabel(COMMAND,"\u2318"),
	};
	
	private int key;
	private String label;
	
	public UIKeyLabel(int key, String label){
		this.key = key;
		this.label = label;
	}
	
	public int getKey(){
		return this.key;
	}
	
	public String getLabel(){
		return this.label;
	}
	
	
}
