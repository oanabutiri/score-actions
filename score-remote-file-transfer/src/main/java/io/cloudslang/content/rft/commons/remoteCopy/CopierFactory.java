package io.cloudslang.content.rft.commons.remoteCopy;

import java.util.HashMap;


public class CopierFactory {
	
	public enum copiers{local, scp, ftp, sftp, smb}
	
    private static HashMap<copiers, Class<? extends ICopier>> executorMap = new HashMap<copiers, Class<? extends ICopier>>();
	
	static{
		executorMap.put(copiers.scp, ScpCopier.class);
		executorMap.put(copiers.local, LocalCopier.class);
		executorMap.put(copiers.ftp, FtpCopier.class);
		executorMap.put(copiers.sftp, SftpCopier.class);
        executorMap.put(copiers.smb, SmbCopier.class);
	}

	public static ICopier getExecutor(String name) throws Exception{
		try{
			return getExecutor(copiers.valueOf(name));
		}
		catch (Exception e){
			throw (new Exception("Protocol "+name+" not supported!"));
		}
	}

	public static ICopier getExecutor(copiers name) throws Exception{
		return executorMap.get(name).newInstance();
	}
}