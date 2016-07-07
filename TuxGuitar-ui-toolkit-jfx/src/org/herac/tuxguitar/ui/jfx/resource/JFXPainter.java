package org.herac.tuxguitar.ui.jfx.resource;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.resource.UIPosition;

public class JFXPainter extends JFXAbstractPainter<GraphicsContext> implements UIPainter {
	
	private int style;
	private boolean antialias;
	private boolean pathEmpty;
	private UIPosition position;
	
	public JFXPainter(GraphicsContext handle){
		super(handle);
		
		this.position = new UIPosition();
	}
	
	public void initPath(int style){
		this.style = style;
		this.pathEmpty = true;
		this.position.setX(0f);
		this.position.setY(0f);
		this.getControl().beginPath();
		this.setAntialias(true);
	}
	
	public void initPath(){
		this.initPath(PATH_DRAW);
	}
	
	public void closePath(){
		if(!this.pathEmpty ){
			if( (this.style & PATH_DRAW) != 0){
				this.getControl().stroke();
			}
			if( (this.style & PATH_FILL) != 0){
				this.getControl().fill();
			}
		}
		this.style = 0;
		this.pathEmpty = true;
		this.position.setX(0f);
		this.position.setY(0f);
		this.getControl().closePath();
		this.setAntialias(false);
	}
	
	public void clearArea(float x, float y, float width, float height) {
		this.getControl().clearRect(x, y, width, height);
	}
	
	public void drawString(String string, float x, float y) {
		Paint fill = this.getControl().getFill();
		this.setAdvanced(false);
		this.getControl().setFill(this.getControl().getStroke());
		this.getControl().fillText(string, x, y);
		this.getControl().setFill(fill);
	}
	
	public void drawString(String string, float x, float y, boolean isTransparent) {
		Paint fill = this.getControl().getFill();
		this.setAdvanced(false);
		this.getControl().setFill(this.getControl().getStroke());
		this.getControl().fillText(string, x, y);
		this.getControl().setFill(fill);
	}
	
	public void drawImage(UIImage image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		this.toAbstractImage(image).paint(this, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight);
	}
	
	public void drawImage(UIImage image, float x, float y) {
		this.toAbstractImage(image).paint(this, x, y);
	}
	
	public void drawNativeImage(Image image, float x, float y) {
		this.getControl().drawImage(image, x, y);
	}
	
	public void drawNativeImage(Image image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		this.getControl().drawImage(image, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight);
	}
	
	public void cubicTo(float xc1, float yc1, float xc2, float yc2, float x1, float y1) {
		this.getControl().bezierCurveTo(xc1, yc1, xc2, yc2, x1, y1);
		this.pathEmpty = false;
	}
	
	public void lineTo(float x, float y) {
		if(!this.antialias && this.style == PATH_DRAW) {
			this.drawPixelLine(this.position.getX(), this.position.getY(), x, y);
			this.moveTo(x, y);
		} else {
			this.getControl().lineTo(x, y);
			this.pathEmpty = false;
		}
	}
	
	public void moveTo(float x, float y) {
		this.getControl().moveTo(x, y);
		this.position.setX(x);
		this.position.setY(y);
		this.pathEmpty = false;
	}
	
	public void addString(String text, float x, float y, UIFont font) {
		if((this.style & PATH_FILL) != 0) {
			this.getControl().fillText(text, x, y);
		} else {
			this.getControl().strokeText(text, x, y);
		}
		this.pathEmpty = false;
	}
	
	public void addArc(float x, float y, float width, float height, float startAngle, float arcAngle) {
		this.getControl().arc((x + (width / 2f)), (y + (height / 2f)), (width / 2f), (height / 2f), startAngle, arcAngle);
		this.pathEmpty = false;
	}
	
	public void addOval(float x, float y, float width, float height) {
		this.addArc(x, y, width, height, 0, 360);
	}
	
	public void addRectangle(float x,float y,float width,float height) {
		if(!this.antialias && this.style == PATH_DRAW) {
			this.drawPixelRectangle(x, y, width, height);
		} else {
			this.getControl().rect(x, y, width, height);
			this.pathEmpty = false;
		}
	}
	
	public void setFont(UIFont font) {
		super.setFont(font);
		this.getControl().setFont(toFont(font));
	}
	
	public void setForeground(UIColor color) {
		this.getControl().setStroke(toColor(color));
	}
	
	public void setBackground(UIColor color) {
		this.getControl().setFill(toColor(color));
	}
	
	public void setLineWidth(float width) {
		this.getControl().setLineWidth((width == UIPainter.THINNEST_LINE_WIDTH ? 1 : width));
	}
	
	public void setLineStyleSolid() {
		//TODO
	}
	
	public void setLineStyleDot(){
		//TODO
	}
	
	public void setLineStyleDash(){
		//TODO
	}
	
	public void setLineStyleDashDot(){
		//TODO
	}
	
	public void setAlpha(int alpha) {
		//TODO
	}
	
	public void setAntialias(boolean antialias){
		this.antialias = antialias;
	}
	
	public void setAdvanced(boolean advanced){
		this.setAntialias(advanced);
	}
	
	public void drawPixelLine(float x1, float y1, float x2, float y2) {
		this.drawPixelLine(Math.round(x1), Math.round(y1), Math.round(x2), Math.round(y2));
	}
	
	public void drawPixelLine(int srcX1, int srcY1, int srcX2, int srcY2) {
		int x1 = (srcX1 <= srcX2 ? srcX1 : srcX2);
		int x2 = (srcX2 >= srcX1 ? srcX2 : srcX1);
		int y1 = (srcY1 <= srcY2 ? srcY1 : srcY2);
		int y2 = (srcY2 >= srcY1 ? srcY2 : srcY1);
		int dx = (x2 - x1);
		int dy = (y2 - y1);
		int lineWidth = Math.max(1, (int) Math.round(this.getControl().getLineWidth()));
		for(int repeat = 0; repeat < lineWidth; repeat ++) {
			if( dx >= dy ) {
				for(int x = x1; x <= x2; x++) {
					this.drawPixel(x, (y1 + dy * (x - x1) / dx) + (repeat - (lineWidth / 2)));
				}
			} else {
				for(int y = y1; y <= y2; y++) {
					this.drawPixel((x1 + dx * (y - y1) / dy) + (repeat - (lineWidth / 2)), y);
				}
			}
		}
	}
	
	public void drawPixelRectangle(float x, float y, float width, float height) {
		this.drawPixelRectangle(Math.round(x), Math.round(y), Math.round(width), Math.round(height));
	}
	
	public void drawPixelRectangle(int x, int y, int width, int height) {
		this.drawPixelLine(x, y, (x + width), y);
		this.drawPixelLine(x, y, x, (y + height));
		this.drawPixelLine(x, (y + height), (x + width), (y + height));
		this.drawPixelLine((x + width), y, (x + width), (y + height));
	}
	
	public void drawPixel(int x, int y) {
		Color stroke = (Color) this.getControl().getStroke();
		this.getControl().getPixelWriter().setColor(x, y, stroke != null ? stroke : Color.BLACK);
	}
}
