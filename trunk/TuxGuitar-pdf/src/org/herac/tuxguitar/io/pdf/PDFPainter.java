package org.herac.tuxguitar.io.pdf;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGFont;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGPainter;

import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;

public class PDFPainter extends PDFResourceFactory implements TGPainter {
	
	private int style;
	
	private PdfContentByte cb;
	
	private PDFColor background;
	
	private PDFColor foreground;
	
	private PDFFont font;
	
	public PDFPainter(){
		super();
	}
	
	public void init(PdfContentByte gc){
		this.cb = gc;
		this.background = new PDFColor(0xff, 0xff, 0xff);
		this.foreground = new PDFColor(0x00, 0x00, 0x00);
		this.font = new PDFFont(BaseFont.HELVETICA, 9, false, false);
	}
	
	public void initPath(int style){
		this.style = style;
		this.cb.newPath();
	}
	
	public void initPath(){
		this.initPath( PATH_DRAW );
	}
	
	public void closePath(){		
		if((this.style & PATH_DRAW) != 0 && (this.style & PATH_FILL) != 0 ) {
			this.cb.setColorStroke(this.foreground.createHandle());
			this.cb.setColorFill(this.background.createHandle());
			this.cb.fillStroke();
		}
		else if((this.style & PATH_DRAW) != 0){
			this.cb.setColorStroke(this.foreground.createHandle());
			this.cb.stroke();
		}
		else if((this.style & PATH_FILL) != 0){
			this.cb.setColorFill(this.background.createHandle());
			this.cb.fill();
		}
		
		this.style = 0;
	}
	
	public PdfContentByte getGC(){
		return this.cb;
	}
	
	public void dispose(){
		this.cb = null;
	}
	
	public boolean isDisposed() {
		return (this.cb == null);
	}
	
	public void cubicTo(float xc1, float yc1, float xc2, float yc2, float x1, float y1) {
		this.cb.curveTo(xc1, this.getY(yc1), xc2, this.getY(yc2), x1, this.getY(y1));
	}
	
	public void lineTo(float x, float y) {
		this.cb.lineTo(x, this.getY(y));
	}

	public void moveTo(float x, float y) {
		this.cb.moveTo(x, this.getY(y));
	}
	
	public void addArc(float x, float y, float width, float height, float startAngle, float arcAngle) {
		this.cb.arc(x, this.getY(y), (x + width), this.getY(y + height), startAngle, arcAngle);
	}

	public void addOval(float x, float y, float width, float height) {
		this.cb.arc(x, this.getY(y), (x + width), this.getY(y + height), 0, 360);
	}

	public void addRectangle(float x, float y, float width, float height) {
		this.cb.moveTo(x, this.getY(y));
		this.cb.lineTo((x + width), this.getY(y));
		this.cb.lineTo((x + width), this.getY(y + height));
		this.cb.lineTo(x, this.getY(y + height));
		this.cb.lineTo(x, this.getY(y));
	}
	
	public void addString(String text, float x, float y, TGFont font) {
		this.addString(text, x, y, new PDFFont(font));
	}
	
	public void addString(String text, float x, float y, PDFFont font) {
		this.cb.beginText();
		this.cb.setColorFill(this.foreground.createHandle());
		this.cb.setColorStroke(this.foreground.createHandle());
		this.cb.setFontAndSize(font.createHandle(), font.getHeight());
		this.cb.moveText(x, this.getY(y));
		this.cb.showText(text);
		this.cb.endText();
	}
	
	public void drawString(String string, float x, float y) {
		this.addString(string, x, y, this.font);
	}

	public void drawString(String string, float x, float y, boolean isTransparent) {
		this.addString(string, x, y, this.font);
	}

	public void setFont(TGFont font) {
		this.font = new PDFFont(font);
	}

	public void setBackground(TGColor color) {
		this.background = new PDFColor(color);
	}
	
	public void setForeground(TGColor color) {
		this.foreground = new PDFColor(color);
	}

	public void setLineWidth(float lineWidth) {
		this.cb.setLineWidth(lineWidth);
	}

	public void setLineStyleSolid() {
		this.cb.setLineDash(1f, 0f, 0f);
	}

	public void setLineStyleDot() {
		this.cb.setLineCap(PdfContentByte.LINE_CAP_ROUND);
		this.cb.setLineDash(0f, 2f, 1f);
	}

	public void setLineStyleDash() {
		this.cb.setLineCap(PdfContentByte.LINE_CAP_ROUND);
		this.cb.setLineDash(1f, 2f, 1f);
	}

	public void setLineStyleDashDot() {
		this.setLineStyleDash();
	}

	public float getFontSize() {
		return this.font.getHeight();
	}

	public float getFMTopLine() {
		return this.getFMAscent();
	}
	
	public float getFMMiddleLine(){
		return ((this.getFMTopLine() - this.getFMBaseLine()) / 2f);
	}
	
	public float getFMBaseLine() {
		return 0;
	}

	public float getFMHeight() {
		return (this.getFMAscent());
	}
	
	public float getFMAscent() {
		return ((this.font.createHandle().getAscent("1234567890") / 1000f) * this.font.getHeight());
	}
	
	public float getFMWidth(String text) {
		return ((this.font.createHandle().getWidth(text) / 1000f) * this.font.getHeight());
	}
	
    public float getY(float y) {
        return this.cb.getPdfDocument().getPageSize().getHeight() - y;
    }
    
	public void setAlpha(int alpha) {
		// not implemented
	}

	public void setAntialias(boolean enabled) {
		// not implemented
	}

	public void setAdvanced(boolean advanced) {
		// not implemented
	}

	public void drawImage(TGImage image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		// not implemented
	}

	public void drawImage(TGImage image, float x, float y) {
		// not implemented		
	}
}
