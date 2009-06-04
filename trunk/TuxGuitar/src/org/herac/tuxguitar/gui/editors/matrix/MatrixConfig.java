package org.herac.tuxguitar.gui.editors.matrix;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.config.TGConfigKeys;
import org.herac.tuxguitar.gui.system.config.TGConfigManager;
import org.herac.tuxguitar.gui.util.DialogUtils;

public class MatrixConfig {
	
	private Font font;
	private Color[] colorLines;
	private Color colorForeground;
	private Color colorBorder;
	private Color colorPosition;
	private Color colorNote;
	private Color colorPlay;
	
	public MatrixConfig(){
		super();
	}
	
	public Font getFont() {
		return this.font;
	}
	
	public Color getColorForeground() {
		return this.colorForeground;
	}
	
	public Color getColorBorder() {
		return this.colorBorder;
	}
	
	public Color getColorPosition() {
		return this.colorPosition;
	}
	
	public Color getColorNote() {
		return this.colorNote;
	}
	
	public Color getColorPlay() {
		return this.colorPlay;
	}
	
	public Color[] getColorLines() {
		return this.colorLines;
	}
	
	public Color getColorLine(int index) {
		return this.colorLines[index];
	}
	
	public void load(){
		Display display = TuxGuitar.instance().getDisplay();
		TGConfigManager config = TuxGuitar.instance().getConfig();
		this.font = new Font(display,config.getFontDataConfigValue(TGConfigKeys.MATRIX_FONT));
		this.colorForeground = new Color(display,config.getRGBConfigValue(TGConfigKeys.MATRIX_COLOR_FOREGROUND));
		this.colorBorder = new Color(display,config.getRGBConfigValue(TGConfigKeys.MATRIX_COLOR_BORDER));
		this.colorPosition = new Color(display,config.getRGBConfigValue(TGConfigKeys.MATRIX_COLOR_POSITION));
		this.colorNote = new Color(display,config.getRGBConfigValue(TGConfigKeys.MATRIX_COLOR_NOTE));
		this.colorPlay = new Color(display,config.getRGBConfigValue(TGConfigKeys.MATRIX_COLOR_PLAY_NOTE));
		this.colorLines = new Color[]{
				new Color(display,config.getRGBConfigValue(TGConfigKeys.MATRIX_COLOR_LINE_1)),
				new Color(display,config.getRGBConfigValue(TGConfigKeys.MATRIX_COLOR_LINE_2)),
				new Color(display,config.getRGBConfigValue(TGConfigKeys.MATRIX_COLOR_LINE_3)),
		};
	}
	
	public void defaults(){
		TGConfigManager config = TuxGuitar.instance().getConfig();
		Properties defaults = config.getDefaults();
		config.setProperty(TGConfigKeys.MATRIX_FONT,defaults.getProperty(TGConfigKeys.MATRIX_FONT));
		config.setProperty(TGConfigKeys.MATRIX_COLOR_FOREGROUND,defaults.getProperty(TGConfigKeys.MATRIX_COLOR_FOREGROUND));
		config.setProperty(TGConfigKeys.MATRIX_COLOR_BORDER,defaults.getProperty(TGConfigKeys.MATRIX_COLOR_BORDER));
		config.setProperty(TGConfigKeys.MATRIX_COLOR_POSITION,defaults.getProperty(TGConfigKeys.MATRIX_COLOR_POSITION));
		config.setProperty(TGConfigKeys.MATRIX_COLOR_NOTE,defaults.getProperty(TGConfigKeys.MATRIX_COLOR_NOTE));
		config.setProperty(TGConfigKeys.MATRIX_COLOR_PLAY_NOTE,defaults.getProperty(TGConfigKeys.MATRIX_COLOR_PLAY_NOTE));
		config.setProperty(TGConfigKeys.MATRIX_COLOR_LINE_1,defaults.getProperty(TGConfigKeys.MATRIX_COLOR_LINE_1));
		config.setProperty(TGConfigKeys.MATRIX_COLOR_LINE_2,defaults.getProperty(TGConfigKeys.MATRIX_COLOR_LINE_2));
		config.setProperty(TGConfigKeys.MATRIX_COLOR_LINE_3,defaults.getProperty(TGConfigKeys.MATRIX_COLOR_LINE_3));
	}
	
