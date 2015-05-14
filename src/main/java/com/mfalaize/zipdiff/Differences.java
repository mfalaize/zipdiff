/*
 * 
 * 
 */
package com.mfalaize.zipdiff;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;

/**
 * Used to keep track of difference between 2 zip files.
 *
 * @author Sean C. Sullivan
 */
public class Differences {
    private final Map<String, ZipEntry> added = new HashMap<String, ZipEntry>();
    private final Map<String, ZipEntry> removed = new HashMap<String, ZipEntry>();
    private final Map<String, ZipEntry[]> changed = new HashMap<String, ZipEntry[]>();
    private final Map<String, ZipEntry> ignored = new HashMap<String, ZipEntry>();
    private String filename1;
    private String filename2;

    public Differences() {
        // todo 
    }

    public void setFilename1(String filename) {
        filename1 = filename;
    }

    public void setFilename2(String filename) {
        filename2 = filename;
    }

    public String getFilename1() {
        return filename1;
    }

    public String getFilename2() {
        return filename2;
    }

    public void fileAdded(String fqn, ZipEntry ze) {
        added.put(fqn, ze);
    }

    public void fileRemoved(String fqn, ZipEntry ze) {
        removed.put(fqn, ze);
    }

    public void fileIgnored(String fqn, ZipEntry ze) {
        ignored.put(fqn, ze);
    }

    public void fileChanged(String fqn, ZipEntry z1, ZipEntry z2) {
        ZipEntry[] entries = new ZipEntry[2];
        entries[0] = z1;
        entries[1] = z2;
        changed.put(fqn, entries);
    }

    public Map<String, ZipEntry> getAdded() {
        return added;
    }

    public Map<String, ZipEntry> getRemoved() {
        return removed;
    }

    public Map<String, ZipEntry[]> getChanged() {
        return changed;
    }

    public Map<String, ZipEntry> getIgnored() {
        return ignored;
    }

    public boolean hasDifferences() {
        return ((getChanged().size() > 0) || (getAdded().size() > 0) || (getRemoved().size() > 0));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (added.size() == 1) {
            sb.append("1 file was");
        } else {
            sb.append(added.size()).append(" files were");
        }
        sb.append(" added to ").append(this.getFilename2()).append("\n");

        Iterator iter = added.keySet().iterator();
        while (iter.hasNext()) {
            String name = (String) iter.next();
            sb.append("\t[added] ").append(name).append("\n");
        }

        if (removed.size() == 1) {
            sb.append("1 file was");
        } else {
            sb.append(removed.size()).append(" files were");
        }
        sb.append(" removed from ").append(this.getFilename2()).append("\n");

        iter = removed.keySet().iterator();
        while (iter.hasNext()) {
            String name = (String) iter.next();
            sb.append("\t[removed] ").append(name).append("\n");
        }

        if (changed.size() == 1) {
            sb.append("1 file changed\n");
        } else {
            sb.append(changed.size()).append(" files changed\n");
        }

        Set keys = getChanged().keySet();
        iter = keys.iterator();
        while (iter.hasNext()) {
            String name = (String) iter.next();
            ZipEntry[] entries = (ZipEntry[]) getChanged().get(name);
            sb.append("\t[changed] ").append(name).append(" ");
            sb.append(" ( size ").append(entries[0].getSize());
            sb.append(" : ").append(entries[1].getSize());
            sb.append(" )\n");
        }
        int differenceCount = added.size() + changed.size() + removed.size();

        sb.append("Total differences: ").append(differenceCount);
        return sb.toString();
    }

}
