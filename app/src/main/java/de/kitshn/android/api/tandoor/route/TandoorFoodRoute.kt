package de.kitshn.android.api.tandoor.route

import android.net.Uri
import de.kitshn.android.api.tandoor.TandoorClient
import de.kitshn.android.api.tandoor.TandoorRequestsError
import de.kitshn.android.api.tandoor.getObject
import de.kitshn.android.api.tandoor.model.TandoorFood
import de.kitshn.android.json
import kotlinx.serialization.Serializable

@Serializable
data class TandoorFoodRouteListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<TandoorFood>
)

class TandoorFoodRoute(client: TandoorClient) : TandoorBaseRoute(client) {

    @Throws(TandoorRequestsError::class)
    suspend fun list(
        query: String? = null,
        page: Int = 1,
        pageSize: Int?
    ): TandoorFoodRouteListResponse {
        val builder = Uri.Builder().appendPath("food")
        if(query != null) builder.appendQueryParameter("query", query)
        if(pageSize != null) builder.appendQueryParameter("page_size", pageSize.toString())
        builder.appendQueryParameter("page", page.toString())

        val response = json.decodeFromString<TandoorFoodRouteListResponse>(
            client.getObject(builder.build().toString()).toString()
        )

        // populate with client and store
        response.results.forEach {
            client.container.food[it.id] = it
            client.container.foodByName[it.name.lowercase()] = it
        }
        return response
    }

    @Throws(TandoorRequestsError::class)
    suspend fun retrieve(): TandoorFoodRouteListResponse {
        return list(
            pageSize = 10000000
        )
    }

}