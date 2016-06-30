package org.herac.tuxguitar.ui.jfx.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import org.herac.tuxguitar.ui.event.UILinkListener;
import org.herac.tuxguitar.ui.jfx.event.JFXLinkListenerManager;
import org.herac.tuxguitar.ui.widget.UILinkLabel;

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
			
			List<Node> nodes = new ArrayList<Node>();
			Pattern p = Pattern.compile(LINK_PATTERN,  Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
			Matcher m = p.matcher(this.text);
			while(m.find()) {
				sIndex = m.start();
				if( sIndex > eIndex ) {
					nodes.add(new Text(this.text.substring(eIndex, sIndex).trim()));
				}
				eIndex = m.end();
				
				nodes.add(this.createHyperlink(m.group(2), m.group(1)));
			}
			
			if( this.text.length() > eIndex ) {
				nodes.add(new Text(this.text.substring(eIndex, this.text.length()).trim()));
			}
			this.getControl().getChildren().addAll(nodes);
		}
	}
	
	public Hyperlink createHyperlink(final String text, final String value) {
		Hyperlink hyperLink = new Hyperlink(text);
		hyperLink.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				JFXLinkLabel.this.fireEvent(value);
			}
		});
		return hyperLink;
	}
}
