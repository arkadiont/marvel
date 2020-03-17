package com.amartin.marvelapplication

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.amartin.marvelapplication.matcher.withRecyclerView
import com.amartin.marvelapplication.ui.favourite.FavouriteActivity
import org.junit.Test

class TestFavouriteActivity : TestBaseUi<FavouriteActivity>(FavouriteActivity::class.java) {

    @Test
    fun favouriteActivityUi() {
        activityTestRule.launchActivity(null)
        onView(withRecyclerView(R.id.recycler).atPosition(0)).perform(click())

        onView(withId(R.id.characterDetailToolbar)).check(
            matches(hasDescendant(withText("Abomination (Ultimate)")))
        )
    }
}