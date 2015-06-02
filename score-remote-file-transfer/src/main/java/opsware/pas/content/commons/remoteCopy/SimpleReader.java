package opsware.pas.content.commons.remoteCopy;

import java.io.File;

public class SimpleReader implements IReader {
	String name;
	File file;
	
	SimpleReader(String fileName, File file){
		name = fileName;
		this.file = file;
	}

	public String getFileName() {
		return name;
	}
	
	public File getFile() {
		return file;
	}
}
