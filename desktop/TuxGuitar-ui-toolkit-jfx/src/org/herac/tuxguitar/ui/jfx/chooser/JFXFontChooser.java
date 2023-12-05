package org.herac.tuxguitar.ui.jfx.chooser;

import java.util.List;

import javafx.scene.text.Font;

import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.chooser.UIFontChooser;
import org.herac.tuxguitar.ui.chooser.UIFontChooserHandler;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.jfx.resource.JFXFont;
import org.herac.tuxguitar.ui.jfx.widget.JFXWindow;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIFontModel;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIListBoxSelect;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UITextField;
import org.herac.tuxguitar.ui.widget.UIWindow;

public class JFXFontChooser implements UIFontChooser {

	private static final Float[] FONT_SIZES = new Float[] {6f, 7f, 8f, 9f, 10f, 11f, 12f, 14f, 16f, 18f, 20f, 22f, 24f, 26f, 28f, 36f, 48f};
	
	private UIFactory uiFactory;
	private JFXWindow window;
	private String text;
	private UIFontModel defaultModel;
	
	public JFXFontChooser(UIFactory uiFactory, JFXWindow window) {
		this.uiFactory = uiFactory;
		this.window = window;
	}
	
	public void choose(final UIFontChooserHandler selectionHandler) {
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = this.uiFactory.createWindow(this.window, true, false);
		
		dialog.setLayout(dialogLayout);
		dialog.setText(this.text != null ? this.text : "Font Chooser");
		
		// ----------------------------------------------------------------------
		UITableLayout panelLayout = new UITableLayout(0f);
		UIPanel panel = this.uiFactory.createPanel(dialog, false);
		panel.setLayout(panelLayout);
		dialogLayout.set(panel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		// font name
		final UIListBoxSelect<String> fontNames = this.uiFactory.createListBoxSelect(panel);
		List<String> availableNames = Font.getFontNames();
		for(String fontName : availableNames){
			fontNames.addItem(new UISelectItem<String>(fontName, fontName));
		}
		fontNames.setSelectedValue(this.getInitialFontName());
		
		panelLayout.set(fontNames, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 200f, null, null);
		panelLayout.set(fontNames, UITableLayout.PACKED_HEIGHT, 200f);
		
		// font style
		final UIListBoxSelect<JFXFontStyle> fontStyles = this.uiFactory.createListBoxSelect(panel);
		fontStyles.addItem(new UISelectItem<JFXFontStyle>("Plain", new JFXFontStyle(false, false)));
		fontStyles.addItem(new UISelectItem<JFXFontStyle>("Bold", new JFXFontStyle(true, false)));
		fontStyles.addItem(new UISelectItem<JFXFontStyle>("Italic", new JFXFontStyle(false, true)));
		fontStyles.addItem(new UISelectItem<JFXFontStyle>("Bold Italic", new JFXFontStyle(true, true)));
		fontStyles.setSelectedValue(this.getInitialFontStyle());
		
		panelLayout.set(fontStyles, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true, 1, 1, 50f, null, null);
		panelLayout.set(fontStyles, UITableLayout.PACKED_HEIGHT, 200f);
		
		// font size
		final UIListBoxSelect<Float> fontSizes = this.uiFactory.createListBoxSelect(panel);
		for(Float fontSize : FONT_SIZES){
			fontSizes.addItem(new UISelectItem<Float>(fontSize.toString(), fontSize));
		}
		fontSizes.setSelectedValue(this.getInitialFontSize());
		
		panelLayout.set(fontSizes, 1, 3, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true, 1, 1, 50f, null, null);
		panelLayout.set(fontSizes, UITableLayout.PACKED_HEIGHT, 200f);
		
		// sample
		UITableLayout samplePanelLayout = new UITableLayout(0f);
		UIPanel samplePanel = this.uiFactory.createPanel(dialog, false);
		samplePanel.setLayout(samplePanelLayout);
		dialogLayout.set(samplePanel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UITextField sampleText = this.uiFactory.createTextField(samplePanel);
		sampleText.setText("Sample");
		samplePanelLayout.set(sampleText, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false, 1, 1, null, 85f, null);
		
		// event listeners
		UISelectionListener sampleTextListener = new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				UIFont uiFont = createFont(fontNames, fontSizes, fontStyles);
				if( uiFont != null ) {
					sampleText.setFont(uiFont);
				}
			}
		};
		
		fontNames.addSelectionListener(sampleTextListener);
		fontSizes.addSelectionListener(sampleTextListener);
		fontStyles.addSelectionListener(sampleTextListener);
		
		// ----------------------------------------------------- //
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = this.uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 3, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_BOTTOM, true, false);
		
		final UIButton buttonOK = this.uiFactory.createButton(buttons);
		buttonOK.setText("Ok");
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				handleSelection(selectionHandler, createFont(fontNames, fontSizes, fontStyles));
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		UIButton buttonCancel = this.uiFactory.createButton(buttons);
		buttonCancel.setText("Cancel");
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				handleSelection(selectionHandler, null);
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		dialog.pack();
		
		UIRectangle parentBounds = this.window.getBounds();
		UIRectangle dialogBounds = dialog.getBounds();
		dialogBounds.getPosition().setX(Math.max(0, parentBounds.getX() + (parentBounds.getWidth() - dialogBounds.getWidth()) / 2f));
		dialogBounds.getPosition().setY(Math.max(0, parentBounds.getY() + (parentBounds.getHeight() - dialogBounds.getHeight()) / 2f));
		dialog.setBounds(dialogBounds);
		
		dialog.open();
	}
	
	public String getInitialFontName() {
		return JFXFont.checkName(this.defaultModel != null ? this.defaultModel.getName() : null);
	}
	
	public Float getInitialFontSize() {
		return (this.defaultModel != null ? this.defaultModel.getHeight() : FONT_SIZES[5]);
	}
	
	public JFXFontStyle getInitialFontStyle() {
		return (this.defaultModel != null ? new JFXFontStyle(this.defaultModel.isBold(), this.defaultModel.isItalic()) : new JFXFontStyle(false, false));
	}
	
	public UIFont createFont(UIListBoxSelect<String> fontNames, UIListBoxSelect<Float> fontSizes, UIListBoxSelect<JFXFontStyle> fontStyles) {
		String fontName = fontNames.getSelectedValue();
		Float fontSize = fontSizes.getSelectedValue();
		JFXFontStyle fontStyle = fontStyles.getSelectedValue();
		if( fontName != null && fontSize != null && fontStyle != null ) {
			return this.uiFactory.createFont(fontName, fontSize, fontStyle.isBold(), fontStyle.isItalic());
		}
		return null;
	}
	
	public void handleSelection(UIFontChooserHandler selectionHandler, UIFont value) {
		selectionHandler.onSelectFont(value != null ? ((JFXFont) value).getControl() : null);
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public void setDefaultModel(UIFontModel defaultModel) {
		this.defaultModel = defaultModel;
	}
	
	private static class JFXFontStyle {
		
		boolean bold;
		boolean italic;
		
		public JFXFontStyle(boolean bold, boolean italic) {
			this.bold = bold;
			this.italic = italic;
		}

		public boolean isBold() {
			return this.bold;
		}

		public boolean isItalic() {
			return this.italic;
		}
		
		@Override
		public boolean equals(Object obj) {
			if( obj instanceof JFXFontStyle ) {
				return (this.hashCode() == obj.hashCode());
			}
			return super.equals(obj);
		}
		
		@Override
		public int hashCode() {
			return (JFXFontStyle.class.getName() + "-" + this.isBold() + "-" + this.isBold()).hashCode();
		}
	}
}
