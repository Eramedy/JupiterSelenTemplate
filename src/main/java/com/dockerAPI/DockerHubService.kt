package com.dockerAPI

import com.config.Config
import com.dockerAPI.dockerHubModel.DockerHubApi
import com.dockerAPI.dockerHubModel.Result
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.invoke.MethodHandles

class DockerHubService {
    private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

    private val dockerAPIInterface: DockerHubInterface

    init {
        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Config.dockerHubURL)
                .client(OkHttpClient.Builder().build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        dockerAPIInterface = retrofit.create(DockerHubInterface::class.java)
    }

    fun getList(): List<Result> {
        log.info("Request browser list tags.")
        val results = mutableListOf<Result>()
        var page = 0
        log.info("Start of requests to {}", Config.dockerHubURL)
        do {
            log.info("Request for {} page", page)
            val response = if (++page == 1)
                dockerAPIInterface.listTags().execute()
            else
                dockerAPIInterface.listTags(page).execute()

            if (!response.isSuccessful) throw DockerAPIException("${response.code()} ${response.errorBody()}")
            log.info("Response came")
            val body = response.body() ?: throw DockerAPIException()
            results.addAll(body.results)
        } while (body.next != null)
        return results
    }

    interface DockerHubInterface {
        @GET("/v2/repositories/selenoid/vnc/tags")
        fun listTags(@Query("page") page: Int? = null,
                     @Query("page_size") pageSize: Int = 1024): Call<DockerHubApi>
    }
}