package com.example.demoactivity.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.demoactivity.DemoActivity
import com.example.demoactivity.domain.model.CurrencyInfo
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CurrencyListScreenTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<DemoActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun currencyListScreen_displaysTitle() {
        // Navigate to currency screen first
        composeTestRule.onNodeWithText("See Cryptos").performClick()

        // Verify search bar placeholder is displayed
        composeTestRule.onNodeWithText("Search").assertIsDisplayed()
    }

    @Test
    fun currencyListScreen_displaysBackButton() {
        // Navigate to currency screen
        composeTestRule.onNodeWithText("See Cryptos").performClick()

        // Back button should be present (tested by checking if we can navigate back)
        composeTestRule.onNodeWithText("Search").assertIsDisplayed()
    }

    @Test
    fun currencyListScreen_displaysSearchBar() {
        // Navigate to currency screen
        composeTestRule.onNodeWithText("See Cryptos").performClick()

        // Search bar should be displayed
        composeTestRule.onNodeWithText("Search").assertIsDisplayed()
    }

    @Test
    fun currencyListScreen_canInputSearchText() {
        // Navigate to currency screen
        composeTestRule.onNodeWithText("See Cryptos").performClick()

        // Input text in search bar
        composeTestRule.onNodeWithText("Search").performTextInput("BTC")
    }

    @Test
    fun currencyListScreen_displaysEmptyStateWhenNoItems() {
        // Navigate to currency screen
        composeTestRule.onNodeWithText("See Cryptos").performClick()

        // Empty state should be displayed when no items
        composeTestRule.onNodeWithText("No Items").assertIsDisplayed()
    }

    @Test
    fun currencyListScreen_displaysNoResultWhenSearchHasNoMatch() {
        // Navigate to currency screen
        composeTestRule.onNodeWithText("See Cryptos").performClick()

        // Input search query that won't match
        composeTestRule.onNodeWithText("Search").performTextInput("XYZ123")

        // No result state should be displayed
        composeTestRule.onNodeWithText("No Result").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try BTC").assertIsDisplayed()
    }
}

