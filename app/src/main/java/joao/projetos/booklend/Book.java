package joao.projetos.booklend;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Book {

    private String title;
    private String author;
    private String coverLink;
    private boolean isLent;
    private String owner;
    private String bookID;

    public Book(){

    }

    public Book(String title, String author, String coverLink, boolean isLent, String owner, String bookID){
        this.title = title;
        this.author = author;
        this.coverLink = coverLink;
        this.isLent = isLent;
        this.owner = owner;
        this.bookID = bookID;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCoverLink() {
        return coverLink;
    }

    public void setCoverLink(String coverLink) {
        this.coverLink = coverLink;
    }

    public boolean isLent() {
        return isLent;
    }

    public void setLent(boolean lent) {
        isLent = lent;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }
}
