package app.tuxguitar.ui.resource;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.util.TGKeyBindFormatter;

public class UIKeyCombination {

	private List<UIKey> keys;

	public UIKeyCombination(List<UIKey> keys) {
		this.keys = new ArrayList<UIKey>();

		if( keys != null ) {
			this.keys.addAll(keys);
		}
	}

	public UIKeyCombination() {
		this(new ArrayList<UIKey>());
	}

	public List<UIKey> getKeys() {
		return this.keys;
	}

	public List<String> getKeyStrings() {
		List<String> keyStrings = new ArrayList<>();
		for (UIKey key : keys) {
			keyStrings.add(key.toString());
		}
		return keyStrings;
	}

	public boolean contains(UIKey key) {
		return this.keys.contains(key);
	}

	public boolean equals(Object o) {
		if( o instanceof UIKeyCombination ) {
			UIKeyCombination keyCombination = (UIKeyCombination) o;
			if( keyCombination.getKeys().size() == this.getKeys().size()) {
				for(UIKey key : keyCombination.getKeys()) {
					if(!this.contains(key)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	public String toString() {
		return TGKeyBindFormatter.defaultTranslate(getKeyStrings());
	}

	public Object clone(){
		return new UIKeyCombination(this.getKeys());
	}
}