	public void save(FontData fontData,
					 RGB rgbForeground,
					 RGB rgbBorder,
					 RGB rgbPosition,
					 RGB rgbNote,
					 RGB rgbPlay,
					 RGB rgbLines[]){
		TGConfigManager config = TuxGuitar.instance().getConfig();
		
		config.setProperty(TGConfigKeys.MATRIX_FONT,fontData);
		config.setProperty(TGConfigKeys.MATRIX_COLOR_FOREGROUND,rgbForeground);
		config.setProperty(TGConfigKeys.MATRIX_COLOR_BORDER,rgbBorder);
		config.setProperty(TGConfigKeys.MATRIX_COLOR_POSITION,rgbPosition);
		config.setProperty(TGConfigKeys.MATRIX_COLOR_NOTE,rgbNote);
		config.setProperty(TGConfigKeys.MATRIX_COLOR_PLAY_NOTE,rgbPlay);
		config.setProperty(TGConfigKeys.MATRIX_COLOR_LINE_1,rgbLines[0]);
		config.setProperty(TGConfigKeys.MATRIX_COLOR_LINE_2,rgbLines[1]);
		config.setProperty(TGConfigKeys.MATRIX_COLOR_LINE_3,rgbLines[2]);
	}
	
	public void dispose(){
		dispose(this.font);
		dispose(this.colorForeground);
		dispose(this.colorBorder);
		dispose(this.colorPosition);
		dispose(this.colorNote);
		dispose(this.colorPlay);
		dispose(this.colorLines);
	}
	
	protected void dispose(Resource[] resources){
		if(resources != null){
			for(int i = 0; i < resources.length; i ++){
				dispose(resources[i]);
			}
		}
	}
	
	protected void dispose(Resource resource){
		if(resource != null){
			resource.dispose();
		}
	}
	
	private static final int MINIMUM_CONTROL_WIDTH = 180;
	private static final int MINIMUM_BUTTON_WIDTH = 80;
	private static final int MINIMUM_BUTTON_HEIGHT = 25;
	
