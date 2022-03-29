package org.herac.tuxguitar.player.impl.midiport.lv2.remote;

public class LV2Session {
	
	private Integer id;
	private LV2Connection connection;
	
	public LV2Session(Integer id, LV2Connection connection) {
		this.id = id;
		this.connection = connection;
	}

	public Integer getId() {
		return id;
	}
	
	public LV2Connection getConnection() {
		return connection;
	}
	
	public void close() {
		this.getConnection().close();
	}
	
	public boolean isClosed() {
		return this.getConnection().isClosed();
	}
}
