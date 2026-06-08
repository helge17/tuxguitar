package app.tuxguitar.nsm;

import java.io.File;

import app.tuxguitar.app.system.config.TGConfigDefaults;
import app.tuxguitar.app.system.properties.TGFilePropertiesHandler;
import app.tuxguitar.app.system.properties.TGResourcePropertiesReader;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.properties.TGProperties;
import app.tuxguitar.util.properties.TGPropertiesException;
import app.tuxguitar.util.properties.TGPropertiesReader;
import app.tuxguitar.util.properties.TGPropertiesWriter;

/*
 * Properties handler that redirects the TuxGuitar "config" resource to the
 * NSM session directory, giving each session its own independent configuration
 * (MIDI device, audio output, soundfont paths, layout preferences, etc.).
 *
 * File layout inside the session directory:
 *   {sessionDir}/tuxguitar.cfg              — main application config
 *   {sessionDir}/plugins/tuxguitar-*.cfg    — per-plugin configs (FluidSynth, JACK, …)
 */
public class NSMConfigPropertiesHandler implements TGPropertiesReader, TGPropertiesWriter {

	private static final String MAIN_MODULE  = "tuxguitar";
	private static final String CFG_SUFFIX   = ".cfg";

	private final TGContext context;
	private final String sessionDir;

	public NSMConfigPropertiesHandler(TGContext context, String sessionDir) {
		this.context = context;
		this.sessionDir = sessionDir;
	}

	// -------------------------------------------------------------------------
	// TGPropertiesReader
	// -------------------------------------------------------------------------

	@Override
	public void readProperties(TGProperties properties, String module) throws TGPropertiesException {
		// 1. Hardcoded Java defaults (main module only)
		if (MAIN_MODULE.equals(module)) {
			TGConfigDefaults.loadProperties(properties);
		}
		// 2. Classpath defaults bundled with the app
		new TGResourcePropertiesReader(this.context, "", CFG_SUFFIX).readProperties(properties, module);
		// 3. Stored session config (overrides defaults when it exists)
		new TGFilePropertiesHandler(filePrefix(module), CFG_SUFFIX).readProperties(properties, module);
	}

	// -------------------------------------------------------------------------
	// TGPropertiesWriter
	// -------------------------------------------------------------------------

	@Override
	public void writeProperties(TGProperties properties, String module) throws TGPropertiesException {
		new TGFilePropertiesHandler(filePrefix(module), CFG_SUFFIX).writeProperties(properties, module);
	}

	// -------------------------------------------------------------------------

	private String filePrefix(String module) {
		if (MAIN_MODULE.equals(module)) {
			return this.sessionDir + File.separator;
		}
		return this.sessionDir + File.separator + "plugins" + File.separator;
	}
}
