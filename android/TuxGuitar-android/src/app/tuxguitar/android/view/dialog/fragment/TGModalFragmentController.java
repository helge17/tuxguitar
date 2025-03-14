package app.tuxguitar.android.view.dialog.fragment;

import app.tuxguitar.android.action.impl.gui.TGOpenFragmentAction;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.application.TGApplicationUtil;
import app.tuxguitar.android.fragment.TGCachedFragmentController;
import app.tuxguitar.android.fragment.TGFragmentController;
import app.tuxguitar.android.view.dialog.TGDialogContext;
import app.tuxguitar.android.view.dialog.TGDialogController;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.util.TGContext;

public abstract class TGModalFragmentController<T extends TGModalFragment> extends TGCachedFragmentController<T> implements TGDialogController {

	public void showDialog(TGActivity activity, TGDialogContext dialogContext) {
		TGContext context = TGApplicationUtil.findContext(activity);

		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGOpenFragmentAction.NAME);
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_CONTROLLER, new TGModalFragmentControllerWrapper<T>(this, context, dialogContext));
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_ACTIVITY, activity);
		tgActionProcessor.process();
	}

	private class TGModalFragmentControllerWrapper<E extends TGModalFragment> implements TGFragmentController<E> {

		private TGContext context;
		private TGDialogContext dialogContext;
		private TGModalFragmentController<E> target;

		public TGModalFragmentControllerWrapper(TGModalFragmentController<E> target, TGContext context, TGDialogContext dialogContext) {
			this.target = target;
			this.context = context;
			this.dialogContext = dialogContext;
		}

		@Override
		public E getFragment() {
			E fragment = this.target.getFragment();

			this.context.setAttribute(fragment.getDialogContextKey(), this.dialogContext);

			return fragment;
		}
	}
}
