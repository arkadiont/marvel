package com.amartin.marvelapplication

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.amartin.marvelapplication.ui.detail.DetailActivity
import com.amartin.marvelapplication.ui.detail.DetailActivity.Companion.CHARACTER
import com.amartin.marvelapplication.utils.pixelsEqualTo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.junit.Test

class TestDetailActivity : TestBaseUi<DetailActivity>(DetailActivity::class.java) {

    private fun getFavouriteDrawable() =
        activityTestRule.activity.
            findViewById<FloatingActionButton>(R.id.characterDetailFavorite).drawable

    private fun isNotFavourite() =
        getFavouriteDrawable().pixelsEqualTo(activityTestRule.activity.getDrawable(R.drawable.ic_favorite_off))

    @Test
    fun detailActivityUi() {
        activityTestRule.launchActivity(Intent().putExtra(CHARACTER, abominationUltimateId))

        onView(withId(R.id.characterDetailToolbar)).check(
            matches(hasDescendant(withText("Abomination (Ultimate)")))
        )
        // ensure we have a favourite for TestFavouriteActivity
        if (isNotFavourite()) {
            onView(withId(R.id.characterDetailFavorite)).perform(click())
        }
        if (isNotFavourite()) {
            assert(false)
        }
    }
}