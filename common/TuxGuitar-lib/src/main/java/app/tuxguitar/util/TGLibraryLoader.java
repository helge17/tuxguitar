package app.tuxguitar.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import app.tuxguitar.util.error.TGErrorManager;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGLibraryLoader {

	private TGContext context;

	private TGLibraryLoader(TGContext context){
		this.context = context;
	}

	public void loadLibrary(File file){
		try{
			System.out.println("Loading: " + file.getAbsolutePath());
			System.load(file.getAbsolutePath());
		}catch(Throwable throwable){
			TGErrorManager.getInstance(this.context).handleError(throwable);
		}
	}

	public void loadLibraries(File folder, String prefix, String extension){
		if(folder != null && folder.exists()){
			for(String library : findLibrariesToLoad(folder, prefix, extension)){
				loadLibraryFile(new File(getLibraryFileName(folder, library)));
			}
		}
	}

	// a folder that matches the library name is not loaded
	private void loadLibraryFile(File file){
		if(file.exists() && !file.isDirectory()){
			loadLibrary(file);
		}
	}

	private String getLibraryFileName(File folder, String library){
		return (folder.getAbsolutePath() + File.separator + library);
	}

	private String getLibraryDependenciesFileName(File folder, String library){
		return (getLibraryFileName(folder, library) + ".deps");
	}

	private List<String> findLibrariesToLoad(File folder, String prefix, String extension){
		List<String> libraries = new ArrayList<String>();
		String[] files = folder.list(createFilenameFilter(prefix, extension));
		String library = null;
		while((library = findNextLibraryToLoad(libraries, folder, files)) != null){
			libraries.add(library);
		}
		return libraries;
	}

	private String findNextLibraryToLoad(List<String> libraries, File folder, String[] files){
		for(int i = 0; i < files.length; i ++){
			if(!libraries.contains(files[i])){
				return findNextLibraryToLoad(libraries, folder, files, files[i]);
			}
		}
		return null;
	}

	// the first dependency of the expected library still to load (recursively), else the expected library
	private String findNextLibraryToLoad(List<String> libraries, File folder, String[] files, String expectedLib){
		for(String dependency : findDependencyLibraries(folder, expectedLib)){
			if(!libraries.contains(dependency) && Arrays.asList(files).contains(dependency)){
				return findNextLibraryToLoad(libraries, folder, files, dependency);
			}
		}
		return expectedLib;
	}

	private List<String> findDependencyLibraries(File folder, String library){
		return findDependencyLibraries(new File(getLibraryDependenciesFileName(folder, library)));
	}

	// a dependencies file that cannot be read is reported and lists nothing
	private List<String> findDependencyLibraries(File file){
		List<String> libraries = new ArrayList<String>();
		if( file.exists() ){
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
				String library = null;
				while((library = reader.readLine()) != null){
					libraries.add(library.trim());
				}
			} catch (IOException e) {
				TGErrorManager.getInstance(this.context).handleError(e);
				return Collections.emptyList();
			}
		}
		return libraries;
	}

	private FilenameFilter createFilenameFilter(final String prefix, final String extension){
		return new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if(prefix != null && prefix.length() > 0){
					int index = name.indexOf(prefix);
					if(index != 0){
						return false;
					}
				}
				if(extension != null && extension.length() > 0){
					int index = name.indexOf(extension);
					if(index != ( name.length() - extension.length() ) ){
						return false;
					}
				}
				return true;
			}
		};
	}

	public static TGLibraryLoader getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGLibraryLoader.class.getName(), new TGSingletonFactory<TGLibraryLoader>() {
			public TGLibraryLoader createInstance(TGContext context) {
				return new TGLibraryLoader(context);
			}
		});
	}
}
