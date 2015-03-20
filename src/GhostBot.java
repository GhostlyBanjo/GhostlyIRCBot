import org.jibble.pircbot.PircBot;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Created by Forrest on 3/14/2015.
 */
public class GhostBot extends PircBot{


    //This is where all of the messages are saved.
    HashMap<String,PriorityQueue<String>> tellList = new HashMap<String, PriorityQueue<String>>();
    //Database of all users ever
    ArrayList<String> userDB = new ArrayList<String>();
    //List of the current channel admins
    ArrayList<String> adminList = new ArrayList<String>();
    //List of the currently online folk
    ArrayList<String> onlineList = new ArrayList<String>();
    //Current Channel
    public String channelo;
    //Current chat history
    String history = "";

    //If sleep is true, it won't listen to any commands other than the wake command
    boolean sleep = false;



    public GhostBot(String name){
        this.setName(name);
        adminList.add("Articalla");

    }

    public void onMessage(String channel, String sender,
                          String login, String hostname, String message) {
        boolean isOp = false;
        //Checks if the sender of the message is an admin on the channel
        if(adminList.contains(sender))
            isOp = true;

            //breaks the message up into individual words
        String[] msg = message.split(" ");

            //checks if the message is referring to the Bot
        if (msg[0].equalsIgnoreCase(this.getName())||msg[0].equalsIgnoreCase(this.getName()+":")) {
if(!sleep){

            //Most of the if() statements are checking the second word and implementing a command.


            //This command returns the current time relative tot he server
            if(msg[1].equalsIgnoreCase("time")){
                this.readTime(channel, sender);
            }

            //adds a user to the admin list
            if (msg[1].equalsIgnoreCase("addOp")&& isOp){
                this.addOp(channel, sender, msg[2]);
            }
            //removes a user from the adminList
            if (msg[1].equalsIgnoreCase("takeOp")&&isOp){
                this.takeOp(channel, sender, msg[2]);
            }
            //Sends a specified user a message to be delievered ot them when they join the chat
            if(msg[1].equalsIgnoreCase("tell")){
                String tgt = msg[2];
                String fin = "";
                for(int x = 3; x< msg.length; x++){
                    fin = fin +" " +  msg[x];
                }
                this.tell(channel, sender, tgt, fin);
            }

            if(msg[1].equalsIgnoreCase("sleep") && isOp){
                sleep = true;
                sendMessage(channel, "Going to sleep!");
            }

               //dumps the chat history to either a file or a private message

            if(msg[1].equalsIgnoreCase("dump")&&isOp){

                if(msg[2].equalsIgnoreCase("ToChat")){
                String[] output = history.split("\n");
                for(String o : output)
                sendMessage(sender, o);}
                if(msg[2].equalsIgnoreCase("toFile")){
                    try {
                        sendMessage(channel, "Writing history to file...");
                            writeHistoryToFile();
                        sendMessage(channel, "Done!");
                    } catch (IOException e) {

                    }
                }}

                //Gives information about the bot

            if(msg[1].equalsIgnoreCase("about")){
                sendMessage(channel, "I'm GhostBot.  I was made on 2015 May 14 by some guy named Forrest.  I only understand a few commands right now.");
            }

                //Returns the list of commands and how to use them
            if(msg[1].equalsIgnoreCase("help")){
                if(msg.length>2){
                    if(msg[2].equalsIgnoreCase("tell")){
                        sendMessage(sender, "Usage: Ghostbot tell <target name> [message]");
                        sendMessage(sender, "This leaves a message for the target person, and I'll relay it to them when they return.");
                    }

                    if(msg[2].equalsIgnoreCase("time")){
                        sendMessage(sender, "Relays the current time and date of the server.");
                    }
                }else{
                    sendMessage(channel,"I'm GhostBot.");
                    sendMessage(channel,"If you need me to do something, say, \"GhostBot <command>\".");
                    sendMessage(channel,"Commands: HELP, TELL, TIME, ABOUT");
                    sendMessage(channel,"For more information, say \"GhostBot help <command name>");
                }
            }

                //Only I may send this message.  It stops the process on the machine, effectively killing the bot.
            if(msg[1].equalsIgnoreCase("kill")&& sender.equalsIgnoreCase("Articalla")){
                sendMessage(channel,"Goodbye, friends.");
                sendAction(channel, "commits seppuku");
                System.exit(0);
            }
        }
            else{
                if((msg[1].equalsIgnoreCase("rise")||msg[1].equalsIgnoreCase("wake")||msg[1].equalsIgnoreCase("arise"))&& sender.equalsIgnoreCase("Articalla"))
                {
                    sleep = false;
                    sendMessage(channel,"Waking up!");
                }
                }
        }

        history = history + "\n" + new java.util.Date().toString() + "|" + sender + ":" + message;
    }
    //Called whenever a user enters the channel
    public void onJoin(String channel, String sender, String login, String hostname){
        onlineList.add(sender);

        if(!sender.equalsIgnoreCase("GhostBot")) {
            if (!userDB.contains(sender)) {
                sendMessage(channel, "Welcome, " + sender + ", to the channel for the first time!");
                userDB.add(sender);
            } else {
                sendMessage(channel, "Welcome back, " + sender + "!");
            }

            if (tellList.containsKey(sender)) {
                sendMessage(sender, sender + ", you have " + tellList.get(sender).size() + " new messages");
                for (String s : tellList.get(sender)) {
                    sendMessage(sender, s);
                    tellList.get(sender).clear();
                }

            }
        }
    }
    //Called whenever a user leaves the channel
    public void onQuit(String sender, String login, String host, String reason){
                onlineList.remove(sender);
                sendMessage(this.getChannels()[0], "Goodbye, "+sender+"!");
    }
    //Writes the chat history to a file, then clears the history
    public void writeHistoryToFile() throws IOException {
        String filename = new java.util.Date().toString().replace(':', '_');

        FileWriter out = new FileWriter(filename + ".txt");

        String[] output = history.split("\n");
        for(String o : output){
            out.write(o);
            out.write((char)10);}
        out.close();
        history = "";
    }
    //Returns the current server time
    public void readTime(String channel, String sender){
        String time = new java.util.Date().toString();
        sendMessage(channel, sender + ", The time is now " + time);
    }
    //Adds target to the admin list
    public void addOp(String channel, String sender, String target){
        adminList.add(target);
        sendMessage(channel,"Added " + target + " to the admin list!");
        sendMessage(target, sender + " added you to the admin list for " + channel);
    }
    //Removes target from the admin list
    public void takeOp(String channel, String sender, String target) {

        adminList.remove(target);
        sendMessage(channel,"Removed " + target + " to the admin list!");
        sendMessage(target, sender + " removed you from the admin list for " + channel);
    }
    //Sends a message to the target to be dlievered to them the very next time the join the channel
    public void tell(String channel, String sender, String target, String message){

        if(!tellList.containsKey(target)){
            PriorityQueue<String> q = new PriorityQueue<String>();
            tellList.put(target, q);
        }
        tellList.get(target).add(sender + ": " + message);
        sendMessage(channel, "Gotcha, fam");
    }
}
