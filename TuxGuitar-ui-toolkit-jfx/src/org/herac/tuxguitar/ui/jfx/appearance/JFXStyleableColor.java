package org.herac.tuxguitar.ui.jfx.appearance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

public class JFXStyleableColor extends Parent {

	public static final Color DEFAULT_COLOR = null;
	
	private ObjectProperty<Color> color;
	
	public JFXStyleableColor(String styleName) {
		new Scene(this);
		
		this.getStylesheets().add(JFXAppearance.CSS_RESOURCE);
		this.setStyle("-color: " + styleName + ";");
		this.applyCss();
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		return StyleableProperties.STYLEABLES;
	}
	
	public Color getColor() {
		return colorProperty().get();
	}
	
	public ObjectProperty<Color> colorProperty() {
		if( this.color == null) {
			this.color = new StyleableObjectProperty<Color>(DEFAULT_COLOR) {

				@Override
				protected void invalidated() {
					super.invalidated();
				}

				@Override
				public CssMetaData<? extends Styleable, Color> getCssMetaData() {
					return StyleableProperties.COLOR;
				}

				@Override
				public Object getBean() {
					return JFXStyleableColor.this;
				}

				@Override
				public String getName() {
					return "color";
				}
			};
		}
		return this.color;
	}

	private static class StyleableProperties {

		private static final CssMetaData<JFXStyleableColor, Color> COLOR = new CssMetaData<JFXStyleableColor, Color>("-color", StyleConverter.getColorConverter(), DEFAULT_COLOR) {

			@Override
			public boolean isSettable(JFXStyleableColor n) {
				return (n.color == null || !n.color.isBound());
			}

			@Override
			public StyleableProperty<Color> getStyleableProperty(JFXStyleableColor n) {
				return (StyleableProperty<Color>) (WritableValue<Color>) n.colorProperty();
			}

		};

		private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Parent.getClassCssMetaData());
			styleables.add(COLOR);
			STYLEABLES = Collections.unmodifiableList(styleables);
		}
	}
}