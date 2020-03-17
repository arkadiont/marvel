package com.amartin.marvelapplication.matcher

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher

class ViewHolderMatcher(private val itemMatcher: Matcher<View> = Matchers.any(View::class.java)): TypeSafeMatcher<RecyclerView.ViewHolder>() {
    override fun describeTo(description: Description?) {
        description?.appendText("is assignable from ViewHolderMatcher")
    }

    override fun matchesSafely(item: RecyclerView.ViewHolder): Boolean =
        try { itemMatcher.matches(item.itemView) }catch (e: Exception) { false }

}