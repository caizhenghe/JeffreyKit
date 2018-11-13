package com.czh.tax;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText mSalaryInputEt, mOldSalaryInputEt, mTakeOffInputEt;
    private TextView mRealSalaryTv, mTaxTv, mFundTv, mSecurityTv;
    private ImageView mResidenceIv;

    private int mMedicalLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSalaryInputEt = (EditText) findViewById(R.id.input_salary_et);
        mOldSalaryInputEt = (EditText) findViewById(R.id.input_old_salary_et);
        mTakeOffInputEt = (EditText) findViewById(R.id.input_takeoff_et);
        mRealSalaryTv = (TextView) findViewById(R.id.real_salary_tv);
        mTaxTv = (TextView) findViewById(R.id.tax_tv);
        mFundTv = (TextView) findViewById(R.id.fund_tv);
        mSecurityTv = (TextView) findViewById(R.id.security_tv);

        mResidenceIv = (ImageView) findViewById(R.id.residence_iv);
        mResidenceIv.setOnCreateContextMenuListener(this);
        mResidenceIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.showContextMenu();
            }
        });
        registerForContextMenu(mResidenceIv);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterForContextMenu(mResidenceIv);
    }

    public void doClick(View v) {
        switch (v.getId()) {
            case R.id.calculate_btn:
                String oldSalaryInput = "";
                boolean isOldSalaryVisible = mOldSalaryInputEt.getVisibility() == View.VISIBLE;
                oldSalaryInput = mOldSalaryInputEt.getText().toString();
                String salaryInput = mSalaryInputEt.getText().toString();
                String takeOffInput = mTakeOffInputEt.getText().toString();
                if (!TaxUtils.isNum(salaryInput)
                        || !TaxUtils.isNum(takeOffInput)
                        || isOldSalaryVisible && !TaxUtils.isNum(oldSalaryInput)) {
                    Toast.makeText(this, "请输入数字！", Toast.LENGTH_SHORT).show();
                    return;
                }
                int salary = TextUtils.isEmpty(salaryInput) ? 0 : Integer.valueOf(salaryInput);
                int oldSalary = TextUtils.isEmpty(salaryInput) ? 0 : Integer.valueOf(oldSalaryInput);
                int extraTakeOff = TextUtils.isEmpty(takeOffInput) ? 0 : Integer.valueOf(takeOffInput);
                float fund = TaxUtils.getFunds(isOldSalaryVisible ? oldSalary : salary, TaxUtils.DEFAULT_FUNDS_RATE);
                float security = TaxUtils.getSecurity(isOldSalaryVisible ? oldSalary : salary, mMedicalLevel);
                float tax = TaxUtils.getTax(this, salary, security, fund, extraTakeOff);
                float result = salary - tax - fund - security - extraTakeOff;
                mFundTv.setText("应缴公积金：" + String.format("%.2f", fund));
                mSecurityTv.setText("应缴社保：" + String.format("%.2f", security));
                mTaxTv.setText("应缴税：" + String.format("%.2f", tax));
                mRealSalaryTv.setText("到手工资：" + String.format("%.2f", result));
                break;
            case R.id.security_tips_tv:
                MedicalRuleActivity.startActivity(this);
                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.residence, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        mOldSalaryInputEt.setVisibility(View.GONE);
        switch (item.getItemId()) {
            case R.id.is_sz_residence:
                mMedicalLevel = TaxUtils.FIRST_MEDICAL_SECURITY_LEVEL;
                Toast.makeText(this, "已切换至深户", Toast.LENGTH_LONG).show();
                return true;
            case R.id.is_not_sz_residence:
                mMedicalLevel = TaxUtils.SECOND_MEDICAL_SECURITY_LEVEL;
                Toast.makeText(this, "已切换至非深户", Toast.LENGTH_LONG).show();
                return true;
            case R.id.change_salary_recently_for_sz_residence:
                mOldSalaryInputEt.setVisibility(View.VISIBLE);
                return true;
        }
        return super.onContextItemSelected(item);
    }
}
