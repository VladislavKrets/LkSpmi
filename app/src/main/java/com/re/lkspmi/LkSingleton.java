package com.re.lkspmi;

import ru.spmi.lk.authorization.LkSpmi;
import ru.spmi.lk.entities.profile.ProfileCurrent;

public class LkSingleton {
    private LkSpmi lkSpmi;
    private ProfileCurrent profileCurrent;
    private static LkSingleton instance;

    private LkSingleton(){}

    public static LkSingleton getInstance(){
        if (instance == null) instance = new LkSingleton();
        return instance;
    }

    public LkSpmi getLkSpmi() {
        return lkSpmi;
    }

    public void setLkSpmi(LkSpmi lkSpmi) {
        this.lkSpmi = lkSpmi;
    }

    public ProfileCurrent getProfileCurrent() {
        return profileCurrent;
    }

    public void setProfileCurrent(ProfileCurrent profileCurrent) {
        this.profileCurrent = profileCurrent;
    }
}
