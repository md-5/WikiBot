package net.md_5;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.jibble.pircbot.PircBot;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WikiBot extends PircBot
{

    private static final String channel = "#wiki";
    private final Set<String> known = new HashSet<String>();

    public WikiBot()
    {
        setName( "Wiki" );
        new Timer().scheduleAtFixedRate( new TimerTask()
        {

            @Override
            public void run()
            {
                try
                {
                    System.out.println( "Checking for updates" );
                    Document doc = Jsoup.connect( "http://www.spigotmc.org/wiki/special/recent" ).get();
                    Elements table = doc.select( ".primaryContent.wikiPage>table>tbody" ).first().getElementsByTag( "tr" );
                    boolean first = known.isEmpty();
                    for ( int i = table.size() - 1; i >= 0; i-- )
                    {
                        Elements columns = table.get( i ).getElementsByTag( "td" );
                        String name = columns.get( 1 ).text();
                        String link = columns.get( 1 ).getElementsByTag( "a" ).first().attr( "abs:href" );
                        String date = columns.get( 2 ).text();
                        String editor = columns.get( 3 ).text();

                        String hash = String.format( "Page %s edited by %s at %s [%s]", name, editor, date, link );

                        if ( !first && !known.contains( hash ) )
                        {
                            sendMessage( channel, hash );
                            System.out.println( hash );
                        }
                        known.add( hash );
                    }
                } catch ( Exception ex )
                {
                    ex.printStackTrace();
                    sendMessage( channel, "Exception: " + ex.getClass() + ":" + ex.getMessage() );
                }
            }
        }, 0, 60000 );
    }

    @Override
    protected void onConnect()
    {
        joinChannel( channel );
    }

    public static void main(String[] args) throws Exception
    {
        new WikiBot().connect( "irc.spi.gt" );
    }
}
