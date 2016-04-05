package uk.co.liammartin.shout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ShoutFilter extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.shout_filter_menu);
    }

    public void openMainShoutActivity(View view) {
        //Creating the Intent to go to the ShoutFilter screen
        Intent getNameScreenIntent = new Intent(this, MainActivity.class);

        getNameScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        //Start the shout filter intent!
        startActivity(getNameScreenIntent);
    }

}
