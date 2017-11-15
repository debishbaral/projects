package com.company;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import org.lwjgl.Sys;

/**
 * Created by madan on 4/26/17.
 */
public class GameImp{
    public static void main(String[] args) {
        int m=0;
        long st = System.currentTimeMillis();
        for (int i=0 ; i<480; i++){
            for (int j=0; j<800; j++){
                for (int k=0; k<480; k++){
                    for (int l=0; l<800; l++){
                        m++;
                    }
                }
            }
        }
        System.out.println(m);
        System.out.println((System.currentTimeMillis()-st));
    }
}
