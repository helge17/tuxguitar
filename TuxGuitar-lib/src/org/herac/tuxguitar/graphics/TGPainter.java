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
	
	public void cubicTo(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5);
	
	public void lineTo(float arg0, float arg1);
	
	public void moveTo(float arg0, float arg1);
	
	public void addString(String arg0, float arg1, float arg2, TGFont arg3);
	
	public void addArc(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5);
	
	public void addOval(float arg0, float arg1, float arg2, float arg3);
	
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
	
	public float getFMAscent();
	
	public float getFMDescent();
	
	public float getFMWidth( String text );
	
}
