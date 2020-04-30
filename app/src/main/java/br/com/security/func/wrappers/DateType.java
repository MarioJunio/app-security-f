package br.com.security.func.wrappers;

/**
 * Created by mariomartins on 17/09/17.
 */

public class DateType extends ListItemCheckin {

    private String date;

    public DateType(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int getType() {
        return TYPE_DATE;
    }

}
