package org.herac.tuxguitar.midi.synth;

public class TGSynthExtension<T> {
	
	private Class<T> extensionClass;
	private T extension;
	
	public TGSynthExtension(Class<T> extensionClass, T extension) {
		this.extensionClass = extensionClass;
		this.extension = extension;
	}

	public Class<T> getExtensionClass() {
		return extensionClass;
	}

	public T getExtension() {
		return extension;
	}
}
