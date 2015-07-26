package org.herac.tuxguitar.io.image;

import org.herac.tuxguitar.app.printer.PrintStyles;

public class ImageExporterSettings {
	
	private PrintStyles styles;
	private ImageFormat format;
	private String path;
	
	public ImageExporterSettings() {
		super();
	}

	public PrintStyles getStyles() {
		return styles;
	}

	public void setStyles(PrintStyles styles) {
		this.styles = styles;
	}

	public ImageFormat getFormat() {
		return format;
	}

	public void setFormat(ImageFormat format) {
		this.format = format;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
