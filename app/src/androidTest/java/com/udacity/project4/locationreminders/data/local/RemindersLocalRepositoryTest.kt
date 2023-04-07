package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.util.wrapEspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
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
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersDatabase: RemindersDatabase
    private lateinit var reminderRepository: RemindersLocalRepository

    @Before
    fun setupLocalRepository() {
        wrapEspressoIdlingResource{
            remindersDatabase = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RemindersDatabase::class.java
            ).allowMainThreadQueries()
                .build()
            reminderRepository =
                RemindersLocalRepository(
                    remindersDatabase.reminderDao(), Dispatchers.Main
                )
        }
    }

    @Test
    fun getReminder_retrievesCorrectSavedReminder() = runBlocking{
        wrapEspressoIdlingResource{
            val data = ReminderDTO(
                "title todo",
                "description todo",
                "location todo",
                100.00,
                50.00
            )
            reminderRepository.saveReminder(data)

            val result = reminderRepository.getReminder(data.id)

            result as Result.Success
            assertThat(true, `is`(true))
            val loadedData = result.data
            assertThat(loadedData.id, `is`(data.id))
            assertThat(loadedData.title, `is`(data.title))
            assertThat(loadedData.description, `is`(data.description))
            assertThat(loadedData.location, `is`(data.location))
            assertThat(loadedData.latitude, `is`(data.latitude))
            assertThat(loadedData.longitude, `is`(data.longitude))
        }
    }

    @Test
    fun getReminder_GivenInvalidReminderId_returnsError() = runBlocking {
        wrapEspressoIdlingResource{
            reminderRepository.deleteAllReminders() //make sure the repository is empty

            val result = reminderRepository.getReminder("123")

            val error = (result is Result.Error)
            assertThat(error, `is`(true))
        }
    }

    @After
    fun cleanUp() = remindersDatabase.close()


}