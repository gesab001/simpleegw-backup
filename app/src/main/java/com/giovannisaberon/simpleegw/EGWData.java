package com.giovannisaberon.simpleegw;

public class EGWData {
    private int id;
    private String bookcode;
    private String title;
    private int page;
    private int paragraph;
    private String word;

    public EGWData(int id, String bookcode, String title, int page, int paragraph, String word){
        this.id = id;
        this.bookcode = bookcode;
        this.title = title;
        this.page = page;
        this.paragraph = paragraph;
        this.word = word;
    }

    public int setId(){
        return this.id;
    }

    public String setBookcode(){
        return this.bookcode;
    }

    public String setTitle(){
        return this.title;
    }

    public int setPage(){
        return this.page;
    }

    public int setParagraph(){
        return this.paragraph;
    }

    public String setWord(){
        return this.word;
    }

    public int getId(){
        return this.id;
    }

    public String getBookcode(){
        return this.bookcode;
    }

    public String getTitle(){
        return this.title;
    }

    public int getPage(){
        return this.page;
    }

    public int getParagraph(){
        return this.paragraph;
    }

    public String getWord(){
        return this.word;
    }


    public String toString(){
        String string = this.word + " (" + this.bookcode + " " + this.page + ", " + this.paragraph + ")";
        return string;
    }

}
