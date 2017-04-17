package org.herac.tuxguitar.graphics;

public interface TGPainter extends TGResourceFactory, TGResource {
	
	static final int PATH_DRAW = 0x01;
	
	static final int PATH_FILL = 0x02;
	
	static final float THINNEST_LINE_WIDTH = 0;
	
	void initPath(int style);
	
	void initPath();
	
	void closePath();
	
	void drawString(String string, float x, float y);
	
	void drawString(String string, float x, float y, boolean isTransparent);
	
	void drawImage(TGImage image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight);
	
	void drawImage(TGImage image, float x, float y);
	
	void cubicTo(float xc1, float yc1, float xc2, float yc2, float x1, float y1);
	
	void lineTo(float x, float y);
	
	void moveTo(float x, float y);
	
	void addCircle(float x, float y, float width);
	
	void addRectangle(float x,float y,float width,float height);
	
	void setFont(TGFont font);
	
	void setForeground(TGColor color);
	
	void setBackground(TGColor color);
	
	void setLineWidth(float lineWidth);
	
	void setLineStyleSolid();
	
	void setLineStyleDot();
	
	void setLineStyleDash();
	
	void setLineStyleDashDot();
	
	void setAlpha(int alpha);
	
	void setAntialias(boolean enabled);
	
	void setAdvanced(boolean advanced);
	
	float getFontSize();
	
	float getFMBaseLine();
	
	float getFMTopLine();
	
	float getFMMiddleLine();
	
	float getFMHeight();
	
	float getFMWidth( String text );
	
}
