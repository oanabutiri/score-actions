package io.cloudslang.content.rft.commons;
import java.io.File;

public abstract class SimpleCopier implements ICopier {

	public int timeout;

	public void copyTo(ICopier destination, String sourcePath, String destPath) throws Exception{
		//if either the source or the destination protocols are local, useTemp is false, i.e. we do not use a temp file
		//boolean useTemp = CopierFactory.copiers.local.name().equals(destination.getImplementation().getProtocolName())?false:CopierFactory.copiers.local.name().equals(this.getProtocolName())?false:true;

		if (!CopierFactory.copiers.local.name().equals(destination.getImplementation().getProtocolName())){
			IReader reader = null;
			try{
				reader = getFile(sourcePath);
				destination.getImplementation().putFile(reader, destPath);
			}
			finally{
				if (!CopierFactory.copiers.local.name().equals(this.getImplementation().getProtocolName())
						&& reader != null && reader.getFile() != null) {
					reader.getFile().delete();
				}
			}
		}
		else{
			if (new File(destPath).exists() && !Boolean.parseBoolean(destination.getCustomArgument(simpleArgument.overwrite))) {
				throw new Exception("The specified file exists, and overwrite is disabled.");
			}
			getFile(sourcePath, new File(destPath));
		}
	}

	@Override
	public void setTimeout(int timeout){
		this.timeout = timeout;
	}

	protected abstract IReader getFile(String source) throws Exception;//, boolean useTemp) throws Exception;

	protected abstract void getFile(String source, File destination) throws Exception;

	protected abstract void putFile(IReader sourceFile, String destination) throws Exception;

	protected String getSimpleFileName(String path){
		//String name = (path.contains("/"))?path.substring(path.lastIndexOf("/")+1):path;
		//modified to handle both paths with / and paths with \
		String name = (path.contains("/"))?path.substring(path.lastIndexOf("/")+1):(path.contains("\\"))?path.substring(path.lastIndexOf("\\")+1):path;
		return name;
	}

	public SimpleCopier getImplementation() throws Exception{
		return this;
	}

	@Override
	public void setCredentials(String host, int port, String username, String password) throws UnsupportedOperationException{
		if (host != null && host.length()>0) {
			throw new UnsupportedOperationException(getProtocolName()+" does not allow the host to be set.");
		} else if (username != null && username.length()>0) {
			throw new UnsupportedOperationException(getProtocolName()+" does not allow the username to be set.");
		} else if (password != null && password.length()>0) {
			throw new UnsupportedOperationException(getProtocolName()+" does not allow the password to be set.");
		} else if (port >= 0) {
			throw new UnsupportedOperationException(getProtocolName()+" does not allow the port to be set.");
		}
	}

	@Override
	public void setCredentials(String host, int port, String username, String password, String privateKeyFile) throws UnsupportedOperationException{
		if (privateKeyFile != null && privateKeyFile.length()>0) {
			throw new UnsupportedOperationException(getProtocolName()+" does not allow the privateKeyFile to be set.");
		}
		setCredentials(host, port, username, password);
	}

	@Override
	public void setCustomArgument(simpleArgument name, String value){
		throw new UnsupportedOperationException(getProtocolName()+" does not allow "+name.name()+" to be set");
	}

	@Override
	public void setCustomArgument(complexArgument name, Object value){
		throw new UnsupportedOperationException(getProtocolName()+" does not allow "+name.name()+" to be set");
	}

	@Override
	public String getCustomArgument(simpleArgument name){
		throw new UnsupportedOperationException(getProtocolName()+" does not support "+name.name());
	}
}
