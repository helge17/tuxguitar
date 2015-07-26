package org.herac.tuxguitar.io.abc;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGLocalFileImporter;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;

public class ABCSongImporter implements TGLocalFileImporter{
	
	public static final String PROVIDER_ID = ABCSongImporter.class.getName();
	
	public ABCSongImporter(){
		super();
	}
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat("ABC", new String[]{"abc"});
	}
	
	public String getImportName() {
		return "ABC";
	}

	public String getProviderId() {
		return PROVIDER_ID;
	}

	public TGSongStream openStream(TGSongStreamContext context) {
		return new ABCSongImporterStream(context);
	}
	
}
