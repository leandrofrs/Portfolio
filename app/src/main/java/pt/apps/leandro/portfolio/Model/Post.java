package pt.apps.leandro.portfolio.Model;



public class Post {

   private String Title, Desc,image, username;



    public Post() {
    }

    public Post(String title, String desc, String image, String username) {
        Title = title;
        Desc = desc;
        this.image = image;
        this.username = username;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
