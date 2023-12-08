package org.herac.tuxguitar.io.svg;

import java.io.PrintWriter;

import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongWriter;
import org.herac.tuxguitar.io.base.TGSongWriterHandle;

public class SVGSongWriter implements TGSongWriter {
	
	public static final TGFileFormat FILE_FORMAT = new TGFileFormat("Scalable Vector Graphics", "image/svg+xml", new String[]{"svg"});
	
	public SVGSongWriter() {
		super();
	}
	
	public TGFileFormat getFileFormat() {
		return FILE_FORMAT;
	}
	
	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		try {
			SVGStyles styles = handle.getContext().getAttribute(SVGStyles.class.getName());
			if( styles == null ) {
				styles = new SVGStyles();
				styles.configureWithDefaults();
			}
			
			StringBuffer svgBuffer = new StringBuffer();
			
			SVGController svgController = new SVGController(styles);
			svgController.load(handle.getSong().clone(svgController.getSongManager().getFactory()));
			svgController.write(svgBuffer);
			svgController.getResourceBuffer().disposeAllResources();
			
			PrintWriter svgWriter = new PrintWriter(handle.getOutputStream());
			svgWriter.write( svgBuffer.toString() );
			svgWriter.flush();
			svgWriter.close();
		} catch( Throwable throwable ){
			throw new TGFileFormatException(throwable);
		}
	}
}
