/*
 * 
 * 
 */
package com.mfalaize.zipdiff.output;

import com.mfalaize.zipdiff.Differences;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Sean C. Sullivan
 */
public abstract class AbstractBuilder
        implements Builder {
    public void build(String filename, Differences d) throws IOException {
        FileOutputStream fos;

        fos = new FileOutputStream(filename);
        build(fos, d);
        fos.flush();
    }

    public abstract void build(OutputStream out, Differences d);
}
