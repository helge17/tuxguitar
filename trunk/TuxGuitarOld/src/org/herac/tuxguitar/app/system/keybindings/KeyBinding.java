package org.herac.tuxguitar.app.system.keybindings;

public class KeyBinding {
	
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
	
	public boolean isSameAs(KeyBinding kb){
		if( kb != null ){
			if( this.getMask() == kb.getMask() ){
				if( this.getKey() == kb.getKey() ){
					return true;
				}
				if((this.getKey() & 0xffff) == (kb.getKey() & 0xffff) ){
					return (!KeyBindingUtil.isSpecialKey(this.getKey()) && !KeyBindingUtil.isSpecialKey(kb.getKey()));
				}
			}
		}
		return false;
	}
	
	public String toString(){
		String mask = KeyBindingUtil.getConversionMask( this.getMask() );
		String key = KeyBindingUtil.getConversionKey( this.getKey() );
		if( key == null ){
			key = Character.toString((char)(this.getKey() & 0xffff));
		}
		return (mask + key);
	}
	
	public Object clone(){
		return new KeyBinding(getKey(),getMask());
	}
}