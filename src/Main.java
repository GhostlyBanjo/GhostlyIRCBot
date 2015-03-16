/**
 * Created by Forrest on 3/14/2015.
 */
public class Main {

       static GhostBot bot;

    public static void main(String[] args) throws Exception{
        bot = new GhostBot();

        bot.setVerbose(true);
        bot.connect("nova.esper.net");
        bot.joinChannel("#Audax");
    }



}
