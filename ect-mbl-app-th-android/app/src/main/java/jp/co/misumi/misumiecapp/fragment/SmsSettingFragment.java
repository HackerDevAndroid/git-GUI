package jp.co.misumi.misumiecapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.data.DataContainer;
import jp.co.misumi.misumiecapp.data.SmsSetting;


/**
 * デバイスインフォ画面
 */
public class SmsSettingFragment extends BaseFragment implements SmsSetting.SmsValueListener {

    String info;
    SmsSetting oldValue;
    SmsSetting curentValue;

    Button submitBtn;
    Spinner smsSpinner;
    EditText phoneEdt;
    CheckBox chkFinishOrder;
    CheckBox chkFinishExport;
    CheckBox chkFinishChange;
    RadioGroup timeRadioGroup;
    RadioButton noSettingTimeRadioBtn;
    RadioButton settingTimeRadioBtn;
    Spinner sendingTimeSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ToDo: get data from API
        oldValue = new SmsSetting(true, "0912345678", true, false, false, true, 1);
        curentValue = new SmsSetting(true, "0912345678", true, false, false, true, 1);
        curentValue.setValueChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflateLayout(inflater, R.layout.fragment_sms_setting, container, false);

        smsSpinner = (Spinner) view.findViewById(R.id.send_sms_spinner);
        phoneEdt = (EditText) view.findViewById(R.id.phoneEdt);
        chkFinishOrder = (CheckBox) view.findViewById(R.id.chkFinishOrder);
        chkFinishExport = (CheckBox) view.findViewById(R.id.chkFinishExport);
        chkFinishChange = (CheckBox) view.findViewById(R.id.chkFinishChange);
        timeRadioGroup = (RadioGroup) view.findViewById(R.id.timeRadioGroup);
        noSettingTimeRadioBtn = (RadioButton) view.findViewById(R.id.noSettingRadioBtn);
        settingTimeRadioBtn = (RadioButton) view.findViewById(R.id.settingRadioBtn);
        sendingTimeSpinner = (Spinner) view.findViewById(R.id.sendingTimeSpinner);
        submitBtn = (Button) view.findViewById(R.id.submit_btn);

        //init send sms spinner
        initSpinner(smsSpinner, getContext().getResources().getStringArray(R.array
                .send_sms_option), 0, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    // Allow to send SMS
                    case 0:
                        curentValue.setSendSms(true);
                        break;
                    //Not Allow to send SMS
                    case 1:
                        SmsSetting.resetValue(curentValue);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        phoneEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                curentValue.setPhoneNumber(editable.toString());
            }
        });

        chkFinishOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                curentValue.setFinishOrder(b);
            }
        });
        chkFinishExport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                curentValue.setFinishExport(b);
            }
        });
        chkFinishChange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                curentValue.setFinishChange(b);
            }
        });

        timeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.noSettingRadioBtn:
                        curentValue.setSettingTime(false);
                        break;
                    case R.id.settingRadioBtn:
                        curentValue.setSettingTime(true);
                        break;
                }
            }
        });

        if (oldValue.isSendSms()) {
            smsSpinner.setSelection(0);
        } else {
            smsSpinner.setSelection(1);
        }

        if (!TextUtils.isEmpty(oldValue.getPhoneNumber())) {
            phoneEdt.setText(oldValue.getPhoneNumber());
        }

        if (oldValue.isFinishOrder()) {
            chkFinishOrder.setChecked(true);
        } else {
            chkFinishOrder.setChecked(false);
        }

        if (oldValue.isFinishExport()) {
            chkFinishExport.setChecked(true);
        } else {
            chkFinishExport.setChecked(false);
        }

        if (oldValue.isFinishChange()) {
            chkFinishChange.setChecked(true);
        } else {
            chkFinishChange.setChecked(false);
        }

        if (oldValue.isSettingTime()) {
            settingTimeRadioBtn.setChecked(true);
        } else {
            noSettingTimeRadioBtn.setChecked(true);
        }

        return view;
    }

    /**
     * getScreenId
     *
     * @return
     */
    @Override
    public String getScreenId() {
        return ScreenId.SmsSetting;
    }

    //サイカタ
    protected String getSaicataId() {
        return jp.co.misumi.misumiecapp.SaicataId.SmsSetting;
    }

    private void checkEnableSubmitButton() {
        if (SmsSetting.isEquals(oldValue, curentValue)) {
            submitBtn.setEnabled(false);
        } else {
            submitBtn.setEnabled(true);
        }
    }

    @Override
    public void onValueChange(int key) {
        checkEnableSubmitButton();
        if (key == SmsSetting.KEY_SEND_SMS_STATUS_CHANGE) {
            checkSendSmsStatus();
        }

        if (key == SmsSetting.KEY_SETTING_TIME_CHANGE) {
            checkSettingTimeStatus();
        }
    }

    private void checkSettingTimeStatus() {
        if (curentValue.isSettingTime()) {
            sendingTimeSpinner.setEnabled(true);
            //Todo: get value from API
            String[] sendingTimes = {"10-11", "11-12"};
            initSpinner(sendingTimeSpinner, sendingTimes, oldValue.getTimeRange(), new
                    AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i,
                                                   long l) {
                            curentValue.setTimeRange(i);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
        } else {
            sendingTimeSpinner.setAdapter(null);
            sendingTimeSpinner.setEnabled(false);
            curentValue.setTimeRange(-1);
        }
    }

    private void checkSendSmsStatus() {
        if (curentValue.isSendSms()) {
            enableSettingView(true);
        } else {
            phoneEdt.setText("");
            chkFinishOrder.setChecked(false);
            chkFinishExport.setChecked(false);
            chkFinishChange.setChecked(false);
            noSettingTimeRadioBtn.setChecked(true);
            enableSettingView(false);
        }
    }

    private void enableSettingView(boolean enable) {
        if (enable) {
            phoneEdt.setEnabled(enable);
            chkFinishOrder.setEnabled(enable);
            chkFinishExport.setEnabled(enable);
            chkFinishChange.setEnabled(enable);
            timeRadioGroup.setEnabled(enable);
            noSettingTimeRadioBtn.setEnabled(enable);
            settingTimeRadioBtn.setEnabled(enable);
            sendingTimeSpinner.setEnabled(enable);
        }
    }

    private void initSpinner(Spinner spinner, String[] values, int selection, AdapterView
            .OnItemSelectedListener listener) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                R.layout.spinner_background,
                values
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(selection);
        spinner.setOnItemSelectedListener(listener);
    }
}


