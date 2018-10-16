package pro.rasht.museum.ar.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by janisharali on 29/08/16.
 */
public class Model_Gallery {

    private String id_img;

    private String title_img;

    private String years_img;

    private String desc_img;

    private String like_img;

    private String image_img;




    public String getImage_img() {
        return image_img;
    }

    public void setImage_img(String image_img) {
        this.image_img = image_img;
    }

    public String getId_img() {
        return id_img;
    }

    public void setId_img(String id_img) {
        this.id_img = id_img;
    }

    public String getLike_img() {
        return like_img;
    }

    public void setLike_img(String like_img) {
        this.like_img = like_img;
    }



    public String getTitle_img() {
        return title_img;
    }

    public void setTitle_img(String title_img) {
        this.title_img = title_img;
    }

    public String getYears_img() {
        return years_img;
    }

    public void setYears_img(String years_img) {
        this.years_img = years_img;
    }

    public String getDesc_img() {
        return desc_img;
    }

    public void setDesc_img(String desc_img) {
        this.desc_img = desc_img;
    }
}
