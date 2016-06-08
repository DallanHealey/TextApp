import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Hashtable;

public class TextServerThread implements Runnable
{
    static Socket socket;
    static BufferedReader in;
    static PrintStream out;
    private static boolean didWork;

    static String usersPhoneNumber;
    static Hashtable<Integer, String> recipientsPhoneNumber;

    TextServerThread (Socket socket) throws IOException
    {
        this.socket = socket;
        recipientsPhoneNumber = new Hashtable<Integer, String>();

        //Work around
        String[] args = {};
        GUI.main(args);
    }

    //Starts the threaded portion
    public void run ()
    {
        try
        {
            System.out.println("User connected");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());

            usersPhoneNumber = in.readLine();
            System.out.println("User's phone number is: " + usersPhoneNumber);
            GUI.frame.setTitle("Text App  - " + usersPhoneNumber);

            while (TextServer.isRunning)
            {
                String message = in.readLine();
                String[] data = message.split(": ");

                try
                {
                    Long.parseLong(data[0]);
                }
                catch (Exception e)
                {
                    continue;
                }

               didWork = GUI.onNewText(data[0]);

                message = data[1];
                System.out.println(data[0] + ": " + message);

                if (didWork)
                    GUI.insertString(GUI.messagesList.get(GUI.TAB_NUMBER), data[0] + ": " + message + "\n");
                else
                    GUI.insertString(GUI.messagesList.get(GUI.tabPane.getSelectedIndex()), data[0] + ": " + message + "\n");
            }
        }
        catch (Exception e)
        {
            System.out.println("An error occurred");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                stop();
            }
            catch (Exception e)
            {
                System.out.println("Everything didn't close right");
                e.printStackTrace();
            }
        }
    }

    static void sendMessage (String message)
    {
        if (recipientsPhoneNumber.get(GUI.tabPane.getSelectedIndex()) == null || recipientsPhoneNumber.isEmpty())
            return;

        if(GUI.didWork)
            out.println(recipientsPhoneNumber.get(GUI.TAB_NUMBER) + ": " + message);
        else
            out.println(recipientsPhoneNumber.get(GUI.tabPane.getSelectedIndex()) + ": " + message);
    }

    static void stop () throws IOException
    {
        in.close();
        out.close();
        socket.close();
        System.exit(0);
    }
}

