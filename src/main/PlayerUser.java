package main;




import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mjair
 */
public class PlayerUser implements Serializable  {
    private String name = "";   //Nickname in GUI
    private int games1pl = 0;   //count of games 1 Player
    private int games2pl = 0;   //count of games 2 Player
    private int score1pl = 0;   //Score in 1 Player Mode
    private int score2pl = 0;   //Score in 2 Player Mode

    //constructor
    public PlayerUser(String s){
        name = s;
    }
    
    //HighScore Ausgabe
    @Override
    public String toString(){
        return name+'\t'+" scored "+'\t'+Integer.toString(score1pl)+" points in "+Integer.toString(games1pl)+" 1-Player games... and "+'\t'+Integer.toString(score2pl)+" points in "+Integer.toString(games2pl)+" 2-Player games.";
    }
    
    //Getter
    public String getName() {
        return name;
    }
    //get count of Games
    public int getGames1pl() {
        return games1pl;
    }
    
    public int getGames2pl() {
        return games2pl;
    }
    //get Scores
    public int getScore1pl() {
        return score1pl;
    }
    
    public int getScore2pl() {
        return score2pl;
    }

    //Setter
    public void setName(String name) {
        this.name = name;
    }
    
    //Setter -> Adder (add up 1)
    //add count Games - starting a game counts up 1
    public void add1plGame() {
        games1pl++;
    }
    public void add2plGame() {
        games2pl++;
    }
    
    //add Score - each correct guess equals one point
    public void addScore1pl() {
        score1pl++;
    }
    public void addScore2pl() {
        score2pl++;
    }
    
    
    
}


