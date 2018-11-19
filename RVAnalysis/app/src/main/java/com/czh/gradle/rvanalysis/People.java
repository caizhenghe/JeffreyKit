package com.czh.gradle.rvanalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class People {

    enum Body {
        BODY_SLIM, BODY_FAT, BODY_TALL, BODY_SHORT
    }


    enum Appearance {
        FACE_UGLY, FACE_BEAUTIFUL
    }

    private String name;
    private Body body;
    private Appearance appearance;

    private String[] nameArray = {"lily", "marie", "alice"};
    private Body[] bodyArray = {Body.BODY_FAT, Body.BODY_SHORT, Body.BODY_SLIM, Body.BODY_TALL};
    private Appearance[] faceArray = {Appearance.FACE_UGLY, Appearance.FACE_BEAUTIFUL};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Body getBody() {
        return body;
    }

    public String getBodyString() {
        String result;
        switch (body) {
            case BODY_FAT:
                result = "fat";
                break;
            case BODY_SLIM:
                result = "slim";
                break;
            case BODY_TALL:
                result = "tall";
                break;
            case BODY_SHORT:
                result = "short";
                break;
            default:
                result = "normal";
                break;

        }
        return result;
    }

    public String getAppearanceString() {
        String result = "";
        switch (appearance) {
            case FACE_UGLY:
                result = "ugly";
                break;
            case FACE_BEAUTIFUL:
                result = "beautiful";
                break;
        }
        return result;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Appearance getAppearance() {
        return appearance;
    }

    public void setAppearance(Appearance appearance) {
        this.appearance = appearance;
    }

    public People randomName() {
        // random(): 0~1
        int index = (int) (nameArray.length * Math.random());
        name = nameArray[index];
        return this;
    }

    public People randomBody() {
        int index = (int) (bodyArray.length * Math.random());
        body = bodyArray[index];
        return this;
    }

    public People randomAppearance() {
        int index = (int) (faceArray.length * Math.random());
        appearance = faceArray[index];
        return this;
    }
}
