package io.github.pawgli.sudoku.data.remote

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://sugoku.herokuapp.com/"
private const val PATH_BOARD = "board"
private const val QUERY_DIFFICULTY = "difficulty"

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .build()

interface SudokuApiService {
    @GET(PATH_BOARD)
    suspend fun getBoard(@Query(QUERY_DIFFICULTY) type: String): NetworkBoard
}

object SudokuApi {
    val service: SudokuApiService by lazy {
        retrofit.create(SudokuApiService::class.java)
    }
}