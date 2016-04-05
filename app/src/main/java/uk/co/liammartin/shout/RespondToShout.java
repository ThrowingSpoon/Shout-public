package uk.co.liammartin.shout;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class RespondToShout extends AppCompatActivity {

    TabLayout tab_layout;
    ViewPager view_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting our main view
        setContentView(R.layout.respond_to_shout);

        //finding our view pager and setting its adapter
        view_pager = (ViewPager) findViewById(R.id.respond_to_shout_view_pager);
        view_pager.setAdapter(new RespondAdapter(
                getSupportFragmentManager(),
                getApplicationContext()));

        //finding our tab layout and setting it up with the view pager to display the fragments
        tab_layout = (TabLayout) findViewById(R.id.respond_to_shout_tab_layout);
        tab_layout.setupWithViewPager(view_pager);

        //Deciding what will happen when the tabs are interacted with, room to expand here
        //for example you could implement a refresh function here at: onTabReselected
        tab_layout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                view_pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //Could implement a refresh function here eventually when the servlet is working
            }
        });
    }
}
