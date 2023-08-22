package com.hk210.postit;

import java.io.Serializable;

public class OnboardingItem implements Serializable {
    public int image;
    public String title;
    public String des;



    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }


}
