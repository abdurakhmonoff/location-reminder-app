package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainTestCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.P])
class RemindersListViewModelTest {

    @get:Rule
    var mainCorotineRule = MainTestCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var remindersListViewModel:RemindersListViewModel

    @Before
    fun setup(){
        val reminder = ReminderDTO("Title","Desc","Location", 0.0, 0.0)
        val reminder2 = ReminderDTO("Title2","Desc2","Location2", 0.0, 0.0)
        dataSource = FakeDataSource()
        runBlocking {
            dataSource.saveReminder(reminder)
            dataSource.saveReminder(reminder2)
        }
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext() as Application, dataSource)
    }

    @After
    fun cleanupDataSource() = runBlocking {
        dataSource.deleteAllReminders()
        stopKoin()
    }

    @Test
    fun shouldReturnError() = mainCorotineRule.runBlockingTest{
        dataSource.setReturnError(true)
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showSnackBar.getOrAwaitValue(),`is`("Failed to get reminders (Test)"))
    }

    @Test
    fun check_loading() = mainCorotineRule.runBlockingTest{
        mainCorotineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCorotineRule.resumeDispatcher()
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }


    @Test
    fun check_noReminders() = runBlockingTest {
        dataSource.deleteAllReminders()
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.remindersList.getOrAwaitValue().size,`is`(0))
    }

}