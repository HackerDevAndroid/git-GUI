package jp.co.misumi.misumiecapp.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Locale;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;

public class LanguageSettingFragment extends BaseFragment {
    private String selectedLanguage;
    private String currentSettingLanguage = "th";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflateLayout(inflater, R.layout.fragment_language_setting, container, false);

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.languageRadioGroup);
        RadioButton defaultRadioBtn = (RadioButton) view.findViewById(R.id.defaultRadioBtn);
        RadioButton englishRadioBtn = (RadioButton) view.findViewById(R.id.englishRadioBtn);
        selectedLanguage = AppConfig.getInstance().getSelectedLanguageSetting();
        currentSettingLanguage = selectedLanguage;
        if (selectedLanguage != null && selectedLanguage.equals("en")) {
            englishRadioBtn.setChecked(true);
        } else {
            defaultRadioBtn.setChecked(true);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.defaultRadioBtn:
                        currentSettingLanguage = "th";
                        break;
                    case R.id.englishRadioBtn:
                        currentSettingLanguage = "en";
                        break;
                }
            }
        });

        Button submitBtn = (Button) view.findViewById(R.id.complete_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSettingLanguage != null && !currentSettingLanguage.equals(selectedLanguage)) {
                    setLocale(currentSettingLanguage);
                    AppConfig.getInstance().setSelectedLanguage(currentSettingLanguage);
                }
            }
        });
        return view;
    }

    private void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = getActivity().getIntent();
        getActivity().finish();
        startActivity(refresh);
    }

    /**
     * getScreenId
     *
     * @return
     */
    @Override
    public String getScreenId() {
        return ScreenId.LanguageSetting;
    }

    //サイカタ
    protected String getSaicataId() {
        return jp.co.misumi.misumiecapp.SaicataId.LanguageInfo;
    }
}


