package com.re.lkspmi.utils;

import ru.spmi.lk.authorization.LkSpmi;
import ru.spmi.lk.entities.profile.ProfileCurrent;

public class LkSingleton {
    private LkSpmi lkSpmi;
    private ProfileCurrent profileCurrent;
    private static LkSingleton instance;
    private boolean isLoaded = false;

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

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }
}
