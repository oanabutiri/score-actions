package io.cloudslang.content.rft.commons.remoteCopy.sftp;

import com.jcraft.jsch.UserInfo;

public  class MyUserInfo implements UserInfo{

	public String getPassword(){
		return passwd; 
		}

  
	public boolean promptPassword(String arg0) {
		// TODO Auto-generated method stub
		return promptPassword;
	}


	public boolean promptPassphrase(String arg0) {
		// TODO Auto-generated method stub
		return promptPassphrase;
	}


	public boolean promptYesNo(String _prompt) {
		return promptYesNo;
	}


	public void showMessage(String arg0) {
		// TODO Auto-generated method stub
		
	}

	 private String  passwd;
	 private boolean promptYesNo;
	 private boolean promptPassphrase;
	 private boolean promptPassword;
	 private String  Passphrase;
	 private String  privateKey;
	 
	public String getPrivateKey() {
		return privateKey;
	}


	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public void setPromptPassword(boolean promptPassword) {
		this.promptPassword = promptPassword;
	}

	public void setPromptYesNo(boolean promptYesNo) {
		this.promptYesNo = promptYesNo;
	}

	public void setPassphrase(String passphrase) {
		Passphrase = passphrase;
	}

	public String getPassphrase() {
		return Passphrase;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
    
 }
