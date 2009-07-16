package org.herac.tuxguitar.gui.editors.fretboard;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
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

public class FretBoardConfig {
	
	public static final int DISPLAY_TEXT_NOTE = 0x01;
	
	public static final int DISPLAY_TEXT_SCALE = 0x02;
	
	public static final int DIRECTION_RIGHT = 0;
	
	public static final int DIRECTION_LEFT = 1;
	
	private int style;
	private int direction;
	private Font font;
	private Color colorBackground;
	private Color colorString;
	private Color colorFretPoint;
	private Color colorNote;
	private Color colorScale;
	
	public FretBoardConfig(){
		super();
	}
	
	public int getStyle() {
		return this.style;
	}
	
	public Font getFont() {
		return this.font;
	}
	
	public Color getColorBackground() {
		return this.colorBackground;
	}
	
	public Color getColorString() {
		return this.colorString;
	}
	
	public Color getColorFretPoint() {
		return this.colorFretPoint;
	}
	
	public Color getColorNote() {
		return this.colorNote;
	}
	
	public Color getColorScale() {
		return this.colorScale;
	}
	
	public int getDirection(){
		return this.direction;
	}
	
	public void load(){
		Display display = TuxGuitar.instance().getDisplay();
		TGConfigManager config = TuxGuitar.instance().getConfig();
		this.style = config.getIntConfigValue(TGConfigKeys.FRETBOARD_STYLE);
		this.direction = config.getIntConfigValue(TGConfigKeys.FRETBOARD_DIRECTION, DIRECTION_RIGHT );
		this.font = new Font(display,config.getFontDataConfigValue(TGConfigKeys.FRETBOARD_FONT));
		this.colorBackground = new Color(display,config.getRGBConfigValue(TGConfigKeys.FRETBOARD_COLOR_BACKGROUND));
		this.colorString = new Color(display,config.getRGBConfigValue(TGConfigKeys.FRETBOARD_COLOR_STRING));
		this.colorFretPoint = new Color(display,config.getRGBConfigValue(TGConfigKeys.FRETBOARD_COLOR_FRET_POINT));
		this.colorNote = new Color(display,config.getRGBConfigValue(TGConfigKeys.FRETBOARD_COLOR_NOTE));
		this.colorScale = new Color(display,config.getRGBConfigValue(TGConfigKeys.FRETBOARD_COLOR_SCALE));
	}
	
	public void defaults(){
		TGConfigManager config = TuxGuitar.instance().getConfig();
		Properties defaults = config.getDefaults();
		config.setProperty(TGConfigKeys.FRETBOARD_STYLE,defaults.getProperty(TGConfigKeys.FRETBOARD_STYLE));
		config.setProperty(TGConfigKeys.FRETBOARD_DIRECTION,defaults.getProperty(TGConfigKeys.FRETBOARD_DIRECTION));
		config.setProperty(TGConfigKeys.FRETBOARD_FONT,defaults.getProperty(TGConfigKeys.FRETBOARD_FONT));
		config.setProperty(TGConfigKeys.FRETBOARD_COLOR_BACKGROUND,defaults.getProperty(TGConfigKeys.FRETBOARD_COLOR_BACKGROUND));
		config.setProperty(TGConfigKeys.FRETBOARD_COLOR_STRING,defaults.getProperty(TGConfigKeys.FRETBOARD_COLOR_STRING));
		config.setProperty(TGConfigKeys.FRETBOARD_COLOR_FRET_POINT,defaults.getProperty(TGConfigKeys.FRETBOARD_COLOR_FRET_POINT));
		config.setProperty(TGConfigKeys.FRETBOARD_COLOR_NOTE,defaults.getProperty(TGConfigKeys.FRETBOARD_COLOR_NOTE));
		config.setProperty(TGConfigKeys.FRETBOARD_COLOR_SCALE,defaults.getProperty(TGConfigKeys.FRETBOARD_COLOR_SCALE));
	}
	
