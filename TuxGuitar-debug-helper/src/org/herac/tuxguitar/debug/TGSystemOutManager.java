package org.herac.tuxguitar.debug;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGSystemOutManager {
	
	private PrintStream defaultSystemOut;
	private PrintStream defaultSystemErr;
	private OutputStream fileOutputStream;
	
	public TGSystemOutManager() {
		this.defaultSystemOut = System.out;
		this.defaultSystemErr = System.err;
	}
	
	public void install() throws IOException {
		this.openFileOutputStream();
		System.setOut(this.createPrintStream(this.defaultSystemOut));
		System.setErr(this.createPrintStream(this.defaultSystemErr));
	}
	
	public void uninstall() throws IOException {
		System.setOut(this.defaultSystemOut);
		System.setErr(this.defaultSystemErr);
		this.closeFileOutputStream();
	}
	
	public void openFileOutputStream() throws IOException {
		this.closeFileOutputStream();
		
		File file = new File(TGFileUtils.PATH_USER_DIR + File.separator + "log" + File.separator + "tuxguitar.log");
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		
		this.fileOutputStream = new FileOutputStream(file);
	}
	
	public void closeFileOutputStream() throws IOException {
		if( this.fileOutputStream != null ) {
			this.fileOutputStream.close();
			this.fileOutputStream = null;
		}
	}
	
	public PrintStream createPrintStream(OutputStream defaultStream) throws IOException {
		List<OutputStream> outputStreams = new ArrayList<OutputStream>();
		outputStreams.add(defaultStream);
		outputStreams.add(this.fileOutputStream);
		
		return new PrintStream(new TGSystemOutStreamWrapper(outputStreams));
	}
	
	public static TGSystemOutManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGSystemOutManager.class.getName(), new TGSingletonFactory<TGSystemOutManager>() {
			public TGSystemOutManager createInstance(TGContext context) {
				return new TGSystemOutManager();
			}
		});
	}
}
