package main;


import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.controlsfx.dialog.Dialogs;

/**
 * MusikRaten
 *
 * @author mj
 */
public class SongManager {

    public static List<String> songFiles = new ArrayList<>();   //Arraylist for Songpaths
    public static List<Song> songList = new ArrayList<>();      //ArrayList for Songs
    public static Set<Song> gameSet = new HashSet<>();          //HashSet for Songs
    public static List<Song> Songs4Game;                        //List with 4 random songs

    public SongManager() {

    }

    public static void walk(String dir) {

        File root = new File(dir);    //file or folder from dir
        File[] list = root.listFiles();     //get all files/folders in startpath

        if (list == null) {
            return;   //dir was empty !
        }
        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath());
            } else if (f.isFile()) {
                if (f.toString().contains(".mp3")) {
                    try {
                        songFiles.add(f.getCanonicalPath());
                    } catch (IOException ex) {
                        Logger.getLogger(SongManager.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        }
    }

    public static void createSongList() throws IOException, UnsupportedTagException, InvalidDataException {
        int count = songFiles.size();   //Anzahl der mp3Files im Verzeichnis
        int k = 1;                      //update int i with int k in following loop
        if (count > 2000) {                //falls mehr als 4000 mp3's nur jedes dritte nehmen - das sollte reichen 
            k = 2;
        } else if (count > 4000) {                //falls mehr als 2000 mp3's nur jedes dritte nehmen - das sollte reichen 
            k = 3;
        }

        try {
            for (int i = 1; i < count; i = i + k) {
                String s = songFiles.get(i);
                Mp3File myMP3 = new Mp3File(s);
                String artist = "";
                String album = "";
                String title = "";
                String genre = "";
                String path = "";
                if (myMP3.hasId3v2Tag()) {                   //itunes hat keine id3v2 tags mehr in original form gelassen sondern zu unicode formattiert
                    artist = myMP3.getId3v2Tag().getArtist();       //werden jedoch hier ausgelesen
                    album = myMP3.getId3v2Tag().getAlbum();
                    title = myMP3.getId3v2Tag().getTitle();
                    genre = myMP3.getId3v2Tag().getGenreDescription();
                    path = songFiles.get(i);
                    //System.out.println(Integer.toString(i)+" Id3v2 "+artist+" "+genre);   for Debugging
                } else if (myMP3.hasId3v1Tag()) {
                    artist = myMP3.getId3v1Tag().getArtist();
                    album = myMP3.getId3v1Tag().getAlbum();
                    title = myMP3.getId3v1Tag().getTitle();
                    genre = myMP3.getId3v1Tag().getGenreDescription();
                    path = songFiles.get(i);
                    //System.out.println(Integer.toString(i)+" Id3v1");  only a few have this tag, at least in my Music Library 
                } else {
                    continue;
                }
                if (artist != null && genre != null && path != null) {
                    Song mySong = new Song(title, artist, genre, album, path);
                    songList.add(mySong);
                }
            }
        } catch (IOException | UnsupportedTagException | InvalidDataException ex) {
            //Logger.getLogger(SongManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void createGameSet() {
        try {
        //erstellt Collection HashSet mit Songs für Game, Set für Artist darf nur einmal vorkommen
            //Genre einschränken auf pop, rock, alternative, ...?
            //predicates (filter)
            CharSequence genreCharPop = "Pop";
            Predicate<Song> genrePop = p -> p.getGenre().contains(genreCharPop);
            CharSequence genreCharRock = "Rock";
            Predicate<Song> genreRock = p -> p.getGenre().contains(genreCharRock);
            CharSequence genreCharMetal = "Metal";
            Predicate<Song> genreMetal = p -> p.getGenre().contains(genreCharMetal);
            CharSequence genreCharBlues = "Blues";
            Predicate<Song> genreBlues = p -> p.getGenre().contains(genreCharBlues);
            CharSequence genreCharAlternative = "Alternative";
            Predicate<Song> genreAlternative = p -> p.getGenre().contains(genreCharAlternative);
            CharSequence genreCharOldies = "Oldies";
            Predicate<Song> genreOldies = p -> p.getGenre().contains(genreCharOldies);
            CharSequence genreCharGrunge = "Grunge";
            Predicate<Song> genreGrunge = p -> p.getGenre().contains(genreCharGrunge);
            //concatonate predicates to one
            Predicate<Song> fullPredicate = genreRock.or(genrePop).or(genreMetal).or(genreGrunge).or(genreOldies).or(genreAlternative).or(genreBlues);

            //gameSet erstellen anhand der predicates
            gameSet = songList.stream()
                    .filter(fullPredicate)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            Dialogs.create()
                    .title("Error Blindtest - MusikRaten")
                    .masthead(null)
                    .message("An error occured while saving the gameSet. Cause:" + e.getMessage())
                    .showInformation();
        }

    }

    public static List<Song> get4Songs() {
        Songs4Game = new ArrayList<Song>(gameSet);
        Collections.shuffle(Songs4Game);
        while (Songs4Game.size() > 4) {     //reduce until 4 are left
            Songs4Game.remove(3);      //removes any (here index 3)
        }
        //return list
        return Songs4Game;
    }

}

/*    Code snippets

 //return mySong.getArtist() + " - " + mySong.getTitle() + " - " + mySong.getAlbum() + " - " + mySong.getGenre() + " - " + mySong.getGenre();


        //print out for debugging
        for (Song s : Songs4Game) {
            System.out.println(s.getArtist() + " " + s.getPath());
        }
 */
