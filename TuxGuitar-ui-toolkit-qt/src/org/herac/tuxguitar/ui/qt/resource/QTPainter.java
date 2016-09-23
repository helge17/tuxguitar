package org.herac.tuxguitar.ui.qt.resource;

import org.herac.tuxguitar.ui.qt.QTComponent;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;

import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.Qt.PenStyle;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QImage;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPainterPath;
import com.trolltech.qt.gui.QPen;

public class QTPainter extends QTComponent<QPainter> implements UIPainter {
	
	private int style;
	private boolean pathEmpty;
	private QPainterPath path;
	
	public QTPainter(QPainter handle){
		super(handle);
		
		this.getControl().setBackground(new QBrush(new QColor(0, 0, 0, 0)));
	}
	
	public void initPath(int style){
		this.style = style;
		this.path = new QPainterPath();
		this.pathEmpty = true;
		this.setAntialias(true);
	}
	
	public void initPath(){
		this.initPath( PATH_DRAW );
	}
	
	public void closePath(){
		if(! this.pathEmpty ){
			if((this.style & PATH_DRAW) != 0){
				this.getControl().drawPath(this.path);
			}
			if( (this.style & PATH_FILL) != 0){
				this.getControl().fillPath(this.path, this.getControl().background());
			}
		}
		this.style = 0;
		this.path.dispose();
		this.pathEmpty = true;
		this.setAntialias(false);
	}
	
	public void dispose(){
		this.getControl().end();
		super.dispose();
	}
	
	public void drawString(String string, float x, float y) {
		this.setAdvanced(false);
		this.getControl().drawText(toInt(x), toInt(y), string);
	}
	
	public void drawString(String string, float x, float y, boolean isTransparent) {
		this.setAdvanced(false);
		this.getControl().drawText(toInt(x), toInt(y), string);
	}
	
	public void drawImage(UIImage image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		this.setAdvanced(false);
		this.getControl().drawImage(toRect(destX, destY, destWidth, destHeight), getImage(image), toRect(srcX, srcY, srcWidth, srcHeight));
	}
	
	public void drawImage(UIImage image, float x, float y) {
		this.setAdvanced(false);
		this.getControl().drawImage(toInt(x), toInt(y), getImage(image));
	}
	
	public void cubicTo(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
		this.path.cubicTo(arg0, arg1, arg2, arg3, arg4, arg5);
		this.pathEmpty = false;
	}
	
	public void lineTo(float arg0, float arg1) {
		this.path.lineTo(arg0, arg1);
		this.pathEmpty = false;
	}
	
	public void moveTo(float arg0, float arg1) {
		this.path.moveTo(arg0, arg1);
		this.pathEmpty = false;
	}
	
	public void addString(String text, float x, float y, UIFont font) {
		this.path.addText(x, y, getFont(font), text);
		this.pathEmpty = false;
	}
	
	public void addArc(float x, float y, float width, float height, float startAngle, float arcAngle) {
		this.path.arcMoveTo(x, y, width, height, startAngle);
		this.path.arcTo(x, y, width, height, startAngle, arcAngle);
		this.pathEmpty = false;
	}
	
	public void addOval(float x, float y, float width, float height) {
		this.addArc(x, y, width, height, 0, 360);
	}
	
	public void addRectangle(float x,float y,float width,float height) {
		this.path.addRect(x, y, width, height);
		this.pathEmpty = false;
	}
	
	public void setFont(UIFont font) {
		this.getControl().setFont(getFont(font));
	}
	
	public void setBackground(UIColor color) {
		this.getControl().setBackground(new QBrush(((QTColor)color).getControl()));
	}
	
	public void setForeground(UIColor color) {
		QPen qPen = this.getControl().pen();
		qPen.setColor(((QTColor)color).getControl());
		
		this.getControl().setPen(qPen);
	}
	
	public void setLineWidth(float width) {
		QPen qPen = this.getControl().pen();
		qPen.setWidthF(width);
		
		this.getControl().setPen(qPen);
	}
	
	public void setLineStyleSolid() {
		QPen qPen = this.getControl().pen();
		qPen.setStyle(PenStyle.SolidLine);
		
		this.getControl().setPen(qPen);
	}
	
	public void setLineStyleDot() {
		QPen qPen = this.getControl().pen();
		qPen.setStyle(PenStyle.DotLine);
		
		this.getControl().setPen(qPen);
	}
	
	public void setLineStyleDash(){
		QPen qPen = this.getControl().pen();
		qPen.setStyle(PenStyle.DashLine);
		
		this.getControl().setPen(qPen);
	}
	
	public void setLineStyleDashDot(){
		QPen qPen = this.getControl().pen();
		qPen.setStyle(PenStyle.DashDotLine);
		
		this.getControl().setPen(qPen);
	}
	
	public void setAlpha(int alpha) {
		// TODO
	}
	
	public void setAntialias(boolean enabled){
		this.getControl().setRenderHint(QPainter.RenderHint.Antialiasing, enabled);
	}
	
	public void setAdvanced(boolean advanced){
		this.setAntialias(advanced);
	}
	
	public float getFontSize() {
		return this.getControl().font().pointSize();
	}
	
	public float getFMTopLine() {
		return this.getFontSize();
	}
	
	public float getFMMiddleLine(){
		return ((this.getFMTopLine() - this.getFMBaseLine()) / 2f);
	}
	
	public float getFMBaseLine() {
		return 0;
	}
	
	public float getFMHeight() {
		return this.getControl().fontMetrics().height();
	}
	
	public float getFMWidth( String text ){
		return this.getControl().fontMetrics().width(text);
	}
	
	public QImage getImage(UIImage image){
		if( image instanceof QTImage ){
			return ((QTImage)image).getControl();
		}
		return null;
	}
	
	public QColor getColor(UIColor color){
		if( color instanceof QTColor ){
			return ((QTColor)color).getControl();
		}
		return null;
	}
	
	public QFont getFont(UIFont font){
		if( font instanceof QTFont ){
			return ((QTFont)font).getControl();
		}
		return null;
	}
	
	public QRect toRect(float x, float y, float width, float height) {
		return new QRect(toInt(x), toInt(y), toInt(width), toInt(height));
	}
	
	public int toInt(float value) {
		return Math.round(value);
	}
}
