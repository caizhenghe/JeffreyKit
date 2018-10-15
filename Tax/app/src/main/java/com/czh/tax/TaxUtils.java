package com.czh.tax;

import android.content.Context;
import android.widget.Toast;

import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/10/14.
 */

public class TaxUtils {
    public static final float DEFAULT_FUNDS_RATE = 0.05f;          // 公积金缴纳比例
    public static final int FIRST_MEDICAL_SECURITY_LEVEL = 1;     // 一级医保
    public static final int SECOND_MEDICAL_SECURITY_LEVEL = 2;    // 二级医保
    public static final int THIRD_MEDICAL_SECURITY_LEVEL = 3;     // 三级医保

    private static final int TAX_RATE_LEVEL = 7;                   // 税率等级数量
    private static final int TAX_COLLECT_THRESHOLD = 5000;         // 个税起征点
    private static final int AVERAGE_SOCIAL_SALARY = 8348;         // 社平工资
    private static final int MIN_SOCIAL_SALARY = 2200;             // 最低工资

    private static final int FIRST_MEDICAL_SECURITY_MAX_BASE = AVERAGE_SOCIAL_SALARY * 3;      // 一级医保最高基数
    private static final float FIRST_MEDICAL_SECURITY_MIN_BASE = AVERAGE_SOCIAL_SALARY * 0.6f; // 一级医保最低基数
    private static final int SECOND_MEDICAL_SECURITY_BASE = AVERAGE_SOCIAL_SALARY;             // 二级医保基数
    private static final int THIRD_MEDICAL_SECURITY_BASE = AVERAGE_SOCIAL_SALARY;              // 三级级医保基数
    private static final int OLD_SECURITY_MAX_BASE = AVERAGE_SOCIAL_SALARY * 3;                // 养老险最高基数
    private static final int OLD_SECURITY_MIN_BASE = MIN_SOCIAL_SALARY;                        // 养老险最低基数
    private static final int LOST_JOB_SECURITY_BASE = MIN_SOCIAL_SALARY;                       // 失业险基数

    private static final float LOSE_JOB_SECURITY_RATE = 0.005f;         // 失业险缴纳比例
    private static final float OLD_SECURITY_RATE = 0.08f;               // 养老险缴纳比例
    private static final float FIRST_MEDICAL_SECURITY_RATE = 0.02f;     // 一级医保缴纳比例
    private static final float SECOND_MEDICAL_SECURITY_RATE = 0.002f;   // 二级医保缴纳比例
    private static final float THIRD_MEDICAL_SECURITY_RATE = 0.001f;    // 三级医保缴纳比例


    private static final int[] sRealSalaryBounds = new int[]{
            0,
            3000,
            12000,
            25000,
            35000,
            55000,
            80000,
            Integer.MAX_VALUE};

    private static final TaxRateBean[] sTaxRates = new TaxRateBean[]{
            new TaxRateBean(0.03f, 0),
            new TaxRateBean(0.1f, 210),
            new TaxRateBean(0.2f, 1410),
            new TaxRateBean(0.25f, 2660),
            new TaxRateBean(0.30f, 4410),
            new TaxRateBean(0.35f, 7160),
            new TaxRateBean(0.45f, 15160),
    };

    static class TaxRateBean {
        float taxRate;
        int quickTakeOff;

        TaxRateBean(float taxRate, int takeOff) {
            this.taxRate = taxRate;
            this.quickTakeOff = takeOff;
        }
    }

    public static float getTax(Context context, int salary, float security, float fund, float extraTakeOff) {
        // 新规个税起征点：5000
        // 全月应纳税所得额 = 税前应发工资 - 个人社保 - 个人公积金 - 额外扣除 - 个税起征点
        // 工资个税 = 全月应纳税所得额 * 税率 - 速算扣除数
        float realSalary = salary - security - fund - extraTakeOff - TAX_COLLECT_THRESHOLD;
        float tax = 0;
        if (realSalary <= 0) {
            Toast.makeText(context, "努力工作，早日纳税！", Toast.LENGTH_SHORT).show();
            return 0;
        }

        for (int i = 0; i < TAX_RATE_LEVEL; i++) {
            if (realSalary > sRealSalaryBounds[i] && realSalary <= sRealSalaryBounds[i + 1]) {
                tax = realSalary * sTaxRates[i].taxRate - sTaxRates[i].quickTakeOff;
                break;
            }
        }

        return tax;
    }

    public static float getSecurity(int salary, int medicalSecurityLevel) {
        float medicalBase = salary;
        float medicalRate = FIRST_MEDICAL_SECURITY_RATE;
        float oldBase = salary;

        switch (medicalSecurityLevel) {
            case FIRST_MEDICAL_SECURITY_LEVEL:
                if (salary > FIRST_MEDICAL_SECURITY_MAX_BASE) {
                    medicalBase = FIRST_MEDICAL_SECURITY_MAX_BASE;
                } else if (salary < FIRST_MEDICAL_SECURITY_MIN_BASE) {
                    medicalBase = FIRST_MEDICAL_SECURITY_MIN_BASE;
                }
                break;
            case SECOND_MEDICAL_SECURITY_LEVEL:
                medicalBase = SECOND_MEDICAL_SECURITY_BASE;
                medicalRate = SECOND_MEDICAL_SECURITY_RATE;
                break;
            case THIRD_MEDICAL_SECURITY_LEVEL:
                medicalBase = THIRD_MEDICAL_SECURITY_BASE;
                medicalRate = THIRD_MEDICAL_SECURITY_RATE;
                break;
        }

        if (oldBase > OLD_SECURITY_MAX_BASE) {
            oldBase = OLD_SECURITY_MAX_BASE;
        } else if (oldBase < OLD_SECURITY_MIN_BASE) {
            oldBase = OLD_SECURITY_MIN_BASE;
        }

        return medicalBase * medicalRate + oldBase * OLD_SECURITY_RATE + LOST_JOB_SECURITY_BASE * LOSE_JOB_SECURITY_RATE;
    }

    public static float getFunds(int salary, float securityRate) {
        return salary * securityRate;
    }

    public static boolean isNum(String text) {
        return Pattern.matches("^[0-9]*$", text);
    }

}
