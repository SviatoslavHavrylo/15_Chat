/**
 * Created by urba on 05.01.2018.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static final int PORT = 13001;
    private BufferedReader in;
    private PrintWriter out;
    private static String username;
    private Scanner scan = new Scanner(System.in);

    private String getServerAddress() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Введите IP для подключения к серверу.");
        System.out.println("Формат: xxx.xxx.xxx.xxx");
        String ip = scan.nextLine();
        return ip;
    }

    private String getName() {
        System.out.println("Введите свой ник:");
        username = scan.nextLine();
        System.out.println("Welcome, " + username + ".");
        return username;
    }

    public void run() throws IOException {
        // Make connection and initialize streams
        try {
            String serverAddress = getServerAddress();
            Socket socket = new Socket(serverAddress, PORT);
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Запускаем вывод всех входящих сообщений в консоль
            // Resender resend = new Resender();
            // resend.start();

            while (true) {
                String line = in.readLine();
                if (line.startsWith("SUBMITNAME")) {
                    out.println(getName());
                } else if (line.startsWith("NAMEACCEPTED")) {
                    System.out.println("Ваш ник принят");
                    break;
                }
            }

            Thread thread = new Thread(new ReadMessage(in)); //поток чтения входящих сообщений
            thread.start();

            String str = "";
            while (!str.equals("exit")) {
                str = scan.nextLine();
                out.println(str);
            }
            thread.interrupt();
            try {
                in.close();
                out.close();
            } catch (Exception e) {
                System.err.println("Потоки не были закрыты!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (Exception e) {
                System.err.println("Потоки не были закрыты!");
            }
        }
    }

    private class ReadMessage implements Runnable {
        private boolean stoped;
        BufferedReader in;

        public ReadMessage(BufferedReader in) {
            this.in = in;
        }

        public void setStop() {
            stoped = true;
        }

        public void run() {
            try {
                while (true) {
                    String line = in.readLine();
                    if (line.startsWith("MESSAGE")) {
                        //messageArea.append(line.substring(8) + "\n");
                        System.out.println(line.substring(8));
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка при получении сообщения.");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient();
        client.run();
    }
}