	public void configure(Shell shell) {
		final Shell dialog = DialogUtils.newDialog(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("matrix.settings"));
		
		// ----------------------------------------------------------------------
		Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		group.setText(TuxGuitar.getProperty("matrix.settings"));
		
		// fonts
		final FontData fontData = getFontChooser(group,TuxGuitar.getProperty("matrix.font"),this.font.getFontData()[0]);
		
		// colors
		final RGB rgbForeground = getColorChooser(group,TuxGuitar.getProperty("matrix.foreground-color"), this.colorForeground.getRGB());
		
		final RGB rgbLines[] = new RGB[]{
				getColorChooser(group,TuxGuitar.getProperty("matrix.line-color-1"), this.colorLines[0].getRGB()),
				getColorChooser(group,TuxGuitar.getProperty("matrix.line-color-2"), this.colorLines[1].getRGB()),
				getColorChooser(group,TuxGuitar.getProperty("matrix.line-color-over"), this.colorLines[2].getRGB()),
		};
		
		final RGB rgbBorder = getColorChooser(group,TuxGuitar.getProperty("matrix.border-color"), this.colorBorder.getRGB());
		final RGB rgbPosition = getColorChooser(group,TuxGuitar.getProperty("matrix.position-color"), this.colorPosition.getRGB());
		final RGB rgbNote = getColorChooser(group,TuxGuitar.getProperty("matrix.note-color"), this.colorNote.getRGB());
		final RGB rgbPlay = getColorChooser(group,TuxGuitar.getProperty("matrix.play-note-color"), this.colorPlay.getRGB());
		
		// ------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(3, false));
		buttons.setLayoutData(new GridData(SWT.END, SWT.FILL, true, true));
		
		final Button buttonDefaults = new Button(buttons, SWT.PUSH);
		buttonDefaults.setText(TuxGuitar.getProperty("defaults"));
		buttonDefaults.setLayoutData(getButtonData());
		buttonDefaults.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
				defaults();
				applyChanges();
			}
		});
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
				save(fontData, rgbForeground, rgbBorder, rgbPosition, rgbNote, rgbPlay, rgbLines);
				applyChanges();
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonData());
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
			}
		});
		
		dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
	}
	
	protected GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = MINIMUM_BUTTON_WIDTH;
		data.minimumHeight = MINIMUM_BUTTON_HEIGHT;
		return data;
	}
	
	protected void applyChanges(){
		this.dispose();
		this.load();
	}
	
	private RGB getColorChooser(final Composite parent,String title,RGB rgb){
		Label label = new Label(parent, SWT.NULL);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
		label.setText(title);
		
		ButtonColor button = new ButtonColor(parent, SWT.PUSH, TuxGuitar.getProperty("choose"));
		button.setLayoutData(getAlignmentData(MINIMUM_CONTROL_WIDTH,SWT.FILL));
		button.loadColor(rgb);
		
		return button.getValue();
	}
	
	private FontData getFontChooser(final Composite parent,String title,FontData fontData){
		final FontData selection = new FontData(fontData.getName(),fontData.getHeight(),fontData.getStyle());
		
		Label label = new Label(parent, SWT.NULL);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
		label.setText(title);
		
		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(getAlignmentData(MINIMUM_CONTROL_WIDTH,SWT.FILL));
		button.setText(TuxGuitar.getProperty("choose"));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				Font font = new Font(parent.getDisplay(),selection);
				FontDialog fontDialog = new FontDialog(parent.getShell());
				fontDialog.setFontList(font.getFontData());
				FontData fd = fontDialog.open();
				if(fd != null){
					selection.setName( fd.getName() );
					selection.setHeight( fd.getHeight() );
					selection.setStyle( fd.getStyle() );
				}
				font.dispose();
			}
		});
		return selection;
	}
	
	private GridData getAlignmentData(int minimumWidth,int horizontalAlignment){
		GridData data = new GridData();
		data.minimumWidth = minimumWidth;
		data.horizontalAlignment = horizontalAlignment;
		data.verticalAlignment = SWT.DEFAULT;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		return data;
	}
	
	private class ButtonColor {
		protected Button button;
		protected Color color;
		protected RGB value;
		
		public ButtonColor(Composite parent, int style, String text){
			this.value = new RGB(0,0,0);
			this.button = new Button(parent, style);			
			this.button.setText(text);
			this.addListeners();
		}
		
		protected void setLayoutData(Object layoutData){
			this.button.setLayoutData(layoutData);
		}
		
		protected void loadColor(RGB rgb){
			this.value.red = rgb.red;
			this.value.green = rgb.green;
			this.value.blue = rgb.blue;
			
			Color color = new Color(this.button.getDisplay(), this.value);
			this.button.setForeground(color);
			this.disposeColor();
			this.color = color;
		}
		
		protected void disposeColor(){
			if(this.color != null && !this.color.isDisposed()){
				this.color.dispose();
				this.color = null;
			}
		}
		
		private void addListeners(){
			this.button.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					ColorDialog dlg = new ColorDialog(ButtonColor.this.button.getShell());
					dlg.setRGB(ButtonColor.this.value);
					dlg.setText(TuxGuitar.getProperty("choose-color"));
					RGB result = dlg.open();
					if (result != null) {
						ButtonColor.this.loadColor(result);
					}
				}
			});
			this.button.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					ButtonColor.this.disposeColor();
				}
			});
		}
		
		protected RGB getValue(){
			return this.value;
		}
	}
}
