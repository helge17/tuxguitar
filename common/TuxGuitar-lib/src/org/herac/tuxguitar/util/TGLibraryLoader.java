package org.herac.tuxguitar.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.util.error.TGErrorManager;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

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
			List<String> libraries = findLibrariesToLoad(folder, prefix, extension);
			for(int i = 0; i < libraries.size(); i ++){
				String library = (String)libraries.get(i);
				File file = new File(getLibraryFileName(folder, library));
				if(file.exists() && !file.isDirectory()){
					loadLibrary(file);
				}
			}
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
	
	private String findNextLibraryToLoad(List<String> libraries, File folder, String[] files, String expectedLib){		
		List<String> dependencies = findDependencyLibraries(folder, expectedLib);
		if( dependencies != null ){
			Iterator<String> it = dependencies.iterator();
			while(it.hasNext()){
				String dependency = (String) it.next();
				if(!libraries.contains(dependency)){
					for(int i = 0; i < files.length; i ++){
						if( dependency.equals(files[i]) ){
							return findNextLibraryToLoad(libraries, folder, files, dependency);
						}
					}
				}
			}
		}
		return expectedLib;
	}
	
	private List<String> findDependencyLibraries(File folder, String library){
		return findDependencyLibraries(new File(getLibraryDependenciesFileName(folder, library)));
	}
	
	private List<String> findDependencyLibraries(File file){
		try {
			List<String> libraries = new ArrayList<String>();
			if( file.exists() ){
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				String library = null;
				while((library = reader.readLine()) != null){
					libraries.add(library.trim());
				}
				reader.close();
			}
			return libraries;
		} catch (UnsupportedEncodingException e) {
			TGErrorManager.getInstance(this.context).handleError(e);
		} catch (IOException e) {
			TGErrorManager.getInstance(this.context).handleError(e);
		}
		return null;
	}
	
	private FilenameFilter createFilenameFilter(final String prefix, final String extension){
		return new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if(name == null){
					return false;
				}
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
