package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
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
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDatabase() {
        database =
            Room.inMemoryDatabaseBuilder(getApplicationContext(), RemindersDatabase::class.java)
                .build()
    }

    @After
    fun closeDatabase(){
        database.close()
    }

    @Test
    fun saveReminder_getReminderById() = runBlockingTest{
        // Given - reminder to save
        val reminderDTO = ReminderDTO("title","desc","location",0.0,0.0)
        database.reminderDao().saveReminder(reminderDTO)

        // When - Get the reminder by id
        val saved = database.reminderDao().getReminderById(reminderDTO.id)

        // Then - The saved reminder data contains expected value
        assertThat<ReminderDTO>(saved as ReminderDTO, notNullValue())
        assertThat(saved.id,`is`(reminderDTO.id))
        assertThat(saved.description,`is`(reminderDTO.description))
        assertThat(saved.latitude,`is`(reminderDTO.latitude))
        assertThat(saved.longitude,`is`(reminderDTO.longitude))
        assertThat(saved.location,`is`(reminderDTO.location))
        assertThat(saved.title,`is`(reminderDTO.title))
    }

    @Test
    fun addReminders_deleteAllReminders() = runBlockingTest {
        // Given - Reminders to save
        val reminderDTO = ReminderDTO("title","desc","location",0.0,0.0)
        val reminderDTO2 = ReminderDTO("title2","desc2","location2",0.0,0.0)
        database.reminderDao().saveReminder(reminderDTO)
        database.reminderDao().saveReminder(reminderDTO2)

        // When - Delete the all reminders
        database.reminderDao().deleteAllReminders()

        // Then - Check there database is empty
        assertThat(database.reminderDao().getReminders().isEmpty(),`is`(true))
    }

    @Test
    fun databaseIsEmpty_getReminderById() = runBlockingTest{
        // Given - Reminder id
        val reminderId = "7"

        // When - Get the reminder by id
        val reminder = database.reminderDao().getReminderById(reminderId)

        // Then - Reminder not found
        assertThat(reminder, nullValue())
    }

}