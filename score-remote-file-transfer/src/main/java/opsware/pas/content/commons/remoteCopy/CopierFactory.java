package opsware.pas.content.commons.remoteCopy;

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
}