package com.github.whitescent.lazycolumnissue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.whitescent.lazycolumnissue.ui.theme.LazyColumnIssueTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import logcat.logcat

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  @OptIn(FlowPreview::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      LazyColumnIssueTheme {
        val viewModel: AppViewModel = hiltViewModel()
        val uiState by viewModel.combinedFlow.collectAsStateWithLifecycle()
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
          Row {
            Button(onClick = { viewModel.addAccount1() }) {
            }
            Button(onClick = { viewModel.addAccount2() }) {
            }
            Button(onClick = { viewModel.changeActiveAccount() }) {
            }
            Button(onClick = { viewModel.addTimeline() }) {
            }
          }
          uiState?.let {
            val timeline = uiState?.timeline
            val timelinePosition = uiState?.position

            val lazyState = rememberSaveable(uiState?.id, saver = LazyListState.Saver) {
              logcat("TEST") { "init ${timelinePosition?.index}" }
              LazyListState(timelinePosition!!.index, timelinePosition.offset)
            }
            logcat("TEST") { "after init lazy ${lazyState.firstVisibleItemIndex}" }
            val firstVisibleIndex by remember {
              derivedStateOf { lazyState.firstVisibleItemIndex }
            }
            LazyColumn(state = lazyState) {
              items(timeline!!) {
                Box(Modifier.fillMaxWidth().height(100.dp).background(Color.Gray)) {
                  Text(
                    text = it.content,
                    fontSize = 24.sp
                  )
                }
              }
            }
            LaunchedEffect(firstVisibleIndex) {
              launch {
                snapshotFlow { firstVisibleIndex }
                  .debounce(500L)
                  .collectLatest {
                    viewModel.updateTimelinePosition(it, lazyState.firstVisibleItemScrollOffset)
                  }
              }
            }
          }
        }
      }
    }
  }
}
