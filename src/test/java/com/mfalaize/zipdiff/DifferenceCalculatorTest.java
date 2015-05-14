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

package com.mfalaize.zipdiff;

import com.mfalaize.zipdiff.output.Builder;
import com.mfalaize.zipdiff.output.HtmlBuilder;
import com.mfalaize.zipdiff.output.TextBuilder;
import com.mfalaize.zipdiff.output.XmlBuilder;
import org.junit.Test;

import java.io.*;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.Assert.*;

/**
 * @author jastewart
 */
public class DifferenceCalculatorTest {
    private static String ENTRYA = "A";
    private static String ENTRYB = "B";
    private static String ENTRYC = "C";
    private static String ENTRY_CVS = "CVS/Root";
    public static final String SYSTEM_TMP_DIR_PROPERTY = "java.io.tmpdir";
    public static final String TEST_DIR_POSTFIX = File.separator + "UnitTestsDifferenceCalculatorTest";
    private static String testDirPathName;
    // naming convention The Capital letter denotes the entry so A will be the same as A
    // OneEntry denotes that the jar has one entry
    private static String testJarOneEmpty;
    private static String testJarTwoEmpty;
    private static String testJarOneEntryA1Filename;
    private static String testJarOneEntryA2Filename;
    private static String testJarOneEntryB1Filename;
    private static String testJarTwoEntryAB1Filename;
    private static String testJarTwoEntryAC2Filename;
    private static String testJarOneEntryAContentsChangedFilename;

    {
        testDirPathName = "/Users/maxime/Documents/test";
        //System.getProperty(System.getProperty(SYSTEM_TMP_DIR_PROPERTY));
        if (testDirPathName == null) {
            testDirPathName = File.separator + "temp" + TEST_DIR_POSTFIX;
        }
        testJarOneEmpty = testDirPathName + File.separator + "testJarOneEmpty.jar";
        testJarTwoEmpty = testDirPathName + File.separator + "testJarTwoEmpty.jar";
        testJarOneEntryA1Filename = testDirPathName + File.separator + "testJarOneEntryA1Filename.jar";
        testJarOneEntryA2Filename = testDirPathName + File.separator + "testJarOneEntryA2Filename.jar";
        testJarOneEntryB1Filename = testDirPathName + File.separator + "testJarOneEntryB1Filename.jar";
        testJarTwoEntryAB1Filename = testDirPathName + File.separator + "testJarTwoEntryAB1Filename.jar";
        testJarTwoEntryAB1Filename = testDirPathName + File.separator + "testJarTwoEntryAB1Filename.jar";
        testJarTwoEntryAC2Filename = testDirPathName + File.separator + "testJarTwoEntryAC2Filename.jar";
        testJarOneEntryAContentsChangedFilename = testDirPathName + File.separator + "testJarOneEntryAContentsChangedFilename.jar";
    }

    /**
     * Create a jar with only one entry in it. That entry being A
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void createJarOneEntryA1() throws IOException {
        //  create a jar file with no duplicates
        File testDir = new File(testDirPathName);
        testDir.mkdirs();

        JarOutputStream testJarOS =
                new JarOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(testJarOneEntryA1Filename)));

        // ad an entry
        JarEntry entry1 = new JarEntry(ENTRYA);
        testJarOS.putNextEntry(entry1);
        byte data1[] = new byte[2048];
        for (int i = 0; i < data1.length; i++) {
            data1[i] = 'a';
        }
        testJarOS.write(data1);

        testJarOS.flush();
        testJarOS.close();
    }

    /**
     * Create a jar with only one entry in it. That entry being A
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void createJarOneEntryA2() throws IOException {
        //  create a jar file with no duplicates
        File testDir = new File(testDirPathName);
        testDir.mkdirs();
        JarOutputStream testJarOS =
                new JarOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(testJarOneEntryA2Filename)));

        // ad an entry
        JarEntry entry1 = new JarEntry(ENTRYA);
        testJarOS.putNextEntry(entry1);
        byte data1[] = new byte[2048];
        for (int i = 0; i < data1.length; i++) {
            data1[i] = 'a';
        }
        testJarOS.write(data1);

        testJarOS.flush();
        testJarOS.close();
    }

    /**
     * Create a jar with only one entry in it. That entry being A
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void createJarOneEntryAContentsChanged() throws IOException {
        //  create a jar file with no duplicates
        File testDir = new File(testDirPathName);
        testDir.mkdirs();
        JarOutputStream testJarOS =
                new JarOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(testJarOneEntryAContentsChangedFilename)));

        // add an entry
        JarEntry entry1 = new JarEntry(ENTRYA);
        testJarOS.putNextEntry(entry1);
        byte data1[] = new byte[2048];
        for (int i = 0; i < data1.length; i++) {
            data1[i] = 'a';
        }
        // set a different content so that it will come up as changed
        data1[data1.length - 1] = 'b';
        testJarOS.write(data1);

        // add another entry
        JarEntry cvs = new JarEntry(ENTRY_CVS);
        testJarOS.putNextEntry(cvs);

        testJarOS.flush();
        testJarOS.close();
    }

    /**
     * Create a jar with only one entry in it. That entry being A
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void createJarOneEntryB1() throws IOException {
        //  create a jar file with no duplicates
        File testDir = new File(testDirPathName);
        testDir.mkdirs();
        JarOutputStream testJarOS =
                new JarOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(testJarOneEntryB1Filename)));

        // ad an entry
        JarEntry entry1 = new JarEntry(ENTRYB);
        testJarOS.putNextEntry(entry1);
        byte data1[] = new byte[2048];
        for (int i = 0; i < data1.length; i++) {
            data1[i] = 'b';
        }
        testJarOS.write(data1);

        testJarOS.flush();
        testJarOS.close();
    }


    /*
     * Test for Differences calculateDifferences(ZipFile, ZipFile)
     * with the same file - no differences should be found
     */
    @Test
    public void testCalculateDifferencesSameZip() throws IOException {
        createJarOneEntryA1();
        DifferenceCalculator calc = new DifferenceCalculator(testJarOneEntryA1Filename, testJarOneEntryA1Filename);
        Differences differences = calc.getDifferences();
        assertFalse(differences.hasDifferences());
        Map addedEntries = differences.getAdded();
        assertTrue(addedEntries.size() == 0);
        Map removedEntries = differences.getRemoved();
        assertTrue(removedEntries.size() == 0);
        Map changedEntries = differences.getChanged();
        assertTrue(changedEntries.size() == 0);

        exerciseOutputBuilders(differences);

    }

