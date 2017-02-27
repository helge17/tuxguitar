package org.herac.tuxguitar.util;

public class TGVersion {
	
	public static final TGVersion CURRENT = new TGVersion(1,4,20170227);
	
	private int major;
	private int minor;
	private int revision;
	
	public TGVersion(int major,int minor, int revision){
		this.major = major;
		this.minor = minor;
		this.revision = revision;
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
	
	public boolean isSameVersion(TGVersion version){
		if( version == null ){
			return false;
		}
		return ( version.getMajor() == getMajor() && version.getMinor() == getMinor() && version.getRevision() == getRevision());
	}
	
	public String getVersion(){
		String version = (getMajor() + "." + getMinor());
		if( getRevision() > 0 ){
			version += ("." + getRevision());
		}
		return version;
	}
	
	public String toString(){
		return getVersion();
	}
}
