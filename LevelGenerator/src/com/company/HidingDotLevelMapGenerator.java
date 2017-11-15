package com.company;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by madan on 5/2/17.
 */
public class HidingDotLevelMapGenerator {
    public static void main(String[] args) throws IOException {
        String levelFile=args[0];
        String levelMap=args[1];
        int availableSince= Integer.parseInt(args[2]);

        File levelMapFile=new File(levelMap);
        if (!levelMapFile.exists())levelMapFile.createNewFile();
        FileWriter levelMapWriter=new FileWriter(levelMapFile, true);
        BufferedWriter bw=new BufferedWriter(levelMapWriter);
        bw.write("#"+levelFile+","+availableSince+"\n");
        bw.flush();
        bw.close();
    }
}
