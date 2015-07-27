package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.util.TGContext;

public class PDFSongExporter implements TGLocalFileExporter{
	
	public static final String PROVIDER_ID = PDFSongExporter.class.getName();
	
	private TGContext context;
	
	public PDFSongExporter(TGContext context){
		this.context = context;
	}
	
	public String getProviderId() {
		return PROVIDER_ID;
	}
	
	public String getExportName() {
		return "PDF";
	}
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat("PDF", new String[]{"pdf"});
	}
	
	public TGSongStream openStream(TGSongStreamContext streamContext) {
		return new PDFSongStream(this.context, streamContext);
	}
}
