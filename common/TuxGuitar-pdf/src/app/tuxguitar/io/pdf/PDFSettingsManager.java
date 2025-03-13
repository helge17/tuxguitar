package org.herac.tuxguitar.io.pdf;

import java.util.Map;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.configuration.TGConfigManager;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class PDFSettingsManager {

	private TGConfigManager configMgr;
	private PDFSettings settings;

	public PDFSettingsManager(TGContext context) {
		this.settings = new PDFSettings();
		this.configMgr = new TGConfigManager(context, PDFSongWriterPlugin.MODULE_ID);
		this.loadSettings();
	}

	private void loadSettings() {
		Map<String,Float> map = this.settings.getSettingsMap();
		for (String key : map.keySet()) {
			map.put(key, this.configMgr.getFloatValue(key, map.get(key)));
		}
	}

	public PDFSettings getSettings() {
		return this.settings;
	}

	public float getSetting(String key) {
		return this.settings.getSettingsMap().get(key);
	}

	public void setSetting(String key, float value) {
		this.settings.getSettingsMap().put(key, value);
	}

	public void saveSettings() {
		Map<String,Float> map = this.settings.getSettingsMap();
		for (String key : map.keySet()) {
			this.configMgr.setValue(key, map.get(key));
		}
		this.configMgr.save();
	}

	public static PDFSettingsManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, PDFSettingsManager.class.getName(), new TGSingletonFactory<PDFSettingsManager>() {
			public PDFSettingsManager createInstance(TGContext context) {
				return new PDFSettingsManager(context);
			}
		});
	}

}
