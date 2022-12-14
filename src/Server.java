import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket serverSocket;

    public Server (ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try{
           while (!serverSocket.isClosed()){
               Socket socket = serverSocket.accept();
               System.out.println("New client has connected");
               Handler handler = new Handler(socket);
               Thread thread = new Thread(handler);
               thread.start();
           }
        }catch (IOException e){

        }
    }

    public void closeServer(){
        try{
            if (serverSocket != null)
                serverSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket( 1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
