package org.herac.tuxguitar.ui.resource;

import java.util.ArrayList;
import java.util.List;

public class UIKeyConvination {
	
	private static final String MASK_SEPARATOR = "+";
	
	private List<UIKey> keys;
	
	public UIKeyConvination(List<UIKey> keys) {
		this.keys = new ArrayList<UIKey>();
		
		if( keys != null ) {
			this.keys.addAll(keys);
		}
	}
	
	public UIKeyConvination() {
		this(new ArrayList<UIKey>());
	}
	
	public List<UIKey> getKeys() {
		return this.keys;
	}
	
	public boolean contains(UIKey key) {
		return this.keys.contains(key);
	}
	
	public boolean equals(Object o) {
		if( o instanceof UIKeyConvination ) {
			UIKeyConvination keyConvination = (UIKeyConvination) o;
			if( keyConvination.getKeys().size() == this.getKeys().size()) {
				for(UIKey key : keyConvination.getKeys()) {
					if(!this.contains(key)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	public String toString(String separator) {
		StringBuffer fullMask = new StringBuffer();
		for(UIKey key : this.getKeys()){
			if( fullMask.length() > 0 ) {
				fullMask.append(separator);
			}
			fullMask.append(key.toString());
		}
		return fullMask.toString();
	}
	
	public String toString() {
		return this.toString(MASK_SEPARATOR);
	}
	
	public Object clone(){
		return new UIKeyConvination(this.getKeys());
	}
}
