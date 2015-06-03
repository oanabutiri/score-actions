package io.cloudslang.content.rft.utils;

/**
 * Created by butiri on 6/2/2015.
 */
public class RemoteCopyInputs {

    public static final String SRC_HOST = "sourceHost";
    public static final String SRC_PORT = "sourcePort";
    public static final String SRC_USERNAME = "sourceUsername";
    public static final String SRC_PASSWORD = "sourcePassword";
    public static final String SRC_PRIVATE_KEY_FILE = "sourcePrivateKeyFile";
    public static final String SRC_PATH = "sourcePath";
    public static final String SRC_PROTOCOL = "sourceProtocol";
    public static final String SRC_TIMEOUT = "sourceTimeout";
    public static final	String SRC_CHARACTERSET = "sourceCharacterSet";

    public static final String DEST_HOST = "destinationHost";
    public static final String DEST_PORT = "destinationPort";
    public static final String DEST_USERNAME = "destinationUsername";
    public static final String DEST_PASSWORD = "destinationPassword";
    public static final String DEST_PRIVATE_KEY_FILE = "destinationPrivateKeyFile";
    public static final String DEST_PATH = "destinationPath";
    public static final String DEST_PROTOCOL = "destinationProtocol";
    public static final String DEST_TIMEOUT = "destinationTimeout";
    public static final String DEST_CHARACTERSET = "destinationCharacterSet";
    public static final String FILE_TYPE = "fileType";
    public static final String PASSIVE = "passive";

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
    private String passive;

    public String getSourceHost() {
        return sourceHost;
    }

    public void setSourceHost(String sourceHost) {
        this.sourceHost = sourceHost;
    }

    public String getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(String sourcePort) {
        this.sourcePort = sourcePort;
    }

    public String getSourceUsername() {
        return sourceUsername;
    }

    public void setSourceUsername(String sourceUsername) {
        this.sourceUsername = sourceUsername;
    }

    public String getSourcePassword() {
        return sourcePassword;
    }

    public void setSourcePassword(String sourcePassword) {
        this.sourcePassword = sourcePassword;
    }

    public String getSourcePrivateKeyFile() {
        return sourcePrivateKeyFile;
    }

    public void setSourcePrivateKeyFile(String sourcePrivateKeyFile) {
        this.sourcePrivateKeyFile = sourcePrivateKeyFile;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getSourceProtocol() {
        return sourceProtocol;
    }

    public void setSourceProtocol(String sourceProtocol) {
        this.sourceProtocol = sourceProtocol;
    }

    public String getSourceTimeout() {
        return sourceTimeout;
    }

    public void setSourceTimeout(String sourceTimeout) {
        this.sourceTimeout = sourceTimeout;
    }

    public String getSourceCharacterSet() {
        return sourceCharacterSet;
    }

    public void setSourceCharacterSet(String sourceCharacterSet) {
        this.sourceCharacterSet = sourceCharacterSet;
    }

    public String getDestinationHost() {
        return destinationHost;
    }

    public void setDestinationHost(String destinationHost) {
        this.destinationHost = destinationHost;
    }

    public String getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(String destinationPort) {
        this.destinationPort = destinationPort;
    }

    public String getDestinationUsername() {
        return destinationUsername;
    }

    public void setDestinationUsername(String destinationUsername) {
        this.destinationUsername = destinationUsername;
    }

    public String getDestinationPassword() {
        return destinationPassword;
    }

    public void setDestinationPassword(String destinationPassword) {
        this.destinationPassword = destinationPassword;
    }

    public String getDestinationPrivateKeyFile() {
        return destinationPrivateKeyFile;
    }

    public void setDestinationPrivateKeyFile(String destinationPrivateKeyFile) {
        this.destinationPrivateKeyFile = destinationPrivateKeyFile;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    public String getDestinationProtocol() {
        return destinationProtocol;
    }

    public void setDestinationProtocol(String destinationProtocol) {
        this.destinationProtocol = destinationProtocol;
    }

    public String getDestinationTimeout() {
        return destinationTimeout;
    }

    public void setDestinationTimeout(String destinationTimeout) {
        this.destinationTimeout = destinationTimeout;
    }

    public String getDestinationCharacterSet() {
        return destinationCharacterSet;
    }

    public void setDestinationCharacterSet(String destinationCharacterSet) {
        this.destinationCharacterSet = destinationCharacterSet;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getPassive() {
        return passive;
    }

    public void setPassive(String passive) {
        this.passive = passive;
    }
}
