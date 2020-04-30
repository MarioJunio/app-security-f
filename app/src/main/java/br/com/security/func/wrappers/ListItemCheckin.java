package br.com.security.func.wrappers;

/**
 * Created by mariomartins on 17/09/17.
 */

public abstract class ListItemCheckin {

    public static final int TYPE_DATE = 0;
    public static final int TYPE_CHECKIN_TOP = 1;
    public static final int TYPE_CHECKIN_MIDDLE = 2;
    public static final int TYPE_CHECKIN_BOTTOM = 3;
    public static final int TYPE_CHECKIN_FULL = 4;

    abstract public int getType();
}
