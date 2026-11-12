package app.tuxguitar.midi.synth;

import java.util.List;

/**
 * What the synthesizer's lists of processors and program elements share: a lookup that answers null out of range,
 * and an add that leaves out null.
 */
final class TGSynthLists {

	private TGSynthLists() {
	}

	static <T> T get(List<T> list, int index) {
		if( index >= 0 && index < list.size() ){
			return list.get(index);
		}
		return null;
	}

	static <T> void addPresent(List<T> list, T item) {
		if( item != null ){
			list.add(item);
		}
	}
}
