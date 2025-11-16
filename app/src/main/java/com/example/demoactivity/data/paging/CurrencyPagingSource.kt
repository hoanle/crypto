package com.example.demoactivity.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.demoactivity.domain.model.CurrencyInfo

class CurrencyPagingSource(
    private val currencies: List<CurrencyInfo>
) : PagingSource<Int, CurrencyInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CurrencyInfo> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            val startIndex = page * pageSize
            val endIndex = minOf(startIndex + pageSize, currencies.size)

            if (startIndex >= currencies.size) {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            } else {
                val items = currencies.subList(startIndex, endIndex)
                LoadResult.Page(
                    data = items,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (endIndex >= currencies.size) null else page + 1
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CurrencyInfo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

