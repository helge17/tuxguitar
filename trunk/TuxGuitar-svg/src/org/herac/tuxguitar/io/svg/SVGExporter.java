package org.herac.tuxguitar.io.svg;

import java.io.OutputStream;
import java.io.PrintWriter;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGLocalFileExporter;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGSong;

public class SVGExporter implements TGLocalFileExporter {
	
	private OutputStream stream;
	private SVGExporterStyles styles;
	
	public SVGExporter(SVGExporterStyles styles){
		this.styles = styles;
	}
	
	public String getExportName() {
		return "SVG";
	}
	
	public TGFileFormat getFileFormat() {
		return new TGFileFormat("Scalable Vector Graphics","*.svg");
	}
	
	public boolean configure(boolean setDefaults) {
		if( setDefaults ){
			this.styles.configureWithDefaults();
		}else{
			this.styles.configure();
		}
		return this.styles.isConfigured();
	}
	
	public void init(TGFactory factory, OutputStream stream) {
		this.stream = stream;
	}
	
	public void exportSong(TGSong song) throws TGFileFormatException {
		try {
			if( this.styles.isConfigured() ){
				StringBuffer svgBuffer = new StringBuffer();
				
				SVGController svgController = new SVGController(this.styles);
				svgController.load(song.clone(svgController.getSongManager().getFactory()));
				svgController.write(svgBuffer);
				
				PrintWriter svgWriter = new PrintWriter(this.stream);
				svgWriter.write( svgBuffer.toString() );
				svgWriter.flush();
				svgWriter.close();
			}
		} catch( Throwable throwable ){
			throw new TGFileFormatException(throwable);
		}
	}
}
