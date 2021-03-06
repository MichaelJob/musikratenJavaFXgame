package main;

/**
 *
 * @author mjair
 */
public class Song {
    private String title;
    private String artist;
    private String genre;
    private String album;
    private String path;
    
     public Song(String songArtist){
        artist=songArtist;
    }
    
    public Song(String songTitle, String songArtist, String songGenre, String songAlbum, String songPath){
        title=songTitle;
        artist=songArtist;
        genre=songGenre;
        album=songAlbum;
        path=songPath;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public String getAlbum() {
        return album;
    }
    
    public String getPath() {
        return path;
    }
    
    @Override
    public String toString(){
        return getArtist() + " - " + getTitle() + " - " + getAlbum() + " - " + getGenre();

    }
    
    //get Artist as HashCode
   @Override
   public int hashCode() {
        return this.getArtist().hashCode();
   }
    
    //if artist is the same consider the songs as equal to sort them out - MusikRaten only needs one Song from each artist
    @Override
    public boolean equals(Object other){
        return this.getArtist().equals(((Song) other).getArtist());
    }
    
    
}