package app.tuxguitar.app.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.util.TGFileUtils;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

/**
 * Tracks the list of files that were open at the end of the last session,
 * so they can be reopened on startup.
 *
 * <p>The list is always replaced as a whole via {@link #save(List)} based on
 * the currently open documents. This keeps the stored list in sync with reality
 * even if files are deleted or moved externally.
 *
 * <p>Documents expose their location as a {@link java.net.URI}; URLs are used
 * only for internal storage and the public {@link #getURLs()} API, consistent
 * with {@link TGFileHistory}.
 */
public class TGLastOpenFiles {

	private TGContext context;
	private List<URL> urls;

	private TGLastOpenFiles(TGContext context) {
		this.context = context;
		this.urls = new ArrayList<URL>();
		this.loadLastOpenFiles();
	}

	public void save(List<URL> urls) {
		this.urls.clear();
		this.urls.addAll(urls);
		this.checkLimit();
		this.saveLastOpenFiles();
	}

	public List<URL> getURLs() {
		return this.urls;
	}

	private void checkLimit() {
		int maxFiles = TuxGuitar.getInstance().getConfig().getIntegerValue(TGConfigKeys.MAX_LAST_OPEN_FILES, 10);
		while(this.urls.size() > maxFiles){
			this.urls.remove(this.urls.size() - 1);
		}
	}

	public void loadLastOpenFiles() {
		try {
			this.urls.clear();
			if( new File(getLastOpenFilesFileName()).exists() ){
				InputStream inputStream = new FileInputStream(getLastOpenFilesFileName());
				Properties properties = new Properties();
				properties.load(new InputStreamReader(inputStream,StandardCharsets.UTF_8));

				int count = Integer.valueOf(properties.getProperty("lastopen.count", "0"));
				for(int i = 0; i < count;i ++){
					String url = properties.getProperty("lastopen." + i);
					if( url != null && url.length() > 0 ){
						try {
							this.urls.add(new URL(url));
						} catch (Exception e) {
							// skip invalid URLs
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveLastOpenFiles() {
		try {
			Properties properties = new Properties();

			int count = this.urls.size();
			for(int i = 0;i < count;i ++){
				properties.put("lastopen." + i,this.urls.get(i).toString());
			}
			properties.put("lastopen.count",Integer.toString(count));
			properties.store(new OutputStreamWriter(new FileOutputStream(getLastOpenFilesFileName()),StandardCharsets.UTF_8),"Last Open Files");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private String getLastOpenFilesFileName() {
		return TGFileUtils.PATH_USER_CONFIG + File.separator + "last-open-files.properties";
	}

	public static TGLastOpenFiles getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGLastOpenFiles.class.getName(), new TGSingletonFactory<TGLastOpenFiles>() {
			public TGLastOpenFiles createInstance(TGContext context) {
				return new TGLastOpenFiles(context);
			}
		});
	}
}
