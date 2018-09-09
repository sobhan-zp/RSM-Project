package pro.rasht.museum.ar.network.Model;

public class BaseGeo {

    private String name;
    private String detail;
    private String address;
    private String img;
    private String placeid;

    public BaseGeo() {
    }

    public BaseGeo(String name, String detail, String address, String img, String placeid) {
        this.name = name;
        this.detail = detail;
        this.address = address;
        this.img = img;
        this.placeid = placeid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPlaceid() {
        return placeid;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }
}
