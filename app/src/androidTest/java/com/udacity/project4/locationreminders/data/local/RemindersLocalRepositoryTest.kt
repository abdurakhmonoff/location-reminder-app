package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MainAndroidTestCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    val mainAndroidTestCoroutineRule = MainAndroidTestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @Before
    fun setupRepositoryAndDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java
        ).build()

        remindersLocalRepository =
            RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun saveReminder_getReminderById() = runBlockingTest{
        // Given - a reminder
        val reminderDTO = ReminderDTO("title","desc","location",0.0,0.0)

        // When - saving a reminder and getting reminder by id
        remindersLocalRepository.saveReminder(reminderDTO)
        val retrievedReminder = remindersLocalRepository.getReminder(reminderDTO.id) as Result.Success<ReminderDTO>

        // Then - retrieved data contains expected values
        assertThat<ReminderDTO>(retrievedReminder.data as ReminderDTO, notNullValue())
        assertThat(retrievedReminder.data.id,`is`(reminderDTO.id))
        assertThat(retrievedReminder.data.description,`is`(reminderDTO.description))
        assertThat(retrievedReminder.data.latitude,`is`(reminderDTO.latitude))
        assertThat(retrievedReminder.data.longitude,`is`(reminderDTO.longitude))
        assertThat(retrievedReminder.data.location,`is`(reminderDTO.location))
        assertThat(retrievedReminder.data.title,`is`(reminderDTO.title))
    }

    @Test
    fun saveReminders_getReminders() = mainAndroidTestCoroutineRule.runBlockingTest {
        // Given - Few reminders
        val reminder = ReminderDTO("title","desc","location",0.0,0.0)
        val reminder2 = ReminderDTO("title2","desc2","location2",0.0,0.0)

        // When - Saving reminders and getting reminders
        database.reminderDao().saveReminder(reminder)
        database.reminderDao().saveReminder(reminder2)
        val reminders = remindersLocalRepository.getReminders() as Result.Success<List<ReminderDTO>>

        // Then - Retrieved reminders contains saved reminders
        assertThat(reminders, notNullValue())
        assertThat(reminders.data.size,`is`(2))
        assertThat(reminders.data, hasItem(reminder))
        assertThat(reminders.data, hasItem(reminder2))
    }

    @Test
    fun databaseIsEmpty_getReminderById() = runBlockingTest{
        // Given - Reminder id
        val reminderId = "7"

        // When - Get the reminder by id
        val reminder = remindersLocalRepository.getReminder(reminderId) as Result.Error

        // Then - Reminder not found
        assertThat(reminder, notNullValue())
        assertThat(reminder.message,`is`("Reminder not found!"))
    }
}