    /*
     * Test for Differences calculateDifferences(ZipFile, ZipFile)
     */
    @Test
    public void testCalculateDifferencesZipsSameEntries() throws IOException {
        createJarOneEntryA1();
        createJarOneEntryA2();
        DifferenceCalculator calc = new DifferenceCalculator(testJarOneEntryA1Filename, testJarOneEntryA2Filename);
        Differences differences = calc.getDifferences();
        assertFalse(differences.hasDifferences());
        Map addedEntries = differences.getAdded();
        assertTrue(addedEntries.size() == 0);
        Map removedEntries = differences.getRemoved();
        assertTrue(removedEntries.size() == 0);
        Map changedEntries = differences.getChanged();
        assertTrue(changedEntries.size() == 0);

        exerciseOutputBuilders(differences);
    }

    /*
     * Test for Differences calculateDifferences(ZipFile, ZipFile)
     * Test that the differences between two zips with A in one and B in the second.
     * A will have been removed and B will have been added.
     */
    @Test
    public void testCalculateDifferencesZipsDifferentEntries() throws IOException {
        createJarOneEntryA1();
        createJarOneEntryB1();
        DifferenceCalculator calc = new DifferenceCalculator(testJarOneEntryA1Filename, testJarOneEntryB1Filename);
        Differences differences = calc.getDifferences();
        assertTrue(differences.hasDifferences());
        Map addedEntries = differences.getAdded();
        assertTrue(addedEntries.containsKey("B"));
        Map removedEntries = differences.getRemoved();
        assertTrue(removedEntries.containsKey("A"));
        Map changedEntries = differences.getChanged();
        assertTrue(changedEntries.size() == 0);

        exerciseOutputBuilders(differences);

    }


    /*
     * Test for Differences calculateDifferences(ZipFile, ZipFile)
     * Test that the differences between two zips with A in one and A in the second with different content.
     * A will have been removed and B will have been added.
     */
    @Test
    public void testCalculateDifferencesZipsSameEntriesDifferentContent() throws IOException {
        createJarOneEntryA1();
        createJarOneEntryAContentsChanged();
        DifferenceCalculator calc = new DifferenceCalculator(testJarOneEntryA1Filename, testJarOneEntryAContentsChangedFilename);
        calc.setIgnoreCVSFiles(true);
        Differences differences = calc.getDifferences();
        assertTrue(differences.hasDifferences());
        Map addedEntries = differences.getAdded();
        assertTrue(addedEntries.size() == 0);
        Map removedEntries = differences.getRemoved();
        assertTrue(removedEntries.size() == 0);
        Map changedEntries = differences.getChanged();
        assertTrue(changedEntries.containsKey("A"));

        exerciseOutputBuilders(differences);

    }

    private void exerciseHtmlBuilder(Differences differences) {
        assertNotNull(differences);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Builder b = new HtmlBuilder();
        b.build(baos, differences);

        assertTrue(baos.size() > 0);
    }

    private void exerciseXmlBuilder(Differences differences) {
        assertNotNull(differences);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Builder b = new XmlBuilder();
        b.build(baos, differences);

        assertTrue(baos.size() > 0);
    }

    private void exerciseTextBuilder(Differences differences) {
        assertNotNull(differences);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Builder b = new TextBuilder();
        b.build(baos, differences);

        assertTrue(baos.size() > 0);
    }

    private void exerciseOutputBuilders(Differences differences) {
        assertNotNull(differences);
        exerciseHtmlBuilder(differences);
        exerciseXmlBuilder(differences);
        exerciseTextBuilder(differences);
    }

}
