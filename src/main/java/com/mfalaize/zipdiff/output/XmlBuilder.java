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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.util.Set;

/**
 * Generates xml output for a Differences instance
 *
 * @author Sean C. Sullivan
 */
public class XmlBuilder extends AbstractBuilder {

    public void build(OutputStream out, Differences d) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Element root = doc.createElement("zipdiff");
            doc.appendChild(root);
            root.setAttribute("filename1", d.getFilename1() == null ? "filename1.zip" : d.getFilename1());
            root.setAttribute("filename2", d.getFilename2() == null ? "filename2.zip" : d.getFilename2());

            Element diff = doc.createElement("differences");
            root.appendChild(diff);
            writeAdded(doc, diff, d.getAdded().keySet());
            writeRemoved(doc, diff, d.getRemoved().keySet());
            writeChanged(doc, diff, d.getChanged().keySet());

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(doc), new StreamResult(out));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    protected void writeAdded(Document doc, Element diff, Set<String> added) {
        for (String key : added) {
            Element el = doc.createElement("added");
            diff.appendChild(el);
            el.setTextContent(key);
        }

    }

    protected void writeRemoved(Document doc, Element diff, Set<String> removed) {
        for (String key : removed) {
            Element el = doc.createElement("removed");
            diff.appendChild(el);
            el.setTextContent(key);
        }
    }

    protected void writeChanged(Document doc, Element diff, Set<String> changed) {
        for (String key : changed) {
            Element el = doc.createElement("changed");
            diff.appendChild(el);
            el.setTextContent(key);
        }
    }

}
