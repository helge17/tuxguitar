package org.herac.tuxguitar.midi.synth.remote;

public class TGSession {
	
	private Integer id;
	private TGConnection connection;
	
	public TGSession(Integer id, TGConnection connection) {
		this.id = id;
		this.connection = connection;
	}

	public Integer getId() {
		return id;
	}
	
	public TGConnection getConnection() {
		return connection;
	}
	
	public void close() {
		this.getConnection().close();
	}
	
	public boolean isClosed() {
		return this.getConnection().isClosed();
	}
}
