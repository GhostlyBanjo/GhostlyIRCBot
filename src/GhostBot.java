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

    HashMap<String,PriorityQueue<String>> tellList = new HashMap<String, PriorityQueue<String>>();
    ArrayList<String> userDB = new ArrayList<String>();
    String history = "";
    boolean sleep = false;



    public GhostBot(){
        this.setName("GhostBot");
    }

    public void onMessage(String channel, String sender,
                          String login, String hostname, String message) {

        String[] msg = message.split(" ");
        if (msg[0].equalsIgnoreCase("ghostbot")||msg[0].equalsIgnoreCase("ghostbot:")) {
if(!sleep){
            if(msg[1].equalsIgnoreCase("time")){
            String time = new java.util.Date().toString();
            sendMessage(channel, sender + ", The time is now " + time);}

            if(msg[1].equalsIgnoreCase("tell")){
                String tgt = msg[2];
                String fin = "";
                for(int x = 3; x< msg.length; x++){
                    fin = fin +" " +  msg[x];
                }
                if(!tellList.containsKey(tgt)){
                    PriorityQueue<String> q = new PriorityQueue<String>();
                    tellList.put(tgt, q);
                }
                tellList.get(tgt).add(sender + ": " + fin);
                sendMessage(channel, "Gotcha, fam");
            }
            if(msg[1].equalsIgnoreCase("link")){

            }
            if(msg[1].equalsIgnoreCase("sleep")&& sender.equalsIgnoreCase("Articalla")){
                sleep = true;
                sendMessage(channel, "Going to sleep!");
            }
            if(msg[1].equalsIgnoreCase("dump")){
                if(msg[2].equalsIgnoreCase("ToChat")){
                String[] output = history.split("\n");
                for(String o : output)
                sendMessage(channel, o);}
                if(msg[2].equalsIgnoreCase("toFile")){
                    try {
                        sendMessage(channel, "Writing history to file...");
                            String filename = new java.util.Date().toString().replace(':', '_');

                        FileWriter out = new FileWriter(filename + ".txt");

                        String[] output = history.split("\n");
                        for(String o : output){
                            out.write(o);
                            out.write((char)10);}
                        out.close();
                        history = "";

                        sendMessage(channel, "Done!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(msg[1].equalsIgnoreCase("about")){
                sendMessage(channel, "I'm GhostBot.  I was made on 2015 May 14 by some guy named Forrest.  I only understand a few commands right now.");
            }
            if(msg[1].equalsIgnoreCase("help")){
                if(msg.length>2){
                    if(msg[2].equalsIgnoreCase("tell")){
                        sendMessage(sender, "Usage: Ghostbot tell <target name> [message]");
                        sendMessage(sender, "This leaves a message for the target person, and I'll relay it to them when they return.");
                    }
                    if(msg[2].equalsIgnoreCase("dump")){
                        sendMessage(sender, "Usage: Ghostbot dump [toChat/toFile]");
                        sendMessage(sender, "Dumps the chat history since the last dump, either to the chat, or to a file on the server. ");
                    }
                    if(msg[2].equalsIgnoreCase("time")){
                        sendMessage(sender, "Relays the current time and date of the server.");
                    }
                }else{
                    sendMessage(channel,"I'm GhostBot.");
                    sendMessage(channel,"If you need me to do something, say, \"GhostBot <command>\".");
                    sendMessage(channel,"Commands: HELP, TELL, TIME, DUMP, ABOUT");
                    sendMessage(channel,"For more information, say \"GhostBot help <command name>");
                }
            }
            if(msg[1].equalsIgnoreCase("kill")){
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
    public void onJoin(String channel, String sender, String login, String hostname){
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
                }
                sendMessage(sender, "Usage: Ghostbot time");
            }
        }
    }
}
