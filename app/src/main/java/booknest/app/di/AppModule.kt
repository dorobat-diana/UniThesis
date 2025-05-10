package booknest.app.di

import booknest.app.feature.home.data.HomeRepository
import booknest.app.feature.home.data.HomeRepositoryImpl
import booknest.app.feature.post.data.AttractionRepository
import booknest.app.feature.post.data.AttractionRepositoryImpl
import booknest.app.feature.post.data.PostRepository
import booknest.app.feature.post.data.PostRepositoryImpl
import booknest.app.feature.profil.data.ProfileRepository
import booknest.app.feature.profil.data.ProfileRepositoryImpl
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
}