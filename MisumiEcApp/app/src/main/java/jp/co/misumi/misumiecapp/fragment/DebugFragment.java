package jp.co.misumi.misumiecapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import jp.co.misumi.misumiecapp.AppConfig;
import jp.co.misumi.misumiecapp.AppConst;
import jp.co.misumi.misumiecapp.AppLog;
import jp.co.misumi.misumiecapp.R;
import jp.co.misumi.misumiecapp.ScreenId;
import jp.co.misumi.misumiecapp.observer.AppNotifier;
import jp.co.misumi.misumiecapp.util.FileUtil;
import jp.co.misumi.misumiecapp.util.SubsidiaryCode;


/**
 *
 */
public class DebugFragment extends BaseFragment {


    View layoutView;

    Spinner usersSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_debug, container, false);


        layoutView.findViewById(R.id.buttonUpdateApi).setOnClickListener(updateApi);
        layoutView.findViewById(R.id.buttonUpdateNas).setOnClickListener(updateNas);
        layoutView.findViewById(R.id.buttonUpdateSid).setOnClickListener(updateSid);

        
        // API
        ((EditText)layoutView.findViewById(R.id.editTextApiAddr)).setText(AppConfig.getInstance().getApiBaseUrl());
        // NAS
        ((EditText)layoutView.findViewById(R.id.editTextNasAddr)).setText(AppConfig.getInstance().getNasBaseUrl());
        // SessionID
        ((EditText)layoutView.findViewById(R.id.editTextSid)).setText(AppConfig.getInstance().getSessionId());

        // カテゴリ削除
        layoutView.findViewById(R.id.buttonClearCategory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUtil.deleteCategory(getContext());
                AppConfig.getInstance().setCategoryUpdateTime("");
                Toast.makeText(getContext(),R.string.debug_message_category_deleted, Toast.LENGTH_SHORT ).show();
            }
        });

        // ログインIDを設定
        layoutView.findViewById(R.id.buttonSetLoginId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppConfig.getInstance().setLoginId("mojp202");
                AppConfig.getInstance().setLoginPassword("CF2RTKHI");
                AppConfig.getInstance().setEnableIDandPassward(true);
                Toast.makeText(getContext(), R.string.debug_message_set_login, Toast.LENGTH_SHORT).show();
            }
        });

        // ユーザーIDのスピナー
        usersSpinner = (Spinner) layoutView.findViewById(R.id.spinnerUsers);
        usersSpinner.setAdapter(getUsersAdapter());
        usersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    return;
                }
                Users users = (Users) usersSpinner.getAdapter().getItem(position);
                AppConfig.getInstance().setLoginId(users.user);
                AppConfig.getInstance().setLoginPassword(users.pass);
                AppConfig.getInstance().setEnableIDandPassward(true);
                Toast.makeText(getContext(), R.string.debug_message_set_login2, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // 現地コード
        RadioGroup radioGroup = (RadioGroup) layoutView.findViewById(R.id.RadioSubsidiaryGroup);
        switch (AppConst.subsidiaryCode){
            case AppConst.SUBSIDIARY_CODE_MJP:
                ((RadioButton)layoutView.findViewById(R.id.radioJapan)).setChecked(true);
                break;
            case AppConst.SUBSIDIARY_CODE_CHN:
                ((RadioButton)layoutView.findViewById(R.id.radioChinese)).setChecked(true);
                break;
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radioJapan:
                        AppLog.d("JAPAN");
                        AppConst.subsidiaryCode = AppConst.SUBSIDIARY_CODE_MJP;
                        break;
                    case R.id.radioChinese:
                        AppLog.d("CHINESE");
                        AppConst.subsidiaryCode = AppConst.SUBSIDIARY_CODE_CHN;
                        break;
                }
                AppNotifier.getInstance().changeLocalSubsidiary();
            }
        });

        // 通貨
        RadioGroup radioGroupCurrency = (RadioGroup) layoutView.findViewById(R.id.RadioCurrencyGroup);
        switch (AppConfig.getInstance().getCurrencyCode()){
            case AppConst.CURRENCY_CODE_JPY:
                ((RadioButton)layoutView.findViewById(R.id.radioYen)).setChecked(true);
                break;
            case AppConst.CURRENCY_CODE_RMB:
                ((RadioButton)layoutView.findViewById(R.id.radioGen)).setChecked(true);
                break;
            case AppConst.CURRENCY_CODE_USD:
                ((RadioButton)layoutView.findViewById(R.id.radioDoller)).setChecked(true);
                break;
        }
        radioGroupCurrency.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioYen:
                        AppConfig.getInstance().setCurrencyCode(AppConst.CURRENCY_CODE_JPY);
                        break;
                    case R.id.radioGen:
                        AppConfig.getInstance().setCurrencyCode(AppConst.CURRENCY_CODE_RMB);
                        break;
                    case R.id.radioDoller:
                        AppConfig.getInstance().setCurrencyCode(AppConst.CURRENCY_CODE_USD);
                        break;
                }
            }
        });



        return layoutView;
    }


    ArrayAdapter<Users> getUsersAdapter() {
        String[] data1 = {
                "","","",
                "771150","motest04","HQQLQ2CK",
                "771150","motest05","ZS4IPJTC",
                "771150","motest06","MMN7H8YY",
                "769955","motest07","565NZXQ1",
                "769956","motest08","BIUAN1AF",
                "771150","motest09","5WKK74B7",
                "771150","motest16","3WAUZYMR",
                "771152","motest17","8YS3YAH2",
                "746112","motest18","S6BBVG44",
        };
        String[] data2 = {
                "","","",
                "061061","motest132","N7SQGEHF",
                "011368","motest147","TQTD2R8N",
                "771150","motest148","I2GWVRBR",
                "771150","motest149","T3TD9ZPM",
                "771150","motest150","V9NYR3L2",
        };


        ArrayAdapter<Users> usersArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        usersArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (SubsidiaryCode.isJapan()) {
            for (int ii = 0; ii < data1.length; ) {
                usersArrayAdapter.add(new Users(data1[ii++], data1[ii++], data1[ii++]));
            }
        }else{
            for (int ii = 0; ii < data2.length; ) {
                usersArrayAdapter.add(new Users(data2[ii++], data2[ii++], data2[ii++]));
            }
        }

        return usersArrayAdapter;
    }

    class Users {
        String note;
        String user;
        String pass;
        public Users(String note, String user, String pass){
            this.note = note;
            this.user = user;
            this.pass = pass;
        }

        @Override
        public String toString() {
            String result = "";
            if (note != null && !note.isEmpty()){
                result = "(" + note + ") ";
            }
            result += user;
            return result;
        }
    }

    /**
     * getScreenId
     *
     * @return
     */
    @Override
    public String getScreenId() {
        return ScreenId.Debug;
    }

    View.OnClickListener updateApi = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String addr = ((EditText)layoutView.findViewById(R.id.editTextApiAddr)).getText().toString();
            AppConfig.getInstance().setApiBaseUrl(addr);
        }
    };
    View.OnClickListener updateNas = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String addr = ((EditText)layoutView.findViewById(R.id.editTextNasAddr)).getText().toString();
            AppConfig.getInstance().setNasBaseUrl(addr);
        }
    };
    View.OnClickListener updateSid = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String sid = ((EditText)layoutView.findViewById(R.id.editTextSid)).getText().toString();
            AppConfig.getInstance().setSessionId(sid);
        }
    };



	//サイカタ
	protected String getSaicataId() {
		return null;	//nullは送信しない
	}
}


