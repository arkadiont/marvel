package com.amartin.marvelapplication

import com.amartin.marvelapplication.ui.main.MainActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.amartin.marvelapplication.common.adapter.CharacterAdapter
import com.amartin.marvelapplication.matcher.ViewHolderMatcher
import com.amartin.marvelapplication.matcher.withRecyclerView
import org.junit.Test

class TestMainActivity : TestBaseUi<MainActivity>(MainActivity::class.java) {

    @Test
    fun mainActivityUi() {
        activityTestRule.launchActivity(null)

        // ensureLoadCharacters
        onView(withRecyclerView(R.id.recycler).atPosition(2)).check(
            matches(hasDescendant(withText("A.I.M.")))
        )

        // ensureLoadMoreCharactersOnScrollDown. first, advance
        onView(withId(R.id.recycler)).perform(
            RecyclerViewActions.scrollToPosition<CharacterAdapter.ViewHolder>(20)
        )
        // .. want more page (item 42)
        onView(withId(R.id.recycler)).perform(
            RecyclerViewActions.scrollToPosition<CharacterAdapter.ViewHolder>(40)
        )
        onView(withId(R.id.recycler)).perform(
            RecyclerViewActions.scrollToHolder(
                ViewHolderMatcher(hasDescendant(withText("Ancient One (Ultimate)")))
            )
        )

        // clickCharacterNavigatesToDetail
        onView(withId(R.id.recycler)).perform(
            RecyclerViewActions.actionOnItemAtPosition<CharacterAdapter.ViewHolder>(5, click())
        )
        onView(withId(R.id.characterDetailToolbar)).check(
            matches(hasDescendant(withText("Abomination (Ultimate)")))
        )
    }


}