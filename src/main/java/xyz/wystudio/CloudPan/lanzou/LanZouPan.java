package xyz.wystudio.CloudPan.lanzou;

import xyz.wystudio.CloudPan.api.Pan;

import java.util.ArrayList;
import java.util.List;

public class LanZouPan {
    private static LanZouPan instance;

    public static LanZouPan getInstance() {
        if (instance == null) {
            instance = new LanZouPan();
        }
        return instance;
    }

}
