import java.io.IOException;
import java.util.Scanner;

public class Client implements TCPConnectionListener{
    TCPConnection tcpConnection;  // Контракт для связи клиент и сервера

    public static void main(String[] args) throws IOException {
        new Client();      // Анонимный объект
    }

    public Client() throws IOException {
        Scanner scanner = new Scanner(System.in);    //Поток ввода из консоли
        tcpConnection = new TCPConnection(this, "127.0.0.1", 8085); // Создаем соединение для клиента
        String msg = "";
        while (true){
            msg = scanner.nextLine(); // Читаем сообщение из консоли
            tcpConnection.sendString(msg);  // Отправка сообщений

        }
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {

    }

    @Override
    public void onDisconnect(TCPConnection TCPConnection) {
        tcpConnection.disconnect();     // Разрыв соединения
    }

    @Override
    public void onMessageReceived(TCPConnection tcpConnection, String str) {
        System.out.println("Сообщение:" + str); // Вывод полученных сообщений
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception ex) {

    }
}
