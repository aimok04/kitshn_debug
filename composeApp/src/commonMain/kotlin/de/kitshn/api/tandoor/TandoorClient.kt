package de.kitshn.api.tandoor

import co.touchlab.kermit.Logger
import de.kitshn.api.tandoor.route.TandoorCookLogRoute
import de.kitshn.api.tandoor.route.TandoorFoodRoute
import de.kitshn.api.tandoor.route.TandoorKeywordRoute
import de.kitshn.api.tandoor.route.TandoorMealPlanRoute
import de.kitshn.api.tandoor.route.TandoorMealTypeRoute
import de.kitshn.api.tandoor.route.TandoorOpenApiRoute
import de.kitshn.api.tandoor.route.TandoorRecipeBookRoute
import de.kitshn.api.tandoor.route.TandoorRecipeFromSourceRoute
import de.kitshn.api.tandoor.route.TandoorRecipeRoute
import de.kitshn.api.tandoor.route.TandoorShoppingRoute
import de.kitshn.api.tandoor.route.TandoorUserPreferenceRoute
import de.kitshn.api.tandoor.route.TandoorUserRoute
import de.kitshn.json
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.put

@Serializable
data class TandoorCredentialsToken(
    val token: String,
    val scope: String,
    val expires: String
)

@Serializable
data class TandoorCredentials(
    val instanceUrl: String,
    var username: String = "",
    val password: String = "",
    var token: TandoorCredentialsToken? = null
)

class TandoorClient(
    val credentials: TandoorCredentials
) {

    val httpClient = HttpClient {
        followRedirects = true
    }

    val container = TandoorContainer(this)
    val media = TandoorMedia(this)

    val cookLog = TandoorCookLogRoute(this)
    val keyword = TandoorKeywordRoute(this)
    val food = TandoorFoodRoute(this)
    val mealPlan = TandoorMealPlanRoute(this)
    val mealType = TandoorMealTypeRoute(this)
    val recipe = TandoorRecipeRoute(this)
    val recipeBook = TandoorRecipeBookRoute(this)
    val recipeFromSource = TandoorRecipeFromSourceRoute(this)
    val shopping = TandoorShoppingRoute(this)
    val user = TandoorUserRoute(this)
    val userPreference = TandoorUserPreferenceRoute(this)
    val openapi = TandoorOpenApiRoute(this)

    suspend fun login(): TandoorCredentialsToken? {
        val obj = buildJsonObject {
            put("username", credentials.username)
            put("password", credentials.password)
        }

        try {
            return json.decodeFromJsonElement<TandoorCredentialsToken>(postObject("-token-auth/", obj))
        } catch(_: TandoorRequestsError) {
        }

        return null
    }

    suspend fun testConnection(ignoreAuth: Boolean): Boolean {
        try {
            getObject("")
            return true
        } catch(e: TandoorRequestsError) {
            if(ignoreAuth) return e.response?.status == HttpStatusCode.Forbidden
            return false
        }
    }

}