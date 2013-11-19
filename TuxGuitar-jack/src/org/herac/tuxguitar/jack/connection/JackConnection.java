package org.herac.tuxguitar.jack.connection;

public class JackConnection {
	
	private long srcPortId;
	private long dstPortId;
	
	public JackConnection(long srcPortId, long dstPortId){
		this.srcPortId = srcPortId;
		this.dstPortId = dstPortId;
	}

	public long getSrcPortId() {
		return srcPortId;
	}

	public void setSrcPortId(long srcPortId) {
		this.srcPortId = srcPortId;
	}

	public long getDstPortId() {
		return dstPortId;
	}

	public void setDstPortId(long dstPortId) {
		this.dstPortId = dstPortId;
	}

	public boolean equals(Object obj){
		return (this.hashCode() == obj.hashCode());
	}
	
	public int hashCode(){
		return (JackConnection.class.getName() + "-" + this.srcPortId + "-" + this.dstPortId).hashCode();
	} 
}
