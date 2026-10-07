package application;

import java.text.NumberFormat;
import java.util.Locale;

import java.util.ArrayList;
import java.util.List;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

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

	        } catch(SQLException e) {
	            System.out.println("Erro ao conectar ao banco:");
	            System.out.println(e.getMessage());
	            return null;
	        }
	    }
	 public static int buscarTotalProdutosEstoque() {

		    String sql = "SELECT SUM(quantidade) AS total FROM produtos";

		    try(
		        Connection conexao = conectar();
		        PreparedStatement comando = conexao.prepareStatement(sql);
		        ResultSet resultado = comando.executeQuery()
		    ) {
		        if(resultado.next()) {
		            return resultado.getInt("total");
		        }

		    } catch(SQLException e) {
		        System.out.println("Erro ao buscar estoque:");
		        System.out.println(e.getMessage());
		    }
		    return 0;
		}
	 public static void main(String[] args) {

		    Connection conexao = conectar();

		    if(conexao != null) {
		        System.out.println("TESTE DE CONEXÃO: OK!");
		    } else{
		        System.out.println("TESTE DE CONEXÃO: FALHOU!");
		    }
		}
	 
	 public static int buscarTotalClientes() {

		    String sql = "SELECT COUNT(*) AS total FROM clientes";

		    try(
		        Connection conexao = conectar();
		        PreparedStatement comando = conexao.prepareStatement(sql);
		        ResultSet resultado = comando.executeQuery()
		    ) {
		        if (resultado.next()) {
		            return resultado.getInt("total");
		        }
		        
		    } catch (SQLException e) {
		        System.out.println("Erro ao buscar total de clientes:");
		        System.out.println(e.getMessage());
		    }
		    return 0;
		}
	 
	 public static BigDecimal buscarVendasHoje() {

		    String sql = """
		        SELECT COALESCE(SUM(valor_total), 0) AS total
		        FROM vendas
		        WHERE DATE(data_venda) = CURDATE()
		        """;

		    try (
		        Connection conexao = conectar();
		        PreparedStatement comando = conexao.prepareStatement(sql);
		        ResultSet resultado = comando.executeQuery()
		    ) {

		        if (resultado.next()) {
		            return resultado.getBigDecimal("total");
		        }

		    } catch (SQLException e) {
		        System.out.println("Erro ao buscar vendas de hoje:");
		        System.out.println(e.getMessage());
		    }

		    return BigDecimal.ZERO;
		}
	 
	 public static BigDecimal buscarFaturamentoMes() {

		    String sql = """
		        SELECT COALESCE(SUM(valor_total), 0) AS total
		        FROM vendas
		        WHERE MONTH(data_venda) = MONTH(CURDATE())
		          AND YEAR(data_venda) = YEAR(CURDATE())
		        """;

		    try (
		        Connection conexao = conectar();
		        PreparedStatement comando = conexao.prepareStatement(sql);
		        ResultSet resultado = comando.executeQuery()
		    ) {

		        if (resultado.next()) {
		            return resultado.getBigDecimal("total");
		        }

		    } catch (SQLException e) {
		        System.out.println("Erro ao buscar faturamento do mês:");
		        System.out.println(e.getMessage());
		    }

		    return BigDecimal.ZERO;
		}
	 
	 public static Map<String, BigDecimal> buscarVendasUltimos7Dias() {
		    Map<String, BigDecimal> vendas = new LinkedHashMap<>();
		    DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM");
		    for (int i = 6; i >= 0; i--) {

		        LocalDate data = LocalDate.now().minusDays(i);

		        vendas.put(
		            data.format(formato),
		            BigDecimal.ZERO
		        );
		    }
		    String sql = """
		        SELECT DATE(data_venda) AS data,
		               SUM(valor_total) AS total
		        FROM vendas
		        WHERE DATE(data_venda) >= CURDATE() - INTERVAL 6 DAY
		        GROUP BY DATE(data_venda)
		        ORDER BY DATE(data_venda)
		        """;
		    try (
		        Connection conexao = conectar();
		        PreparedStatement comando = conexao.prepareStatement(sql);
		        ResultSet resultado = comando.executeQuery()
		    ) {
		        while (resultado.next()) {
		            LocalDate data =
		                resultado.getDate("data").toLocalDate();
		            BigDecimal total =
		                resultado.getBigDecimal("total");
		            vendas.put(
		                data.format(formato),
		                total
		            );
		        }
		        
		    } catch(SQLException e) {
		        System.out.println("Erro ao buscar vendas dos últimos 7 dias:");
		        System.out.println(e.getMessage());
		    }
		    return vendas;
		}
	 
	 public static Map<String, BigDecimal> buscarVendasPorCategoriaMes() {

		    Map<String, BigDecimal> vendasPorCategoria = new LinkedHashMap<>();

		    String sql = """
		        SELECT
		            c.nome AS categoria,
		            COALESCE(
		                SUM(
		                    CASE
		                        WHEN MONTH(v.data_venda) = MONTH(CURDATE())
		                         AND YEAR(v.data_venda) = YEAR(CURDATE())
		                        THEN iv.quantidade * iv.preco_unitario
		                        ELSE 0
		                    END
		                ),
		                0
		            ) AS total
		        FROM categorias c
		        LEFT JOIN produtos p
		            ON p.categoria_id = c.id
		        LEFT JOIN itens_venda iv
		            ON iv.produto_id = p.id
		        LEFT JOIN vendas v
		            ON v.id = iv.venda_id
		        GROUP BY c.id, c.nome
		        ORDER BY c.id
		        """;

		    try (
		        Connection conexao = conectar();
		        PreparedStatement comando = conexao.prepareStatement(sql);
		        ResultSet resultado = comando.executeQuery()
		    ) {

		        while (resultado.next()) {

		            String categoria = resultado.getString("categoria");
		            BigDecimal total = resultado.getBigDecimal("total");

		            vendasPorCategoria.put(categoria, total);
		        }

		    } catch (SQLException e) {

		        System.out.println("Erro ao buscar vendas por categoria:");
		        System.out.println(e.getMessage());
		    }

		    return vendasPorCategoria;
		}
	 
	 public static List<ProdutoEstoqueBaixo> buscarProdutosEstoqueBaixo() {

		    List<ProdutoEstoqueBaixo> produtos = new ArrayList<>();

		    String sql = """
		        SELECT nome, quantidade, estoque_minimo
		        FROM produtos
		        WHERE quantidade <= estoque_minimo
		        ORDER BY quantidade ASC
		        """;

		    try (
		        Connection conexao = conectar();
		        PreparedStatement comando = conexao.prepareStatement(sql);
		        ResultSet resultado = comando.executeQuery()
		    ) {

		        while (resultado.next()) {

		            String nome = resultado.getString("nome");
		            int estoqueAtual = resultado.getInt("quantidade");
		            int estoqueMinimo = resultado.getInt("estoque_minimo");

		            ProdutoEstoqueBaixo produto =
		                    new ProdutoEstoqueBaixo(
		                            nome,
		                            estoqueAtual,
		                            estoqueMinimo
		                    );

		            produtos.add(produto);
		        }

		    } catch (SQLException e) {

		        System.out.println("Erro ao buscar produtos com estoque baixo:");
		        System.out.println(e.getMessage());
		    }

		    return produtos;
		}
	 
	 public static List<UltimaVenda> buscarUltimasVendas() {

		    List<UltimaVenda> vendas = new ArrayList<>();

		    String sql = """
		        SELECT
		            v.id,
		            COALESCE(
		                GROUP_CONCAT(p.nome ORDER BY p.nome SEPARATOR ', '),
		                'Sem produtos'
		            ) AS produto,
		            COALESCE(c.nome, 'Cliente não informado') AS cliente,
		            DATE_FORMAT(v.data_venda, '%d/%m/%Y %H:%i') AS data,
		            v.valor_total
		        FROM vendas v
		        LEFT JOIN clientes c
		            ON c.id = v.cliente_id
		        LEFT JOIN itens_venda iv
		            ON iv.venda_id = v.id
		        LEFT JOIN produtos p
		            ON p.id = iv.produto_id
		        GROUP BY
		            v.id,
		            c.nome,
		            v.data_venda,
		            v.valor_total
		        ORDER BY v.data_venda DESC, v.id DESC
		        LIMIT 5
		        """;

		    try (
		        Connection conexao = conectar();
		        PreparedStatement comando = conexao.prepareStatement(sql);
		        ResultSet resultado = comando.executeQuery()
		    ) {

		        NumberFormat moeda =
		                NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

		        while (resultado.next()) {

		            UltimaVenda venda =
		                    new UltimaVenda(
		                            resultado.getString("produto"),
		                            resultado.getString("cliente"),
		                            resultado.getString("data"),
		                            moeda.format(
		                                    resultado.getBigDecimal("valor_total")
		                            )
		                    );

		            vendas.add(venda);
		        }

		    } catch (SQLException e) {

		        System.out.println("Erro ao buscar últimas vendas:");
		        System.out.println(e.getMessage());
		    }

		    return vendas;
		}


    // =========================================================
    // PRODUTOS - ABA PRODUTOS
    // =========================================================

    public static List<Produtos> buscarProdutos() {

        List<Produtos> produtos = new ArrayList<>();

        String sql = """
            SELECT
                p.id,
                p.nome,
                p.codigo_barras,
                p.marca,
                p.preco,
                p.quantidade,
                p.estoque_minimo,
                p.unidade,
                p.ativo,
                p.categoria_id,
                c.nome AS categoria
            FROM produtos p
            LEFT JOIN categorias c
                ON c.id = p.categoria_id
            ORDER BY p.nome
            """;

        try (
            Connection conexao = conectar();
            PreparedStatement comando = conexao.prepareStatement(sql);
            ResultSet resultado = comando.executeQuery()
        ) {

            while (resultado.next()) {

                int categoriaIdBanco =
                        resultado.getInt("categoria_id");

                Integer categoriaId =
                        resultado.wasNull()
                                ? null
                                : categoriaIdBanco;

                Produtos produto = new Produtos(
                        resultado.getInt("id"),
                        resultado.getString("nome"),
                        resultado.getString("codigo_barras"),
                        resultado.getString("marca"),
                        resultado.getBigDecimal("preco"),
                        resultado.getInt("quantidade"),
                        resultado.getInt("estoque_minimo"),
                        resultado.getString("unidade"),
                        resultado.getBoolean("ativo"),
                        categoriaId,
                        resultado.getString("categoria")
                );

                produtos.add(produto);
            }

        } catch (SQLException e) {

            System.out.println("Erro ao buscar produtos:");
            System.out.println(e.getMessage());
        }

        return produtos;
    }
    
    public static List<String> buscarNomesCategorias() {

        List<String> categorias = new ArrayList<>();

        String sql = """
            SELECT nome
            FROM categorias
            ORDER BY nome
            """;

        try (
            Connection conexao = conectar();
            PreparedStatement comando =
                    conexao.prepareStatement(sql);
            ResultSet resultado =
                    comando.executeQuery()
        ) {

            while (resultado.next()) {

                categorias.add(
                        resultado.getString("nome")
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Erro ao buscar categorias:"
            );

            System.out.println(
                    e.getMessage()
            );
        }

        return categorias;
    }
    
    public static boolean cadastrarProdutos(Produtos produto) {
    	
    	String sql = """
    			
    			INSERT INTO produtos(
    			nome,
    			codigo_barras,
    			marca,
    			preco,
    			quantidade,
    			estoque_minimo,
    			unidade,
    			ativo,
    			categoria_id
    			)
    			VALUES(
    				 ?,
    			     ?,
    			     ?,
    			     ?,
    			     ?,
    			     ?,
    			     ?,
    			     ?,
    			     ?
    			)
    			
    			""";
    	
    	 try (
    		        Connection conexao = conectar();
    		        PreparedStatement comando =
    		                conexao.prepareStatement(sql)
    		    ) {

    		        comando.setString(
    		                1,
    		                produto.getNome()
    		        );

    		        comando.setString(
    		                2,
    		                produto.getCodigoBarras()
    		        );

    		        comando.setString(
    		                3,
    		                produto.getMarca()
    		        );

    		        comando.setBigDecimal(
    		                4,
    		                produto.getPreco()
    		        );

    		        comando.setInt(
    		                5,
    		                produto.getQuantidade()
    		        );

    		        comando.setInt(
    		                6,
    		                produto.getEstoqueMinimo()
    		        );

    		        comando.setString(
    		                7,
    		                produto.getUnidade()
    		        );

    		        comando.setBoolean(
    		                8,
    		                produto.isAtivo()
    		        );

    		        if (produto.getCategoriaId() != null) {

    		            comando.setInt(
    		                    9,
    		                    produto.getCategoriaId()
    		            );

    		        } else {

    		            comando.setNull(
    		                    9,
    		                    java.sql.Types.INTEGER
    		            );
    		        }

    		        int linhasAfetadas =
    		                comando.executeUpdate();

    		        return linhasAfetadas > 0;

    		    } catch (SQLException e) {

    		        System.out.println(
    		                "Erro ao cadastrar produto:"
    		        );

    		        System.out.println(
    		                e.getMessage()
    		        );

    		        return false;
    		    }
    		}
    public static Integer buscarIdCategoria(
            String nomeCategoria) {

        String sql = """
            SELECT id
            FROM categorias
            WHERE nome = ?
            """;

        try (
            Connection conexao = conectar();
            PreparedStatement comando =
                    conexao.prepareStatement(sql)
        ) {

            comando.setString(
                    1,
                    nomeCategoria
            );

            try (
                ResultSet resultado =
                        comando.executeQuery()
            ) {

                if (resultado.next()) {

                    return resultado.getInt("id");
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Erro ao buscar categoria:"
            );

            System.out.println(
                    e.getMessage()
            );
        }

        return null;
    }
    
    

}

