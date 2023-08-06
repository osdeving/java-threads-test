package demo;

import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Delay;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

public class MockServerApp {

    private static ClientAndServer mockServer;

    public static void main(String[] args) {
        startMockServer();

        // Mantenha o servidor mock em execução por algum tempo (opcional)
        try {
            Thread.sleep(15 * 60000); // Aguarde 1 minuto para manter o servidor mock em execução
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stopMockServer();
    }

    public static void startMockServer() {
        mockServer = ClientAndServer.startClientAndServer(1080);

        // Configurar o comportamento da API 1
        mockServer.when(HttpRequest.request()
                .withMethod("GET")
                .withPath("/api1"))
                .respond(HttpResponse.response()
                        .withBody("Resposta da API 1")
                        .withDelay(Delay.milliseconds(randomDelay(100, 1000))));

        // Configurar o comportamento da API 2
        mockServer.when(HttpRequest.request()
                .withMethod("GET")
                .withPath("/api2"))
                .respond(HttpResponse.response()
                        .withBody("Resposta da API 2")
                        .withDelay(Delay.milliseconds(randomDelay(2000, 7000))));
    }

    public static void stopMockServer() {
        mockServer.stop();
    }

    private static int randomDelay(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }
}
