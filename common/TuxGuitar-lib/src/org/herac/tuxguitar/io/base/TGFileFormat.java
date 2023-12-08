package org.herac.tuxguitar.io.base;

public class TGFileFormat {

	private String name;
	private String mimeType;
	private String[] supportedFormats;
	
	public TGFileFormat(String name, String mimeType, String[] supportedFormats) {
		this.name = name;
		this.mimeType = mimeType;
		this.supportedFormats = supportedFormats;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getMimeType() {
		return this.mimeType;
	}
	
	public String[] getSupportedFormats() {
		return this.supportedFormats;
	}
	
	public boolean isSupportedMimeType(String mimeType) {
		if( mimeType != null ) {
			return (mimeType.toLowerCase().equals(this.mimeType.toLowerCase()));
		}
		return false;
	}
	
	public boolean isSupportedCode(String formatCode) {
		if( formatCode != null ) {
			for(int i = 0 ; i < this.supportedFormats.length ; i ++) {
				if( formatCode.toLowerCase().equals(this.supportedFormats[i].toLowerCase()) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean equals(Object obj) {
		if( obj instanceof TGFileFormat ) {
			return (this.getName() != null && this.getName().equals(((TGFileFormat) obj).getName()));
		}
		return super.equals(obj);
	}
}
