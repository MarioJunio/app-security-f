package br.com.security.func.wrappers;

import br.com.security.func.models.orm.Checkin;

/**
 * Created by mariomartins on 17/09/17.
 */

public class CheckinType extends ListItemCheckin {

    private int type;
    private Checkin checkin;

    public CheckinType(Checkin checkin, int type) {
        this.checkin = checkin;
        setType(type);
    }

    public Checkin getCheckin() {
        return checkin;
    }

    public void setCheckin(Checkin checkin) {
        this.checkin = checkin;
    }

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CheckinType{" +
                "type=" + type +
                ", checkin=" + checkin +
                '}';
    }
}
