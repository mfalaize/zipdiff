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
