package io.cloudslang.content.rft.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * This class contains all static methods for basic File operations:
 *
 * Services provided:
 *   1) Read/Write to text files.
 *   2) Creating/Copying/Deleting Files/Folders
 *   3) File name/path operations
 *
 * This file has a test case in the ../test folder
 */
public class FSUtil {

    public static void copyFile(File src, File dest) throws IOException {
        // Create destination folders as necessary
        dest.getParentFile().mkdirs();

        // Uses NIO
        FileChannel input  = null;
        FileChannel output = null;

        try{
            input  = new FileInputStream(src).getChannel();

            if (dest.isDirectory())
                dest = new File(dest, src.getName());
            output = new FileOutputStream(dest).getChannel();

            output.transferFrom(input, 0, input.size());
        }
        finally{
            if (input != null)
                input.close();
            if (output != null)
                output.close();
        }
    }

    /**
     * Copy directory trees.
     *
     * @param src
     * @param dest
     * @throws IOException
     */
    public static void recursiveCopy(File src, File dest) throws IOException {
        // Copy directory
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdirs();
            }

            // Process children
            for (String child : src.list())
                recursiveCopy(new File(src, child), new File(dest, child));
        }
        else { // Copy file
            copyFile(src, dest);
        }
    }
}
