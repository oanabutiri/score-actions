package io.cloudslang.content.rft.service;

import io.cloudslang.content.rft.utils.StringUtils;
import io.cloudslang.content.rft.utils.RemoteCopyInputs;
import io.cloudslang.content.rft.utils.RemoteCopyOutputs;
import opsware.pas.content.commons.remoteCopy.CopierFactory;
import opsware.pas.content.commons.remoteCopy.CopierFactory.copiers;
import opsware.pas.content.commons.remoteCopy.ICopier;
import opsware.pas.content.commons.remoteCopy.ICopier.simpleArgument;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by butiri on 6/2/2015.
 */
public class RemoteCopy {

    public enum protocols {local, scp, ftp, sftp, smb}

    private String sourceHost;
    private String sourcePort;
    private String sourceUsername;
    private String sourcePassword;
    private String sourcePrivateKeyFile;
    private String sourcePath;
    private String sourceProtocol;
    private String sourceTimeout;
    private String sourceCharacterSet;
    private String destinationHost;
    private String destinationPort;
    private String destinationUsername;
    private String destinationPassword;
    private String destinationPrivateKeyFile;
    private String destinationPath;
    private String destinationProtocol;
    private String destinationTimeout;
    private String destinationCharacterSet;
    private String fileType;
    private boolean passive;

    public Map<String, String> execute(RemoteCopyInputs remoteCopyInputs)  {
        Map<String, String> result = new HashMap<String, String>();
        try {
            processInputs(remoteCopyInputs);

            ICopier src = CopierFactory.getExecutor(sourceProtocol);
            ICopier dest = CopierFactory.getExecutor(destinationProtocol);

            checkOptions(sourceProtocol, sourceHost, fileType);
            checkOptions(destinationProtocol, destinationHost, fileType);

            setCredentials(src, sourceHost, sourcePort, sourceUsername, sourcePassword, sourcePrivateKeyFile);
            setCredentials(dest, destinationHost, destinationPort, destinationUsername, destinationPassword, destinationPrivateKeyFile);

            setAndValidateCharacterSet(src, sourceCharacterSet, RemoteCopyInputs.SRC_CHARACTERSET);
            setAndValidateCharacterSet(dest, destinationCharacterSet, RemoteCopyInputs.DEST_CHARACTERSET);

            setCustomArgumentForFTP(src, dest, sourceProtocol, destinationProtocol, fileType, passive);

            setTimeout(src, sourceTimeout);
            setTimeout(dest, destinationTimeout);

            src.copyTo(dest, sourcePath, destinationPath);
            result.put(RemoteCopyOutputs.RETURN_RESULT, "Copy completed successfully");
            result.put(RemoteCopyOutputs.RETURN_CODE, RemoteCopyOutputs.SUCCESS_RETURN_CODE);

        } catch (Exception e) {
            return exceptionResult(e.getMessage(), e);
        }
        return result;
    }

    private void processInputs(RemoteCopyInputs remoteCopyInputs) throws Exception {

        sourceHost = remoteCopyInputs.getSourceHost();
        sourcePort = remoteCopyInputs.getSourcePort();
        sourceUsername = remoteCopyInputs.getSourceUsername();
        sourcePassword = remoteCopyInputs.getSourcePassword();
        sourcePrivateKeyFile = remoteCopyInputs.getSourcePrivateKeyFile();
        sourcePath = remoteCopyInputs.getSourcePath();
        sourceProtocol = remoteCopyInputs.getSourceProtocol();
        validateProtocol(sourceProtocol.toLowerCase());
        sourceTimeout = remoteCopyInputs.getSourceTimeout();
        sourceCharacterSet = remoteCopyInputs.getSourceCharacterSet();
        destinationHost = remoteCopyInputs.getDestinationHost();
        destinationPort = remoteCopyInputs.getDestinationPort();
        destinationUsername = remoteCopyInputs.getDestinationUsername();
        destinationPassword = remoteCopyInputs.getDestinationPassword();
        destinationPrivateKeyFile = remoteCopyInputs.getDestinationPrivateKeyFile();
        destinationPath = remoteCopyInputs.getDestinationPath();
        destinationProtocol = remoteCopyInputs.getDestinationProtocol();
        validateProtocol(destinationProtocol.toLowerCase());
        destinationTimeout = remoteCopyInputs.getDestinationTimeout();
        destinationCharacterSet = remoteCopyInputs.getDestinationCharacterSet();
        fileType = remoteCopyInputs.getFileType();

        passive = resolveOptionalBoolean(RemoteCopyInputs.PASSIVE, remoteCopyInputs.getPassive(), false);
    }

