package joao.projetos.booklend;

public class BookBuilder {
    private String title;
    private String author;
    private String coverLink;
    private boolean isLent;
    private String owner;
    private String bookID;

    public BookBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public BookBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    public BookBuilder setCoverLink(String coverLink) {
        this.coverLink = coverLink;
        return this;
    }

    public BookBuilder setIsLent(boolean isLent) {
        this.isLent = isLent;
        return this;
    }

    public BookBuilder setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public BookBuilder setBookID(String bookID){
        this.bookID = bookID;
        return this;
    }

    public Book createBook() {
        return new Book(title, author, coverLink, isLent, owner, bookID);
    }
}