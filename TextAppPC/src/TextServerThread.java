import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class TextServerThread implements Runnable
{
    private Socket socket;
    private BufferedReader in;
    protected static PrintStream out;

    protected static String usersPhoneNumber;
    protected static String recipientsPhoneNumber;

    TextServerThread(Socket socket)
    {
        this.socket = socket;
    }

    //Starts the threaded portion
    public void run()
    {
        try
        {
            System.out.println("User connected");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());

            usersPhoneNumber = in.readLine();
            System.out.println("User's phone number is: " + usersPhoneNumber);
            GUI.frame.setTitle("Text App  - " + usersPhoneNumber);

            //out.println("Hello Aaron!!!");

            while(TextServer.isRunning)
            {
                String message = in.readLine();
                String[] data = message.split(": ");
                recipientsPhoneNumber = data[0];
                System.out.println("Data 0: " + data[0]);
                message = data[1];
                System.out.println(recipientsPhoneNumber + ": " + message);
                GUI.messages.getDocument().insertString(GUI.messages.getDocument().getLength(), recipientsPhoneNumber + ": " + message + "\n", null);
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
                in.close();
                out.close();
                socket.close();
            }
            catch (Exception e)
            {
                System.out.println("Everything didn't close right");
                e.printStackTrace();
            }
        }
    }
}

