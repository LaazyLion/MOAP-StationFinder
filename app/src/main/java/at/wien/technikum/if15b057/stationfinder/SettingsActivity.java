package at.wien.technikum.if15b057.stationfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import at.wien.technikum.if15b057.stationfinder.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
