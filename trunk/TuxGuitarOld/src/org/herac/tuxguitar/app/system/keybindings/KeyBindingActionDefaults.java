package org.herac.tuxguitar.app.system.keybindings;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.system.keybindings.xml.KeyBindingReader;
import org.herac.tuxguitar.app.util.TGFileUtils;

public class KeyBindingActionDefaults {

	private static final String DEFAULT_SHORTCUT_FILE = "tuxguitar-shortcuts.xml";

	public static List<KeyBindingAction> getDefaultKeyBindings() {
		List<KeyBindingAction> list = new ArrayList<KeyBindingAction>();
		try {
			InputStream stream = TGFileUtils.getResourceAsStream(DEFAULT_SHORTCUT_FILE);
			if (stream != null) {
				List<KeyBindingAction> defaults = KeyBindingReader.getKeyBindings(stream);
				if (defaults != null) {
					list.addAll(defaults);
				}
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return list;

	}
}
