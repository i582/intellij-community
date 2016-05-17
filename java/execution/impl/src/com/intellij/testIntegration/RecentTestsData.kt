/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.testIntegration

import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.testframework.sm.runner.states.TestStateInfo
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.util.containers.ContainerUtil
import java.util.*

class RecentTestsData {

  private val suitePacks = hashMapOf<String, SuitePackInfo>()
  private var testsWithoutSuites: MutableList<TestInfo> = ContainerUtil.newArrayList<TestInfo>()

  fun addSuite(url: String,
               magnitude: TestStateInfo.Magnitude,
               runDate: Date,
               runConfiguration: RunnerAndConfigurationSettings) 
  {

    val suiteInfo = SuiteInfo(url, magnitude, runDate)

    val suitePack = suitePacks[runConfiguration.uniqueID]
    if (suitePack != null) {
      suitePack.addSuite(suiteInfo)
      return
    }
    
    suitePacks[runConfiguration.uniqueID] = SuitePackInfo(runConfiguration, suiteInfo)
  }

  fun addTest(url: String, magnitude: TestStateInfo.Magnitude, runDate: Date) {
    val testInfo = TestInfo(url, magnitude, runDate)

    val suite = findSuite(url)
    if (suite != null) {
      suite.addTest(testInfo)
      return
    }

    testsWithoutSuites.add(testInfo)
  }

  private fun findSuite(url: String): SuiteInfo? {
    val testName = VirtualFileManager.extractPath(url)

    suitePacks.values.forEach {
      it.suites.forEach {
        if (testName.startsWith(it.suiteName)) {
          return it
        }
      }
    }
    
    return null
  }

  fun getTestsToShow(): List<RecentTestsPopupEntry> {
    distributeUnmatchedTests()
    val packsByDate = suitePacks.values.sortedByDescending { it.runDate }
    return packsByDate.fold(listOf(), { list, pack -> list + pack.entriesToShow() })
  }

  private fun distributeUnmatchedTests() {
    val noSuites = ContainerUtil.newSmartList<TestInfo>()

    for (test in testsWithoutSuites) {
      val url = test.url
      val suite = findSuite(url)
      if (suite != null) {
        suite.addTest(test)
      }
      else {
        noSuites.add(test)
      }
    }

    testsWithoutSuites = noSuites
  }
}

fun SuitePackInfo.entriesToShow(): List<RecentTestsPopupEntry> {
  if (suites.size == 1) {
    return suites[0].entriesToShow()
  }

  val failedSuites = suites.filter { it.failedTests.size > 0 }
  if (failedSuites.size == 0) {
    return listOf(this)
  }
  return failedSuites + this
}

fun SuiteInfo.entriesToShow(): List<RecentTestsPopupEntry> {
  val failed = failedTests
  if (failed.size > 0) {
    return failed.sortedByDescending { it.runDate } + this
  }
  return listOf(this)
}