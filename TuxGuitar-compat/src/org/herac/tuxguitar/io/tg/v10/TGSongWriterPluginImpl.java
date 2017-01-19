package org.herac.tuxguitar.io.tg.v10;

import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.io.tg.TGAbstractSongWriterPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class TGSongWriterPluginImpl extends TGAbstractSongWriterPlugin{

	protected TGSongWriter createOutputStream(TGContext context) throws TGPluginException {
		return new TGSongWriterImpl();
	}
}
