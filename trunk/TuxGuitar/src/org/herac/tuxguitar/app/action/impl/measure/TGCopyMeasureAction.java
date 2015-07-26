package org.herac.tuxguitar.app.action.impl.measure;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.clipboard.MeasureTransferable;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGCopyMeasureAction extends TGActionBase{
	
	public static final String NAME = "action.measure.copy";
	
	public static final String ATTRIBUTE_ALL_TRACKS = "copyAllTracks";
	public static final String ATTRIBUTE_MEASURE_NUMBER_1 = "measureNumber1";
	public static final String ATTRIBUTE_MEASURE_NUMBER_2 = "measureNumber2";
	
	public TGCopyMeasureAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		Integer m1 = context.getAttribute(ATTRIBUTE_MEASURE_NUMBER_1);
		Integer m2 = context.getAttribute(ATTRIBUTE_MEASURE_NUMBER_2);
		Boolean allTracks = context.getAttribute(ATTRIBUTE_ALL_TRACKS);
		if( m1 > 0 && m1 <= m2 ){
			TablatureEditor tablatureEditor = TablatureEditor.getInstance(getContext());
			MeasureTransferable transferable = new MeasureTransferable(tablatureEditor, m1, m2, allTracks);
			tablatureEditor.getClipBoard().addTransferable(transferable);
		}
	}
}
