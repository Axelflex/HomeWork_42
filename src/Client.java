import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;

    public Client(Socket socket, String name){
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.name = name;
        }catch (IOException e){
            closeDownAll(socket, bufferedReader, bufferedWriter);
        }

    }

    public void sendMsg(){
        try {
            bufferedWriter.write(name);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

            while (socket.isConnected()){
                String message = scanner.nextLine();
                System.out.println(Handler.clientHandlers);
                if(message.equals("/name")){
                    System.out.println("Enter a new name");
                    String newName = scanner.nextLine().strip();
                    System.out.println("now you are known as " + newName);
                    bufferedWriter.write(this.name + " now known as " + newName);
                    this.name = newName;
                }else {
                    bufferedWriter.write(name + ": " + message);
                }
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (IOException e){
            closeDownAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenMsg(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromChat;
                while (socket.isConnected()){
                    try{
                        msgFromChat = bufferedReader.readLine();
                        System.out.println(msgFromChat);
                    }catch (IOException e){
                        closeDownAll(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeDownAll(Socket socket , BufferedReader bufferedReader, BufferedWriter bufferedWriter){
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

    public static void main(String[] args) throws IOException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name: ");
        String name = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, name.strip());
        client.listenMsg();
        client.sendMsg();
    }

}
