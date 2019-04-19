package iste.not.com.Helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import iste.not.com.Main.AskNoteFragment;
import iste.not.com.Main.AskQuestionsFragment;
import iste.not.com.Main.NoticeFragment;

public class MainViewPager  extends FragmentPagerAdapter
{
    public MainViewPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i)
    {
        switch (i)
        {
            case 0:
                AskNoteFragment askNoteFragment = new AskNoteFragment();
                return  askNoteFragment;
            case  1:
                AskQuestionsFragment askQuestionsFragment = new AskQuestionsFragment();
                return  askQuestionsFragment;
            case 2:
                NoticeFragment noticeFragment = new NoticeFragment();
                return   noticeFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
