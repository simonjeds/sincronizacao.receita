package com.example.sincronizacao.receita;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.sincronizacao.receita.service.ReceitaService;

@SpringBootApplication
public class SincronizacaoReceitaApplication {

	public static void main(String[] args) {

		// Fiz a lógica no main, não vi necessidade de fazer algo mais estruturado por conta do exercício não ser muito complexo. 
		
		for (String arg : args) {
			String arquivoCSV = arg.toLowerCase();
			String arquivoCSVretorno = arquivoCSV.replace(".csv", "_retorno.csv");
			
			if (!arquivoCSV.contains(".csv")) {
				System.out.println("A aplicação processa apenas arquivo CSV!!");
				continue;
			}
			
		    BufferedReader br = null;
		    
		    FileWriter arq = null;
		    PrintWriter gravarArq = null;
		    
		    String linha = "";
		    
		    String cabecalho = "agencia;conta;saldo;status";
		    String csvDivisor = ";";
		    int quantidadeItensCabecalho = 4;
		    
		    try {
		    	ReceitaService receitaService = new ReceitaService();
		    	br = new BufferedReader(new FileReader(arquivoCSV));
		    	
		    	arq = new FileWriter(arquivoCSVretorno);
		        gravarArq = new PrintWriter(arq);

		        while ((linha = br.readLine()) != null) {
		        	gravarArq.printf(linha);
		        	if (cabecalho.equals(linha)) {
		        		gravarArq.printf(";processado%n");
		        		System.out.println(cabecalho);
		        		continue;
		        	}

		            String[] colunas = linha.split(csvDivisor);
		            
		            if (colunas.length != quantidadeItensCabecalho) {
		            	gravarArq.printf(";Falha%n");
		            	System.out.println(linha + " << Dados Incorreto!");
		            	continue;
		            }
		            
		            try {
		            	System.out.print(linha + " << Enviada para processamento");
			            if (receitaService.atualizarConta(colunas[0], colunas[1].replaceAll("[^0-9]+", ""), Double.parseDouble(colunas[2].replace(",", ".")) , colunas[3])) {
			            	System.out.println(" << (Processada)");
			            	gravarArq.printf(";Sim%n");
			            } else {
			            	System.out.println(" << (Incompatibilidade de dados - conta não processada)");
			            	gravarArq.printf(";Falha%n");
			            }
					} catch (NumberFormatException e) {
				    	gravarArq.printf(";Falha%n");
				    	System.out.println(" << Valor informado na coluna \"saldo\" não é válido!");
					}
		        }

		        System.out.println("Arquivo de retorno \""+arquivoCSVretorno+"\" gerado com sucesso!!");
		    } catch (FileNotFoundException e) {
		        System.out.println(e.getMessage());
		        System.out.println("Processamento interrompido!");
			} catch (Exception e) {
		        System.out.println("");
		        System.out.println(e.getMessage());
		        System.out.println("Processamento interrompido!");
			} finally {
				try {
					if (br != null) 
						br.close();
					if (arq != null) 
						arq.close();
				} catch (IOException e) {
	                System.out.println(e.getMessage());
	            }
		    }
		}
		
		//SpringApplication.run(SincronizacaoReceitaApplication.class, args);
	}
	
}