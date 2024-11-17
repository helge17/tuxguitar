package org.herac.tuxguitar.util;

public class TGVersion implements Comparable<TGVersion> {
	
	public static final TGVersion CURRENT = new TGVersion(1,6,5);
	
	private int major;
	private int minor;
	private int revision;
	
	public TGVersion(int major,int minor, int revision){
		this.major = major;
		this.minor = minor;
		this.revision = revision;
	}
	
	public TGVersion(String version) {
		this.major = 0;
		this.minor = 0;
		this.revision = 0;
		if (version!=null ) {
			try {
				String[] subVersions = version.split("\\.");
				if (subVersions.length > 3 || subVersions.length<2) {
					throw new IllegalArgumentException();
				}
				this.major = Integer.valueOf(subVersions[0]);
				this.minor = Integer.valueOf(subVersions[1]);
				if (subVersions.length > 2) {
					this.revision = Integer.valueOf(subVersions[2]);
				}
				if (this.major<0 || this.minor<0 || this.revision<0) {
					throw new IllegalArgumentException();
				}
			} catch (IllegalArgumentException e) {
				this.major=0;
				this.minor=0;
				this.revision=0;
			}
		}
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
	
	public int compareTo(TGVersion version) {
		if (this.major != version.getMajor()) return (this.major > version.getMajor() ? 1 : -1);
		if (this.minor != version.getMinor()) return (this.minor > version.getMinor() ? 1 : -1);
		if (this.revision != version.getRevision()) return (this.revision > version.getRevision() ? 1 : -1);
		return 0;
	}
}
