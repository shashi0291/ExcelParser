package com.example.xcelparser2.database;

import java.util.Date;
import java.util.TreeMap;

public class FinalResult {
    public Date sortStartTime;

    // certificateId, count
    public TreeMap<String, Integer> mapCertificateIdToCount;
}
