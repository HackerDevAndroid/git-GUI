package jp.co.misumi.misumiecapp;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.co.misumi.misumiecapp.data.DataContainer;
import jp.co.misumi.misumiecapp.fragment.BaseFragment;
import jp.co.misumi.misumiecapp.fragment.TopFragment;


/**
 * フラグメント制御
 */
public class FragmentController {

    private FragmentManager mFragmentManager;

    private List observers = new ArrayList();
    private Handler mHandler;


    public interface FragmentChangeStateListener {
        void changeFragmentState(int event);
    }

//    /**
//     * mAcceptEvent
//     * イベント受け入れに使用する
//     * falseの場合はイベントをイベントを受け入れてはいけない
//     */
//    private boolean mAcceptEvent = true;

    public static final int CHANGE_FRAGMENT = 1;


    public static final int ANIMATION_NON = 0;
    public static final int ANIMATION_FADE_IN = 5;
    public static final int ANIMATION_UP = 7;
    public static final int ANIMATION_SLIDE_IN = 10;
    public static final int ANIMATION_SLIDE_OUT = 11;


    private ActiveFragment activeFragment = null;

    private class ActiveFragment {
        private final int max_count = BuildConfig.fragmentMaxCount;
        int index;
        private BaseFragment baseFragments[] = new BaseFragment[max_count];

        ActiveFragment() {
            index = 0;
        }

        public void clear() {
            index = 0;
        }

        public BaseFragment pop() {
            if (index == 0) {
                return null;
            }
            BaseFragment baseFragment = baseFragments[index];
            index--;
            return baseFragment;
        }

        public void push(BaseFragment baseFragment) {
            index++;
            if (index > max_count - 1) {
                mFragmentManager.beginTransaction().remove(baseFragments[0]).commit();
                System.arraycopy(baseFragments, 1, baseFragments, 0, max_count - 1);
                index = max_count - 1;
            }
//            if (++index > baseFragments.length-1) {
//
//                for (int ii = 1 ; ii < baseFragments.length-1 ; ii++ ){
//                    baseFragments[ii-1] = baseFragments[ii];
//                }
//                index = baseFragments.length-1;
//            }
            baseFragments[index] = baseFragment;
        }

        public void replace(BaseFragment baseFragment) {
            baseFragments[index] = baseFragment;
        }

        public int length() {
            return index + 1;
        }

        public BaseFragment now() {
            return baseFragments[index];
        }

        //-- ADD NT-LWL 16/11/17 AliPay Payment FR -
        public void clearAndReplace(boolean isTop) {
//            index = 0;
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            BaseFragment baseFragment;
            int topIndex = -1;
            for (int i = 0; i < baseFragments.length; i++) {
                if (baseFragments[i] instanceof TopFragment) {
                    topIndex = i;
                    break;
                }
            }
            if (topIndex != -1) {
                baseFragment = baseFragments[topIndex];
//                fragmentTransaction.replace(R.id.body, baseFragments[topIndex]);
            } else {
                baseFragment = new TopFragment();
//                fragmentTransaction.replace(R.id.body, baseFragment);
            }
//            fragmentTransaction.commit();
//            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            activeFragment.clear();
            mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            BaseFragment baseFragment = new TopFragment();
            if (isTop) {
                replaceFragment(baseFragment, ANIMATION_FADE_IN);
                //mFragmentManager.beginTransaction().replace(R.id.body, fragment).commit();
                changeFragment(CHANGE_FRAGMENT);
            } else {
                fragmentTransaction.replace(R.id.body, baseFragment);
                fragmentTransaction.commit();
            }
        }

        //-- ADD NT-LWL 16/11/17 AliPay Payment TO -
    }

    //-- ADD NT-LWL 16/11/17 AliPay Payment FR -
    public void clearAndReplace(boolean isTop) {
        if (activeFragment == null) {
            return;
        }
        activeFragment.clearAndReplace(isTop);
    }

