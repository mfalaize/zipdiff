/*
 * 
 * 
 */

package com.mfalaize.zipdiff.output;

import com.mfalaize.zipdiff.Differences;

import java.io.OutputStream;

/**
 * Builder pattern: <a href="http://wiki.cs.uiuc.edu/patternStories/BuilderPattern">
 * http://wiki.cs.uiuc.edu/patternStories/BuilderPattern</a>
 *
 * @author Sean C. Sullivan
 */
public interface Builder {
    void build(OutputStream out, Differences d);

    void build(String filename, Differences d) throws java.io.IOException;
}
