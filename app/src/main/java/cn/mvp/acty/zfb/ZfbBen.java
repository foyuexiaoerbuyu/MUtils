package cn.mvp.acty.zfb;

import java.util.List;

public class ZfbBen {
    private String year;

    private String month;
    private double monthlyDue;

    public ZfbBen(String year,String month, double monthlyDue) {
        this.year = year;
        this.month = month;
        this.monthlyDue = monthlyDue;
    }


    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getMonthlyDue() {
        return monthlyDue;
    }

    public void setMonthlyDue(double monthlyDue) {
        this.monthlyDue = monthlyDue;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

}
