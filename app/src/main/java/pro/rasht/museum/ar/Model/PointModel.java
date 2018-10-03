package pro.rasht.museum.ar.Model;

public class PointModel {

    private String id;
    private String iduser;
    private String isar;
    private String isvr;
    private String isholo;
    private String geo;
    private String fullname;
    private String description;
    private String phone;
    private String address;
    private String image;
    private String voice;
    private String ishistory;

    public PointModel() {
    }

    public PointModel(String id, String iduser, String isar, String isvr, String isholo, String geo, String fullname, String description, String phone, String address, String image, String voice, String ishistory) {
        this.id = id;
        this.iduser = iduser;
        this.isar = isar;
        this.isvr = isvr;
        this.isholo = isholo;
        this.geo = geo;
        this.fullname = fullname;
        this.description = description;
        this.phone = phone;
        this.address = address;
        this.image = image;
        this.voice = voice;
        this.ishistory = ishistory;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public String getIsar() {
        return isar;
    }

    public void setIsar(String isar) {
        this.isar = isar;
    }

    public String getIsvr() {
        return isvr;
    }

    public void setIsvr(String isvr) {
        this.isvr = isvr;
    }

    public String getIsholo() {
        return isholo;
    }

    public void setIsholo(String isholo) {
        this.isholo = isholo;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getIshistory() {
        return ishistory;
    }

    public void setIshistory(String ishistory) {
        this.ishistory = ishistory;
    }
}
