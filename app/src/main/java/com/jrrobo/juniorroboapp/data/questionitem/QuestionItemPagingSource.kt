package com.jrrobo.juniorroboapp.data.questionitem

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jrrobo.juniorroboapp.network.JuniorRoboApi
import retrofit2.HttpException
import java.io.IOException

private const val QUESTION_ITEM_STARTING_TAKE = 10

/**
 * QuestionPagingSource for pagination of QuestionsList
 */
class QuestionItemPagingSource(
    private val juniorRoboApi: JuniorRoboApi,
    private val cat_id: Int
) : PagingSource<Int, QuestionItem>() {

    private val TAG: String = javaClass.simpleName

    override fun getRefreshKey(state: PagingState<Int, QuestionItem>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuestionItem> {
        val position = params.key ?: QUESTION_ITEM_STARTING_TAKE

        return try {
            val response = juniorRoboApi.getAllQuestionList(
                cat_id = cat_id,
                skip = if (params.loadSize <= 10) 0 else params.loadSize - 10,
                take = params.loadSize + QUESTION_ITEM_STARTING_TAKE
            )


            val questions = response.body()!!
            Log.d(TAG, "load: questions->${questions.toString()}")

            LoadResult.Page(
                data = questions,
                prevKey = if (position == QUESTION_ITEM_STARTING_TAKE) null else position - 1,
                nextKey = if (questions.isEmpty()) null else position + 1//TODO:isEmpty()
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

}