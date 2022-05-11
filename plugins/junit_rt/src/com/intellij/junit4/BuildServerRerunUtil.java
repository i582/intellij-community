// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.junit4;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

public class BuildServerRerunUtil extends Filter {
  @Override
  public boolean shouldRun(Description description) {
    if (!description.isTest()) return true;

    if (description.getMethodName().contains("testTopLevelPropertyUsages")) return true;

    return false;
  }

  @Override
  public String describe() {
    return "Some filter";
  }
}
