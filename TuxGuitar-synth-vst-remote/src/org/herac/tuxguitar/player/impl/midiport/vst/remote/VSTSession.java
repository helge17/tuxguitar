package org.herac.tuxguitar.player.impl.midiport.vst.remote;

public class VSTSession {
	
	private Integer id;
	private VSTConnection connection;
	
	public VSTSession(Integer id, VSTConnection connection) {
		this.id = id;
		this.connection = connection;
	}

	public Integer getId() {
		return id;
	}
	
	public VSTConnection getConnection() {
		return connection;
	}
	
	public void close() {
		this.getConnection().close();
	}
	
	public boolean isClosed() {
		return this.getConnection().isClosed();
	}
}
