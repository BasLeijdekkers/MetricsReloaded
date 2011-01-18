package com.sixrr.stockmetrics;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;

import java.util.Set;

public interface MethodCallMap {
    Set<PsiReference> calculateMethodCallPoints(PsiMethod method);

    Set<PsiReference> calculateTestMethodCallPoints(PsiMethod method);

    Set<PsiReference> calculateProductMethodCallPoints(PsiMethod method);
}
