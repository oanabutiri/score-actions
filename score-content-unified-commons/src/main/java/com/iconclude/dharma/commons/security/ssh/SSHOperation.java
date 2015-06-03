/*
 * Copyright (c) iConclude 2004-2006
 * All rights reserved.
 */
package com.iconclude.dharma.commons.security.ssh;

import com.iconclude.dharma.commons.exception.DharmaException;
import com.iconclude.dharma.commons.util.Dharma;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.util.Map;

/**
 * @author mmerz
 * @author statu
 * @author octavian
 */
public abstract class SSHOperation<SessCreatorT extends SSHSessionCreator,
        ChannelProcessorT extends SSHChannelProcessor> {

    // This operation gets all its bindings from the bindings map; following are the keys...
    // The optional entries are documented as such next to the key
    public static final String HOST = "Host";  //$NON-NLS-1$
    public static final String USERNAME = "User"; //$NON-NLS-1$
    public static final String PASSWORD = "Password"; //$NON-NLS-1$
    public static final String PK_FILE = "PKFile"; // optional //$NON-NLS-1$
    public static final String TIMEOUT = "Timeout"; // optional //$NON-NLS-1$

    /**
     * optional, of type Collection of KerberosTicket
     */
    public static final String KRBTICKETS = "KrbTickets";  //$NON-NLS-1$

    protected final SessCreatorT sessCreator;
    protected final ChannelProcessorT channelProc;

    public SSHOperation(SessCreatorT sessCreator, ChannelProcessorT channelProc) {
        this.sessCreator = sessCreator;
        this.channelProc = channelProc;

        if (null == sessCreator)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.ssh.SSHOperation.NullCreatorError")); //$NON-NLS-1$
        if (null == channelProc)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.ssh.SSHOperation.NullProcessorError")); //$NON-NLS-1$
    }

    /**
     * Template method; puts toghether the execution pieces, delegating most of the work
     * to the policy classes.
     * TODO the thrown exception is the only JSCH dependency in this class; eliminate it
     *
     * @return an instance of the result (it is generated by the processor class)
     * @throws JSchException
     */
    public SSHOperationResult exec() throws JSchException, IOException {
        Map bindings = bindOperation();

        // create the SSH session
        Session session = sessCreator.createSession(bindings);
        if (null == session)
            throw new DharmaException(Dharma.msg("Dharma.security.ssh.SSHOperation.CreationError")); //$NON-NLS-1$
        SSHOperationResult res = null;
        try {
            // create the SSH channel
            channelProc.createChannel(session, bindings);
            // process the channel
            res = channelProc.process();
        } finally {
            if (session.isConnected())
                session.disconnect();
        }
        return res;
    }

    /**
     * Binds the required inputs, env, etc. Minimally, it should return a map having
     * <COMMAND,  command>
     * <HOSTNAME, hostname>
     * <USERNAME, username>
     * <PASSWORD, password>
     * Additional entries may be pushed.
     *
     * @return
     */
    protected abstract Map bindOperation();

}
