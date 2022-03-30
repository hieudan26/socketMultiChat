package AppChat;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;


    public ClientHandler(Socket socket) {
        try{
            this.socket = socket;
            this.bufferedWriter =new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));

            this.bufferedReader =new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: "+clientUsername+" has joined th chat!");

        }catch(IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
            e.printStackTrace();

        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException ex) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                ex.printStackTrace();
                break;
            }
        }
    }
    public void broadcastMessage(String messageToSend) {
        for(ClientHandler clientHandler : clientHandlers){
            try{
                if(!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
            }
            }catch (IOException ex){
                closeEverything(socket,bufferedReader,bufferedWriter);
                ex.printStackTrace();
                break;
            }
        }
    }

    public void removeClienthandler() {
      clientHandlers.remove(this);
        broadcastMessage("SERVER: "+clientUsername+" has leaved th chat!");

    }
    public void closeEverything(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter) {
        try{
            removeClienthandler();
            if(bufferedReader !=null)
               bufferedReader.close();
            if(bufferedWriter !=null)
                bufferedWriter.close();
            if(socket !=null)
                socket.close();
        }catch(IOException e){
            e.printStackTrace();

        }
    }

}
