package org.herac.tuxguitar.android.graphics;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGColorModel;
import org.herac.tuxguitar.graphics.TGFont;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGPainter;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

public class TGPainterImpl extends TGResourceFactoryImpl implements TGPainter {
	
	private boolean pathEmpty;
	
	private int style;
	
	private int alpha;
	
	private Canvas canvas;
	
	private Paint paint;
	
	private Path path;
	
	private TGColorImpl foreground;
	private TGColorImpl background;
	
	public TGPainterImpl(Canvas canvas){
		this.canvas = canvas;
		this.paint = new Paint();
		this.alpha = 0xff;
		this.foreground = new TGColorImpl(new TGColorModel(0, 0, 0));
		this.background = new TGColorImpl(new TGColorModel(255, 255, 255));
	}
	
	public Canvas getCanvas() {
		return this.canvas;
	}
	
	public void initPath(int style){
		this.style = style;
		this.path = new Path();
		this.pathEmpty = true;
		this.setAntialias(true);
	}
	
	public void initPath(){
		this.initPath( PATH_DRAW );
	}
	
	public void closePath(){
		if(! this.pathEmpty ){
			if( (this.style & PATH_DRAW) != 0){
				this.paint.setStyle(Paint.Style.STROKE);
				this.paint.setColor(this.foreground.getHandle(this.alpha) );
				this.canvas.drawPath(this.path,this.paint);
			}
			if( (this.style & PATH_FILL) != 0){
				this.paint.setStyle(Paint.Style.FILL);
				this.paint.setColor(this.background.getHandle(this.alpha) );
				this.canvas.drawPath(this.path,this.paint);
			}
		}
		
		this.style = 0;
		this.path = null;
		this.pathEmpty = true;
		this.setAntialias(false);
	}
	
	public void dispose() {
		this.canvas = null;
		this.paint = null;
	}
	
	public boolean isDisposed() {
		return (this.canvas == null || this.paint == null);
	}
	
	public void addArc(float x, float y, float w, float h, float startAngle, float arcLength) {
		this.path.addArc(new RectF(x, y, x + w, y + h), startAngle, arcLength);
		this.pathEmpty = false;
	}

	public void addOval(float x, float y, float w, float h) {
		this.path.addOval(new RectF(x, y, x + w, y + h), Direction.CW);
		this.pathEmpty = false;
	}

	public void addRectangle(float x, float y, float width, float height) {
		this.path.addRect(x, y, x + width, y + height, Direction.CW);
		this.pathEmpty = false;
	}
	
	public void lineTo(float x, float y) {
		this.path.lineTo(x, y);
		this.pathEmpty = false;
	}

	public void moveTo(float x, float y) {
		this.path.moveTo(x, y);
		this.pathEmpty = false;		
	}

	public void cubicTo(float x1, float y1, float x2, float y2, float x3, float y3) {
		this.path.cubicTo(x1, y1, x2, y2, x3, y3);
		this.pathEmpty = false;
	}

	public void drawImage(TGImage image, float x, float y) {
		this.canvas.drawBitmap(((TGImageImpl)image).getHandle(), x, y, this.paint);
	}

	public void drawImage(TGImage image, float srcX, float srcY, float srcWidth, float srcHeight, float dstX, float dstY, float dstWidth, float dstHeight) {
		this.canvas.drawBitmap(((TGImageImpl)image).getHandle(), toRect(srcX, srcY, srcX + srcWidth, srcY + srcHeight), toRect(dstX, dstY, dstX + dstWidth, dstY + dstHeight), this.paint);
	}
	
	public void drawString(String text, float x, float y) {    	
		this.paint.setStyle(Paint.Style.FILL);
		this.paint.setColor(this.foreground.getHandle(this.alpha));
		this.canvas.drawText(text, x, y, this.paint);
	}

	public void drawString(String text, float x, float y, boolean transparent) {
    	this.paint.setStyle(Paint.Style.FILL);
    	this.paint.setColor(this.foreground.getHandle(this.alpha));
		this.canvas.drawText(text, x, y, this.paint);
	}
	
	public void addString(String arg0, float arg1, float arg2, TGFont arg3) {
		// not implemented..
	}
	
	public void setAdvanced(boolean advanced) {
		this.setAntialias(advanced);
	}
	
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	
	public void setAntialias(boolean antialias) {
		this.paint.setAntiAlias(antialias);
	}
	
	public void setFont(TGFont font) {
		if( font instanceof TGFontImpl ){
			this.paint.setTypeface(Typeface.create(font.getName(), (font.isBold() ? Typeface.BOLD : 0 ) | (font.isItalic() ? Typeface.ITALIC : 0 ) ));
			this.paint.setTextSize(font.getHeight());
		}
	}

	public void setForeground(TGColor color) {
		if( color instanceof TGColorImpl ){
			this.foreground = (TGColorImpl)color;
		}
	}

	public void setBackground(TGColor color) {
		if( color instanceof TGColorImpl ){
			this.background = (TGColorImpl)color;
		}		
	}
	
	public void setLineStyleSolid() {
		this.paint.setPathEffect(null);
	}
	
	public void setLineStyleDash() {
		this.paint.setPathEffect(new DashPathEffect(new float[]{4,1}, 0));
	}

	public void setLineStyleDashDot() {
		this.paint.setPathEffect(new DashPathEffect(new float[]{4,1,1,1}, 0));
	}

	public void setLineStyleDot() {
		this.paint.setPathEffect(new DashPathEffect(new float[]{1,1}, 0));
	}
	
	public void setLineWidth(float width) {
		this.paint.setStrokeWidth(width);
	}

	public float getFontSize() {
		return Math.round(this.paint.getTextSize());
	}

	@Override
	public float getFMBaseLine() {
		return 0f;
	}

	@Override
	public float getFMTopLine() {
		return -((this.getFMAscent() / 10f) * 8f);
	}

	@Override
	public float getFMMiddleLine() {
		return -((this.getFMAscent() / 10f) * 4f);
	}
	
	public float getFMAscent() {
		return (this.paint.getFontMetrics().ascent);
	}

	public float getFMDescent() {
		return (this.paint.getFontMetrics().descent);
	}

	public float getFMHeight() {
		return (this.getFMDescent() - this.getFMAscent());
	}

	public float getFMWidth(String text) {
		Rect bounds = new Rect();
		this.paint.getTextBounds(text, 0, text.length(), bounds);
		return bounds.width();
	}
	
	public Rect toRect(float left, float top, float right, float bottom) {
		return new Rect(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom));
	}
}
