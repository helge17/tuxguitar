package org.herac.tuxguitar.app.io.persistence;

public enum TGPersistenceSettingsMode {
	
	READ, WRITE, READ_WRITE;
	
	public boolean supports(TGPersistenceSettingsMode mode) {
		return (this.equals(mode) || this.equals(READ_WRITE));
	}
}
