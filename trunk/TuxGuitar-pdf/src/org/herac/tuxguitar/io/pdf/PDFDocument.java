package org.herac.tuxguitar.io.pdf;

import java.awt.Graphics2D;
import java.io.OutputStream;

import org.herac.tuxguitar.app.printer.PrintDocument;
import org.herac.tuxguitar.awt.graphics.TGPainterImpl;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGRectangle;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.error.TGErrorManager;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFDocument implements PrintDocument{
	
	private TGContext context;
	private TGPainterImpl painter;
	private TGRectangle bounds;
	private OutputStream stream;
	
	private Document document;
	private PdfContentByte cb;
	private PdfTemplate template;
	private Graphics2D graphics;
	
	public PDFDocument(TGContext context, TGRectangle bounds, OutputStream stream){
		this.context = context;
		this.bounds = bounds;
		this.stream = stream;
		this.painter = new TGPainterImpl();
	}
	
	public TGPainter getPainter() {
		return this.painter;
	}
	
	public TGRectangle getBounds(){
		return this.bounds;
	}
	
	public void pageStart() {
		this.document.newPage();
		this.template = this.cb.createTemplate(this.bounds.getWidth(), this.bounds.getHeight());
		
		this.graphics = new PdfGraphics2D(this.template, this.bounds.getWidth(), this.bounds.getHeight());
		this.painter.init(this.graphics);
	}
	
	public void pageFinish() {
		
		this.painter.dispose();
		this.graphics.dispose();
		
		this.cb.addTemplate(this.template, this.bounds.getX(), this.bounds.getY());
	}
	
	public void start() {
		try {
			this.document = new Document(new Rectangle(this.bounds.getX() + this.bounds.getWidth(), this.bounds.getY() + this.bounds.getHeight()) );
			PdfWriter writer = PdfWriter.getInstance(this.document, this.stream);
			this.document.open();
			this.cb = writer.getDirectContent();
		} catch(Throwable e){
			TGErrorManager.getInstance(this.context).handleError(e);
		}
	}
	
	public void finish() {
		try{
			this.document.close();
		} catch(Throwable throwable){
			TGErrorManager.getInstance(this.context).handleError(throwable);
		}
	}
	
	public boolean isPaintable(int page) {
		return true;
	}
}
