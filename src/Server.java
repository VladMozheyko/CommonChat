import java.net.ServerSocket;
import java.util.ArrayList;

public class Server implements TCPConnectionListener {             // Реализация контракта
    //TODO Сделать запись в файл истории сообщений. С подписью автора сообщения и самого сообщения, т.е. при чтении с сервера
    //TODO должна автоматически происходить классификация автора сообщения и самого сообщения. При каждом запуске сервера читать историю и выводить на консоль
    //TODO * Сделать игру клиент-серверную - "Камень, ножницы, бумага".
    //TODO * Разобрать мою реализацию клиент-серверного Мороского боя:
    private ArrayList<TCPConnection> connections = new ArrayList<>(); // Соединения между клиентом и сервером
    TCPConnection connection;                                        //  Само соединение
    public static void main(String[] args) {
        new Server();                                                //  Запуск сервера
    }

    public Server() {                                               //  Конструктор
        try(ServerSocket serverSocket = new ServerSocket(8085)) {   //  Обрабатываем ошибку при создании сокета
            System.out.println("Server running...");
            while (true){                                           //  В бесконечном цикле слушаем соедиениения
                connection = new TCPConnection(this, serverSocket.accept());  //  Передаем контракт и сокет в соединение
                System.out.println("Клиент подключился" );
            }
        }
        catch (Exception ex){                                     // Обработка исключения
            //  System.out.println("Error");

        }
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {  // При подключении к серверу
        connections.add(tcpConnection);                          // Добавляем соединение в массив
        sendToAllConnections("Привет от сервера");                // Отправляем сообщение

    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        tcpConnection.disconnect();                                // Вызываем метод для разъединения
    }

    @Override
    public void onMessageReceived(TCPConnection tcpConnection, String str) {    // При получении сообщения на сервере
        tcpConnection.setSenderTrue();                                   // Для соединения, которое отправило сообщение ставим метку, чтобы оно его не получило
        for (int i = 0; i < connections.size(); i++) {              // Назначаем получателями все остальные соединения
            connections.get(i).setReceiverTrue();
        }

        sendToAllConnections(str);                        // Отправляем сообщение всем соединениям
        tcpConnection.setSenderFalse();                   // Обнуляем состояние отправителя, чтобы в следующий раз соединение получило ответ от сервера
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception ex) {  // Обработка исключения
        tcpConnection.disconnect();
        connections.remove(tcpConnection);
    }

    public void sendToAllConnections(String msg){   // Отправка сообщения всем соединениям
        for (int i = 0; i < connections.size(); i++) {  // Перебираем массив подключений и отправляем всем сооющение
            if(connections.get(i).isReceiver() && !connections.get(i).isSender()){
                connections.get(i).sendString(msg);
            }
        }
    }
}
