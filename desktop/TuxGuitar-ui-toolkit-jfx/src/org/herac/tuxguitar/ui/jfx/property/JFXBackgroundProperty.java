package org.herac.tuxguitar.ui.jfx.property;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import org.herac.tuxguitar.ui.jfx.resource.JFXColor;
import org.herac.tuxguitar.ui.resource.UIColor;

public class JFXBackgroundProperty {
	
	private Background defaultValue;
	private ObjectProperty<Background> target;
	private ObjectProperty<Background> bind;
	
	public JFXBackgroundProperty(ObjectProperty<Background> target) {
		this.defaultValue = target.get();
		this.target = target;
		this.bind = new SimpleObjectProperty<Background>(this.defaultValue);
	}
	
	public void bind() {
		if(!this.target.isBound()) {
			this.target.bind(this.bind);
		}
	}
	
	public void unbind() {
		this.target.unbind();
		this.target.set(this.defaultValue);
	}
	
	public void set(UIColor color) {
		if( color == null ) {
			this.unbind();
		} else {
			this.bind();
			this.bind.set(this.createBackground(((JFXColor) color).getHandle()));
		}
	}
	
	public UIColor get() {
		Background background = this.target.get();
		if( background != null && background.getFills() != null ) {
			for(BackgroundFill fill : background.getFills()) {
				if( fill.getFill() instanceof Color ) {
					return new JFXColor((Color) fill.getFill());
				}
			}
		}
		return null;
	}
	
	public Background createBackground(Color color) {
		List<BackgroundFill> fills = new ArrayList<BackgroundFill>();
		if( this.defaultValue != null && this.defaultValue.getFills() != null ) {
			for(BackgroundFill fill : this.defaultValue.getFills()) {
				fills.add(new BackgroundFill(color, fill.getRadii(), fill.getInsets()));
			}
		}
		if( fills.isEmpty() ) {
			fills.add(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY));
		}
		
		return new Background(fills.toArray(new BackgroundFill[fills.size()]));
	}
}
