package org.herac.tuxguitar.io.image;

import org.herac.tuxguitar.graphics.control.print.TGPrintSettings;

public class ImageExporterSettings {
	
	private TGPrintSettings styles;
	private ImageFormat format;
	private String path;
	
	public ImageExporterSettings() {
		super();
	}

	public TGPrintSettings getStyles() {
		return styles;
	}

	public void setStyles(TGPrintSettings styles) {
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
