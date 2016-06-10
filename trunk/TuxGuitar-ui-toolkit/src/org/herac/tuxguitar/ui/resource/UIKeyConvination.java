package org.herac.tuxguitar.ui.resource;

public class UIKeyConvination {
	
	private static final String MASK_SEPARATOR = "+";
	
	private UIKey key;
	private UIKeyMask mask;
	
	public UIKeyConvination(UIKey key, UIKeyMask mask){
		this.key = key;
		this.mask = mask;
	}
	
	public UIKeyConvination(){
		this(null, null);
	}
	
	public UIKey getKey() {
		return this.key;
	}
	
	public void setKey(UIKey key) {
		this.key = key;
	}
	
	public UIKeyMask getMask() {
		return this.mask;
	}
	
	public void setMask(UIKeyMask mask) {
		this.mask = mask;
	}
	
	public boolean equals(Object o){
		if( o instanceof UIKeyConvination ) {
			UIKeyConvination kc = (UIKeyConvination) o;
			if( this.getMask().equals(kc.getMask()) ){
				if( this.getKey().equals(kc.getKey()) ){
					return true;
				}
			}
		}
		return false;
	}
	
	public String toString(){
		StringBuffer fullMask = new StringBuffer();
		fullMask.append(this.getMask().toString());
		if( fullMask.length() > 0 ) {
			fullMask.append(MASK_SEPARATOR);
		}
		fullMask.append(this.getKey().toString());
		
		return fullMask.toString();
	}
	
	public Object clone(){
		return new UIKeyConvination(getKey(), getMask());
	}
}
