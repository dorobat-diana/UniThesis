package triptag.app.di

import triptag.app.feature.challanges.data.ChallengeRepository
import triptag.app.feature.challanges.data.ChallengesRepositoryImpl
import triptag.app.feature.home.data.HomeRepository
import triptag.app.feature.home.data.HomeRepositoryImpl
import triptag.app.feature.post.data.AttractionRepository
import triptag.app.feature.post.data.AttractionRepositoryImpl
import triptag.app.feature.post.data.PostRepository
import triptag.app.feature.post.data.PostRepositoryImpl
import triptag.app.feature.profil.data.ProfileRepository
import triptag.app.feature.profil.data.ProfileRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideProfileRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): ProfileRepository {
        return ProfileRepositoryImpl(auth, firestore, storage)
    }

    @Provides
    @Singleton
    fun provideHomeRepository(
        firestore: FirebaseFirestore,
        profileRepository: ProfileRepository
    ): HomeRepository {
        return HomeRepositoryImpl(firestore, profileRepository)
    }

    @Provides
    @Singleton
    fun providePostRepository(
        firestore: FirebaseFirestore,
        profileRepository: ProfileRepository
    ): PostRepository {
        return PostRepositoryImpl(firestore, profileRepository)
    }

    @Provides
    @Singleton
    fun provideAttractionRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): AttractionRepository {
        return AttractionRepositoryImpl(auth, firestore, storage)
    }

    @Provides
    @Singleton
    fun provideChallengeRepository(
        firestore: FirebaseFirestore
    ): ChallengeRepository {
        return ChallengesRepositoryImpl(firestore)
    }
}
