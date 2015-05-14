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
package com.mfalaize.zipdiff.output;

import com.mfalaize.zipdiff.Differences;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Set;

/**
 * Generates html output for a Differences instance
 *
 * @author Sean C. Sullivan
 */
public class HtmlBuilder extends AbstractBuilder {

    public void build(OutputStream out, Differences d) {
        PrintWriter pw = new PrintWriter(out);

        pw.println("<html>");
        pw.println("<META http-equiv=\"Content-Type\" content=\"text/html\">");
        pw.println("<head>");
        pw.println("<title>File differences</title>");
        pw.println("</head>");

        pw.println("<body text=\"#000000\" vlink=\"#000000\" alink=\"#000000\" link=\"#000000\">");

        pw.println(getStyleTag());
        pw.print("<p>First file: ");
        String filename1 = d.getFilename1();

        if (filename1 == null) {
            filename1 = "filename1.zip";
        }
        pw.print(filename1);
        pw.println("<br>");

        pw.print("Second file: ");

        String filename2 = d.getFilename2();

        if (filename2 == null) {
            filename2 = "filename2.zip";
        }
        pw.print(filename2);
        pw.println("</p>");

        writeAdded(pw, d.getAdded().keySet());
        writeRemoved(pw, d.getRemoved().keySet());
        writeChanged(pw, d.getChanged().keySet());
        pw.println("<hr>");
        pw.println("<p>");
        pw.println("Generated at " + new java.util.Date());
        pw.println("</p>");
        pw.println("</body>");

        pw.println("</html>");

        pw.flush();

    }

    protected void writeAdded(PrintWriter pw, Set<String> added) {
        writeDiffSet(pw, "Added", added);
    }

    protected void writeRemoved(PrintWriter pw, Set<String> removed) {
        writeDiffSet(pw, "Removed", removed);
    }

    protected void writeChanged(PrintWriter pw, Set<String> changed) {
        writeDiffSet(pw, "Changed", changed);
    }

    protected void writeDiffSet(PrintWriter pw, String name, Set<String> s) {
        pw.println("<TABLE CELLSPACING=\"1\" CELLPADDING=\"3\" WIDTH=\"100%\" BORDER=\"0\">");
        pw.println("<tr>");
        pw.println("<td class=\"diffs\" colspan=\"2\">" + name + " (" + s.size() + " entries)</td>");
        pw.println("</tr>");
        pw.println("<tr>");
        pw.println("<td width=\"20\">");
        pw.println("</td>");
        pw.println("<td>");
        if (s.size() > 0) {
            pw.println("<ul>");
            for (String key : s) {
                pw.print("<li>");
                pw.print(key);
                pw.println("</li>");
            }
            pw.println("</ul>");
        }
        pw.println("</td>");
        pw.println("</tr>");
        pw.println("</table>");

    }

    protected String getStyleTag() {
        StringBuilder sb = new StringBuilder();

        sb.append("<style type=\"text/css\">");
        sb.append(" body, p { ");
        sb.append(" font-family: verdana,arial,helvetica; ");
        sb.append(" font-size: 80%; ");
        sb.append(" color:#000000; ");
        sb.append(" } \n");
        sb.append(" 	  .diffs { \n");
        sb.append("         font-family: verdana,arial,helvetica; \n");
        sb.append("         font-size: 80%; \n");
        sb.append(" font-weight: bold; \n");
        sb.append(" text-align:left; \n");
        sb.append(" background:#a6caf0; \n");
        sb.append(" } \n");
        sb.append(" tr, td { \n");
        sb.append(" font-family: verdana,arial,helvetica; \n");
        sb.append(" font-size: 80%; \n");
        sb.append(" background:#eeeee0; \n");
        sb.append(" } \n");
        sb.append(" </style>\n");

        return sb.toString();
    }

}
