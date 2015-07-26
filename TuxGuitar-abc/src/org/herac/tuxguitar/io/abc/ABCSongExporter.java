package org.herac.tuxguitar.io.abc;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;

public class ABCSongExporter implements TGLocalFileExporter{
	
	public static final String PROVIDER_ID = ABCSongExporter.class.getName();
	
	public String getProviderId() {
		return PROVIDER_ID;
	}
	
	public String getExportName() {
		return "Abc";
	}
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat("Abc", new String[]{"abc"});
	}

	public TGSongStream openStream(TGSongStreamContext context) {
		return new ABCSongExporterStream(context);
	}
}