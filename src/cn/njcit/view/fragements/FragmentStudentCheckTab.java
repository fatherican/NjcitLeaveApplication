package cn.njcit.view.fragements;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.njcit.R;
import com.viewpagerindicator.TabPageIndicator;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_student_check_tab)
public class FragmentStudentCheckTab extends Fragment {
    private static final String[] CONTENT = new String[] { "待审批", "最新审批", "历史"};
    private View rootView;

    @ViewById(R.id.pager)
    ViewPager pager;
    @ViewById(R.id.indicator)
    TabPageIndicator indicator;


    /**
     * 为避免每次切换tab的时候重新加载fragment，强制重写了此方法。
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_student_check_tab,null);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    @AfterViews
    public void initAdapter(){
        FragmentPagerAdapter adapter = new GoogleMusicAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
    }


    class GoogleMusicAdapter extends FragmentPagerAdapter {
        public GoogleMusicAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch(position){
                case 0:
                    fragment = FragmentStudentCheckUnCheckdLeaveFragment_.newInstance();
                    break;
                case 1:
                    fragment = FragmentStudentCheckNewCheckdLeaveFragment_.newInstance();
                    break;
                case 2:
                    fragment = FragmentStudentCheckHistoryCheckdLeaveFragment_.newInstance();
                    break;

            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }
}