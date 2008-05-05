package org.herac.tuxguitar.util;

public class TGVersion {
	
	public static final TGVersion CURRENT = new TGVersion(1,0,0,4);
	
	private int major;
	private int minor;
	private int revision;
	private int candidate;
	
	public TGVersion(int major,int minor, int revision,int candidate){
		this.major = major;
		this.minor = minor;
		this.revision = revision;
		this.candidate = candidate;
	}
	
	public int getMajor() {
		return this.major;
	}
	
	public int getMinor() {
		return this.minor;
	}
	
	public int getRevision() {
		return this.revision;
	}
	
	public int getCandidate() {
		return this.candidate;
	}
	
	public boolean isSameVersion(TGVersion version){
		if( version == null ){
			return false;
		}
		return ( version.getMajor() == getMajor() && version.getMinor() == getMinor() && version.getRevision() == getRevision() && version.getCandidate() == getCandidate());
	}
	
	public String getVersion(){
		String version = (getMajor() + "." + getMinor());
		if( getRevision() > 0 ){
			version += ("." + getRevision());
		}
		if( getCandidate() > 0){
			version += ("-rc" + getCandidate());
		}
		return version;
	}
	
	public String toString(){
		return getVersion();
	}
}
