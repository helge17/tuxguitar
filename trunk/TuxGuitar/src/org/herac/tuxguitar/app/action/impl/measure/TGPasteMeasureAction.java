package org.herac.tuxguitar.app.action.impl.measure;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.action.TGActionException;
import org.herac.tuxguitar.app.clipboard.CannotInsertTransferException;
import org.herac.tuxguitar.app.clipboard.MeasureTransferable;
import org.herac.tuxguitar.app.clipboard.Transferable;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;

public class TGPasteMeasureAction extends TGActionBase{
	
	public static final String NAME = "action.measure.paste";
	
	public static final String ATTRIBUTE_PASTE_MODE = "pasteMode";
	public static final String ATTRIBUTE_PASTE_COUNT = "pasteCount";
	
	public TGPasteMeasureAction(TGContext context) {
		super(context, NAME);
	}
	
	protected void processAction(TGActionContext context){
		try {
			Integer pasteMode = context.getAttribute(ATTRIBUTE_PASTE_MODE);
			Integer pasteCount = context.getAttribute(ATTRIBUTE_PASTE_COUNT);
			
			if( pasteMode > 0 && pasteCount > 0 ){
				Transferable transferable = TablatureEditor.getInstance(getContext()).getClipBoard().getTransferable();
				if(transferable instanceof MeasureTransferable){
					((MeasureTransferable)transferable).setTransferType( pasteMode );
					((MeasureTransferable)transferable).setPasteCount( pasteCount );
					
					transferable.insertTransfer();
				}
			}
		} catch (CannotInsertTransferException e) {
			throw new TGActionException(e);
		}
	}
}
