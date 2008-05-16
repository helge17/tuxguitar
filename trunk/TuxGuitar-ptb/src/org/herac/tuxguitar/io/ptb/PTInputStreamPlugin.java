package org.herac.tuxguitar.io.ptb;

import org.herac.tuxguitar.gui.system.plugins.base.TGInputStreamPlugin;
import org.herac.tuxguitar.io.base.TGInputStreamBase;

public class PTInputStreamPlugin extends TGInputStreamPlugin{
	
	protected TGInputStreamBase getInputStream() {
		return new PTInputStream();
	}
	
	public String getAuthor() {
		return "Julian Casadesus <julian@casadesus.com.ar>";
	}
	
	public String getDescription() {
		return "PTB File Format plugin";
	}
	
	public String getName() {
		return "PTB File Format plugin";
	}
	
	public String getVersion() {
		return "1.0";
	}
}