    private Map<String, String> exceptionResult(String message, Exception e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        String eStr = writer.toString().replace("\u0000", "");

        Map<String, String> returnResult = new HashMap<String, String>();
        returnResult.put(RemoteCopyOutputs.RETURN_RESULT, message);
        returnResult.put(RemoteCopyOutputs.RETURN_CODE, RemoteCopyOutputs.FAILURE_RETURN_CODE);
        returnResult.put(RemoteCopyOutputs.EXCEPTION, eStr);
        return returnResult;
    }

    private void checkOptions(String copier, String host, String type) throws Exception {
        switch (copiers.valueOf(copier)) {
            case local:
                if (host == null || !host.trim().equalsIgnoreCase("localhost")) {
                    throw new Exception("When the protocol is local, the host must be localhost!\n");
                }
                break;
            case scp:
                break;
            case ftp:
                if (type == null || type.length() <= 0 || !(type.equalsIgnoreCase("ascii") || type.equalsIgnoreCase("binary"))) {
                    throw new Exception("When the FTP protocol is used, the type must have the value 'ascii' or 'binary'!\n");
                }
                break;
            case sftp:
                break;
            default:
                break;
        }
    }

    public void setCredentials(ICopier copier, String host, String portString, String username, String password, String privateKeyFile) {
        int port = -1;
        if (portString != null && !portString.isEmpty())
            port = Integer.parseInt(portString);
        if (copiers.valueOf(copier.getProtocolName()) == copiers.local) {
            host = "";
        }
        if (privateKeyFile != null && privateKeyFile.length() > 0) {
            copier.setCredentials(host, port, username, password, privateKeyFile);
        } else {
            copier.setCredentials(host, port, username, password);
        }
    }

    public boolean setCharacterSet(ICopier copier, String characterSetName) {
        if (copiers.valueOf(copier.getProtocolName()) == copiers.ftp
                || copiers.valueOf(copier.getProtocolName()) == copiers.sftp) {
            if (!StringUtils.isNull(characterSetName)) {
                if (!Charset.isSupported(characterSetName)) {
                    return false;
                }
            }
            copier.setCustomArgument(ICopier.simpleArgument.characterSet, characterSetName);
        } //else do nothing
        return true;
    }

    private void setTimeout(ICopier src, String srcTimeout) {
        if ((srcTimeout != null) && (srcTimeout.length() > 0)) {
            src.setTimeout(Integer.parseInt(srcTimeout));
        }
    }

    private void setCustomArgumentForFTP(ICopier src, ICopier dest, String srcProtocol, String destProtocol, String type, boolean passive) throws Exception {
        if (type != null && type.length() > 0) {
            if (copiers.valueOf(srcProtocol) == copiers.ftp || copiers.valueOf(destProtocol) == copiers.ftp) {
                if (copiers.valueOf(srcProtocol) == copiers.ftp) {
                    src.setCustomArgument(simpleArgument.type, type);
                }
                if (copiers.valueOf(destProtocol) == copiers.ftp) {
                    dest.setCustomArgument(simpleArgument.type, type);
                }
            } else {
                throw new Exception("The fileType input must be empty when the FTP protocol is not used!");
            }
        }

        if (passive) {
            if (copiers.valueOf(srcProtocol) == copiers.ftp) {
                src.setCustomArgument(simpleArgument.passive, Boolean.toString(passive));
            }
            if (copiers.valueOf(destProtocol) == copiers.ftp) {
                dest.setCustomArgument(simpleArgument.passive, Boolean.toString(passive));
            }
        }
    }

    private void setAndValidateCharacterSet(ICopier copier, String characterSet, String source) throws Exception {
        try {
            if (!setCharacterSet(copier, characterSet)) {
                throw new Exception(source + " input: " + characterSet + " is not a valid character set name");
            }
        } catch (IllegalCharsetNameException icne) {
            throw new Exception(source + " input: " + characterSet + " is not a valid character set name");
        }
    }

    private void validateProtocol(String srcProtocol) throws Exception {
        try {
            protocols.valueOf(srcProtocol);
        } catch (Exception e) {
            throw (new Exception("Protocol " + srcProtocol + " not supported!"));
        }
    }

    private Boolean resolveOptionalBoolean(String name, String value, Boolean def) throws IllegalArgumentException {
        if (value == null || value.isEmpty()) {
            return def;
        } else if ("true".equalsIgnoreCase(value)) {
            return Boolean.valueOf(true);
        } else if ("false".equalsIgnoreCase(value)) {
            return Boolean.valueOf(false);
        } else {
            throw new IllegalArgumentException("Invalid value for input \'" + name + "\'.  Valid values: true, false");
        }
    }


}
