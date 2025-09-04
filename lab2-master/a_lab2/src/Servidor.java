import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Servidor {

    private ServerSocket server;
    private int porta = 1025;
    private final String ARQUIVO_FORTUNAS = "fortune-br.txt";

    public void iniciar() {
        List<String> fortunas = new ArrayList<>();
        // Carrega as fortunas do arquivo para a memória
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_FORTUNAS))) {
            StringBuilder fortunaAtual = new StringBuilder();
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().equals("%")) {
                    if (fortunaAtual.length() > 0) {
                        fortunas.add(fortunaAtual.toString().trim());
                        fortunaAtual.setLength(0);
                    }
                } else {
                    fortunaAtual.append(linha).append("\n");
                }
            }
            if (fortunaAtual.length() > 0) {
                fortunas.add(fortunaAtual.toString().trim());
            }
        } catch (IOException e) {
           
        }
        
        System.out.println("Servidor iniciado na porta: " + porta);

        try {
            server = new ServerSocket(porta);
            while (true) {
                Socket socket = server.accept(); // Processo fica bloqueado, à espera de conexões

                // cria os fluxos de entrada e saida
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                DataOutputStream saida = new DataOutputStream(socket.getOutputStream());

                // recebimento da mensagem JSON
                String jsonRecebido = entrada.readUTF();

                // processamento da mensagem
                String resposta = "{\"result\": \"false\"}\n"; // Resposta padrão de erro

                if (jsonRecebido.contains("\"method\": \"read\"")) {
                    if (!fortunas.isEmpty()) {
                        Random rand = new Random();
                        String fortunaAleatoria = fortunas.get(rand.nextInt(fortunas.size()));
                        fortunaAleatoria = fortunaAleatoria.replace("\"", "\\\"").replace("\n", "\\n");
                        resposta = "{\"result\": \"" + fortunaAleatoria + "\"}\n";
                    }
                } else if (jsonRecebido.contains("\"method\": \"write\"")) {
                    // extrai o argumento (a nova fortuna) da mensagem JSON
                    int inicio = jsonRecebido.indexOf("args\": [\"") + 9;
                    int fim = jsonRecebido.lastIndexOf("\"]");
                    if (inicio < fim) {
                        String novaFortuna = jsonRecebido.substring(inicio, fim);
                        novaFortuna = novaFortuna.replace("\\\"", "\"").replace("\\n", "\n");

                        // escreve a nova fortuna no arquivo
                        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_FORTUNAS, true))) {
                            bw.newLine();
                            bw.write(novaFortuna);
                            bw.newLine();
                            bw.write("%");
                            fortunas.add(novaFortuna); 
                            
                          
                            String fortunaFormatada = novaFortuna.replace("\"", "\\\"").replace("\n", "\\n");
                            resposta = "{\"result\": \"" + fortunaFormatada + "\"}\n";
                        } catch (IOException e) {

                        }
                    }
                }
                
                // envio de dados
                saida.writeUTF(resposta);

                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Servidor().iniciar();
    }
}