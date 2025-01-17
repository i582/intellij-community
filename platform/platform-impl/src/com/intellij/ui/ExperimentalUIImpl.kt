// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.ui

import com.fasterxml.jackson.jr.ob.JSON
import java.io.IOException
import java.util.*

/**
 * @author Konstantin Bulenkov
 */
class ExperimentalUIImpl : ExperimentalUI() {
  override fun loadIconMappings() = loadIconMappingsImpl()

  companion object {
    @JvmStatic
    fun loadIconMappingsImpl(): Map<String, String> {
      val json = JSON.builder().enable().enable(JSON.Feature.READ_ONLY).build()
      try {
        val fin = Objects.requireNonNull(ExperimentalUIImpl::class.java.getResource("ExpUIIconMapping.json")).openStream()
        return mutableMapOf<String, String>().apply { readDataFromJson(json.mapFrom(fin), "", this) }
      }
      catch (ignore: IOException) {
      }
      return emptyMap()
    }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    private fun readDataFromJson(json: Map<String, Any>, prefix: String, result: MutableMap<String, String>) {
      json.forEach { (key, value) ->
        when (value) {
          is String -> result[value] = prefix + key
          is Map<*, *> -> readDataFromJson(value as Map<String, Any>, "$prefix$key/", result)
          is List<*> -> value.forEach { result[it as String] = "$prefix$key" }
        }
      }
    }
  }
}