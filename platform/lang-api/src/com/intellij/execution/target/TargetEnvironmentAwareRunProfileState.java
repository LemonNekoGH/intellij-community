// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.execution.target;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.process.ProcessOutputType;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Experimental
public interface TargetEnvironmentAwareRunProfileState extends RunProfileState {
  void prepareTargetEnvironmentRequest(@NotNull TargetEnvironmentRequest request,
                                       @Nullable TargetEnvironmentConfiguration configuration,
                                       @NotNull TargetProgressIndicator targetProgressIndicator) throws ExecutionException;

  void handleCreatedTargetEnvironment(@NotNull TargetEnvironment targetEnvironment,
                                      @NotNull TargetProgressIndicator targetProgressIndicator)
    throws ExecutionException;


  interface TargetProgressIndicator {
    TargetProgressIndicator EMPTY = new TargetProgressIndicator() {
      @Override
      public void addText(@NotNull String text, @NotNull Key<?> key) { }

      @Override
      public boolean isCanceled() {
        return false;
      }
    };

    void addText(@NotNull String text, @NotNull Key<?> key);

    default void addSystemLine(@NotNull String message) {
      addText(message + "\n", ProcessOutputType.SYSTEM);
    }

    boolean isCanceled();
  }
}