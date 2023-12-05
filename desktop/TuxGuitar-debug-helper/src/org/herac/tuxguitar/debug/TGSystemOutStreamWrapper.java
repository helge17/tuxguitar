package org.herac.tuxguitar.debug;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class TGSystemOutStreamWrapper extends OutputStream {

	private List<OutputStream> outputStreams;
	
	public TGSystemOutStreamWrapper(List<OutputStream> outputStreams) throws FileNotFoundException {
		this.outputStreams = outputStreams;
	}
	
	@Override
	public void write(int b) throws IOException {
		for(OutputStream outputStream : this.outputStreams) {
			outputStream.write(b);
		}
	}
	
	@Override
	public void flush() throws IOException {
		for(OutputStream outputStream : this.outputStreams) {
			outputStream.flush();
		}
	}
	
	@Override
	public void close() throws IOException {
		for(OutputStream outputStream : this.outputStreams) {
			outputStream.close();
		}
	}
}
