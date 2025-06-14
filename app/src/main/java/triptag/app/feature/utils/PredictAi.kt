package triptag.app.feature.utils

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

data class PredictionResponse(val attraction: String)

interface PredictApi {
    @Multipart
    @POST("{endpoint}")
    fun predictImage(
        @Path("endpoint") endpoint: String,
        @Part image: MultipartBody.Part
    ): Call<PredictionResponse>
}
