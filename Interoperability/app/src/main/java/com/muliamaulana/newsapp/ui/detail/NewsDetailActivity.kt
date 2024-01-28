package com.muliamaulana.newsapp.ui.detail

import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import com.muliamaulana.newsapp.R
import com.muliamaulana.newsapp.data.local.entity.NewsEntity
import com.muliamaulana.newsapp.databinding.ActivityNewsDetailBinding
import com.muliamaulana.newsapp.ui.ViewModelFactory

class NewsDetailActivity : AppCompatActivity() {

    private lateinit var newsDetail: NewsEntity
    private lateinit var binding: ActivityNewsDetailBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel: NewsDetailViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        newsDetail =
            IntentCompat.getParcelableExtra(intent, NEWS_DATA, NewsEntity::class.java) as NewsEntity
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NewsDetailScreen(
                        newsDetail = newsDetail,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    private fun setBookmarkState(state: Boolean) {
        val menuItem = binding.topAppBar.menu.findItem(R.id.action_bookmark)
        if (state) {
            menuItem?.icon = ContextCompat.getDrawable(this, R.drawable.ic_bookmarked_white)
        } else {
            menuItem?.icon = ContextCompat.getDrawable(this, R.drawable.ic_bookmark_white)
        }
    }

    companion object {
        const val NEWS_DATA = "data"
    }
}

@Composable
fun NewsDetailScreen(
    newsDetail: NewsEntity,
    viewModel: NewsDetailViewModel
) {
    viewModel.setNewsData(newsDetail)
    val bookmarkStatus by viewModel.bookmarkStatus.observeAsState(false)
    NewsDetailContent(
        title = newsDetail.title,
        url = newsDetail.url.toString(),
        bookmarkStatus = bookmarkStatus,
        updateBookmarkStatus = {
            viewModel.changeBookmark(newsDetail)
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailContent(
    title: String,
    url: String,
    bookmarkStatus: Boolean,
    updateBookmarkStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },

                actions = {
                    IconButton(onClick = updateBookmarkStatus) {
                        Icon(
                            painter = if (bookmarkStatus) {
                                painterResource(id = R.drawable.ic_bookmarked_white)
                            } else {
                                painterResource(id = R.drawable.ic_bookmark_white)
                            },
                            contentDescription = stringResource(id = R.string.save_bookmark)
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AndroidView(
                factory = {
                    WebView(it).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        webViewClient = WebViewClient()
                        loadUrl(url)
                    }
                },
                update = {
                    it.loadUrl(url)
                })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewsDetailContentPreview() {
    MaterialTheme {
        NewsDetailContent("New News", "www.dicoding.com", false, {})
    }
}