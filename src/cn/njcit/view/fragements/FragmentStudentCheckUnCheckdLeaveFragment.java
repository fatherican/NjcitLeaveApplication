package cn.njcit.view.fragements;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.njcit.R;
import com.viewpagerindicator.TabPageIndicator;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_student_check_uncheck)
public class FragmentStudentCheckUnCheckdLeaveFragment extends Fragment {
    private static final String[] CONTENT = new String[] { "Recent", "Artists", "Albums", "Songs", "Playlists", "Genres" };
    private View rootView;

    @ViewById(R.id.pager)
    ViewPager pager;
    @ViewById(R.id.indicator)
    TabPageIndicator indicator;

    public static Fragment newInstance() {
        return new FragmentStudentCheckUnCheckdLeaveFragment_();
    }

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
            rootView = inflater.inflate(R.layout.fragment_student_check_uncheck,null);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    @AfterViews
    public void initAdapter(){
    }

}