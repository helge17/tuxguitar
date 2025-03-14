package app.tuxguitar.util.plugin;

import app.tuxguitar.util.TGContext;

public interface TGPlugin {

	String getModuleId();

	void connect(TGContext context) throws TGPluginException;

	void disconnect(TGContext context) throws TGPluginException;
}