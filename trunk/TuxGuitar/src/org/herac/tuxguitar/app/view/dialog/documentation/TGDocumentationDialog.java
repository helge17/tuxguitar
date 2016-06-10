package org.herac.tuxguitar.app.view.dialog.documentation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.util.TGException;

public class TGDocumentationDialog {
	
	private static final String RESOURCE_PATH = "help";
	private static final String TEMPORAL_PATH = System.getProperty("java.io.tmpdir") + File.separator + "tuxguitar";
	
	private TGViewContext context;
	
	public TGDocumentationDialog(TGViewContext context) {
		this.context = context;
	}
	
	public void show() {
		try {
			URL url = getIndexUrl();
			if( url != null ){
				TGApplication.getInstance(this.context.getContext()).getApplication().openUrl(url);
			}
		} catch (Throwable throwable ) {
			throw new TGException(throwable);
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
