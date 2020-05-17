/*
 * Copyright 2005-2020 Sixth and Red River Software, Bas Leijdekkers
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.util.*;
import java.io.*;s

public class Simple {

    /**
     * this is some javadoc
     * no params
     */
    public Simple() {
        super();
    }

    public void a() { System.out.println(); }
    
    void f(List<String> l, String a) throws IOException {
        String detail = "\n- " + "entry.getKey()" + ": " + "stringify(entry.getValue())";
        l.stream().anyMatch(/*1*/s/*2*/ ->/*3*/s/*4*/!=/*5*/ null/*6*/ &&/*7*/!s./*8*/isEmpty()/*9*//*10*/);
        l.stream().filter(s -> s != null).anyMatch(s -> !s.isEmpty());
        try (InputStream in = new FileInputStream("")){
            new Object() {{
                int J;
                /*
                 */
                System.out.println(in);
            }};
        } catch (RuntimeException | AssertionError e) {
            class X {
                void m() {
                    System.out.println(e);
                }
            }
        }
    }
}