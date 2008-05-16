package org.herac.tuxguitar.io.gtp;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.gui.system.plugins.base.TGInputStreamPlugin;
import org.herac.tuxguitar.gui.system.plugins.base.TGOutputStreamPlugin;
import org.herac.tuxguitar.gui.system.plugins.base.TGPluginList;
import org.herac.tuxguitar.io.base.TGInputStreamBase;
import org.herac.tuxguitar.io.base.TGOutputStreamBase;

public class GTPPluginList extends TGPluginList{
	
	protected List getPlugins() {
		List plugins = new ArrayList();
		plugins.add(new TGInputStreamPlugin() {
			protected TGInputStreamBase getInputStream() {
				return new GP5InputStream();
			}
		});
		plugins.add(new TGInputStreamPlugin() {
			protected TGInputStreamBase getInputStream() {
				return new GP4InputStream();
			}
		});
		plugins.add(new TGInputStreamPlugin() {
			protected TGInputStreamBase getInputStream() {
				return new GP3InputStream();
			}
		});
		plugins.add(new TGInputStreamPlugin() {
			protected TGInputStreamBase getInputStream() {
				return new GP2InputStream();
			}
		});
		plugins.add(new TGInputStreamPlugin() {
			protected TGInputStreamBase getInputStream() {
				return new GP1InputStream();
			}
		});
		plugins.add(new TGOutputStreamPlugin() {
			protected TGOutputStreamBase getOutputStream() {
				return new GP5OutputStream();
			}
		});
		plugins.add(new TGOutputStreamPlugin() {
			protected TGOutputStreamBase getOutputStream() {
				return new GP4OutputStream();
			}
		});
		plugins.add(new TGOutputStreamPlugin() {
			protected TGOutputStreamBase getOutputStream() {
				return new GP3OutputStream();
			}
		});
		return plugins;
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
