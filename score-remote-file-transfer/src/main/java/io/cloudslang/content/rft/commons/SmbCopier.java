package io.cloudslang.content.rft.commons;

import io.cloudslang.content.rft.utils.Address;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import java.io.*;

/**
 * User: bancl
 * Date: 4/23/2015
 */
public class SmbCopier extends SimpleCopier {

    String sourceFileName;
    private String host;
    private String domain = "";
    private String username;
    private String password;

    @Override
    protected IReader getFile(String source) throws Exception {
        File tempFile;
        tempFile = File.createTempFile("SMBCopy", ".tmp");

        getFile(source, tempFile);
        return new SimpleReader(sourceFileName, tempFile);
    }

    @Override
    protected void getFile(String source, File destination) throws Exception {
        String fileUrl = "smb://" + host + "/" + getFormattedPath(source);
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domain,
                username, password);
        SmbFile smbFile = new SmbFile(fileUrl, auth);
        SmbFileInputStream smbInputStream = new SmbFileInputStream(smbFile);
        FileOutputStream fileOutputStream = new FileOutputStream(destination);
        inputStreamToOutputStream(smbInputStream, fileOutputStream);
        if (sourceFileName == null)
            sourceFileName = getSimpleFileName(source);
    }

    private String getFormattedPath(String path) {
        return path.replaceFirst("\\:", "\\$").replaceAll("\\\\", "/");
    }

    @Override
    protected void putFile(IReader sourceFile, String destination) throws Exception {
        String fileUrl = "smb://" + host + "/" + getFormattedPath(destination);
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domain,
                username, password);
        SmbFile smbFile = new SmbFile(fileUrl, auth);
        SmbFileOutputStream smbOs = new SmbFileOutputStream(smbFile);
        FileInputStream fis = new FileInputStream(sourceFile.getFile());
        inputStreamToOutputStream(fis, smbOs);
    }

    private static void inputStreamToOutputStream(InputStream in, OutputStream out)
            throws IOException {
        byte[] buf = new byte[1024000];
        try {
            int byteData = buf.length;
            while (true) {
                byteData = in.read(buf, 0, byteData);
                if (byteData == -1)
                    break;
                out.write(buf, 0, byteData);
            }

        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
        }
    }

    @Override
    public String getProtocolName() {
        return CopierFactory.copiers.smb.name();
    }

    @Override
    public void setCredentials(String host, int port, String username,
                               String password) throws UnsupportedOperationException {
        Address address = new Address(host, port);
        this.host = address.getBareHost();
        this.username=username;
        this.password = password;
    }
}
