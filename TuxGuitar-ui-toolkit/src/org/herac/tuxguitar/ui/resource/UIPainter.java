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
	
	void cubicTo(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5);
	
	void lineTo(float arg0, float arg1);
	
	void moveTo(float arg0, float arg1);
	
	void addString(String arg0, float arg1, float arg2, UIFont arg3);
	
	void addArc(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5);
	
	void addOval(float arg0, float arg1, float arg2, float arg3);
	
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
	
	float getFMAscent();
	
	float getFMDescent();
	
	float getFMWidth( String text );
}
