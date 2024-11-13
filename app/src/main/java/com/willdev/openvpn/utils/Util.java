package com.willdev.openvpn.utils;

import com.willdev.openvpn.model.Words;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public String findWord(String name) {
        if (Global.WORD_LIST != null && Global.WORD_LIST.size() > 0) {
            List<Words> results = new ArrayList<>();
            for (Words item : Global.WORD_LIST) {
                if (item.name.equals(name)) {
                    results.add(item);
                }
            }

            if (results.isEmpty())
                return "";
            else
                return results.get(0).value;
        } else {
            return "";
        }
    }

    public String findKey(String value) {

        if (Global.WORD_LIST != null && Global.WORD_LIST.size() > 0) {
            List<Words> results = new ArrayList<>();
            for (Words item : Global.WORD_LIST) {
                if (item.value.equals(value)) {
                    results.add(item);
                }
            }

            if (results.isEmpty())
                return "";
            else
                return results.get(0).value;
        } else {
            return "";
        }
    }

    public String setText(String code, String defaultText) {

        String word = findWord(code);

        if(!word.equals(""))
            return word;
        else
            return defaultText;
    }
}
