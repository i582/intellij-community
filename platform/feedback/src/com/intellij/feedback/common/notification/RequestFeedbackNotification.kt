// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.feedback.common.notification

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.util.NlsSafe

/**
 * Basic notification for feedback requests
 */

class RequestFeedbackNotification(@NlsSafe title: String, @NlsSafe content: String) : Notification(
  "Feedback In IDE",
  title, content,
  NotificationType.INFORMATION
)