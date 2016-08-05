//package org.herac.tuxguitar.android.action.impl.edit;
//
//import org.herac.tuxguitar.action.TGActionContext;
//import org.herac.tuxguitar.action.TGActionException;
//import org.herac.tuxguitar.android.TuxGuitar;
//import org.herac.tuxguitar.android.action.TGActionBase;
//import org.herac.tuxguitar.editor.undo.TGCannotRedoException;
//import org.herac.tuxguitar.util.TGContext;
//
//public class TGRedoAction extends TGActionBase{
//	
//	public static final String NAME = "action.edit.redo";
//	
//	public TGRedoAction(TGContext context) {
//		super(context, NAME);
//	}
//	
//	protected void processAction(TGActionContext context){
//		try {
//			TuxGuitar tuxguitar = TuxGuitar.getInstance(getContext());
//			if( tuxguitar.getUndoableManager().canRedo()){
//				tuxguitar.getUndoableManager().redo();
//			}
//		} catch (TGCannotRedoException e) {
//			throw new TGActionException(e);
//		}
//	}
//}
