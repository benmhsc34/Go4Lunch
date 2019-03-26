package com.example.benja.go4lunch;

import android.content.ComponentName;
import android.content.Context;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.widget.AutoCompleteTextView;

import com.example.benja.go4lunch.controllers.Activities.MainActivity;
import com.example.benja.go4lunch.controllers.Activities.RestaurantActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@SuppressWarnings("WeakerAccess")
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = getTargetContext();

        assertEquals("com.example.benja.go4lunch", appContext.getPackageName());
    }

    @Rule
    public final ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    private MainActivity mActivity = null;

    @Before
    public void setUp() {

        mActivity = mainActivityActivityTestRule.getActivity();
        Intents.init();
        assertThat(mActivity, notNullValue());
    }

    @Test
    public void tabSwitch() {
        onView(withId(R.id.action_map_view)).perform(click()).check(matches(isSelected()));
        SystemClock.sleep(800); //Need some time for the tabs to load

        onView(withId(R.id.action_list_view)).perform(click()).check(matches(isSelected()));
        SystemClock.sleep(800); //Need some time for the tabs to load

        onView(withId(R.id.action_workmates)).perform(click()).check(matches(isSelected()));
        SystemClock.sleep(800); //Need some time for the tabs to load
    }

    @Test
    public void testNavigationDrawer() {
        //Opening Navigation Drawer
        onView(withContentDescription(R.string.navigation_drawer_open)).perform(click());

        // Start the screen of activity.
        onView(withId(R.id.activity_welcome_nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.activity_welcome_drawer_settings));


        onView(isRoot()).perform(ViewActions.pressBack());


        //Closing it by clicking next to the Navigation Drawer
        onView(withId(R.id.activity_main_menu_toolbar_search)).perform(click());

    }


    @Test
    public void testSearchToolbar(){

        AutoCompleteTextView tv = mActivity.findViewById(R.id.myEditText);

        //Click on search icon in the Toolbar
        onView(withId(R.id.activity_main_menu_toolbar_search)).perform(click());

        //Check if EditText appears
        onView(withId(R.id.myEditText))
                .perform(typeText("This is a test"))
                .check(matches(isDisplayed()));



        String inputSearch = tv.getText().toString();

        assertEquals("This is a test", inputSearch);
    }
    @Test
    public void testRestaurantRecyclerView(){

        onView(withId(R.id.action_list_view)).perform(click()).check(matches(isSelected()));
        SystemClock.sleep(800); //Need some time for the tabs to load

        RecyclerView rv = mActivity.findViewById(R.id.fragment_list_restaurant_view_recycler_view);
        rv.setItemAnimator(null);

        Espresso.onView(ViewMatchers.withId(R.id.fragment_list_restaurant_view_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, ViewActions.click()));

        intended(hasComponent(new ComponentName(getTargetContext(), RestaurantActivity.class)));

    }

    @After
    public void tearDown(){
        Intents.release();
    }
}
