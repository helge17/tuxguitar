package org.herac.tuxguitar.awt.graphics;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;

public class AWTBufferedPainter extends AWTAbstractPainter {

	private List<GTKPainterCommand> commands;

	public AWTBufferedPainter() {
		this.commands = new ArrayList<GTKPainterCommand>();
	}
	
	public void initPath(final int style) {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.initPath(style);
			}
		});
	}

	public void initPath() {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.initPath();
			}
		});
	}

	public void closePath() {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.closePath();
			}
		});
	}

	public void drawString(final String string, final float x, final float y) {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.drawString(string, x, y);
			}
		});
	}

	public void drawImage(final UIImage image, final float srcX, final float srcY, final float srcWidth, final float srcHeight, final float destX, final float destY, final float destWidth, final float destHeight) {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.drawImage(image, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight);
			}
		});
	}

	public void drawImage(final UIImage image, final float x, final float y) {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.drawImage(image, x, y);
			}
		});
	}

	public void cubicTo(final float xc1, final float yc1, final float xc2, final float yc2, final float x1, final float y1) {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.cubicTo(xc1, yc1, xc2, yc2, x1, y1);
			}
		});
	}

	public void lineTo(final float x, final float y) {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.lineTo(x, y);
			}
		});
	}

	public void moveTo(final float x, final float y) {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.moveTo(x, y);
			}
		});
	}

	public void addCircle(final float x, final float y, final float width) {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.addCircle(x, y, width);
			}
		});
	}

	public void addRectangle(final float x, final float y, final float width, final float height) {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.addRectangle(x, y, width, height);
			}
		});
	}

	public void setFont(final UIFont font) {
		super.setFont(font);
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.setFont(font);
			}
		});
	}

	public void setForeground(final UIColor color) {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.setForeground(color);
			}
		});
	}

	public void setBackground(final UIColor color) {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.setBackground(color);
			}
		});
	}

	public void setLineWidth(final float lineWidth) {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.setLineWidth(lineWidth);
			}
		});
	}

	public void setLineStyleSolid() {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.setLineStyleSolid();
			}
		});
	}

	public void setLineStyleDot() {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.setLineStyleDot();
			}
		});
	}

	public void setLineStyleDash() {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.setLineStyleDash();
			}
		});
	}

	public void setLineStyleDashDot() {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.setLineStyleDashDot();
			}
		});
	}

	public void setAlpha(final int alpha) {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.setAlpha(alpha);
			}
		});
	}

	public void setAntialias(final boolean enabled) {
		this.commands.add(new GTKPainterCommand() {
			public void process(UIPainter painter) {
				painter.setAntialias(enabled);
			}
		});
	}
	
	public void process(UIPainter painter) {
		for (GTKPainterCommand command : this.commands) {
			command.process(painter);
		}
	}
	
	private interface GTKPainterCommand {

		void process(UIPainter painter);
	}
}
