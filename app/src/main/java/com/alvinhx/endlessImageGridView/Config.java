package com.alvinhx.endlessImageGridView;

import com.fivehundredpx.api.auth.AccessToken;

import java.util.HashMap;

/**
 * to store value sets
 * Created by solor on 2016-02-09.
 */
public class Config {

    private static Config instance;
    private HashMap<String,AccessToken> map;

    public static final String TOKEN = "auth_token";


    private Config(){ map = new HashMap<String,AccessToken>();}

    public static Config getInstance(){
        if(instance == null){
            instance = new Config();
        }
        return instance;
    }

    public static void clearInstance(){instance = null;}

    public void setKey(String key, AccessToken value){map.put(key,value);}

    public AccessToken getKey(String key){ return map.get(key);}


}
