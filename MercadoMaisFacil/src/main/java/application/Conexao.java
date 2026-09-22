package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
	
	private static final String URL = "jdbc:mysql://localhost:3306/mercado_mais_facil";
	
	private static final String USUARIO = "root";
	
	private static final String SENHA = "Aluno";
	
	 public static Connection conectar() {
	        try {
	            Connection conexao = DriverManager.getConnection(
	                    URL,
	                    USUARIO,
	                    SENHA
	            );
	            System.out.println("Conectado ao banco com sucesso!");
	            return conexao;

	        } catch (SQLException e) {
	            System.out.println("Erro ao conectar ao banco:");
	            System.out.println(e.getMessage());
	            return null;
	        }
	    }
	 public static int buscarTotalProdutosEstoque() {

		    String sql = "SELECT SUM(quantidade) AS total FROM produtos";

		    try (
		        Connection conexao = conectar();
		        PreparedStatement comando = conexao.prepareStatement(sql);
		        ResultSet resultado = comando.executeQuery()
		    ) {
		        if (resultado.next()) {
		            return resultado.getInt("total");
		        }

		    } catch (SQLException e) {
		        System.out.println("Erro ao buscar estoque:");
		        System.out.println(e.getMessage());
		    }
		    return 0;
		}
}