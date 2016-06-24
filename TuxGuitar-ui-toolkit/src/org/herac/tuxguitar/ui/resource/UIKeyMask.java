//package org.herac.tuxguitar.ui.resource;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class UIKeyMask {
//	
//	private static final String MASK_SEPARATOR = "+";
//	
//	private List<UIKey> keys;
//	
//	public UIKeyMask(List<UIKey> keys) {
//		this.keys = new ArrayList<UIKey>(keys);
//		if( keys != null ) {
//			this.keys.addAll(keys);
//		}
//	}
//	
//	public UIKeyMask() {
//		this(new ArrayList<UIKey>());
//	}
//	
//	public List<UIKey> getKeys() {
//		return keys;
//	}
//	
//	public boolean contains(UIKey key) {
//		return this.keys.contains(key);
//	}
//	
//	public boolean equals(Object o) {
//		if( this.getKeys() == null ) {
//			return super.equals(o);
//		}
//		if( o instanceof UIKeyMask ) {
//			UIKeyMask keyMask = (UIKeyMask) o;
//			if( keyMask.getKeys() != null && keyMask.getKeys().size() == this.getKeys().size()) {
//				for(UIKey key : keyMask.getKeys()) {
//					if(!this.contains(key)) {
//						return false;
//					}
//				}
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	public String toString(){
//		StringBuffer fullMask = new StringBuffer();
//		for(UIKey key : this.getKeys()){
//			if( fullMask.length() > 0 ) {
//				fullMask.append(MASK_SEPARATOR);
//			}
//			fullMask.append(key.toString());
//		}
//		return fullMask.toString();
//	}
//}
