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
package com.mfalaize.zipdiff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Checks and compiles differences between two zip files.
 * It also has the ability to exclude entries from the comparison
 * based on a regular expression.
 *
 * @author Sean C. Sullivan
 */
public class DifferenceCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DifferenceCalculator.class);

    private ZipFile file1;
    private ZipFile file2;
    private boolean ignoreTimestamps = false;
    private boolean ignoreCVSFiles = false;
    private boolean compareCRCValues = true;
    private Pattern filesToIgnorePattern;

    /**
     * Constructor taking 2 filenames to compare
     *
     * @throws java.io.IOException
     */
    public DifferenceCalculator(String filename1, String filename2) throws java.io.IOException {
        this(new File(filename1), new File(filename2));
    }

    /**
     * Constructor taking 2 Files to compare
     *
     * @throws java.io.IOException
     */
    public DifferenceCalculator(File f1, File f2) throws java.io.IOException {
        this(new ZipFile(f1), new ZipFile(f2));
    }

    /**
     * Constructor taking 2 ZipFiles to compare
     */
    public DifferenceCalculator(ZipFile zf1, ZipFile zf2) {
        file1 = zf1;
        file2 = zf2;
    }

    /**
     * @param patterns A set of regular expressions that when matched against a ZipEntry
     *                 then that ZipEntry will be ignored from the comparison.
     * @see java.util.regex
     */
    public void setFilenameRegexToIgnore(Set<String> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            filesToIgnorePattern = null;
        } else {
            String regex = "";

            for (String pattern : patterns) {
                if (regex.length() > 0) {
                    regex += "|";
                }
                regex += "(" + pattern + ")";
            }
            filesToIgnorePattern = Pattern.compile(regex);
            LOGGER.debug("Regular expression is : " + regex);
        }
    }

    /**
     * returns true if fileToIgnorePattern matches the filename given.
     *
     * @param filepath  The file path
     * @param entryName The name of the file to check to see if it should be ignored.
     * @return true if the file should be ignored.
     */
    protected boolean ignoreThisFile(String filepath, String entryName) {
        if (entryName == null) {
            return false;
        } else if (isCVSFile(filepath, entryName) && (ignoreCVSFiles())) {
            return true;
        } else if (filesToIgnorePattern == null) {
            return false;
        } else {
            Matcher m = filesToIgnorePattern.matcher(entryName);
            boolean match = m.matches();
            if (match) {
                LOGGER.debug("Found a match against : " + entryName + " so excluding");
            }
            return match;
        }
    }

    protected boolean isCVSFile(String filepath, String entryName) {
        return entryName != null && ((filepath.contains("CVS/")) || (entryName.contains("CVS/")));
    }

    /**
     * Ensure that the comparison checks against the CRCs of the entries.
     *
     * @param b true ensures that CRCs will be checked
     */
    public void setCompareCRCValues(boolean b) {
        compareCRCValues = b;
    }

    /**
     * @return true if this instance will check the CRCs of each ZipEntry
     */
    public boolean getCompareCRCValues() {
        return compareCRCValues;
    }

    /**
     * Opens the ZipFile and builds up a map of all the entries. The key is the name of
     * the entry and the value is the ZipEntry itself.
     *
     * @param zf The ZipFile for which to build up the map of ZipEntries
     * @return The map containing all the ZipEntries. The key being the name of the ZipEntry.
     * @throws java.io.IOException
     */
    protected Map<String, ZipEntry> buildZipEntryMap(ZipFile zf) throws java.io.IOException {
        Map<String, ZipEntry> zipEntryMap = new HashMap<String, ZipEntry>();
        try {
            Enumeration<? extends ZipEntry> entries = zf.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                InputStream is = null;
                try {
                    is = zf.getInputStream(entry);
                    processZipEntry("", entry, is, zipEntryMap);
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
            }
        } finally {
            zf.close();
        }

        return zipEntryMap;
    }

    /**
     * Will place ZipEntries for a given ZipEntry into the given Map. More ZipEntries will result
     * if zipEntry is itself a ZipFile. All embedded ZipFiles will be processed with their names
     * prefixed onto the names of their ZipEntries.
     *
     * @param prefix      The prefix of the ZipEntry that should be added to the key. Typically used
     *                    when processing embedded ZipFiles. The name of the embedded ZipFile would be the prefix of
     *                    all the embedded ZipEntries.
     * @param zipEntry    The ZipEntry to place into the Map. If it is a ZipFile then all its ZipEntries
     *                    will also be placed in the Map.
     * @param is          The InputStream of the corresponding ZipEntry.
     * @param zipEntryMap The Map in which to place all the ZipEntries into. The key will
     *                    be the name of the ZipEntry.
     * @throws IOException
     */
    protected void processZipEntry(String prefix, ZipEntry zipEntry, InputStream is, Map<String, ZipEntry> zipEntryMap) throws IOException {
        if (ignoreThisFile(prefix, zipEntry.getName())) {
            LOGGER.debug("ignoring file: " + zipEntry.getName());
        } else {
            String name = prefix + zipEntry.getName();

            LOGGER.debug("processing ZipEntry: " + name);

            if (zipEntry.isDirectory()) {
                zipEntryMap.put(name, zipEntry);
            } else if (isZipFile(name)) {
                processEmbeddedZipFile(zipEntry.getName() + "/", is, zipEntryMap);
                zipEntryMap.put(name, zipEntry);
            } else {
                zipEntryMap.put(name, zipEntry);
            }
        }
    }

    protected void processEmbeddedZipFile(String prefix, InputStream is, Map<String, ZipEntry> m) throws java.io.IOException {
        ZipInputStream zis = new ZipInputStream(is);

        ZipEntry entry = zis.getNextEntry();

        while (entry != null) {
            processZipEntry(prefix, entry, zis, m);
            zis.closeEntry();
            entry = zis.getNextEntry();
        }

    }

    /**
     * Returns true if the filename has a valid zip extension.
     * i.e. jar, war, ear, zip etc.
     *
     * @param filename The name of the file to check.
     * @return true if it has a valid extension.
     */
    public static boolean isZipFile(String filename) {
        boolean result;

        if (filename == null) {
            result = false;
        } else {
            String lowercaseName = filename.toLowerCase();
            result = lowercaseName.endsWith(".zip") || lowercaseName.endsWith(".ear") || lowercaseName.endsWith(".war")
                    || lowercaseName.endsWith(".rar") || lowercaseName.endsWith(".jar");
        }

        return result;
    }

    /**
     * Calculates all the differences between two zip files.
     * It builds up the 2 maps of ZipEntries for the two files
     * and then compares them.
     *
     * @param zf1 The first ZipFile to compare
     * @param zf2 The second ZipFile to compare
     * @return All the differences between the two files.
     * @throws java.io.IOException
     */
    protected Differences calculateDifferences(ZipFile zf1, ZipFile zf2) throws java.io.IOException {
        Map<String, ZipEntry> map1 = buildZipEntryMap(zf1);
        Map<String, ZipEntry> map2 = buildZipEntryMap(zf2);

        return calculateDifferences(map1, map2);
    }

    /**
     * Given two Maps of ZipEntries it will generate a Differences of all the
     * differences found between the two maps.
     *
     * @return All the differences found between the two maps
     */
    protected Differences calculateDifferences(Map<String, ZipEntry> m1, Map<String, ZipEntry> m2) {
        Differences d = new Differences();

        Set<String> names1 = m1.keySet();
        Set<String> names2 = m2.keySet();

        Set<String> allNames = new HashSet<String>();
        allNames.addAll(names1);
        allNames.addAll(names2);

        for (String name : allNames) {
            if (!ignoreThisFile("", name)) {
                if (names1.contains(name) && (!names2.contains(name))) {
                    d.fileRemoved(name, m1.get(name));
                } else if (names2.contains(name) && (!names1.contains(name))) {
                    d.fileAdded(name, m2.get(name));
                } else if (names1.contains(name) && (names2.contains(name))) {
                    ZipEntry entry1 = m1.get(name);
                    ZipEntry entry2 = m2.get(name);
                    if (!entriesMatch(entry1, entry2)) {
                        d.fileChanged(name, entry1, entry2);
                    }
                } else {
                    throw new IllegalStateException("unexpected state");
                }
            }
        }

        return d;
    }

    /**
     * returns true if the two entries are equivalent in type, name, size, compressed size
     * and time or CRC.
     *
     * @param entry1 The first ZipEntry to compare
     * @param entry2 The second ZipEntry to compare
     * @return true if the entries are equivalent.
     */
    protected boolean entriesMatch(ZipEntry entry1, ZipEntry entry2) {
        boolean result;

        result =
                (entry1.isDirectory() == entry2.isDirectory())
                        && (entry1.getSize() == entry2.getSize())
                        && (entry1.getCompressedSize() == entry2.getCompressedSize())
                        && (entry1.getName().equals(entry2.getName()));

        if (!isIgnoringTimestamps()) {
            result = result && (entry1.getTime() == entry2.getTime());
        }

        if (getCompareCRCValues()) {
            result = result && (entry1.getCrc() == entry2.getCrc());
        }
        return result;
    }

    public void setIgnoreTimestamps(boolean b) {
        ignoreTimestamps = b;
    }

    public boolean isIgnoringTimestamps() {
        return ignoreTimestamps;
    }

    public boolean ignoreCVSFiles() {
        return ignoreCVSFiles;
    }

    public void setIgnoreCVSFiles(boolean b) {
        ignoreCVSFiles = b;
    }

    /**
     * @return all the differences found between the two zip files.
     * @throws java.io.IOException
     */
    public Differences getDifferences() throws java.io.IOException {
        Differences d = calculateDifferences(file1, file2);
        d.setFilename1(file1.getName());
        d.setFilename2(file2.getName());

        return d;
    }
}
