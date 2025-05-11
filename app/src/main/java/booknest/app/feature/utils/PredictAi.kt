package booknest.app.feature.utils

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Url

interface PredictApi {
    @Multipart
    @POST
    fun predictImage(
        @Url url: String,
        @Part image: MultipartBody.Part
    ): Call<PredictionResult>
}


data class PredictionResult(val attraction: String)

