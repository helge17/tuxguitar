package org.herac.tuxguitar.io.svg;

import java.io.OutputStream;
import java.io.PrintWriter;

import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.io.base.TGFileFormatException;
import org.herac.tuxguitar.io.base.TGSongStream;
import org.herac.tuxguitar.io.base.TGSongStreamContext;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;

public class SVGExporterStream implements TGSongStream {
	
	private TGContext context;
	private TGSongStreamContext streamContext;
	
	public SVGExporterStream(TGContext context, TGSongStreamContext streamContext) {
		this.context = context;
		this.streamContext = streamContext;
	}
	
	public void process() throws TGFileFormatException {
		try {
			OutputStream stream = this.streamContext.getAttribute(OutputStream.class.getName());
			TGSong song = this.streamContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
			SVGExporterStyles styles = this.streamContext.getAttribute(SVGExporterStyles.class.getName());
			if( styles == null ) {
				styles = new SVGExporterStyles();
				styles.configureWithDefaults();
			}
			
			StringBuffer svgBuffer = new StringBuffer();
			
			SVGController svgController = new SVGController(this.context, styles);
			svgController.load(song.clone(svgController.getSongManager().getFactory()));
			svgController.write(svgBuffer);
			
			PrintWriter svgWriter = new PrintWriter(stream);
			svgWriter.write( svgBuffer.toString() );
			svgWriter.flush();
			svgWriter.close();
		} catch( Throwable throwable ){
			throw new TGFileFormatException(throwable);
		}
	}
}
