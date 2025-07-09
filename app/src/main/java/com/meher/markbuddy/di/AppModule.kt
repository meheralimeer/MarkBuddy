package com.meher.markbuddy.di

import androidx.room.Room
import com.meher.markbuddy.data.AppDatabase
import com.meher.markbuddy.ui.viewmodel.MarkBuddyViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "gpa_database"
        ).build()
    }

    single { get<AppDatabase>().semesterDao() }
    single { get<AppDatabase>().courseDao() }
    single { get<AppDatabase>().assignmentDao() }
    single { get<AppDatabase>().quizDao() }
    single { get<AppDatabase>().examDao() }

    viewModel { MarkBuddyViewModel(get(), get(), get(), get(), get()) }
}