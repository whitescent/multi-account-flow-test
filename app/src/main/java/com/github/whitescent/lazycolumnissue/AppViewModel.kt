package com.github.whitescent.lazycolumnissue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.github.whitescent.lazycolumnissue.database.AccountEntity
import com.github.whitescent.lazycolumnissue.database.AppDatabase
import com.github.whitescent.lazycolumnissue.database.TimelineEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModel @Inject constructor(
  private val db: AppDatabase
) : ViewModel() {

  private val timelineDao = db.timelineDao()
  private val accountDao = db.accountDao()

  private val activeAccountFlow = accountDao
    .getActiveAccountFlow()
    .filterNotNull()
    .distinctUntilChanged { old, new -> old.id == new.id }

  val combinedFlow = activeAccountFlow
    .flatMapLatest { activeAccount ->
      val timelineFlow = timelineDao.getStatusListWithFlow(activeAccount.id)
      timelineFlow.map {
        UiState(activeAccount.id, it, TimelinePosition(activeAccount.index, activeAccount.offset))
      }
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(),
      initialValue = null
    )

  fun addAccount1() {
    viewModelScope.launch {
      accountDao.addAccount(
        account = AccountEntity(0, "Luckye", "mas.com", true)
      )
      timelineDao.insertAll(
        timelineEntity = (1..100).toList().map { TimelineEntity(0, "Luckye $it", 1) }
      )
    }
  }

  fun addAccount2() {
    viewModelScope.launch {
      accountDao.addAccount(
        account = AccountEntity(0, "whitet", "mmm.com", true)
      )
      timelineDao.insertAll(
        timelineEntity = (1..20).toList().map { TimelineEntity(0, "whitet $it", 2) }
      )
    }
  }

  fun changeActiveAccount() {
    viewModelScope.launch {
      val current = accountDao.getActiveAccount()
      current?.let {
        accountDao.setActiveAccount(accountId = if (it.id == 1L) 2 else 1)
      }
    }
  }

  fun addTimeline() {
    viewModelScope.launch {
      val current = accountDao.getActiveAccount()!!
      val size = timelineDao.getStatusList(current.id).size
      timelineDao.insertAll(
        timelineEntity = (size..size + 20).toList().map { TimelineEntity(0, "${current.username} $it", current.id) }
      )
    }
  }

  suspend fun updateTimelinePosition(firstVisibleItemIndex: Int, offset: Int) {
    val activeAccount = accountDao.getActiveAccount()!!
    db.withTransaction {
      accountDao.insertOrUpdate(
        activeAccount.copy(index = firstVisibleItemIndex, offset = offset)
      )
    }
  }
}

data class UiState(
  val id: Long,
  val timeline: List<TimelineEntity>,
  val position: TimelinePosition
)

data class TimelinePosition(
  val index: Int = 0,
  val offset: Int = 0
)
