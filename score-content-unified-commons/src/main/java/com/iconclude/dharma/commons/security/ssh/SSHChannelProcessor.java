/*
 * Copyright (c) iConclude 2004-2006
 * All rights reserved.
 */
package com.iconclude.dharma.commons.security.ssh;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author octavian
 */
public abstract class SSHChannelProcessor {

    protected final CountDownLatch streamsToWaitFor;
    protected Logger logger = Logger.getLogger(this.getClass());
    private boolean _notified = false;

    protected SSHChannelProcessor() {
        streamsToWaitFor = null;
    }

    protected SSHChannelProcessor(int numberOfStreams) {
        streamsToWaitFor = new CountDownLatch(numberOfStreams);
    }

    public abstract void createChannel(Session session, Map bindings) throws JSchException, IOException;

    public abstract SSHOperationResult process() throws JSchException;

    synchronized public boolean isNotified() {
        return _notified;
    }

    synchronized public void setNotified(boolean notified) {
        this._notified = notified;
    }

    public void wakeUp() {
        if (streamsToWaitFor != null) {
            streamsToWaitFor.countDown();
        } // note no else.
        wakeUp2();
    }

    private void wakeUp2() {
        synchronized (this) {
            this._notified = true;
            this.notify();
        }
    }

    protected boolean waitForTimeout(long timeout) {
        if (streamsToWaitFor != null) {
            try {
                // await returns false when there is a timeout.
                return false == streamsToWaitFor.await(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                logger.warn("SSHChannelProcessor.waitForTimeout was interrupted");
                // we did not really handle the interrupted ex:
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return waitForTimeout2(timeout);
    }

    /**
     * XBAN: this method is buggy. There can be more than one stream that can
     * call wakeUp but the first one to call will unhook this method from waiting
     * which may result in partial content being read from the stream (because
     * the process() thread thinks all the streams are done). The reason why
     * this method is kept is for content backward compat, although I could
     * not find places in the content where wakeUp() or setNotified are called.
     * <p/>
     * you must set notify to false before calling this method in order to
     * have it actually call this.wait
     *
     * @param timeout
     */
    protected synchronized boolean waitForTimeout2(long timeout) {
        boolean isTimedOut = false;
        try { // endless loop for disabled timeout
            final long startTime = System.currentTimeMillis();

            // notified will only return true, if wakeUp is called (or setNotified(true))
            // so the problem becomes there's no way with the java threading to tell if
            // we timed out or not (at least that i can see).  So we have to keep taking
            // our best guess at how long we've slept and how long we have to go.
            while (!isNotified() && !isTimedOut) {
                if (timeout <= 0) {
                    //TODO: put this in a loop and log periodically
                    //  Possibly abort after some default master timeout
                    this.wait(); // wait for ever
                } else {
                    // well-defined max time to run for the command
                    long t = startTime + timeout - System.currentTimeMillis();

                    // doing this so we give the benefit of timeout vs notify, to notify
                    // also it ensure we wait at least once, if we had a really lagged system or
                    // the timeout was really small
                    if (t < 1) {
                        t = 1;
                    }
                    this.wait(t);
                    isTimedOut = (startTime + timeout) <= System.currentTimeMillis();
                }
            }
        } catch (InterruptedException ie) {
            logger.warn("SSHChannelProcessor.waitForTimeout was interrupted"); //$NON-NLS-1$
        }
        return isTimedOut;
    }
}
