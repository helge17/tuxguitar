package org.herac.tuxguitar.app.editors.channel;

import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.song.models.TGChannel;

public interface TGChannelSettingsDialog {
	
	public void show(Shell parent, TGChannel channel);
	
}
