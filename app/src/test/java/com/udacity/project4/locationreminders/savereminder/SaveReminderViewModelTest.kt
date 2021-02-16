package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainTestCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

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

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SaveReminderViewModelTest {

    @get:Rule
    var mainCorotineRule = MainTestCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    private lateinit var context: Context

    @Before
    fun setup(){
        val reminder = ReminderDTO("Title","Desc","Location", 0.0, 0.0)
        val reminder2 = ReminderDTO("Title2","Desc2","Location2", 0.0, 0.0)
        dataSource = FakeDataSource()
        runBlocking {
            dataSource.saveReminder(reminder)
            dataSource.saveReminder(reminder2)
        }
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext() as Application, dataSource)

        context = ApplicationProvider.getApplicationContext()
    }

    @After
    fun cleanupDataSource() = runBlocking{
        dataSource.deleteAllReminders()
        stopKoin()
    }

    @Test
    fun check_loading() = mainCorotineRule.runBlockingTest{
        val reminderDataItem = ReminderDataItem("Title","Desc","Location", 0.0, 0.0)
        mainCorotineRule.pauseDispatcher()

        saveReminderViewModel.validateAndSaveReminder(reminderDataItem)
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(),`is`(true))

        mainCorotineRule.resumeDispatcher()

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(),`is`(false))
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue(),`is`(context.getString(R.string.reminder_saved)))
    }

    @Test
    fun shouldReturnError() = mainCorotineRule.runBlockingTest{
        saveReminderViewModel.validateAndSaveReminder(ReminderDataItem("title","desc",null,0.0,0.0))
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),`is`(R.string.err_select_location))
    }

}