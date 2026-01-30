package com.doseyenc.evently.di;

import com.doseyenc.evently.data.repository.EventRepositoryImpl;
import com.doseyenc.evently.domain.repository.EventRepository;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class AppModule {

    @Binds
    public abstract EventRepository bindEventRepository(EventRepositoryImpl impl);
}
