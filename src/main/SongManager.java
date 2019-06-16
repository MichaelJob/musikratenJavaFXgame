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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 * SongManager.java Walking through directorys and importing mp3 - creating data
 * to play with
 *
 * @author Michael Job
 */
public class SongManager {

    public static List<String> songFiles = new ArrayList<>();   //Arraylist for Songpaths
    public static List<Song> songList = new ArrayList<>();      //ArrayList for Songs
    public static Set<Song> gameSet = new HashSet<>();          //HashSet for Songs
    public static List<Song> Songs4Game;                        //List with 4 random songs

    //Constructor on first Start (no import)
    public SongManager() {

    }

    //Constructor with importSet and importSongList
    public SongManager(Set<Song> importSet, List<Song> importSongList) {
        songList = importSongList;
        gameSet = importSet;
    }

    //do the work of the SongManager (combines the next three methods)
    public static void getSongFiles() {
        String directory = Main.getDir();
        try {
            SongManager.walk(directory);
            SongManager.createSongList();
            SongManager.createGameSet();
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    //walk through folders seeking all mp3 files
    public static void walk(String dir) {
        File root = new File(dir);          //file or folder from dir
        File[] list = root.listFiles();     //get all files/folders in startpath
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

    //create song objects and add them to List
    public static void createSongList() throws IOException, UnsupportedTagException, InvalidDataException {
        //TODO: Thread and progress bar
        //let user know to wait a while
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Blindtest - MusikRaten");
        dialog.setContentText("It can take a while to search&seek all your mp3 Files. Please, be patient");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        dialog.showAndWait();

        int count = songFiles.size();   //Anzahl der mp3Files im Verzeichnis
        int k = 1;                      //update int i with int k in following loop
        if (count > 2000) {                //falls mehr als 2000 mp3's nur jedes Zweite nehmen - das sollte reichen 
            k = 2;
        } else if (count > 4000) {                //falls mehr als 4000 mp3's nur jedes Dritte nehmen - das sollte reichen 
            k = 3;
        } else if (count > 8000) {                //falls mehr als 8000 mp3's nur jedes Vierte nehmen - das sollte reichen 
            k = 4;
        } else if (count > 12000) {                //falls mehr als 12000 mp3's nur jedes Sechste nehmen - das sollte reichen 
            k = 6;
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
                byte[] cover = null;
                String mime = null;

                if (myMP3.hasId3v2Tag()) {                   //itunes hat keine id3v2 tags mehr in original form gelassen sondern zu unicode formattiert
                    artist = myMP3.getId3v2Tag().getArtist();       //werden jedoch hier ausgelesen
                    album = myMP3.getId3v2Tag().getAlbum();
                    title = myMP3.getId3v2Tag().getTitle();
                    genre = myMP3.getId3v2Tag().getGenreDescription();
                    path = songFiles.get(i);
                    cover = myMP3.getId3v2Tag().getAlbumImage();
                    mime = myMP3.getId3v2Tag().getAlbumImageMimeType();
                } else if (myMP3.hasId3v1Tag()) {
                    artist = myMP3.getId3v1Tag().getArtist();
                    album = myMP3.getId3v1Tag().getAlbum();
                    title = myMP3.getId3v1Tag().getTitle();
                    genre = myMP3.getId3v1Tag().getGenreDescription();
                    path = songFiles.get(i);
                    cover = null;
                    mime = null;
                } else {
                    continue;
                }
                if (artist != null && genre != null && path != null) {
                    Song mySong = new Song(title, artist, genre, album, path, cover, mime);
                    songList.add(mySong);
                }
            }
        } catch (IOException | UnsupportedTagException | InvalidDataException ex) {
            //Logger.getLogger(SongManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //genreChooser
    public static Predicate<Song> getChoosedGenrePredicates() {
        int genreChooser = Main.getGenreChooser();
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
        CharSequence genreCharRap = "Rap";
        Predicate<Song> genreRap = p -> p.getGenre().contains(genreCharRap);
        CharSequence genreCharPunk = "Punk";
        Predicate<Song> genrePunk = p -> p.getGenre().contains(genreCharPunk);
        CharSequence genreCharCrossover = "Crossover";
        Predicate<Song> genreCrossover = p -> p.getGenre().contains(genreCharCrossover);
        CharSequence genreCharBeat = "Beat";
        Predicate<Song> genreBeat = p -> p.getGenre().contains(genreCharBeat);
        CharSequence genreCharElectro = "Electro";
        Predicate<Song> genreElectro = p -> p.getGenre().contains(genreCharElectro);
        CharSequence genreCharIndie = "Indie";
        Predicate<Song> genreIndie = p -> p.getGenre().contains(genreCharIndie);
        CharSequence genreCharHard = "Hard";
        Predicate<Song> genreHard = p -> p.getGenre().contains(genreCharHard);
        CharSequence genreCharHip = "Hip";
        Predicate<Song> genreHip = p -> p.getGenre().contains(genreCharHip);
        CharSequence genreCharHouse = "House";
        Predicate<Song> genreHouse = p -> p.getGenre().contains(genreCharHouse);
        CharSequence genreCharAll = "";
        Predicate<Song> genreAll = p -> p.getGenre().contains(genreCharAll);

        //concatonate predicates to one by choosed genre filter
        Predicate<Song> choosePredicate;
        switch (genreChooser) {
            case 2: //2:=the harder stuff; 
                choosePredicate = genreMetal.or(genreHard).or(genreGrunge).or(genreCrossover).or(genrePunk);
                break;
            case 3: //3:=the softer stuff; 
                choosePredicate = genreRock.or(genrePop)
                        .or(genreOldies).or(genreAlternative)
                        .or(genreBlues).or(genreIndie);
                break;
            case 4: //4:=
                choosePredicate = genreRap.or(genreCrossover)
                        .or(genreBeat).or(genreElectro).or(genreHip).or(genreHouse);
                break;
            case 5: //5:=every last mp3 file (dangermode - can contain embarrassing mp3's); 
                choosePredicate = genreAll;
                break;
            default:
                //case 1:= all hardcoded (recommended setting) genres; 
                choosePredicate = genreRock.or(genrePop)
                        .or(genreMetal).or(genreGrunge)
                        .or(genreOldies).or(genreAlternative)
                        .or(genreBlues).or(genreRap).or(genrePunk).or(genreCrossover)
                        .or(genreBeat).or(genreElectro).or(genreIndie).or(genreHard);
                break;
        }
        return choosePredicate;

    }

    //creates Collection HashSet with Songs, Set for only one song of each artist
    //Genre Predicate as choosen by user
    public static void createGameSet() {
        Predicate<Song> GenrePredicates = getChoosedGenrePredicates();
        try {
            //shuffle songList - otherwise everytime the same first title of each artist is making it into the gameSet!
            Collections.shuffle(songList);
            //gameSet erstellen anhand der predicates
            gameSet = songList.stream()
                    .filter(GenrePredicates)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Blindtest - MusikRaten");
            dialog.setContentText("An error occured while creating the gameSet. Cause:" + e.getMessage());
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            dialog.showAndWait();
        }
        //test if gameSet is big enougth - if it contains less than 4 diffrent artist, we can not play at all - there should be at least about 160 artists... the more the better
        if (gameSet.size() < 4) {
            //make it impossible to play
            Main.setPlayable(false);
            //show info
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Blindtest - MusikRaten");
            dialog.setContentText("There is not even 4 diffrent artists in the selected folder."
                    + "We can not play with this selection. Sorry!");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            dialog.showAndWait();
        } else if (gameSet.size() < 80) {
            //we can play again
            Main.setPlayable(true);
            //show dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Blindtest - MusikRaten");
            dialog.setContentText("There are only a few diffrent artists in the selected folder."
                    + "Get yourself some more music files. Otherwise playing will be quite foolish.");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            dialog.showAndWait();
        } else {
            //we can play again
            Main.setPlayable(true);
        }
    }

    //called each time a new Song has to be guessed
    public static List<Song> get4Songs() {
        //create Songs4Game new with full gameSet
        Songs4Game = new ArrayList<>(gameSet);
        //shuffle List
        Collections.shuffle(Songs4Game);
        // limit Songs4Game to 4 songs
        Songs4Game = Songs4Game.stream()
                .limit(4)
                .collect(Collectors.toList());
        //return list
        return Songs4Game;
    }

}
