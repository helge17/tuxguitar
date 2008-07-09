package org.herac.tuxguitar.io.gtp;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.system.plugins.TGPluginSetup;
import org.herac.tuxguitar.gui.system.plugins.base.TGInputStreamPlugin;
import org.herac.tuxguitar.gui.system.plugins.base.TGOutputStreamPlugin;
import org.herac.tuxguitar.gui.system.plugins.base.TGPluginList;
import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;

public class GTPPluginList extends TGPluginList implements TGPluginSetup{
	
	protected List getPlugins() {
		GTPSettingsUtil.instance().load();
		
		List plugins = new ArrayList();
		plugins.add(new TGInputStreamPlugin() {
			protected TGInputStreamBase getInputStream() {
				return new GP5InputStream(GTPSettingsUtil.instance().getSettings());
			}
		});
		plugins.add(new TGInputStreamPlugin() {
			protected TGInputStreamBase getInputStream() {
				return new GP4InputStream(GTPSettingsUtil.instance().getSettings());
			}
		});
		plugins.add(new TGInputStreamPlugin() {
			protected TGInputStreamBase getInputStream() {
				return new GP3InputStream(GTPSettingsUtil.instance().getSettings());
			}
		});
		plugins.add(new TGInputStreamPlugin() {
			protected TGInputStreamBase getInputStream() {
				return new GP2InputStream(GTPSettingsUtil.instance().getSettings());
			}
		});
		plugins.add(new TGInputStreamPlugin() {
			protected TGInputStreamBase getInputStream() {
				return new GP1InputStream(GTPSettingsUtil.instance().getSettings());
			}
		});
		plugins.add(new TGOutputStreamPlugin() {
			protected TGOutputStreamBase getOutputStream() {
				return new GP5OutputStream(GTPSettingsUtil.instance().getSettings());
			}
		});
		plugins.add(new TGOutputStreamPlugin() {
			protected TGOutputStreamBase getOutputStream() {
				return new GP4OutputStream(GTPSettingsUtil.instance().getSettings());
			}
		});
		plugins.add(new TGOutputStreamPlugin() {
			protected TGOutputStreamBase getOutputStream() {
				return new GP3OutputStream(GTPSettingsUtil.instance().getSettings());
			}
		});
		return plugins;
	}
	
	public void setupDialog(Shell parent) {
		GTPSettingsUtil.instance().configure(parent);
	}
	
	public String getAuthor() {
		return "Julian Casadesus <julian@casadesus.com.ar>";
	}
	
	public String getName() {
		return "GPx File Format plugin";
	}
	
	public String getDescription() {
		return "GPx File Format plugin for TuxGuitar";
	}
	
	public String getVersion() {
		return "1.0";
	}
}
