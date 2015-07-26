package org.herac.tuxguitar.app.view.dialog.marker;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.marker.TGModifyMarkerAction;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMarker;
import org.herac.tuxguitar.song.models.TGMeasureHeader;

public class TGMarkerEditor {
	
	private static final int MINIMUM_CONTROL_WIDTH = 180;
	private static final int MINIMUM_BUTTON_WIDTH = 80;
	private static final int MINIMUM_BUTTON_HEIGHT = 25;
	
	private TGViewContext context;
	private TGMarker marker;
	private Shell dialog;
	private Spinner measureSpinner;
	private Text titleText;
	private Button colorButton;
	private Color colorButtonValue;
	
	public TGMarkerEditor(TGViewContext context) {
		this.context = context;
	}
	
	public void show() {
		this.createEditableMarker();
		
		final Shell parent = this.context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		this.dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.dialog.setLayout(new GridLayout());
		this.dialog.setText(TuxGuitar.getProperty("marker"));
		
		// ----------------------------------------------------------------------
		Group group = new Group(this.dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		group.setText(TuxGuitar.getProperty("marker"));
		
		// Measure Number
		final int measureCount = TuxGuitar.getInstance().getDocumentManager().getSong().countMeasureHeaders();
		Label measureLabel = new Label(group, SWT.NULL);
		measureLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true,true));
		measureLabel.setText(TuxGuitar.getProperty("measure"));
		
		this.measureSpinner = new Spinner(group, SWT.BORDER);
		this.measureSpinner.setLayoutData(getAlignmentData(MINIMUM_CONTROL_WIDTH,SWT.FILL));
		this.measureSpinner.setMinimum(1);
		this.measureSpinner.setMaximum(measureCount);
		this.measureSpinner.setSelection(this.marker.getMeasure());
		this.measureSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int selection = TGMarkerEditor.this.measureSpinner.getSelection();
				if (selection < 1) {
					TGMarkerEditor.this.measureSpinner.setSelection(1);
				} else if (selection > measureCount) {
					TGMarkerEditor.this.measureSpinner.setSelection(measureCount);
				}
			}
		});
		
		// Title
		Label titleLabel = new Label(group, SWT.NULL);
		titleLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
		titleLabel.setText(TuxGuitar.getProperty("title"));
		this.titleText = new Text(group, SWT.BORDER);
		this.titleText.setLayoutData(getAlignmentData(MINIMUM_CONTROL_WIDTH,SWT.FILL));
		this.titleText.setText(this.marker.getTitle());
		
		// Color
		Label colorLabel = new Label(group, SWT.NULL);
		colorLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
		colorLabel.setText(TuxGuitar.getProperty("color"));
		this.colorButton = new Button(group, SWT.PUSH);
		this.colorButton.setLayoutData(getAlignmentData(MINIMUM_CONTROL_WIDTH,SWT.FILL));
		this.colorButton.setText(TuxGuitar.getProperty("choose"));
		this.colorButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ColorDialog dlg = new ColorDialog(TGMarkerEditor.this.dialog);
				dlg.setRGB(TGMarkerEditor.this.dialog.getDisplay().getSystemColor(SWT.COLOR_BLACK).getRGB());
				dlg.setText(TuxGuitar.getProperty("choose-color"));
				RGB rgb = dlg.open();
				if (rgb != null) {
					TGMarkerEditor.this.marker.getColor().setR(rgb.red);
					TGMarkerEditor.this.marker.getColor().setG(rgb.green);
					TGMarkerEditor.this.marker.getColor().setB(rgb.blue);
					TGMarkerEditor.this.setButtonColor();
				}
			}
		});
		this.colorButton.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				TGMarkerEditor.this.disposeButtonColor();
			}
		});
		this.setButtonColor();
		
		// ------------------BUTTONS--------------------------
		Composite buttons = new Composite(this.dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2, false));
		buttons.setLayoutData(new GridData(SWT.END, SWT.FILL, true, true));
		
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = MINIMUM_BUTTON_WIDTH;
		data.minimumHeight = MINIMUM_BUTTON_HEIGHT;
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(data);
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				updateMarker();
				TGMarkerEditor.this.dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(data);
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				TGMarkerEditor.this.dialog.dispose();
			}
		});
		
		this.dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(this.dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
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
	
	protected void setButtonColor(){
		Color color = new Color(this.dialog.getDisplay(), this.marker.getColor().getR(), this.marker.getColor().getG(), this.marker.getColor().getB());
		
		this.colorButton.setForeground( color );
		this.disposeButtonColor();
		this.colorButtonValue = color;
	}
	
	protected void disposeButtonColor(){
		if(this.colorButtonValue != null && !this.colorButtonValue.isDisposed()){
			this.colorButtonValue.dispose();
			this.colorButtonValue = null;
		}
	}
	
	protected void updateMarker() {
		this.marker.setMeasure(this.measureSpinner.getSelection());
		this.marker.setTitle(this.titleText.getText());
		
		TGSongManager songManager = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGModifyMarkerAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER, this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER));
		tgActionProcessor.setAttribute(TGModifyMarkerAction.ATTRIBUTE_MODIFIED_MARKER, this.marker.clone(songManager.getFactory()));
		tgActionProcessor.process();
	}
	
	private void createEditableMarker() {
		TGSongManager songManager = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		TGMarker marker = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MARKER);
		if( marker == null ) {
			TGMeasureHeader header = this.context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
			
			marker = songManager.getFactory().newMarker();
			marker.setMeasure(header.getNumber());
		}
		
		this.marker = marker.clone(songManager.getFactory());
	}
}
