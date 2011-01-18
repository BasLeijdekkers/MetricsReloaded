/*
 * Copyright 2005, Sixth and Red River Software
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

package com.sixrr.stockmetrics.utils;

import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;

public class LineUtil {
    private LineUtil() {
        super();
    }

    public static int countLines(PsiElement element) {
        final String text = element.getText();
        return countLines(text);
    }

    private static int countLines(String text) {
        int lines = 1;
        boolean onEmptyLine = false;
        final char[] chars = text.toCharArray();
        for (final char aChar : chars) {
            if (aChar == '\n' || aChar == '\r') {
                if (!onEmptyLine) {
                    lines++;
                    onEmptyLine = true;
                }
            } else if (aChar == ' ' || aChar == '\t') {
                //don't do anything
            } else {
                onEmptyLine = false;
            }
        }
        return lines;
    }

    public static int countCommentOnlyLines(PsiComment comment) {
        final String text = comment.getText();
        int totalLines = countLines(text);
        boolean isOnSameLineBeforeCode = false;
        if (!endsInLineBreak(comment)) {
            PsiElement nextSibling = comment.getNextSibling();

            while (nextSibling != null) {
                if (nextSibling instanceof PsiComment ||
                        nextSibling instanceof PsiWhiteSpace) {
                    if (containsLineBreak(nextSibling)) {
                        break;
                    }
                } else {
                    isOnSameLineBeforeCode = true;
                }
                nextSibling = nextSibling.getNextSibling();
            }
        }
        boolean isOnSameLineAfterCode = false;
        PsiElement prevSibling = comment.getPrevSibling();
        while (prevSibling != null) {
            if (prevSibling instanceof PsiComment ||
                    prevSibling instanceof PsiWhiteSpace) {
                if (containsLineBreak(prevSibling)) {
                    break;
                }
            } else {
                isOnSameLineAfterCode = true;
            }
            prevSibling = prevSibling.getPrevSibling();
        }

        if (isOnSameLineAfterCode) {
            totalLines = Math.max(totalLines - 1, 0);
        }
        if (isOnSameLineBeforeCode) {
            totalLines = Math.max(totalLines - 1, 0);
        }

        return totalLines;
    }

    private static boolean endsInLineBreak(PsiComment element) {
        if (element == null) {
            return false;
        }
        final String text = element.getText();
        if (text == null) {
            return false;
        }
        final char endChar = text.charAt(text.length() - 1);
        return endChar == '\n' || endChar == '\r';
    }

    private static boolean containsLineBreak(PsiElement element) {
        if (element == null) {
            return false;
        }
        final String text = element.getText();
        if (text == null) {
            return false;
        }
        return text.indexOf("\n") >= 0 || text.indexOf("\r") >= 0;
    }
}
