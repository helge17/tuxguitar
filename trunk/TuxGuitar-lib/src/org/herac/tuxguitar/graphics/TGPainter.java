package org.herac.tuxguitar.graphics;

public interface TGPainter extends TGResourceFactory, TGResource {
	
	public static final int PATH_DRAW = 0x01;
	
	public static final int PATH_FILL = 0x02;
	
	public static final float THINNEST_LINE_WIDTH = 0;
	
	public void initPath(int style);
	
	public void initPath();
	
	public void closePath();
	
	public void drawString(String string, float x, float y);
	
	public void drawString(String string, float x, float y, boolean isTransparent);
	
	public void drawImage(TGImage image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight);
	
	public void drawImage(TGImage image, float x, float y);
	
	public void cubicTo(float xc1, float yc1, float xc2, float yc2, float x1, float y1);
	
	public void lineTo(float x, float y);
	
	public void moveTo(float x, float y);
	
	public void addString(String text, float x, float y, TGFont font);
	
	public void addArc(float x, float y, float width, float height, float startAngle, float arcAngle);
	
	public void addOval(float x, float y, float width, float height);
	
	public void addRectangle(float x,float y,float width,float height);
	
	public void setFont(TGFont font);
	
	public void setForeground(TGColor color);
	
	public void setBackground(TGColor color);
	
	public void setLineWidth(float lineWidth);
	
	public void setLineStyleSolid();
	
	public void setLineStyleDot();
	
	public void setLineStyleDash();
	
	public void setLineStyleDashDot();
	
	public void setAlpha(int alpha);
	
	public void setAntialias(boolean enabled);
	
	public void setAdvanced(boolean advanced);
	
	public float getFontSize();
	
	public float getFMBaseLine();
	
	public float getFMTopLine();
	
	public float getFMMiddleLine();
	
	public float getFMHeight();
	
	public float getFMWidth( String text );
	
}
