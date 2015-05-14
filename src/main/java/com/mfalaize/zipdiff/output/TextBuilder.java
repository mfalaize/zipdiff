/*
 * 
 * 
 */
package com.mfalaize.zipdiff.output;

import com.mfalaize.zipdiff.Differences;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * @author Sean C. Sullivan
 */
public class TextBuilder extends AbstractBuilder {
    public void build(OutputStream out, Differences d) {
        PrintWriter pw = new PrintWriter(out);
        pw.println(d.toString());
        pw.flush();
    }
}
