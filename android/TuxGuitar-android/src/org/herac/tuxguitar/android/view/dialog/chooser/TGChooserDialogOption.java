package org.herac.tuxguitar.android.view.dialog.chooser;

public class TGChooserDialogOption<T> {

	private String label;
	private T value;

	public TGChooserDialogOption(String label, T value) {
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public T getValue() {
		return value;
	}
}
