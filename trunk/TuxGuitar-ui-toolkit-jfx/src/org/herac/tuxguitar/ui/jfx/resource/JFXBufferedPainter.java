package org.herac.tuxguitar.ui.jfx.resource;

import javafx.scene.image.Image;

import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;

public class JFXBufferedPainter extends JFXAbstractPainter<JFXBufferedImageHandle> implements UIPainter {
	
	public JFXBufferedPainter(JFXBufferedImageHandle handle) {
		super(handle);
	}
	
	public void addCommand(JFXBufferedPainterCommand command) {
		this.getControl().getCommands().add(command);
	}
	
	@Override
	public void initPath(final int style) {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.initPath(style);
			}
		});
	}

	@Override
	public void initPath() {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.initPath();
			}
		});
	}

	@Override
	public void closePath() {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.closePath();
			}
		});
	}

	public void clearArea(final float x, final float y, final float width, final float height) {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.clearArea(tx + x, ty + y, width, height);
			}
		});
	}
	
	@Override
	public void drawString(final String string, final float x, final float y) {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.drawString(string, tx + x, ty + y);
			}
		});
	}

	@Override
	public void drawString(final String string, final float x, final float y, final boolean isTransparent) {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.drawString(string, tx + x, ty + y, isTransparent);
			}
		});
	}

	@Override
	public void drawImage(UIImage image, final float srcX, final float srcY, final float srcWidth, final float srcHeight, final float destX, final float destY, final float destWidth, final float destHeight) {
		final UIImage imageCopy = this.toAbstractImage(image).clone();
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.drawImage(imageCopy, srcX, srcY, srcWidth, srcHeight, tx + destX, ty + destY, destWidth, destHeight);
			}
		});
	}

	@Override
	public void drawImage(UIImage image, final float x, final float y) {
		final UIImage imageCopy = this.toAbstractImage(image).clone();
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.drawImage(imageCopy, tx + x, ty + y);
			}
		});
	}

	@Override
	public void drawNativeImage(final Image image, final float srcX, final float srcY, final float srcWidth, final float srcHeight, final float destX, final float destY, final float destWidth, final float destHeight) {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.drawNativeImage(image, srcX, srcY, srcWidth, srcHeight, tx + destX, ty + destY, destWidth, destHeight);
			}
		});
	}

	@Override
	public void drawNativeImage(final Image image, final float x, final float y) {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.drawNativeImage(image, tx + x, ty + y);
			}
		});
	}
	
	@Override
	public void cubicTo(final float xc1, final float yc1, final float xc2, final float yc2, final float x1, final float y1) {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.cubicTo(tx + xc1, ty + yc1, tx + xc2, ty + yc2, tx + x1, ty + y1);
			}
		});
	}

	@Override
	public void lineTo(final float x, final float y) {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.lineTo(tx + x, ty + y);
			}
		});
	}

	@Override
	public void moveTo(final float x, final float y) {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.moveTo(tx + x, ty + y);
			}
		});
	}

	@Override
	public void addString(final String text, final float x, final float y, final UIFont font) {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.addString(text, tx + x, ty + y, font);
			}
		});
	}

	@Override
	public void addArc(final float x, final float y, final float width, final float height, final float startAngle, final float arcAngle) {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.addArc(tx + x, ty + y, width, height, startAngle, arcAngle);
			}
		});
	}

	@Override
	public void addOval(final float x, final float y, final float width, final float height) {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.addOval(tx + x, ty + y, width, height);
			}
		});
	}

	@Override
	public void addRectangle(final float x, final float y, final float width, final float height) {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.addRectangle(tx + x, ty + y, width, height);
			}
		});
	}

	@Override
	public void setFont(UIFont font) {
		super.setFont(font);
		
		final UIFont fontCopy = new JFXFont(font.getName(), font.getHeight(), font.isBold(), font.isItalic());
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.setFont(fontCopy);
			}
		});
	}

	@Override
	public void setForeground(UIColor color) {
		final UIColor colorCopy = new JFXColor(color.getRed(), color.getGreen(), color.getBlue());
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.setForeground(colorCopy);
			}
		});
	}

	@Override
	public void setBackground(UIColor color) {
		final UIColor colorCopy = new JFXColor(color.getRed(), color.getGreen(), color.getBlue());
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.setBackground(colorCopy);
			}
		});
	}

	@Override
	public void setLineWidth(final float lineWidth) {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.setLineWidth(lineWidth);
			}
		});
	}

	@Override
	public void setLineStyleSolid() {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.setLineStyleSolid();
			}
		});
	}

	@Override
	public void setLineStyleDot() {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.setLineStyleDot();
			}
		});
	}

	@Override
	public void setLineStyleDash() {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.setLineStyleDash();
			}
		});
	}

	@Override
	public void setLineStyleDashDot() {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.setLineStyleDashDot();
			}
		});
	}

	@Override
	public void setAlpha(final int alpha) {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.setAlpha(alpha);
			}
		});
	}

	@Override
	public void setAntialias(final boolean enabled) {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.setAntialias(enabled);
			}
		});
	}

	@Override
	public void setAdvanced(final boolean advanced) {
		this.addCommand(new JFXBufferedPainterCommand() {
			public void paint(JFXAbstractPainter<?> painter, float tx, float ty) {
				painter.setAdvanced(advanced);
			}
		});
	}
}
