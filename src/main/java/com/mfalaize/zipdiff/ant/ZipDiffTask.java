/**
 * Copyright (C) 2015 Maxime Falaize (maxime.falaize@gmail.com)
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * 
 * 
 */

package com.mfalaize.zipdiff.ant;

import com.mfalaize.zipdiff.DifferenceCalculator;
import com.mfalaize.zipdiff.Differences;
import com.mfalaize.zipdiff.output.Builder;
import com.mfalaize.zipdiff.output.HtmlBuilder;
import com.mfalaize.zipdiff.output.TextBuilder;
import com.mfalaize.zipdiff.output.XmlBuilder;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * @author Sean C. Sullivan
 */
public class ZipDiffTask extends Task {
    private String filename1;
    private String filename2;
    private String destfile;
    private boolean ignoreTimestamps = false;
    private boolean ignoreCVSFiles = false;
    private boolean compareCRCValues = true;

    public void setFilename1(String name) {
        filename1 = name;
    }

    public void setFilename2(String name) {
        filename2 = name;
    }

    public void setIgnoreTimestamps(boolean b) {
        ignoreTimestamps = b;
    }

    public boolean getIgnoreTimestamps() {
        return ignoreTimestamps;
    }

    public void setIgnoreCVSFiles(boolean b) {
        ignoreCVSFiles = b;
    }

    public boolean getIgnoreCVSFiles() {
        return ignoreCVSFiles;
    }

    public void setCompareCRCValues(boolean b) {
        compareCRCValues = b;
    }

    public boolean getCompareCRCValues() {
        return compareCRCValues;
    }

    public void execute() throws BuildException {
        validate();

        // this.log("Filename1=" + filename1, Project.MSG_DEBUG);
        // this.log("Filename2=" + filename2, Project.MSG_DEBUG);
        // this.log("destfile=" + getDestFile(), Project.MSG_DEBUG);

        Differences d = calculateDifferences();

        try {
            writeDestFile(d);
        } catch (java.io.IOException ex) {
            throw new BuildException(ex);
        }

    }

    protected void writeDestFile(Differences d) throws java.io.IOException {
        String destfilename = getDestFile();

        Builder builder;

        if (destfilename.endsWith(".html")) {
            builder = new HtmlBuilder();
        } else if (destfilename.endsWith(".xml")) {
            builder = new XmlBuilder();
        } else {
            builder = new TextBuilder();
        }

        builder.build(destfilename, d);
    }

    public String getDestFile() {
        return destfile;
    }

    public void setDestFile(String name) {
        destfile = name;
    }

    protected Differences calculateDifferences() throws BuildException {
        DifferenceCalculator calculator;

        Differences d;

        try {
            calculator = new DifferenceCalculator(filename1, filename2);
            calculator.setCompareCRCValues(getCompareCRCValues());
            calculator.setIgnoreTimestamps(getIgnoreTimestamps());
            calculator.setIgnoreCVSFiles(getIgnoreCVSFiles());

            // todo : calculator.setFilenamesToIgnore(patterns);

            d = calculator.getDifferences();
        } catch (java.io.IOException ex) {
            throw new BuildException(ex);
        }

        return d;
    }

    protected void validate() throws BuildException {
        if ((filename1 == null) || (filename1.length() < 1)) {
            throw new BuildException("filename1 is required");
        }

        if ((filename2 == null) || (filename2.length() < 1)) {
            throw new BuildException("filename2 is required");
        }

        String destinationfile = getDestFile();

        if ((destinationfile == null) || (destinationfile.length() < 1)) {
            throw new BuildException("destfile is required");
        }
    }

}
