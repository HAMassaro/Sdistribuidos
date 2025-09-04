/**
 * Lab0: Leitura de Base de Dados Não-Distribuida
 * * Autor: Lucio A. Rocha
 * Ultima atualizacao: 20/02/2023
 * * Referencias: 
 * https://docs.oracle.com/javase/tutorial/essential/io
 * */

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Principal_v0 {

	public final static Path path = Paths			
			.get("src\\fortune-br.txt");
	private int NUM_FORTUNES = 0;

	public class FileReader {

		public int countFortunes() throws FileNotFoundException {

			int lineCount = 0;

			InputStream is = new BufferedInputStream(new FileInputStream(
					path.toString()));
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
					is))) {

				String line = "";
				while (!(line == null)) {

					if (line.equals("%"))
						lineCount++;

					line = br.readLine();

				}// fim while

				// System.out.println(lineCount); // Comentado para limpar a saída
			} catch (IOException e) {
				System.out.println("SHOW: Excecao na leitura do arquivo.");
			}
			return lineCount;
		}

		public void parser(HashMap<Integer, String> hm)
				throws FileNotFoundException {

			InputStream is = new BufferedInputStream(new FileInputStream(
					path.toString()));
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
					is))) {

				int lineCount = 0;

				String line = "";
				while (!(line == null)) {

					if (line.equals("%"))
						lineCount++;

					line = br.readLine();
					StringBuffer fortune = new StringBuffer();
					while (!(line == null) && !line.equals("%")) {
						fortune.append(line + "\n");
						line = br.readLine();
						// System.out.print(lineCount + ".");
					}

					hm.put(lineCount, fortune.toString());
					// System.out.println(fortune.toString()); // comentado para limpar a saida
					// System.out.println(lineCount); // comentado para limpar a saida

				}// fim while

			} catch (IOException e) {
				System.out.println("SHOW: Excecao na leitura do arquivo.");
			}
		}

		public void read(HashMap<Integer, String> hm)
				throws FileNotFoundException {

			//SEU CODIGO AQUI
			if (NUM_FORTUNES == 0) {
				System.out.println("Nenhuma fortuna para ler no arquivo.");
				return;
			}
			
			System.out.println("\n--- Lendo uma Fortuna Aleatória ---");
			SecureRandom random = new SecureRandom();
			int randomIndex = random.nextInt(NUM_FORTUNES);
			
			String fortune = hm.get(randomIndex);
			
			System.out.println("Sua fortuna de hoje eh:");
			System.out.println(fortune);
			System.out.println("-------------------------------------\n");
		}

		public void write(HashMap<Integer, String> hm)
				throws FileNotFoundException {

			//SEU CODIGO AQUI
			System.out.println("--- Adicionar Nova Fortuna ---");
			System.out.println("Digite a nova fortuna, digite 'FIM' em uma nova linha para salvar.");
			
			Scanner scanner = new Scanner(System.in);
			StringBuilder newFortune = new StringBuilder();
			String line;
			
			while (!(line = scanner.nextLine()).equalsIgnoreCase("FIM")) {
				newFortune.append(line).append(System.lineSeparator());
			}
			
			scanner.close();
			
			if (newFortune.length() == 0) {
				System.out.println("Nenhuma fortuna foi adicionada.");
				return;
			}
			
			
			newFortune.setLength(newFortune.length() - System.lineSeparator().length());

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toString(), true))) {
				writer.newLine(); 
				writer.write(newFortune.toString());
				writer.newLine();
				writer.write("%");
				System.out.println("Fortuna adicionada com sucesso!");
			} catch (IOException e) {
				System.err.println("Ocorreu um erro ao escrever no arquivo de fortunas: " + e.getMessage());
			}
		}
	}

	public void iniciar() {

		FileReader fr = new FileReader();
		try {
			NUM_FORTUNES = fr.countFortunes();
			HashMap<Integer, String> hm = new HashMap<Integer, String>();
			fr.parser(hm);
			fr.read(hm);
			fr.write(hm);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		new Principal_v0().iniciar();
	}

}
