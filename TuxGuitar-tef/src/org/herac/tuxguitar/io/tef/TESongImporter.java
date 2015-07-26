package org.herac.tuxguitar.io.tef;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGLocalFileImporter;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;

public class TESongImporter implements TGLocalFileImporter{
	
	public TESongImporter(){
		super();
	}
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat("Tef", new String[]{"tef"});
	}
	
	public String getImportName() {
		return "Tef";
	}
	
	public String getProviderId() {
		return this.getClass().getName();
	}

	public TGSongStream openStream(TGSongStreamContext context) {
		return new TESongStream(context);
	}
}