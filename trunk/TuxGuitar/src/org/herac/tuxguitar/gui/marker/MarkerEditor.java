package org.herac.tuxguitar.gui.marker;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
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
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.undo.undoables.UndoableJoined;
import org.herac.tuxguitar.gui.undo.undoables.custom.UndoableChangeMarker;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGMarker;

public class MarkerEditor {
	public static final int STATUS_NEW = 1;
	public static final int STATUS_EDIT = 2;
	
	private static final int MINIMUN_CONTROL_WIDTH = 180;    
    private static final int MINIMUN_BUTTON_WIDTH = 80;
    private static final int MINIMUN_BUTTON_HEIGHT = 25;	
	
	private int status;
	protected TGMarker marker;
	protected Shell dialog;	
	protected Spinner measureSpinner;
	protected Text titleText;
	protected Button colorButton;	
	
	protected boolean accepted; 
	
	public MarkerEditor(TGMarker marker) {
		this(marker,STATUS_NEW);
	}	
	
	public MarkerEditor(TGMarker marker,int status) {
		this.marker = marker.clone(TuxGuitar.instance().getSongManager().getFactory());
		this.status = status;
	}		

	public boolean open(Shell shell) {
		this.accepted = false;
		
		this.dialog = DialogUtils.newDialog(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.dialog.setLayout(new GridLayout());
		this.dialog.setText(TuxGuitar.getProperty("marker"));

		// ----------------------------------------------------------------------
		Group group = new Group(this.dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		group.setText(TuxGuitar.getProperty("marker"));
		
		// Measure Number
		final int measureCount = TuxGuitar.instance().getSongManager().getSong().countMeasureHeaders();
		Label measureLabel = new Label(group, SWT.NULL);
		measureLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true,true));
		measureLabel.setText(TuxGuitar.getProperty("measure"));

		this.measureSpinner = new Spinner(group, SWT.BORDER);
		this.measureSpinner.setLayoutData(getAlignmentData(MINIMUN_CONTROL_WIDTH,SWT.FILL));
		this.measureSpinner.setMinimum(1);
		this.measureSpinner.setMaximum(measureCount);
		this.measureSpinner.setSelection(this.marker.getMeasure());
		this.measureSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int selection = MarkerEditor.this.measureSpinner.getSelection();
				if (selection < 1) {
					MarkerEditor.this.measureSpinner.setSelection(1);
				} else if (selection > measureCount) {
					MarkerEditor.this.measureSpinner.setSelection(measureCount);
				}
			}
		});

		// Title
		Label titleLabel = new Label(group, SWT.NULL);
		titleLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
		titleLabel.setText(TuxGuitar.getProperty("title"));
		this.titleText = new Text(group, SWT.BORDER);
		this.titleText.setLayoutData(getAlignmentData(MINIMUN_CONTROL_WIDTH,SWT.FILL));
		this.titleText.setText(this.marker.getTitle());

		// Color
		Label colorLabel = new Label(group, SWT.NULL);
		colorLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
		colorLabel.setText(TuxGuitar.getProperty("color"));
		this.colorButton = new Button(group, SWT.PUSH);
		this.colorButton.setLayoutData(getAlignmentData(MINIMUN_CONTROL_WIDTH,SWT.FILL));
		this.colorButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ColorDialog dlg = new ColorDialog(MarkerEditor.this.dialog);
				dlg.setRGB(MarkerEditor.this.dialog.getDisplay().getSystemColor(SWT.COLOR_BLACK).getRGB());
				dlg.setText(TuxGuitar.getProperty("choose-color"));
				RGB rgb = dlg.open();
				if (rgb != null) {
					MarkerEditor.this.marker.getColor().setR(rgb.red);
					MarkerEditor.this.marker.getColor().setG(rgb.green);
					MarkerEditor.this.marker.getColor().setB(rgb.blue);       
					MarkerEditor.this.colorButton.redraw();					
				}
			}
		});		
		this.colorButton.addPaintListener(new PaintListener() {		
			public void paintControl(PaintEvent e) {
				Color color = new Color(MarkerEditor.this.dialog.getDisplay(), MarkerEditor.this.marker.getColor().getR(), MarkerEditor.this.marker.getColor().getG(), MarkerEditor.this.marker.getColor().getB());
				TGPainter painter = new TGPainter(e.gc);
				painter.setBackground(color);
				painter.initPath(TGPainter.PATH_FILL);
				painter.addRectangle(5,5,MarkerEditor.this.colorButton.getSize().x - 10,MarkerEditor.this.colorButton.getSize().y - 10);
				painter.closePath();
		        color.dispose();
			}		
		});		
		// ------------------BUTTONS--------------------------
		Composite buttons = new Composite(this.dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2, false));
		buttons.setLayoutData(new GridData(SWT.END, SWT.FILL, true, true));

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = MINIMUN_BUTTON_WIDTH;
		data.minimumHeight = MINIMUN_BUTTON_HEIGHT;

		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(data);
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				updateMarker();
				MarkerEditor.this.accepted = true;
				MarkerEditor.this.dialog.dispose();
			}
		});

		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(data);
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				MarkerEditor.this.dialog.dispose();
			}
		});

		this.dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(this.dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
		
		return this.accepted;
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

	protected void updateMarker() {	
		int oldMeasure = this.marker.getMeasure();
		this.marker.setMeasure(this.measureSpinner.getSelection());
		this.marker.setTitle(this.titleText.getText());
		this.marker = this.marker.clone(TuxGuitar.instance().getSongManager().getFactory());
		
		TGSongManager manager = TuxGuitar.instance().getSongManager();
		
		//comienza el undoable
		UndoableJoined joinedUndoable = new UndoableJoined();
		
		if(this.status == STATUS_EDIT && oldMeasure != this.marker.getMeasure()){
			UndoableChangeMarker undoable = UndoableChangeMarker.startUndo(manager.getMarker(oldMeasure));			
			TuxGuitar.instance().getSongManager().removeMarker(oldMeasure);			
			joinedUndoable.addUndoableEdit(undoable.endUndo(null));
		}
		UndoableChangeMarker undoable = UndoableChangeMarker.startUndo(manager.getMarker(this.marker.getMeasure()));		
		TuxGuitar.instance().getSongManager().updateMarker(this.marker);
		joinedUndoable.addUndoableEdit(undoable.endUndo(this.marker));

		// termia el undoable
		TuxGuitar.instance().getUndoableManager().addEdit(joinedUndoable.endUndo());
		TuxGuitar.instance().getFileHistory().setUnsavedFile();
	}
}
