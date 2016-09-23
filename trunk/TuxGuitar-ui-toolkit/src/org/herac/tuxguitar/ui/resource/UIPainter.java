package org.herac.tuxguitar.ui.resource;

public interface UIPainter extends UIResource {
	
	int PATH_DRAW = 0x01;
	
	int PATH_FILL = 0x02;
	
	float THINNEST_LINE_WIDTH = 0;
	
	void initPath(int style);
	
	void initPath();
	
	void closePath();
	
	void drawString(String string, float x, float y);
	
	void drawString(String string, float x, float y, boolean isTransparent);
	
	void drawImage(UIImage image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight);
	
	void drawImage(UIImage image, float x, float y);
	
	void cubicTo(float xc1, float yc1, float xc2, float yc2, float x1, float y1);
	
	void lineTo(float x, float y);
	
	void moveTo(float x, float y);
	
	void addString(String text, float x, float y, UIFont font);
	
	void addArc(float x, float y, float width, float height, float startAngle, float arcAngle);
	
	void addOval(float x, float y, float width, float height);
	
	void addRectangle(float x,float y,float width,float height);
	
	void setFont(UIFont font);
	
	void setForeground(UIColor color);
	
	void setBackground(UIColor color);
	
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