    //-- ADD NT-LWL 16/11/17 AliPay Payment TO -
    public FragmentController(AppCompatActivity activity) {

        if (activeFragment == null) {
            activeFragment = new ActiveFragment();
        }

        mFragmentManager = activity.getSupportFragmentManager();

        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
//                AppLog.d("++++");
//                AppLog.d("getBackStackEntryCount=" + mFragmentManager.getBackStackEntryCount());
//                for (Fragment fragment:mFragmentManager.getFragments()){
//                    if (fragment != null){
//                        AppLog.d("" + fragment.toString());
//                    }else{
//                        AppLog.d("null");
//                    }
//                }
//                AppLog.d("++++");
//                changeFragment(CHANGE_FRAGMENT);
            }
        });

        mHandler = new Handler(mCallback);
    }

    public void close() {
        activeFragment = null;
    }

    private void setAnimation(int animation, FragmentTransaction transaction) {
        switch (animation) {
//            case ANIMATION_RL_WITH_BEFORE_FRAGMENT:
//                transaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
//                break;
//            case ANIMATION_LR_WITCH_BEFORE_FRAGMENT:
//                transaction.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right,R.anim.slide_in_right,R.anim.slide_out_left);
//                break;
//            case ANIMATION_RL:
//                transaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left);
//                break;
//            case ANIMATION_LR:
//                transaction.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right);
//                break;
            case ANIMATION_FADE_IN:
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.erace);
                break;
//            case ANIMATION_FADE_IN_WITH_BEFORE_FRAGMENT:
//                transaction.setCustomAnimations(R.anim.fade_in,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
//                break;
//            case ANIMATION_UP:
//                transaction.setCustomAnimations(R.anim.slide_in_up,R.anim.erace);
//                break;
//            case ANIMATION_UP_WITH_BEFORE_FRAGMENT:
//                transaction.setCustomAnimations(R.anim.slide_in_up,R.anim.slide_in_up);
//                break;
//            case ANIMATION_LOGIN_OUTFRAME:
//                transaction.setCustomAnimations(R.anim.stay, R.anim.slide_out_down);
//                break;
            case ANIMATION_SLIDE_IN:
                transaction.setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.slide_out_left_to_right,
                        R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case ANIMATION_SLIDE_OUT:
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right,
                        R.anim.slide_in_right_to_left, R.anim.slide_out_left_to_right);
                break;

            case ANIMATION_NON:
            default:
                break;
        }
    }

    /**
     * stackFragment
     *
     * @param fragment
     * @param animation
     */
    public void stackFragment(BaseFragment fragment, int animation) {


        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        setAnimation(animation, fragmentTransaction);
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        activeFragment.push(fragment);
        changeFragment(CHANGE_FRAGMENT);
    }

    /**
     * stackFragment
     *
     * @param fragment
     * @param animation
     * @param container
     */
    public void stackFragment(BaseFragment fragment, int animation, DataContainer container) {
        fragment.setParameter(container);

        stackFragment(fragment, animation);
    }


    /**
     * replaceFragment
     *
     * @param fragment
     * @param animation
     */
    public void replaceFragment(BaseFragment fragment, int animation) {

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        setAnimation(animation, fragmentTransaction);
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commit();
        activeFragment.replace(fragment);
        changeFragment(CHANGE_FRAGMENT);
    }

    //--ADD NT-LWL 17/05/20 Share FR -
    // 避免 IllegalStateException: Can not perform this action after onSaveInstanceState
    public void replaceFragmentAllowingStateLoss(BaseFragment fragment, int animation) {

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        setAnimation(animation, fragmentTransaction);
        fragmentTransaction.replace(R.id.body, fragment);
        fragmentTransaction.commitAllowingStateLoss();
        activeFragment.replace(fragment);
        changeFragment(CHANGE_FRAGMENT);
    }
    //--ADD NT-LWL 17/05/20 Share FR -

    /**
     * replaceFragment
     *
     * @param fragment
     * @param animation
     * @param container
     */
    public void replaceFragment(BaseFragment fragment, int animation, DataContainer container) {
        fragment.setParameter(container);

        replaceFragment(fragment, animation);
    }


    public void clearStack(final BaseFragment fragment) {

        final Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                BaseFragment baseFragment = getCurrentFragment();
                if (baseFragment == null) {
                    return;
                }
                while (!baseFragment.isResumed()) {
                    try {
                        AppLog.d("wait resume");
                        Thread.sleep(10);
                    } catch (InterruptedException ignored) {
                    }
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        activeFragment.clear();
                        mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        replaceFragment(fragment, ANIMATION_FADE_IN);
                        //mFragmentManager.beginTransaction().replace(R.id.body, fragment).commit();
                        changeFragment(CHANGE_FRAGMENT);
                    }
                });

            }
        }).start();

    }

    public int getFragmentCount() {
        return activeFragment.length();
    }

    public BaseFragment getCurrentFragment() {
        if (activeFragment == null) {
            return null;
        }
        BaseFragment baseFragment = activeFragment.now();
        if (baseFragment != null) {
            AppLog.d("current fragment=" + baseFragment.toString());
        }
        return baseFragment;
    }


    /**
     * onBackKey
     *
     * @return
     */
    public boolean onBackKey() {

        int count = getFragmentCount();
        if (count > 1) {
            mFragmentManager.popBackStack();
            activeFragment.pop();
            changeFragment(CHANGE_FRAGMENT);
        }
        return true;
    }


    public void onHeaderEvent(int event, Objects objects) {
        BaseFragment baseFragment = getCurrentFragment();
        if (baseFragment != null) {
            baseFragment.onHeaderEvent(event, objects);
        }
    }


    /**
     * addFragmentChangeListener
     *
     * @param listener
     */
    public void addFragmentChangeListener(FragmentChangeStateListener listener) {
        observers.add(listener);
    }

    /**
     * removeFragmentChangeListener
     *
     * @param listener
     */
    public void removeFragmentChangeListener(FragmentChangeStateListener listener) {
        observers.remove(listener);
    }

    /**
     * changeFragment
     *
     * @param event
     */
    private void changeFragment(int event) {
        Message msg = mHandler.obtainMessage();
        msg.what = event;
        msg.sendToTarget();
    }

    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            for (Object observer : observers) {
                FragmentChangeStateListener o = (FragmentChangeStateListener) observer;
                o.changeFragmentState(msg.what);
            }
            return false;
        }
    };
}
