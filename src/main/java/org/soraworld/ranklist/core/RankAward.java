package org.soraworld.ranklist.core;

import org.soraworld.hocon.node.Setting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Himmelt
 */
public class RankAward implements Serializable {
    @Setting
    private final HashMap<String, ArrayList<String>> rewards = new HashMap<>();

    public List<String> getAwards(int top) {
        for (Map.Entry<String, ArrayList<String>> e : rewards.entrySet()) {
            String[] ss = e.getKey().split("-");
            if (ss.length == 2) {
                try {
                    int min = Integer.parseInt(ss[0]);
                    int max = Integer.parseInt(ss[1]);
                    if (min <= top && top <= max) {
                        return e.getValue();
                    }
                } catch (Throwable ignored) {
                }
            }
        }
        return new ArrayList<>();
    }
}
