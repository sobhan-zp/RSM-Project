package pro.rasht.museum.ar.Model;

public class HoloModel {

    private String id;
    private String iduser;
    private String id_point;
    private String url;

    public HoloModel() {
    }

    public HoloModel(String id, String iduser, String id_point, String url) {
        this.id = id;
        this.iduser = iduser;
        this.id_point = id_point;
        this.url = url;
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

    public String getId_point() {
        return id_point;
    }

    public void setId_point(String id_point) {
        this.id_point = id_point;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
