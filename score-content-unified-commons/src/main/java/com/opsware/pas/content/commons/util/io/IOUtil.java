package com.opsware.pas.content.commons.util.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

public class IOUtil {

	/**
	 * Copies data from one stream to another.
	 * 
	 * @param in
	 * @param out
	 * @param closeInput
	 * @param closeOutput
	 * 
	 * @throws IOException
	 */
	public static void copyAll(InputStream in, OutputStream out, boolean closeInput, boolean closeOutput) throws IOException{
		try{
			byte [] buffer = (in.available()>1024)?new byte[in.available()]:new byte[1024];

			int read;
			while ((read = in.read(buffer)) >=0){
				out.write(buffer, 0, read);
			}
			out.flush();
		}
		finally{
			if (closeInput)
				in.close();
			if (closeOutput)
				out.close();
		}
	}
	
	/**
     * Decodes (reads) a String from an InputStream 
     * 
     * This method blocks until the InputStream is closed.
     * 
	 * @param is
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static String InputStreamToString(InputStream is) throws IOException {
		return InputStreamToString(is, Charset.defaultCharset());
	}

	/**
     * Decodes (reads) a String from an InputStream with a specified Character Set
     * 
     * This method blocks until the InputStream is closed.
     * 
	 * @param is
	 * @param charSet
	 * 
	 * @return
	 * @throws IOException 
	 * 
	 * @throws Exception
	 */
	public static String InputStreamToString(InputStream is, Charset charSet) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		String line = null;
		try {
			br = new BufferedReader(new InputStreamReader(is, charSet));
			while(null != (line = br.readLine()))
				sb.append(line + "\n");
			br.close();
		}
		finally {
			try {
				if (null != br)
					br.close();
			}catch(Exception e){}
		}
		
		return sb.toString();
	}

	/**
     * Decodes (reads) a String from an InputStream with a specified Character Set Name
     * 
     * This method blocks until the InputStream is closed.
     * 
	 * @param is
	 * @param charSetName
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static String InputStreamToString(InputStream is, String charSetName) throws IOException {		
		return InputStreamToString(is, Charset.forName(charSetName));
	}
	
	/**
	 * Converts the input stream to byte buffer
	 * 
	 * @param is
	 * @return bb
	 * 
	 * @throws IOException 
	 * 
	 */
	public static ByteBuffer InputStreamToByteBuffer(InputStream is) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte [] buf = new byte [1024];
		while (is.available()>0){
			int read = is.read(buf);
			out.write(buf, 0, read);
		}
		ByteBuffer bb = ByteBuffer.wrap(out.toByteArray());
		return bb;
	}
	
	/**
	 * Decodes (reads) a String from an InputStream
	 * 
	 * Compared to InputStreamToString, this method only reads data from
	 * an InputStream's while data is available.  It will return as soon 
	 * as the InputStream buffer is empty. It does not block until the 
	 * InputStream is closed
	 * 
	 * @param in
	 * 
	 * @return A String decoded from the InputStream using the platform's default character set
	 * 
	 * @throws IOException
	 */
	public static String read(InputStream in) throws IOException{
		//using a StringBuffer is more efficient then simple string concatenation
		StringBuffer readBuffer = new StringBuffer();
		while (in.available() > 0){
			byte [] buff = new byte [in.available()];
			int charsRead = in.read(buff);
			if (charsRead >0){
				readBuffer.append(new String(buff, 0, charsRead));
			}
		}
		return readBuffer.toString();
	}
	
	/**
	 * Handles closing a stream.
	 * Guarantees closing the stream object will not raise an exception.
	 * 
	 * @param closeable
	 */
	public static void close (java.io.Closeable closeable) {
	    if (closeable == null)
	        return;
	    
	    try {
	        closeable.close();
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	/**
     * Decodes a byte buffer using the default character set.
     * 
     * @param bb byte buffer
     * @return a decoded string
     * @throws IOException
     */
    public static String decodeBytes(byte[] bb) throws IOException {
        if (bb == null) {
            throw new IllegalArgumentException("null byte buffer");
        }
        return decodeBytes(ByteBuffer.wrap(bb));
    }

    /**
     * Decodes a byte buffer using the default character set.
     * 
     * @param bb byte buffer
     * @return a decoded string
     * @throws IOException
     */
    public static String decodeBytes(ByteBuffer bb) throws IOException {
        return decodeBytes(bb, Charset.defaultCharset());
    }

    /**
     * Decodes a byte buffer using a specific character set.
     * 
     * @param bb byte buffer
     * @param cset character set
     * @return a decoded string
     * @throws IOException
     */
    public static String decodeBytes(byte[] bb, Charset cset) throws IOException {
        if (bb == null) {
            throw new IllegalArgumentException("null byte buffer");
        }
        return decodeBytes(ByteBuffer.wrap(bb), cset);
    }

    /**
     * Decodes a byte buffer using a specific character set.
     * 
     * @param bb byte buffer
     * @param cset character set
     * @return a decoded string
     * @throws IOException
     */
    public static String decodeBytes(ByteBuffer bb, Charset cset) throws IOException {
        if (bb == null) {
            throw new IllegalArgumentException("null byte buffer");
        }
        StringBuilder buf = new StringBuilder(bb.remaining());
        decodeBytes(bb, cset, buf);
        return buf.toString();
    }

    /**
     * Decodes a byte buffer using a specific character set.
     * 
     * @param bb byte buffer
     * @param cset character set
     * @param target where to append the decoded stuff, for instance a StringBuilder
     * @throws IOException
     */
    public static void decodeBytes(ByteBuffer bb, Charset cset, Appendable target) throws IOException {
        if (bb == null) {
            throw new IllegalArgumentException("null byte buffer");
        }
        if (cset == null) {
            throw new IllegalArgumentException("null charset");
        }
        if (target == null) {
            throw new IllegalArgumentException("null target");
        }
        CharsetDecoder decoder = cset.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.REPLACE);
        decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        // this is a temp buffer where we drain the byte buffer into. 
        // When it gets full, we dump it in the appendable. This
        // is a lot of copying but I am not sure how can it be 
        // optimized. Ideally it should all work via streaming, but
        // at some point there will always be some sort of buffer in
        // memory. 
        CharBuffer cb = CharBuffer.allocate(1024);
        boolean eof;
        do {
            eof = !bb.hasRemaining();
            if (CoderResult.OVERFLOW == decoder.decode(bb, cb, eof)) {
                drainCharBuf(cb, target);
            }
        } while (!eof);

        // after the decode loop we have to flush the decoder in yet another loop...
        while (CoderResult.OVERFLOW == decoder.flush(cb)) {
            drainCharBuf(cb, target);
        }

        // ... and after we flushed, we still have to drain it. 
        drainCharBuf(cb, target);
    }

    private static void drainCharBuf(CharBuffer cb, Appendable target) throws IOException {
        cb.flip();
        if (cb.hasRemaining()) {
            target.append(cb.toString());
        }
        cb.clear();
    }



}

