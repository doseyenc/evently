package com.doseyenc.evently.di;

import com.doseyenc.evently.data.repository.EventRepositoryImpl;
import com.doseyenc.evently.domain.repository.CommentRepository;
import com.doseyenc.evently.domain.repository.EventRepository;
import com.doseyenc.evently.domain.repository.LiveStatusRepository;
import com.doseyenc.evently.domain.repository.ParticipantRepository;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class AppModule {

    @Binds
    public abstract EventRepository bindEventRepository(EventRepositoryImpl impl);

    @Binds
    public abstract ParticipantRepository bindParticipantRepository(EventRepositoryImpl impl);

    @Binds
    public abstract CommentRepository bindCommentRepository(EventRepositoryImpl impl);

    @Binds
    public abstract LiveStatusRepository bindLiveStatusRepository(EventRepositoryImpl impl);
}
