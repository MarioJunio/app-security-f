package br.com.security.func.models.stub;

import java.util.Collection;

import br.com.security.func.models.orm.Checkin;

/**
 * Created by mariomartins on 15/09/17.
 */

public class SyncCheckins {

    private Long dataSync;
    private Collection<Checkin> checkins;

    public SyncCheckins(Long dataSync, Collection<Checkin> checkins) {
        super();
        this.dataSync = dataSync;
        this.checkins = checkins;
    }

    public Long getDataSync() {
        return dataSync;
    }

    public void setDataSync(Long dataSync) {
        this.dataSync = dataSync;
    }

    public Collection<Checkin> getCheckins() {
        return checkins;
    }

    public void setCheckins(Collection<Checkin> checkins) {
        this.checkins = checkins;
    }

}
