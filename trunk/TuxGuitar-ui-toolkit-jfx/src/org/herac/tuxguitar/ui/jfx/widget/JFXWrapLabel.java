package org.herac.tuxguitar.ui.jfx.widget;

import org.herac.tuxguitar.ui.jfx.resource.JFXFont;
import org.herac.tuxguitar.ui.jfx.resource.JFXFontMetrics;
import org.herac.tuxguitar.ui.widget.UIWrapLabel;

import javafx.scene.layout.Region;
import javafx.scene.text.Font;

public class JFXWrapLabel extends JFXLabel implements UIWrapLabel {
	
	private String text;
	
	public JFXWrapLabel(JFXContainer<? extends Region> parent) {
		super(parent);
	}

	public String getText() {
		if( this.text == null ) {
			this.text = this.getControl().getText();
		}
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
		this.computeWrappedText(this.getPackedSize().getWidth());
	}
	
	public void computeWrappedText(Float fixedWidth) {
		if( fixedWidth == null || fixedWidth <= 0 ) {
			this.getControl().setText(this.text);
		} else {
			JFXFont font = (this.getFont() != null ? (JFXFont) this.getFont() : new JFXFont(Font.getDefault()));
			JFXFontMetrics fontMetrics = font.getFontMetrics();
			
			StringBuffer text = new StringBuffer();
			StringBuffer line = new StringBuffer();
			String space = (" ");
			String[] words = this.text.split(space);
			for(String word : words) {
				if( line.length() > 0 ) {
					if( fontMetrics.getWidth(line.toString() + space + word) > fixedWidth ) {
						text.append(line);
						text.append("\n");
						line = new StringBuffer();
					} else {
						line.append(space);
					}
				}
				line.append(word);
			}
			if( line.length() > 0 ) {
				text.append(line);
			}
			
			this.getControl().setText(text.toString());
		}
	}
	
	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		this.computeWrappedText(fixedWidth);
		
		super.computePackedSize(fixedWidth, fixedHeight);
	}
}
