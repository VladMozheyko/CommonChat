public interface TCPConnectionListener {   // Создаем контракт для работы в сети
    /*
    Контракт:
    Должны быть методы для обработки сообщений, соединения клиента и сервера и их разъединения,
    обработки исключений
     */
     void onConnectionReady(TCPConnection tcpConnection);

     void onDisconnect(TCPConnection TCPConnection);

     void onMessageReceived(TCPConnection tcpConnect, String str);

     void onException(TCPConnection tcpConnection, Exception ex);
}
