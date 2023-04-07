package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersDatabase: RemindersDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        remindersDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = remindersDatabase.close()

    @Test
    fun saveReminder_savesReminderDTOtoDatabaseAndRetrievableById() = runBlocking {
        val reminder = ReminderDTO(
            "Title todo",
            "Description todo",
            "Location todo",
            10.0,
            20.0)

        remindersDatabase.reminderDao().saveReminder(reminder)

        val result: ReminderDTO? = remindersDatabase.reminderDao().getReminderById(reminder.id)
        assertThat(result, `is`(reminder))
    }

    @Test
    fun deleteAllReminders_removesAllSavedReminderInDatabase() = runBlocking {
        val reminder = ReminderDTO(
            "Title todo",
            "Description todo",
            "Location todo",
            10.0,
            20.0)
        val id = reminder.id
        remindersDatabase.reminderDao().saveReminder(reminder)

        remindersDatabase.reminderDao().deleteAllReminders()

        val result = remindersDatabase.reminderDao().getReminderById(id)
        assertThat(result, `is`(CoreMatchers.nullValue()))
    }
}