package org.herac.tuxguitar.ui.jfx.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import org.herac.tuxguitar.ui.event.UILinkListener;
import org.herac.tuxguitar.ui.jfx.event.JFXLinkListenerManager;
import org.herac.tuxguitar.ui.widget.UILinkLabel;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

public class JFXLinkLabel extends JFXRegion<TextFlow> implements UILinkLabel {
	
	private static final String LINK_PATTERN = ("<a[^>]+href=[\"|'](.*?)[\"|']+[^>]*>(.+?)<\\/a>");
	
	private JFXLinkListenerManager linkListener;
	private String text;
	private Float wrapWidth;
	
	public JFXLinkLabel(JFXContainer<? extends Region> parent) {
		super(new TextFlow(), parent);
		
		this.linkListener = new JFXLinkListenerManager(this);
	}
	
	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
		this.updateTextFlow();
	}
	
	public Float getWrapWidth() {
		return wrapWidth;
	}

	public void setWrapWidth(Float wrapWidth) {
		this.wrapWidth = wrapWidth;
		this.updateTextFlow();
	}
	
	public void fireEvent(String link) {
		this.linkListener.fireEvent(link);
	}
	
	public void addLinkListener(UILinkListener listener) {
		this.linkListener.addListener(listener);
	}

	public void removeLinkListener(UILinkListener listener) {
		this.linkListener.removeListener(listener);
	}
	
	public void updateTextFlow() {
		if(!this.getControl().getChildren().isEmpty() ) {
			this.getControl().getChildren().clear();
		}
		if( this.text != null ) {
			int sIndex = 0;
			int eIndex = 0;
			
			FontMetrics fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(Font.getDefault());
			StringBuilder text = new StringBuilder();
			StringBuilder line = new StringBuilder();
			
			List<Node> nodes = new ArrayList<Node>();
			Pattern p = Pattern.compile(LINK_PATTERN,  Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
			Matcher m = p.matcher(this.text);
			while(m.find()) {
				sIndex = m.start();
				if( sIndex > eIndex ) {
					nodes.add(new Text(this.computeWrappedText(fontMetrics, text, line, this.text.substring(eIndex, sIndex))));
				}
				eIndex = m.end();
				
				nodes.add(this.createHyperlink(this.computeWrappedText(fontMetrics, text, line, m.group(2)), m.group(1)));
			}
			
			if( this.text.length() > eIndex ) {
				nodes.add(new Text(this.computeWrappedText(fontMetrics, text, line, this.text.substring(eIndex, this.text.length()))));
			}
			this.getControl().getChildren().addAll(nodes);
			this.getControl().applyCss();
		}
	}
	
	public Hyperlink createHyperlink(final String text, final String value) {
		Hyperlink hyperLink = new Hyperlink(text);
		hyperLink.setPadding(new Insets(0));
		hyperLink.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				JFXLinkLabel.this.fireEvent(value);
			}
		});
		return hyperLink;
	}
	
	public String computeWrappedText(FontMetrics fontMetrics, StringBuilder textBuffer, StringBuilder lineBuffer, String text) {
		if( this.getWrapWidth() != null && text != null && !text.isEmpty() ) {
			int start = (textBuffer.length() + lineBuffer.length());
			
			String space = (" ");
			String[] words = text.split(space);
			for(String word : words) {
				if( lineBuffer.length() > 0 ) {
					if( fontMetrics.computeStringWidth(lineBuffer.toString() + space + word) > this.getWrapWidth() ) {
						textBuffer.append(lineBuffer);
						textBuffer.append("\r\n");
						lineBuffer.setLength(0);
					} else {
						lineBuffer.append(space);
					}
				}
				lineBuffer.append(word);
			}
			String fullText = (textBuffer.toString() + lineBuffer.toString());
			String wrappedText = fullText.substring(start, fullText.length());
			
			return wrappedText;
		}
		return text;
	}
}
