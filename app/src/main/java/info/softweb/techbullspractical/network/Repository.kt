package info.softweb.techbullspractical.network

import dagger.hilt.android.scopes.ActivityRetainedScoped
import info.softweb.techbullspractical.di.RemoteDataSource
import javax.inject.Inject

@ActivityRetainedScoped
class Repository @Inject constructor(
    remoteDataSource: RemoteDataSource
) {
    val remote = remoteDataSource
}