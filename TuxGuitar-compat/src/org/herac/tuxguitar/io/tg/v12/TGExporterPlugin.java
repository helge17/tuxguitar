package org.herac.tuxguitar.io.tg.v12;

import org.herac.tuxguitar.io.tg.TGExporterPluginImpl;

public class TGExporterPlugin extends TGExporterPluginImpl{

	public TGExporterPlugin() {
		super(new TGOutputStreamProvider());
	}
}
