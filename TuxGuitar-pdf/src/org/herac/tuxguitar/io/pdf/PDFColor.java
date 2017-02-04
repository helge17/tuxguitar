package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGColorModel;

import com.itextpdf.text.BaseColor;

public class PDFColor extends TGColorModel implements TGColor {
	
	private boolean disposed;
	
	public PDFColor(int red, int green, int blue){
		super(red, green, blue);
	}
	
	public PDFColor(TGColorModel model) {
		this(model.getRed(), model.getGreen(), model.getBlue());
	}

	public PDFColor(TGColor color) {
		this(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	public void dispose() {
		this.disposed = true;
	}

	public boolean isDisposed() {
		return this.disposed;
	}
	
	public BaseColor createHandle() {
		return new BaseColor(Math.round(getRed()), Math.round(getGreen()), Math.round(getBlue()));
	}
}
