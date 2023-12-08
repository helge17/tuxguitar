package org.herac.tuxguitar.android.browser.gdrive.gdaa;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TGDriveBrowserOutputStream extends BufferedOutputStream {
	
	private boolean closed;
	private Runnable onFinish;
	
	public TGDriveBrowserOutputStream(OutputStream out, Runnable onFinish) {
		super(out);
		
		this.onFinish = onFinish;
	}
	
	public void close() throws IOException {
		if(!this.closed) {
			super.close();
			
			this.closed = true;
			this.onFinish.run();
		}
	}
}
