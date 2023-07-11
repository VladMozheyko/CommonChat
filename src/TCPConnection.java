import java.io.*;
import java.net.Socket;

public class TCPConnection {                            // Класс-обертка для работы с сетью - решает вопросы сетевого взаимодействия
    private Socket socket;                              // Сокет
    private Thread thread;                              // Конкретный поток исполнения. Под каждого клиента будет создаваться свой поток
    private BufferedReader reader;                      // Поток(Stream) чтения
    private BufferedWriter writer;                      // Поток(Stream) записи
    private TCPConnectionListener listener;             // Экземпляр контракта - передаем функционал, через объект (композиция) - Паттерн Стратегия
    private boolean isSender;                           // Переменная, которая указывает на то, что соединение является отправителем сообщения
    private boolean isReceiver;                         // Переменная, которая указывает на то, что соединение является получателем сообщение

    /**
     * Метод для создания клиеннта
     * @param listener  своя реализация слушателя(контракта)
     * @param ip  адрес сервера
     * @param port порт
     * @throws IOException исключение при соединении
     */
    public TCPConnection(TCPConnectionListener listener, String ip, int port) throws IOException {
        this(listener, new Socket(ip, port));
    }

    /**
     * Метод для создания сервера
     * @param listener реализация контракта
     * @param socket сокет, для соединения клиента и сервера
     * @throws IOException исключение при соединении
     */
    public TCPConnection(TCPConnectionListener listener, Socket socket) throws IOException {
        this.socket = socket;                                                           // Блок инициализации
        this.listener = listener;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        /*
        Когда подлючается новый клиент к серверу для их взаимодействия создается отдельный поток и так для каждого
        клиента - сервер в одно время работает со всеми клиентами
         */
        thread = new Thread(new Runnable() {    // Создаем отдельный поток
            @Override
            public void run() {                 // Метод, который выполняет поток
                String msg = "";                // Создаем строку
                listener.onConnectionReady(TCPConnection.this);  // Создаем соединение
                while (!thread.isInterrupted()){        // До тех пор, пока мы не разорвали соединение, выполняем задачу
                    try {
                        msg = reader.readLine();       // Слушаем сообщения от клиентов
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    listener.onMessageReceived(TCPConnection.this, msg); // Отправляем сообщения серверу
                }
            }
        });
        thread.start();                         // Запускаем поток
    }

    public synchronized void sendString(String str) {
        try {
            writer.write(str + "\r\n");
            writer.flush();
        } catch (IOException e) {
            listener.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            listener.onException(TCPConnection.this, e);
        }
    }

    public synchronized void setReceiverTrue() {
        isReceiver = true;
    }

    public synchronized void setReceiverFalse() {
        isReceiver = false;
    }

    public synchronized boolean isReceiver() {
        return isReceiver;
    }


    public synchronized void setSenderTrue() {
        isSender = true;
    }

    public synchronized void setSenderFalse() {
        isSender = false;
    }

    public synchronized boolean isSender() {
        return isSender;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public String toString() {    // Выводим информацию о соединении
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
