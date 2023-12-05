package org.herac.tuxguitar.jack;

public class JackPort {
	
	private long jackPortId;
	private String jackPortName;
	
	public JackPort(long jackPortId, String jackPortName) {
		this.jackPortId = jackPortId;
		this.jackPortName = jackPortName;
	}

	public long getJackPortId() {
		return this.jackPortId;
	}

	public String getJackPortName() {
		return this.jackPortName;
	}
}
