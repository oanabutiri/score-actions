/*
 * Copyright (c) iConclude 2004-2006
 * All rights reserved.
 */
package com.iconclude.dharma.commons.security.ssh;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author octavian
 */
public abstract class SSHChannelProcessor {

    protected final CountDownLatch streamsToWaitFor;

    protected SSHChannelProcessor() {
        streamsToWaitFor = null;
    }

    public abstract void createChannel(Session session, Map bindings) throws JSchException, IOException;

    public abstract SSHOperationResult process() throws JSchException;

}
