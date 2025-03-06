package org.herac.tuxguitar.app.view.dialog.documentation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIBrowser;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGException;

public class TGDocumentationDialog {

	private static final String RESOURCE_PATH = "help";
	private static final String TEMPORAL_PATH = System.getProperty("java.io.tmpdir") + File.separator + "tuxguitar" + "-" + System.getProperty("user.name");

	private TGViewContext context;

	public TGDocumentationDialog(TGViewContext context) {
		this.context = context;
	}

	public void show() {
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UIWindow dialog = uiFactory.createWindow(uiParent, false, true);
		UIBrowser browser = null;
		boolean isEmbeddedBrowserAvailable;
		URL url = null;

		try {
			url = getIndexUrl();
		} catch (Throwable throwable ) {
			throw new TGException(throwable);
		}
		if (url==null) {
			// no content identified, no need to go any further
			return;
		}
		// try to create embedded browser, and to load url
		try {
			browser = uiFactory.createBrowser(dialog);
			browser.loadUrl(url);
			isEmbeddedBrowserAvailable = true;
		} catch (Throwable throwable ) {
			throwable.printStackTrace();
			isEmbeddedBrowserAvailable = false;
		}

		if (isEmbeddedBrowserAvailable) {
			// display in dialog
			final UITableLayout dialogLayout = new UITableLayout();

			dialog.setLayout(dialogLayout);
			dialog.setText(TuxGuitar.getProperty("help.doc"));
			dialog.setBounds(new UIRectangle(new UISize(820f, 660f)));
			dialogLayout.set(browser, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
			TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_LAYOUT);
		} else {
			// call external browser
			TGApplication.getInstance(this.context.getContext()).getApplication().openUrl(url);
		}
	}

	private URL getIndexUrl() throws Throwable{
		URL url = TGResourceManager.getInstance(this.context.getContext()).getResource(RESOURCE_PATH + "/index.html");
		if( url != null && !TGFileUtils.isLocalFile( url ) ){
			String path = TEMPORAL_PATH + File.separator + RESOURCE_PATH;
			copyTemporalResources(path, RESOURCE_PATH, TGFileUtils.getFileNames(this.context.getContext(), RESOURCE_PATH ));
			url = new File( path + File.separator + "index.html" ).toURI().toURL();
		}
		return url;
	}

	private void copyTemporalResources( String dstPath , String resourcePath, String[] resources ) throws Throwable{
		if( resources != null ){
			for( int i = 0 ; i < resources.length ; i ++ ){
				File file = new File( dstPath + File.separator + resources[i] );
				file.getParentFile().mkdirs();
				String resource = (resourcePath + "/" + resources[i]);
				String[] children = TGFileUtils.getFileNames(this.context.getContext(), resource);
				if( children != null && children.length > 0 ){
					copyTemporalResources( file.getAbsolutePath(), resource , children );
				}else if( !file.exists() ){
					InputStream in = TGResourceManager.getInstance(this.context.getContext()).getResourceAsStream(resource);
					if( in != null ){
						OutputStream out = new FileOutputStream( file );
						int len = 0;
						byte[] buf = new byte[1024];
						while ((len = in.read(buf)) > 0){
							out.write(buf, 0, len);
						}
						out.flush();
						out.close();
						in.close();
					}
				}
			}
		}
	}
}
