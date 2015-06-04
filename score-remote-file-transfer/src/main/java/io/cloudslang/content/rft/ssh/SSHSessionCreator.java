/*
 * Copyright (c) iConclude 2004-2006
 * All rights reserved.
 */
package io.cloudslang.content.rft.ssh;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Map;

/**
 * @author mmerz
 * @author statu
 * @author octavian
 */
public interface SSHSessionCreator {
    Session createSession(Map bindings) throws JSchException;
}
