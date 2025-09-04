import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    
    private int porta = 1025;
    
    public void iniciar() {
        System.out.println("Cliente iniciado.");
        
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\nEscolha uma opção:");
                System.out.println("1. Ler uma fortuna (read)");
                System.out.println("2. Escrever uma fortuna (write)");
                System.out.println("3. Sair");
                System.out.print("> ");
                
                String escolha = scanner.nextLine();
                
                switch (escolha) {
                    case "1": // leitura
                        try (Socket socket = new Socket("127.0.0.1", porta)) {
                            DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
                            DataInputStream entrada = new DataInputStream(socket.getInputStream());
                            
                            // a mensagem JSON é enviada ao servidor
                            saida.writeUTF("{\"method\": \"read\", \"args\": [\"\"]}\n");

                            // resultado do servidor
                            String jsonResposta = entrada.readUTF();

                            // mostra o resultado na tela
                            int inicio = jsonResposta.indexOf("\"result\": \"") + 11;
                            int fim = jsonResposta.lastIndexOf("\"}");
                            String resultado = jsonResposta.substring(inicio, fim);
                            resultado = resultado.replace("\\n", "\n").replace("\\\"", "\"");
                            
                            System.out.println("---------------------------------");
                            System.out.println(resultado);
                            System.out.println("---------------------------------");

                        } catch (Exception e) {
                            System.err.println("Erro ao comunicar com o servidor: " + e.getMessage());
                        }
                        break;

                    case "2": // escrita
                        System.out.print("Digite a nova fortuna: ");
                        String novaFortuna = scanner.nextLine();

                        try (Socket socket = new Socket("127.0.0.1", porta)) {
                            DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
                            DataInputStream entrada = new DataInputStream(socket.getInputStream());
                            
                            
                            String fortunaFormatada = novaFortuna.replace("\"", "\\\"").replace("\n", "\\n");

                            
                            saida.writeUTF("{\"method\": \"write\", \"args\": [\"" + fortunaFormatada + "\"]}\n");

                            
                            entrada.readUTF(); 
                            System.out.println("Fortuna enviada com sucesso!");

                        } catch (Exception e) {
                            System.err.println("Erro ao comunicar com o servidor: " + e.getMessage());
                        }
                        break;

                    case "3": //sair
                        System.out.println("Encerrando o cliente.");
                        return; 

                    default:
                        System.out.println("Opção invalida.");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new Cliente().iniciar();
    }
}