package org.herac.tuxguitar.ui.resource;

public interface UIPainter extends UIResource {
	
	int PATH_DRAW = 0x01;
	
	int PATH_FILL = 0x02;
	
	float THINNEST_LINE_WIDTH = 0;
	
	void initPath(int style);
	
	void initPath();
	
	void closePath();
	
	void drawString(String string, float x, float y);
	
	void drawImage(UIImage image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight);
	
	void drawImage(UIImage image, float x, float y);
	
	void cubicTo(float xc1, float yc1, float xc2, float yc2, float x1, float y1);
	
	void lineTo(float x, float y);
	
	void moveTo(float x, float y);
	
	void addCircle(float x, float y, float width);
	
	void addRectangle(float x,float y,float width,float height);
	
	default void addRoundedRectangle(float x1, float y1, float w, float h, float br) {
		//control point
		float cp =  (float) (br - br * 4.*(Math.sqrt(2.)-1.)/3.);
		float x2 = x1 + w;
		float y2 = y1 + h;
		
		moveTo(x1 + br, y1);
		cubicTo(x1 + cp, y1, x1, y1 + cp, x1, y1 + br);
		lineTo(x1, y2 - br);
		cubicTo(x1, y2 - cp, x1 + cp, y2, x1 + br, y2);
		lineTo(x2 - br, y2);
		cubicTo(x2 - cp, y2, x2, y2 - cp, x2, y2 - br);
		lineTo(x2, y1 + br);
		cubicTo(x2, y1 + cp, x2 - cp, y1, x2 - br, y1);
		lineTo(x1 + br, y1);
	}
	
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
	
	float getFontSize();
	
	float getFMBaseLine();
	
	float getFMTopLine();
	
	float getFMMiddleLine();
	
	float getFMHeight();
	
	float getFMWidth( String text );
}
