package org.herac.tuxguitar.ui.resource;

public class UIKey {
	
	public static final UIKey F1 = new UIKey(0x100000a);
	public static final UIKey F2 = new UIKey(0x100000b);
	public static final UIKey F3 = new UIKey(0x100000c);
	public static final UIKey F4 = new UIKey(0x100000d);
	public static final UIKey F5 = new UIKey(0x100000e);
	public static final UIKey F6 = new UIKey(0x100000f);
	public static final UIKey F7 = new UIKey(0x1000010);
	public static final UIKey F8 = new UIKey(0x1000011);
	public static final UIKey F9 = new UIKey(0x1000012);
	public static final UIKey F10 = new UIKey(0x1000013);
	public static final UIKey F11 = new UIKey(0x1000014);
	public static final UIKey F12 = new UIKey(0x1000015);
	public static final UIKey ESC = new UIKey(0x1b);
	public static final UIKey PAUSE = new UIKey(0x1000055);
	public static final UIKey PRINT_SCREEN = new UIKey(0x1000057);
	public static final UIKey INSERT = new UIKey(0x1000009);
	public static final UIKey DELETE = new UIKey(0x7f);
	public static final UIKey HOME = new UIKey(0x1000007);
	public static final UIKey PAGE_UP = new UIKey(0x1000005);
	public static final UIKey PAGE_DOWN = new UIKey(0x1000006);
	public static final UIKey END = new UIKey(0x1000008);
	public static final UIKey ALT = new UIKey(0x10000);
	public static final UIKey CONTROL = new UIKey(0x40000);
	public static final UIKey COMMAND = new UIKey(0x400000);
	public static final UIKey SHIFT = new UIKey(0x20000);
	public static final UIKey TAB = new UIKey(0x9);
	public static final UIKey BACKSPACE = new UIKey(0x8);
	public static final UIKey SPACE = new UIKey(0x20);
	public static final UIKey ENTER = new UIKey(0xd);
	public static final UIKey UP = new UIKey(0x1000001);
	public static final UIKey DOWN = new UIKey(0x1000002);
	public static final UIKey LEFT = new UIKey(0x1000003);
	public static final UIKey RIGHT = new UIKey(0x1000004);
	
	private Integer code;
	
	public UIKey(Integer code) {
		this.code = code;
	}
	
	public Integer getCode() {
		return this.code;
	}
	
	public boolean equals(Object o) {
		if( this.getCode() == null ) {
			return super.equals(o);
		}
		if( o instanceof UIKey ) {
			return this.getCode().equals(((UIKey) o).getCode());
		}
		return false;
	}
	
	public String toString(){
		for(int i = 0; i < UIKeyLabel.LABELS.length; i++){
			if( this.equals(UIKeyLabel.LABELS[i].getKey())){
				return UIKeyLabel.LABELS[i].getLabel();
			}
		}
		return Character.toString((char)(this.getCode() & 0xffff));
	}
}
