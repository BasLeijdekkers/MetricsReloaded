/*
 * Copyright 2005-2016 Sixth and Red River Software, Bas Leijdekkers
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

import com.intellij.psi.*;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.util.StringTokenizer;

public final class LineUtil {

    private LineUtil() {}

    public static int countLines(PsiElement element) {
        if (element instanceof PsiCompiledElement) {
            return 0;
        }
        final String text = element.getText();
        element.getContainingFile().getVirtualFile().getDetectedLineSeparator();
        return countLines(text);
    }

    public static int countBlankLines(PsiElement element) {
        if (element instanceof PsiCompiledElement) {
            return 0;
        }
        final String text = element.getText();
        final String lineSeparator = text.contains("\r")? "\r" : "\n";
        final int totalLinesCount = 1 + StringUtils.countMatches(text, lineSeparator);
        return totalLinesCount - countLines(text);
    }

    static int countLines(String text) {
        int lines = 0;
        boolean onEmptyLine = true;
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
        if (!onEmptyLine) {
            lines++;
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

    private static boolean endsInLineBreak(PsiElement element) {
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
        return text != null && (text.contains("\n") || text.contains("\r"));
    }
}
