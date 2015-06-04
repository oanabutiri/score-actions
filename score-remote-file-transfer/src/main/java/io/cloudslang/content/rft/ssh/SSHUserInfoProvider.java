package io.cloudslang.content.rft.ssh;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import java.io.Closeable;

/**
 * a mixed in interface to serve user information, must be closed after usage.
 *
 * @author statu
 */
public interface SSHUserInfoProvider extends UserInfo, UIKeyboardInteractive, Closeable {

}
