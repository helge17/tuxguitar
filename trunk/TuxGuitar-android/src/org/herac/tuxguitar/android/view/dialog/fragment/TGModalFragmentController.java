package org.herac.tuxguitar.android.view.dialog.fragment;

import android.app.Activity;

import org.herac.tuxguitar.android.action.impl.gui.TGOpenFragmentAction;
import org.herac.tuxguitar.android.application.TGApplicationUtil;
import org.herac.tuxguitar.android.fragment.TGCachedFragmentController;
import org.herac.tuxguitar.android.fragment.TGFragmentController;
import org.herac.tuxguitar.android.view.dialog.TGDialogContext;
import org.herac.tuxguitar.android.view.dialog.TGDialogController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;

public abstract class TGModalFragmentController<T extends TGModalFragment> extends TGCachedFragmentController<T> implements TGDialogController {

	public void showDialog(Activity activity, TGDialogContext dialogContext) {
		TGContext context = TGApplicationUtil.findContext(activity);

		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGOpenFragmentAction.NAME);
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_CONTROLLER, new TGModalFragmentControllerWrapper<T>(this, context, dialogContext));
		tgActionProcessor.setAttribute(TGOpenFragmentAction.ATTRIBUTE_ACTIVITY, activity);
		tgActionProcessor.process();
	}

	private class TGModalFragmentControllerWrapper<T extends TGModalFragment> implements TGFragmentController<T> {

		private TGContext context;
		private TGDialogContext dialogContext;
		private TGModalFragmentController<T> target;

		public TGModalFragmentControllerWrapper(TGModalFragmentController<T> target, TGContext context, TGDialogContext dialogContext) {
			this.target = target;
			this.context = context;
			this.dialogContext = dialogContext;
		}

		@Override
		public T getFragment() {
			T fragment = this.target.getFragment();

			this.context.setAttribute(fragment.getDialogContextKey(), this.dialogContext);

			return fragment;
		}
	}
}
