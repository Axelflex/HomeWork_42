import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Handler implements Runnable{

    public static ArrayList<Handler> clientHandkers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;

    public Handler (Socket socket){
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader((socket.getInputStream())));
            this.name = bufferedReader.readLine();
            clientHandkers.add(this);
            serverMsg(name + " has entered chat");
        }catch (IOException e){
            closeDownAll(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String clientMsg;
        while (socket.isConnected()){
            try{
                clientMsg = bufferedReader.readLine();
                serverMsg(clientMsg);
            }catch (IOException e){
                closeDownAll(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void  serverMsg(String message){
        for (Handler handler : clientHandkers){
            try{
                if(!handler.name.equals(name)){
                    handler.bufferedWriter.write(message);
                    handler.bufferedWriter.newLine();
                    handler.bufferedWriter.flush();
                }
            }catch (IOException e){
                closeDownAll(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClient(){
        clientHandkers.remove(this);
        serverMsg(name + " has left chat");
    }

    public void closeDownAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClient();
        try{
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
