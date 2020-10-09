// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package git4idea.index.ui

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vcs.changes.Change
import com.intellij.util.ui.JBUI.Borders.empty
import com.intellij.vcs.commit.CommitProgressPanel
import com.intellij.vcs.commit.CommitProgressUi
import com.intellij.vcs.commit.NonModalCommitPanel
import git4idea.index.GitFileStatus
import git4idea.index.GitStageTracker
import kotlin.properties.Delegates.observable

private fun GitStageTracker.State.getStaged(): Set<GitFileStatus> =
  rootStates.values.flatMapTo(mutableSetOf()) { it.getStaged() }

private fun GitStageTracker.RootState.getStaged(): Set<GitFileStatus> =
  statuses.values.filterTo(mutableSetOf()) { it.getStagedStatus() != null }

class GitStageCommitPanel(project: Project) : NonModalCommitPanel(project) {
  private val progressPanel = CommitProgressPanel()

  private var oldStaged: Set<GitFileStatus> = emptySet()

  var state: GitStageTracker.State by observable(GitStageTracker.State.EMPTY) { _, _, newValue ->
    val newStaged = newValue.getStaged()

    if (oldStaged != newStaged) fireInclusionChanged()
    oldStaged = newStaged
  }

  init {
    Disposer.register(this, commitMessage)

    progressPanel.setup(this, commitMessage.editorField)
    bottomPanel = {
      add(progressPanel.apply { border = empty(6) })
      add(commitActionsPanel)
    }
    buildLayout()
  }

  override val commitProgressUi: CommitProgressUi get() = progressPanel

  override fun activate(): Boolean = true
  override fun refreshData() = Unit

  override fun getDisplayedChanges(): List<Change> = emptyList()
  override fun getIncludedChanges(): List<Change> = emptyList()
  override fun getDisplayedUnversionedFiles(): List<FilePath> = emptyList()
  override fun getIncludedUnversionedFiles(): List<FilePath> = emptyList()

  override fun includeIntoCommit(items: Collection<*>) = Unit

  override fun showCommitOptions(popup: JBPopup, isFromToolbar: Boolean, dataContext: DataContext) =
    if (isFromToolbar) popup.showAbove(toolbar.component) else popup.showInBestPositionFor(dataContext)
}
