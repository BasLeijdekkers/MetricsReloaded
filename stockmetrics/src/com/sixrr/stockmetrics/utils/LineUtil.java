/*
 * Copyright 2005-2021 Sixth and Red River Software, Bas Leijdekkers
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

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LineUtil {

    private LineUtil() {}

    public static int countLines(@Nullable PsiElement element) {
        if (element == null || element instanceof PsiCompiledElement) {
            return 0;
        }
        return countLines(element.getText());
    }

    static int countLines(String text) {
        if (text == null) {
            return 0;
        }
        int lines = 0;
        boolean onEmptyLine = true;
        final char[] chars = text.toCharArray();
        for (char aChar : chars) {
            if (aChar == '\n' || aChar == '\r') {
                if (!onEmptyLine) {
                    lines++;
                    onEmptyLine = true;
                }
            } else if (aChar != ' ' && aChar != '\t') {
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

    public static String getCommentText(PsiComment comment) {
        if (comment instanceof PsiDocComment) {
            final PsiDocComment docComment = (PsiDocComment)comment;
            final StringBuilder result = new StringBuilder();
            for (PsiElement element : docComment.getDescriptionElements()) {
                result.append(element.getText());
            }
            return result.toString();
        }
        else {
            final IElementType type = comment.getTokenType();
            final String text = comment.getText();
            return (type == JavaTokenType.END_OF_LINE_COMMENT)
                    ? StringUtil.trimStart(text, "//")
                    : StringUtil.trimEnd(text.substring(2), "*/");
        }
    }

    public static int calculateCommentLinesOfCode(PsiElement element) {
        final CommentLinesOfCodeProcessor processor = new CommentLinesOfCodeProcessor();
        PsiTreeUtil.processElements(element, processor);
        return processor.count;
    }

    private static class CommentLinesOfCodeProcessor implements PsiElementProcessor<PsiElement> {
        private boolean newline = true;
        int count = 0;

        @Override
        public boolean execute(@NotNull PsiElement e) {
            if (e instanceof PsiWhiteSpace) {
                newline |= StringUtil.containsChar(e.getText(), '\n');
            } else if (e instanceof PsiComment) {
                count += StringUtil.countChars(getCommentText((PsiComment) e), '\n');
                if (newline) {
                    count++;
                    newline = false;
                }
            }
            return true;
        }
    }
}
