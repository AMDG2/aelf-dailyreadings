package co.epitre.aelf_lectures.bible;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import co.epitre.aelf_lectures.LecturesActivity;
import co.epitre.aelf_lectures.R;

public class BibleMenuFragment extends Fragment {

    /**
     * Global Views
     */
    protected ActionBar actionBar;
    protected NavigationView drawerView;
    protected LecturesActivity activity;
    protected Menu mMenu;

    /**
     * Pager
     */
    protected BibleMenuPagerAdapter mBibleMenuPagerAdapter;
    protected ViewPager mViewPager;
    protected TabLayout mTabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Load global views
        activity = (LecturesActivity) getActivity();
        actionBar = activity.getSupportActionBar();
        drawerView = activity.findViewById(R.id.drawer_navigation_view);

        // Option menu
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_section_bible_menu, container, false);

        // Setup the pager
        mBibleMenuPagerAdapter = new BibleMenuPagerAdapter(getChildFragmentManager(), BibleBookList.getInstance());
        mViewPager = view.findViewById(R.id.bible_menu_pager);
        mViewPager.setAdapter(mBibleMenuPagerAdapter);
        mTabLayout = view.findViewById(R.id.bible_menu_layout);
        mTabLayout.setupWithViewPager(mViewPager);

        return view;
    }
}
