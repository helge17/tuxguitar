package org.herac.tuxguitar.io.tg.v10;

import org.herac.tuxguitar.io.tg.TGExporterPluginImpl;

public class TGExporterPlugin extends TGExporterPluginImpl{

	public TGExporterPlugin() {
		super(new TGOutputStreamProvider());
	}
}
