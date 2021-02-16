package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO> = mutableListOf()) :
    ReminderDataSource {

    private var isReturnsError = false

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (isReturnsError) return Result.Error("Failed to get reminders (Test)")
        return Result.Success(reminders.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (isReturnsError) return Result.Error("Failed to get reminder (Test)")
        val foundReminder = reminders.find { it.id == id }
        return if (foundReminder != null) {
            Result.Success(foundReminder)
        } else {
            Result.Error("Reminder not found!")
        }
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }

    fun setReturnError(boolean: Boolean) {
        isReturnsError = boolean
    }


}