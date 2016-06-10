package test;

import org.herac.tuxguitar.ui.UIApplication;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIWindow;

import swtimpl.SWTApplication;

public class UITest {
	
	public static void main(String[] s) throws Throwable {
		
/////
		final UIApplication uiApplication = new SWTApplication();
		final UIFactory uiFactory = uiApplication.getFactory();
		
		UITableLayout uiLayout = new UITableLayout();
		final UIWindow uiWindow = uiFactory.createWindow();
		uiWindow.setLayout(uiLayout);
		
		UIButton uiButton = uiFactory.createButton(uiWindow);
		uiButton.setText("la ajsdaj sdk asdjkakjsd");
		uiButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				UIWindow dialog = uiFactory.createWindow();
				dialog.maximize();
				dialog.open();
			}
		});
		
		uiLayout.set(uiButton, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		uiWindow.pack();
//		uiWindow.maximize();
		uiWindow.open();
		uiWindow.join();
		
		uiApplication.dispose();
	}
}
