package com.example.android.responder;

import java.util.HashMap;

/**
 * Created by Omkar Vaidya on 3/19/2017.
 */

public class LocationCode {

    HashMap<String, String> locationMap;
    HashMap<String, Integer> locationCode;

    public LocationCode() {
        locationMap = new HashMap<String, String>();
        locationCode = new HashMap<String, Integer>();
        //Use major value of your beacon. Value must be in hex
        locationMap.put("4369", "Marston");
        locationMap.put("8738", "Rawlings");
        locationMap.put("13107", "CSE");
        locationMap.put("17476", "Reitz");
        locationCode.put("4369", 1);
        locationCode.put("8738", 3);
        locationCode.put("13107", 5);
        locationCode.put("17476", 7);
    }

    public String getLocationAt(String code) {
        if (locationMap.containsKey(code))
            return locationMap.get(code);
        else
            return null;
    }

    public int getCode(String code, String type) {
        if (locationCode.containsKey(code))
        {
            if (type.equals("entry"))
                return locationCode.get(code);
            else
                return locationCode.get(code) + 1;
        }
        else
            return 0;
    }
}
