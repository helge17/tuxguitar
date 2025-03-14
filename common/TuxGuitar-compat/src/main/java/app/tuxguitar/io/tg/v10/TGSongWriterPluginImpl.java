package app.tuxguitar.io.tg.v10;

import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.tg.TGAbstractSongWriterPlugin;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginException;

public class TGSongWriterPluginImpl extends TGAbstractSongWriterPlugin{

	protected TGSongWriter createOutputStream(TGContext context) throws TGPluginException {
		return new TGSongWriterImpl();
	}
}
