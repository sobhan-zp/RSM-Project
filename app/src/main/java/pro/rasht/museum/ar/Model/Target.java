package pro.rasht.museum.ar.Model;

public class Target {

    private String id;
    private String name;
    private String url;
    private String value;

    public Target() {
    }

    public Target(String id, String name, String url, String value) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
