package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.widget.UIWrapLabel;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

public class JFXWrapLabel extends JFXLabel implements UIWrapLabel {
	
	private String text;
	private Float wrapWidth;
	
	public JFXWrapLabel(JFXContainer<? extends Region> parent) {
		super(parent);
	}

	public Float getWrapWidth() {
		return wrapWidth;
	}

	public void setWrapWidth(Float wrapWidth) {
		this.wrapWidth = wrapWidth;
	}
	
	public String getText() {
		if( this.text == null ) {
			this.text = this.getControl().getText();
		}
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
		this.computeWrappedText();
	}
	
	public void computeWrappedText() {
		if( this.getWrapWidth() == null ) {
			this.getControl().setText(this.text);
		} else {
			FontMetrics fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(this.getControl().getFont());
			
			StringBuffer text = new StringBuffer();
			StringBuffer line = new StringBuffer();
			String space = (" ");
			String[] words = this.text.split(space);
			for(String word : words) {
				if( line.length() > 0 ) {
					if( fontMetrics.computeStringWidth(line.toString() + space + word) > this.getWrapWidth() ) {
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
	
	public void computePackedSize() {
		this.computeWrappedText();
		
		super.computePackedSize();
	}
}