	public void save(int style, int direction, FontData fontData,RGB rgbBackground,RGB rgbString,RGB rgbFretPoint,RGB rgbNote,RGB rgbScale){
		TGConfigManager config = TuxGuitar.instance().getConfig();
		config.setProperty(TGConfigKeys.FRETBOARD_STYLE,style);
		config.setProperty(TGConfigKeys.FRETBOARD_DIRECTION,direction);
		config.setProperty(TGConfigKeys.FRETBOARD_FONT,fontData);
		config.setProperty(TGConfigKeys.FRETBOARD_COLOR_BACKGROUND,rgbBackground);
		config.setProperty(TGConfigKeys.FRETBOARD_COLOR_STRING,rgbString);
		config.setProperty(TGConfigKeys.FRETBOARD_COLOR_FRET_POINT,rgbFretPoint);
		config.setProperty(TGConfigKeys.FRETBOARD_COLOR_NOTE,rgbNote);
		config.setProperty(TGConfigKeys.FRETBOARD_COLOR_SCALE,rgbScale);
	}
	
	public void saveDirection( int direction ){
		TGConfigManager config = TuxGuitar.instance().getConfig();
		config.setProperty(TGConfigKeys.FRETBOARD_DIRECTION,direction);
		
		this.direction = direction;
	}
	
	public void dispose(){
		this.font.dispose();
		this.colorBackground.dispose();
		this.colorString.dispose();
		this.colorFretPoint.dispose();
		this.colorNote.dispose();
		this.colorScale.dispose();
	}
	
	private static final int MINIMUM_CONTROL_WIDTH = 180;
	private static final int MINIMUM_BUTTON_WIDTH = 80;
	private static final int MINIMUM_BUTTON_HEIGHT = 25;
	
	public void configure(Shell shell) {
		final Shell dialog = DialogUtils.newDialog(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("fretboard.settings"));
		
		// ----------------------------------------------------------------------
		Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		group.setText(TuxGuitar.getProperty("fretboard.settings"));
		
		final FontData fontData = getFontChooser(group,TuxGuitar.getProperty("fretboard.font"),this.font.getFontData()[0]);
		
		// Color
		final RGB rgbBackground = getColorChooser(group,TuxGuitar.getProperty("fretboard.background-color"), this.colorBackground.getRGB());
		final RGB rgbString = getColorChooser(group,TuxGuitar.getProperty("fretboard.string-color"), this.colorString.getRGB());
		final RGB rgbFretPoint = getColorChooser(group,TuxGuitar.getProperty("fretboard.fretpoint-color"), this.colorFretPoint.getRGB());
		final RGB rgbNote = getColorChooser(group,TuxGuitar.getProperty("fretboard.note-color"), this.colorNote.getRGB());
		final RGB rgbScale = getColorChooser(group,TuxGuitar.getProperty("fretboard.scale-note-color"), this.colorScale.getRGB());
		
		Label directionLabel = new Label(group, SWT.NULL);
		directionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
		directionLabel.setText(TuxGuitar.getProperty("fretboard.direction"));
		
		final Combo directionCombo = new Combo(group , SWT.DROP_DOWN | SWT.READ_ONLY);
		directionCombo.setLayoutData(getAlignmentData(MINIMUM_CONTROL_WIDTH,SWT.FILL));
		directionCombo.add(TuxGuitar.getProperty("fretboard.right-mode"));
		directionCombo.add(TuxGuitar.getProperty("fretboard.left-mode"));
		directionCombo.select( this.direction );
		
		// ----------------------------------------------------------------------
		group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		group.setText(TuxGuitar.getProperty("fretboard.settings.options"));
		
		final Button displayTextNote = new Button(group,SWT.CHECK);
		displayTextNote.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		displayTextNote.setText(TuxGuitar.getProperty("fretboard.display-note-text"));
		displayTextNote.setSelection( (this.style & DISPLAY_TEXT_NOTE) != 0 );
		
		final Button displayTextScale = new Button(group,SWT.CHECK);
		displayTextScale.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		displayTextScale.setText(TuxGuitar.getProperty("fretboard.display-scale-text"));
		displayTextScale.setSelection( (this.style & DISPLAY_TEXT_SCALE) != 0 );
		
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
				int style = 0;
				style |= (displayTextNote.getSelection() ? DISPLAY_TEXT_NOTE : 0 );
				style |= (displayTextScale.getSelection() ? DISPLAY_TEXT_SCALE : 0 );
				
				int direction = directionCombo.getSelectionIndex();
				if( direction != DIRECTION_RIGHT && direction != DIRECTION_LEFT ){
					direction = DIRECTION_RIGHT;
				}
				//if( direction )
				dialog.dispose();
				
				save(style, direction, fontData, rgbBackground, rgbString, rgbFretPoint, rgbNote, rgbScale);
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
