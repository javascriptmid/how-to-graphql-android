package android.rnita.me.frontpage

import android.os.Bundle
import android.rnita.me.frontpage.databinding.MainActivityBinding
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.rx2.Rx2Apollo
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class MainActivity : AppCompatActivity() {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()

    private val apolloClient = ApolloClient.builder()
        .okHttpClient(okHttpClient)
        .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST)
        .serverUrl("http://e91709ec.ngrok.io")
        .subscriptionTransportFactory(
            WebSocketSubscriptionTransport.Factory(
                "http://e91709ec.ngrok.io",
                okHttpClient
            )
        )
        .build()

    private val disposables = CompositeDisposable()
    private val adapter = MainAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<MainActivityBinding>(this, R.layout.main_activity)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        refreshList()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    private fun refreshList() {
        Rx2Apollo.from(apolloClient.query(FeedQuery.builder().build()))
            .map { it.data()?.feed()!! }
            .singleOrError()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    adapter.feed.clear()
                    adapter.feed.addAll(it.links)
                    adapter.notifyDataSetChanged()
                },
                onError = {
                    Log.d("getPosts Error", it.message)
                    Toast.makeText(this, "Failure getting posts", Toast.LENGTH_SHORT).show()
                }
            )
            .addTo(disposables)

    }
}
