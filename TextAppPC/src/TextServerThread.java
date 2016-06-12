import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Hashtable;

class TextServerThread implements Runnable
{
    private static Socket socket;
    private static BufferedReader in;
    private static PrintStream out;

    static String usersPhoneNumber;
    static Hashtable<Integer, String> recipientsPhoneNumber;
    static Hashtable<String, Integer> recipientsPhoneNumberBackwards;

    TextServerThread (Socket socket) throws IOException
    {
        TextServerThread.socket = socket;
        recipientsPhoneNumber = new Hashtable<>();
        recipientsPhoneNumberBackwards = new Hashtable<>();
    }

    //Starts the threaded portion
    public void run ()
    {
        try
        {
            //Sets up the input and output streams
            System.out.println("User connected");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());

            //First data sent is the user's phone number which is recorded here. Also sets the title to contain the phone number
            usersPhoneNumber = in.readLine();
            System.out.println("User's phone number is: " + usersPhoneNumber);
            GUI.frame.setTitle("Text App  - " + usersPhoneNumber);

            //Loop to receive the messages
            while (TextServer.isRunning)
            {
                //Message contains "phone number": "message", so the split is needed to figure out what is what
                String message = in.readLine();
                String[] data = message.split(": ");

                //Checks for signatures and throws it out if there is one. Works by checking if there is a phone number before the message.
                try
                {
                    Long.parseLong(data[0]);
                }
                catch (Exception e)
                {
                    continue;
                }

                //Checks if the message is from a new user and then creates a tab if need be
                GUI.onNewText(data[0]);

                message = data[1];
                System.out.println(data[0] + ": " + message);

                //Inserts the message into the correct area
                if (GUI.didWork)
                    GUI.insertString(GUI.messagesList.get(GUI.TAB_NUMBER), data[0] + ": " + message);
                else
                    GUI.insertString(GUI.messagesList.get(GUI.tabPane.getSelectedIndex()), data[0] + ": " + message);

                //Must be here so that the message sends to the correct tab and user
                GUI.didWork = false;
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

    /**
     * Sends message back to Android phone to be sent to user
     * @param message The text to be sent to user (DOES NOT CONTAIN A PHONE NUMBER)
     */
    static void sendMessage (String message)
    {
        if (recipientsPhoneNumber.get(GUI.tabPane.getSelectedIndex()) == null || recipientsPhoneNumber.isEmpty())
            return;

        if (GUI.didWork)
            out.println(recipientsPhoneNumber.get(GUI.TAB_NUMBER) + ": " + message);
        else
            out.println(recipientsPhoneNumber.get(GUI.tabPane.getSelectedIndex()) + ": " + message);
    }

    /**
     * Closes all open inputs and outputs
     */
    static void stop () throws IOException
    {
        in.close();
        out.close();
        socket.close();
        TextServer.isRunning = false;
    }
}

