package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

import javafx.stage.FileChooser;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class ControleProdutos {

    private static final int PRODUTOS_POR_PAGINA = 10;

    // =========================================================
    // BOTÕES
    // =========================================================

    @FXML
    private Button btnExportar;

    @FXML
    private Button btnImportar;

    @FXML
    private Button btnLimparCampos;

    @FXML
    private Button btnNovosProdutos;

    @FXML
    private Button btnSalvarProduto;

    // =========================================================
    // COLUNAS DA TABELA
    // =========================================================

    @FXML
    private TableColumn<Produtos, Void> colAcoes;

    @FXML
    private TableColumn<Produtos, String> colCategoria;

    @FXML
    private TableColumn<Produtos, String> colCodigo;

    @FXML
    private TableColumn<Produtos, String> colEstoque;

    @FXML
    private TableColumn<Produtos, String> colMarca;

    @FXML
    private TableColumn<Produtos, String> colPreco;

    @FXML
    private TableColumn<Produtos, String> colProduto;

    @FXML
    private TableColumn<Produtos, String> colStatus;

    // =========================================================
    // COMBOBOX
    // =========================================================

    @FXML
    private ComboBox<String> comboTodasCategorias;

    @FXML
    private ComboBox<String> comboTodosStatus;

    @FXML
    private ComboBox<String> comboUnidade;

    @FXML
    private ComboBox<String> comboCategoria;

    // =========================================================
    // LABELS
    // =========================================================

    @FXML
    private Label lblCategorias;

    @FXML
    private Label lblDataAtual;

    @FXML
    private Label lblDiaSemana;

    @FXML
    private Label lblEstoqueBaixo;

    @FXML
    private Label lblProdutosAtivos;

    @FXML
    private Label lblTotalProdutos;

    @FXML
    private Label lblTotalProdutosLista;

    @FXML
    private Label lblMostrandoProdutos;
    
    // =========================================================
    // IMAGES
    // =========================================================
    
    @FXML
    private ImageView imgCodigoBarras;

    // =========================================================
    // TABELA E PAGINAÇÃO
    // =========================================================

    @FXML
    private TableView<Produtos> tableProdutos;

    @FXML
    private Pagination paginacaoProdutos;

    // =========================================================
    // CAMPOS
    // =========================================================

    @FXML
    private TextField txtCodBarras;

    @FXML
    private TextField txtEstoqueInicial;

    @FXML
    private TextField txtNomeProduto;

    @FXML
    private TextField txtPesquisarProdutos;

    @FXML
    private TextField txtPreco;

    // =========================================================
    // DADOS
    // =========================================================

    private final ObservableList<Produtos> todosProdutos = FXCollections.observableArrayList();
            
    private final  ObservableList<Produtos> produtosFiltrados = FXCollections.observableArrayList();

    private Timeline relogio;
    
    private Produtos produtoEmEdicao = null;

    // =========================================================
    // INITIALIZE
    // =========================================================

    @FXML
    public void initialize() {

        configurarUnidades();
        carregarCategorias();

        configurarColunas();
        configurarColunaAcoes();
        configurarPaginacao();
        configurarFiltros();

        atualizarDataHora();
        iniciarRelogio();

        // Carrega os produtos reais do MySQL
        definirProdutos(Conexao.buscarProdutos());

        btnSalvarProduto.setOnAction(evento -> salvarProduto());
        btnLimparCampos.setOnAction(evento -> limparCampos());
        btnExportar.setOnAction(evento -> exportarProdutos());
        btnImportar.setOnAction(evento -> importarProdutos());
    }

    // =========================================================
    // CONFIGURAÇÃO DAS COLUNAS
    // =========================================================

    private void configurarColunas() {

        colCodigo.setCellValueFactory(dados ->
                new SimpleStringProperty(
                        String.valueOf(dados.getValue().getId())
                )
        );

        colProduto.setCellValueFactory(dados ->
                new SimpleStringProperty(
                        textoOuPadrao(dados.getValue().getNome(), "-")
                )
        );

        colCategoria.setCellValueFactory(dados ->
                new SimpleStringProperty(
                        textoOuPadrao(
                                dados.getValue().getCategoria(),
                                "Sem categoria"
                        )
                )
        );

        colMarca.setCellValueFactory(dados ->
                new SimpleStringProperty(
                        textoOuPadrao(dados.getValue().getMarca(), "-")
                )
        );

        colPreco.setCellValueFactory(dados ->
                new SimpleStringProperty(
                        formatarPreco(dados.getValue())
                )
        );

        // ==========================================
        // NOVA COLUNA ESTOQUE COM CORES DINÂMICAS
        // ==========================================
        colEstoque.setCellValueFactory(dados ->
                new SimpleStringProperty(
                        String.valueOf(dados.getValue().getQuantidade())
                )
        );
        
        colEstoque.setCellFactory(coluna -> new TableCell<Produtos, String>() {
            @Override
            protected void updateItem(String item, boolean vazio) {
                super.updateItem(item, vazio);
        
                if (vazio || item == null) {
                    setText(null);
                    setStyle(""); 
                } else {
                    setText(item);
                    
                    Produtos produto = getTableRow().getItem();
                    
                    if (produto != null) {
                        int estoqueAtual = produto.getQuantidade();
                        int estoqueMinimo = produto.getEstoqueMinimo();
                        
                        String estilo = "-fx-alignment: CENTER;";
        
                        if (estoqueAtual <= 0) {
                            estilo += "-fx-text-fill: #E74C3C; -fx-font-weight: bold;";
                        } else if (estoqueAtual <= estoqueMinimo) {
                            estilo += "-fx-text-fill: #F1C40F; -fx-font-weight: bold;";
                        } else {
                            estilo += "-fx-text-fill: white;"; 
                        }
                        
                        setStyle(estilo);
                    }
                }
            }
        });
        
        // ==========================================
        // COLUNA STATUS COM BOLINHA VERDE/VERMELHA
        // ==========================================
        colStatus.setCellValueFactory(dados ->
                new SimpleStringProperty(
                        dados.getValue().getStatus()
                )
        );

        colStatus.setCellFactory(coluna -> new TableCell<Produtos, String>() {
            @Override
            protected void updateItem(String item, boolean vazio) {
                super.updateItem(item, vazio);

                if (vazio || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);

                    Circle bolinha = new Circle(4);

                    if (item.equals("Ativo")) {
                        bolinha.setFill(Color.web("#2ECC71")); 
                    } else {
                        bolinha.setFill(Color.web("#E74C3C")); 
                    }

                    setGraphic(bolinha);
                    setGraphicTextGap(8); 
                    setStyle("-fx-alignment: CENTER; -fx-text-fill: white;");
                }
            }
        });
    }

    // =========================================================
    // MÉTODOS AUXILIARES RESTAURADOS
    // =========================================================

    private String textoOuPadrao(String texto, String padrao) {
        if (texto == null || texto.isBlank()) {
            return padrao;
        }
        return texto;
    }

    private String formatarPreco(Produtos produto) {
        if (produto.getPreco() == null) {
            return "R$ 0,00";
        }

        NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return moeda.format(produto.getPreco());
    }

    // =========================================================
    // COLUNA AÇÕES
    // =========================================================

    private void configurarColunaAcoes() {

        colAcoes.setCellFactory(coluna ->
                new TableCell<Produtos, Void>() {

            private final Button btnVisualizar =
                    criarBotaoAcao(
                            "👁",
                            "Visualizar produto",
                            "#184E83"
                    );

            private final Button btnEditar =
                    criarBotaoAcao(
                            "✎",
                            "Editar produto",
                            "#4059CF"
                    );

            private final Button btnExcluir =
                    criarBotaoAcao(
                            "🗑",
                            "Excluir produto",
                            "#B23B3B"
                    );

            private final HBox caixaAcoes =
                    new HBox(
                            6,
                            btnVisualizar,
                            btnEditar,
                            btnExcluir
                    );

            {
                caixaAcoes.setAlignment(Pos.CENTER);

                btnVisualizar.setOnAction(evento -> {
                    Produtos produto = getTableRow().getItem();

                    if (produto != null) {
                        visualizarProduto(produto);
                    }
                });

                btnEditar.setOnAction(evento -> {
                    Produtos produto = getTableRow().getItem();

                    if (produto != null) {
                        editarProduto(produto);
                    }
                });

                btnExcluir.setOnAction(evento -> {
                    Produtos produto = getTableRow().getItem();

                    if (produto != null) {
                        excluirProduto(produto);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean vazio) {

                super.updateItem(item, vazio);

                if (vazio
                        || getTableRow() == null
                        || getTableRow().getItem() == null) {

                    setGraphic(null);

                } else {

                    setGraphic(caixaAcoes);
                }
            }
        });
    }

    private Button criarBotaoAcao(
            String icone,
            String tooltip,
            String corFundo) {

        Button botao = new Button(icone);

        botao.setPrefSize(32, 28);
        botao.setMinSize(32, 28);
        botao.setMaxSize(32, 28);
        botao.setFocusTraversable(false);

        botao.setTooltip(
                new Tooltip(tooltip)
        );

        botao.setStyle(
                "-fx-background-color: " + corFundo + ";" +
                "-fx-background-radius: 5px;" +
                "-fx-border-color: #3F648D;" +
                "-fx-border-radius: 5px;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 0;"
        );

        return botao;
    }

    // =========================================================
    // PAGINAÇÃO
    // =========================================================

    private void configurarPaginacao() {

        paginacaoProdutos.setMaxPageIndicatorCount(5);

        paginacaoProdutos
                .currentPageIndexProperty()
                .addListener(
                        (
                                observable,
                                paginaAnterior,
                                paginaNova
                        ) -> atualizarPagina(
                                paginaNova.intValue()
                        )
                );
    }

    private void atualizarPaginacao() {

    	int totalProdutos = produtosFiltrados.size();

        int totalPaginas = Math.max(
                        1,
                        (int) Math.ceil((double) totalProdutos/ PRODUTOS_POR_PAGINA));

        paginacaoProdutos.setPageCount(totalPaginas);

        int paginaAtual = paginacaoProdutos.getCurrentPageIndex();

        if (paginaAtual >= totalPaginas) {

            paginaAtual = totalPaginas - 1;

            paginacaoProdutos.setCurrentPageIndex(paginaAtual);
        }

        atualizarPagina(paginaAtual);
        atualizarTotalProdutos();
    }

    private void atualizarPagina(int indicePagina) {

    	int totalProdutos = produtosFiltrados.size();

        if (totalProdutos == 0) {

            tableProdutos.setItems(FXCollections.observableArrayList());

            lblMostrandoProdutos.setText("Mostrando 0 de 0 produtos");

            paginacaoProdutos.setVisible(false);
            paginacaoProdutos.setManaged(false);
            return;
        }

        paginacaoProdutos.setVisible(true);
        paginacaoProdutos.setManaged(true);

        int inicio =
                indicePagina
                        * PRODUTOS_POR_PAGINA;

        int fim =
                Math.min(
                        inicio + PRODUTOS_POR_PAGINA,
                        totalProdutos
                );

        ObservableList<Produtos> produtosDaPagina =
                FXCollections.observableArrayList(
                		produtosFiltrados.subList(
                		        inicio,
                		        fim
                		)
                );

        tableProdutos.setItems(
                produtosDaPagina
        );

        lblMostrandoProdutos.setText(
                "Mostrando "
                        + (inicio + 1)
                        + " a "
                        + fim
                        + " de "
                        + totalProdutos
                        + " produtos"
        );
    }

    private void atualizarTotalProdutos() {

        int totalProdutos = todosProdutos.size();

        long produtosAtivos = todosProdutos.stream()
                .filter(Produtos::isAtivo)
                .count();

        long estoqueBaixo = todosProdutos.stream()
                .filter(produto ->
                        produto.getQuantidade()
                                <= produto.getEstoqueMinimo()
                )
                .count();

        long totalCategorias = todosProdutos.stream()
                .filter(produto ->
                        produto.getCategoriaId() != null
                )
                .map(Produtos::getCategoriaId)
                .distinct()
                .count();

        lblTotalProdutos.setText(
                String.valueOf(totalProdutos)
        );

        if (lblTotalProdutosLista != null) {
        	lblTotalProdutosLista.setText(String.valueOf(produtosFiltrados.size()));
        }

        lblProdutosAtivos.setText(String.valueOf(produtosAtivos));

        lblEstoqueBaixo.setText(String.valueOf(estoqueBaixo));

        lblCategorias.setText(String.valueOf(totalCategorias));
    }
    

    private void configurarFiltros() {

        // Filtro de categorias
        comboTodasCategorias.getItems().setAll("Todas as categorias");
        comboTodasCategorias.getItems().addAll(
        comboCategoria.getItems()
        );
        comboTodasCategorias.setValue("Todas as categorias");

        // Filtro de status e estoque
        comboTodosStatus.getItems().setAll(
                "Todos os status",
                "Ativos",
                "Inativos",
                "Estoque baixo"
        );
        comboTodosStatus.setValue("Todos os status");

        // Pesquisa por texto
        txtPesquisarProdutos.textProperty().addListener((observable, anterior, atual) -> aplicarFiltros());

        // Alteração da categoria
        comboTodasCategorias.valueProperty().addListener((observable, anterior, atual) -> aplicarFiltros());

        // Alteração do status
        comboTodosStatus.valueProperty().addListener((observable, anterior, atual) -> aplicarFiltros());
    }

    
    private void aplicarFiltros() {

        String pesquisa = normalizar(txtPesquisarProdutos.getText());
        String categoriaSelecionada = comboTodasCategorias.getValue();
        String statusSelecionado = comboTodosStatus.getValue();

        List<Produtos> filtrados = todosProdutos.stream()

                // Pesquisa por nome, código, marca ou categoria
                .filter(produto -> pesquisa.isEmpty()
                        || normalizar(produto.getNome()).contains(pesquisa)
                        || normalizar(produto.getCodigoBarras()).contains(pesquisa)
                        || normalizar(produto.getMarca()).contains(pesquisa)
                        || normalizar(produto.getCategoria()).contains(pesquisa)
                        || String.valueOf(produto.getId()).contains(pesquisa)
                )

                // Filtro de categoria
                .filter(produto -> categoriaSelecionada == null
                        || categoriaSelecionada.equals("Todas as categorias")
                        || categoriaSelecionada.equals(produto.getCategoria())
                )

                // Filtro de status e estoque baixo
                .filter(produto -> {

                    if (statusSelecionado == null
                            || statusSelecionado.equals("Todos os status")) {
                        return true;
                    }

                    switch (statusSelecionado) {

                        case "Ativos":
                            return produto.isAtivo();

                        case "Inativos":
                            return !produto.isAtivo();

                        case "Estoque baixo":
                            return produto.getQuantidade()
                                    <= produto.getEstoqueMinimo();

                        default:
                            return true;
                    }
                })

                .toList();

        produtosFiltrados.setAll(filtrados);

        // Volta para a primeira página quando os filtros mudam
        paginacaoProdutos.setCurrentPageIndex(0);

        atualizarPaginacao();
    }
    
    private String normalizar(String texto) {

        if (texto == null) {
            return "";
        }

        return Normalizer.normalize(
                texto.trim().toLowerCase(Locale.ROOT),
                Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }


    // =========================================================
    // RECEBE PRODUTOS DO BANCO
    // =========================================================

    public void definirProdutos(List<Produtos> produtos) {

        todosProdutos.setAll(produtos);

        aplicarFiltros();
    }

    public void adicionarProduto(Produtos produto) {

        if (produto == null) {
            return;
        }

        todosProdutos.add(produto);

        aplicarFiltros();
    }
    
    // =========================================================
    // EXPORTAR PRODUTOS
    // =========================================================
    
    private void exportarProdutos() {
    	
    	if (produtosFiltrados.isEmpty()) {
    		mostrarAviso(
    				"Nenhum produto",
    				"Não existem produtos na lista atual para exportar."
    				);
    		return;
    	}
    	
    	FileChooser seletor = new FileChooser();
    	seletor.setTitle("Exportar produtos");
    	seletor.setInitialFileName("produtos_mercado_mais_facil.csv");
    	seletor.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivo CSV (*.csv)", "*.csv"));
    	
    	File arquivo = seletor.showSaveDialog(btnExportar.getScene().getWindow());
    	
    	if (arquivo == null) {
    		return;
    		
    	}
    	
    	StringBuilder conteudo = new StringBuilder("\uFEFF");
    	
    	conteudo.append("ID;Nome;Código de barras;Categoria;Marca;Preço;Quantidade;Estoque mínimo;Unidade;Status").append(System.lineSeparator());
    	
    	for (Produtos produto : produtosFiltrados) {
    		
    		conteudo.append(valorCsv(String.valueOf(produto.getId())))
		            .append(';')
		            .append(valorCsv(produto.getNome()))
		            .append(';')
		            .append(valorCsv(produto.getCodigoBarras()))
		            .append(';')
		            .append(valorCsv(produto.getCategoria()))
		            .append(';')
		            .append(valorCsv(produto.getMarca()))
		            .append(';')
		            .append(valorCsv(produto.getPreco() == null ? "" : produto.getPreco().toPlainString()))
		            .append(';')
		            .append(valorCsv(String.valueOf(produto.getQuantidade())))
		            .append(';')
		            .append(valorCsv(String.valueOf(produto.getEstoqueMinimo())))
		            .append(';')
		            .append(valorCsv(produto.getUnidade()))
		            .append(';')
		            .append(valorCsv(produto.getStatus()))
		            .append(System.lineSeparator());
    	}
    	
    	try {
    		Files.writeString(arquivo.toPath(), conteudo.toString(), StandardCharsets.UTF_8);
    		
    		Alert sucesso = new Alert(AlertType.INFORMATION);
            sucesso.setTitle("Exportação concluída");
            sucesso.setHeaderText("Produtos exportados com sucesso!");
            sucesso.setContentText("Arquivo salvo em:\n" + arquivo.getAbsolutePath());
            sucesso.showAndWait();
            
    	} catch (IOException e) {
            e.printStackTrace();

            Alert erro = new Alert(AlertType.ERROR);
            erro.setTitle("Erro na exportação");
            erro.setHeaderText("Não foi possível exportar os produtos.");
            erro.setContentText(e.getMessage());
            erro.showAndWait();
        }
    }
    
    private String valorCsv(String valor) {

        if (valor == null) {
            valor = "";
        }

        return "\"" + valor.replace("\"", "\"\"") + "\"";
    }
    
    // =========================================================
    // IMPORTAR PRODUTOS
    // =========================================================
    
    private void importarProdutos() {

        FileChooser seletor = new FileChooser();
        seletor.setTitle("Importar produtos");
        seletor.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivo CSV (*.csv)", "*.csv"));

        File arquivo = seletor.showOpenDialog(btnImportar.getScene().getWindow());

        if (arquivo == null) {
            return;
        }

        int importados = 0;
        int duplicados = 0;
        int erros = 0;

        try {
            List<String> linhas = Files.readAllLines(arquivo.toPath(), StandardCharsets.UTF_8);
            boolean primeiraLinha = true;

            for (String linha : linhas) {
                // Remove o caractere invisível BOM (\uFEFF) se existir no início do ficheiro
                if (primeiraLinha && linha.startsWith("\uFEFF")) {
                    linha = linha.substring(1);
                }

                // Pula o cabeçalho
                if (primeiraLinha && linha.toLowerCase().contains("nome")) {
                    primeiraLinha = false;
                    continue;
                }
                primeiraLinha = false;

                if (linha.trim().isEmpty()) continue;

                // Divide por ponto e vírgula, ignorando os que estiverem dentro de aspas
                String[] dados = linha.split(";(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (dados.length < 9) {
                    erros++;
                    continue;
                }

                String nome = limparValorCsv(dados[1]);
                String codigoBarras = limparValorCsv(dados[2]);
                String categoriaNome = limparValorCsv(dados[3]);
                String marca = limparValorCsv(dados[4]);
                String precoStr = limparValorCsv(dados[5]).replace(",", ".");
                String quantidadeStr = limparValorCsv(dados[6]);
                String estoqueMinimoStr = limparValorCsv(dados[7]);
                String unidade = limparValorCsv(dados[8]);
                String status = dados.length > 9 ? limparValorCsv(dados[9]) : "Ativo";

                // ==========================================
                // VERIFICAÇÃO DE DUPLICIDADE EM MEMÓRIA
                // ==========================================
                boolean jaExiste = todosProdutos.stream().anyMatch(p -> 
                    p.getNome().equalsIgnoreCase(nome) || 
                    (!codigoBarras.isEmpty() && codigoBarras.equals(p.getCodigoBarras()))
                );

                if (jaExiste) {
                    duplicados++;
                    continue;
                }

                // Conversões de números
                BigDecimal preco = precoStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(precoStr);
                int quantidade = quantidadeStr.isEmpty() ? 0 : Integer.parseInt(quantidadeStr);
                int estoqueMinimo = estoqueMinimoStr.isEmpty() ? 5 : Integer.parseInt(estoqueMinimoStr);
                boolean ativo = status.equalsIgnoreCase("Ativo");

                // ==========================================
                // VERIFICAÇÃO DA CATEGORIA
                // ==========================================
                Integer categoriaId = null;
                if (!categoriaNome.isEmpty()) {
                    categoriaId = Conexao.buscarIdCategoria(categoriaNome);
                }

                Produtos novoProduto = new Produtos(
                        0, // ID gerado pelo MySQL
                        nome,
                        codigoBarras.isEmpty() ? null : codigoBarras,
                        marca.isEmpty() ? null : marca,
                        preco,
                        quantidade,
                        estoqueMinimo,
                        unidade.isEmpty() ? "UN" : unidade,
                        ativo,
                        categoriaId,
                        categoriaNome
                );

                // Salva no banco de dados
                if (Conexao.cadastrarProduto(novoProduto)) {
                    importados++;
                } else {
                    erros++;
                }
            }

            Alert resultado = new Alert(AlertType.INFORMATION);
            resultado.setTitle("Importação Concluída");
            resultado.setHeaderText("Resumo da importação");
            resultado.setContentText(
                    "Produtos importados com sucesso: " + importados + "\n" +
                    "Produtos duplicados (ignorados): " + duplicados + "\n" +
                    "Erros de formatação/salvamento: " + erros
            );
            resultado.showAndWait();

            // Atualiza a tabela na tela se novos itens foram adicionados
            if (importados > 0) {
                definirProdutos(Conexao.buscarProdutos());
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAviso("Erro na importação", "Ocorreu um erro ao ler o arquivo:\n" + e.getMessage());
        }
    }

    private String limparValorCsv(String valor) {
        if (valor == null) return "";
        valor = valor.trim();
        // Remove as aspas do início e fim colocadas na exportação
        if (valor.startsWith("\"") && valor.endsWith("\"") && valor.length() >= 2) {
            valor = valor.substring(1, valor.length() - 1);
        }
        // Desfaz o escape de aspas duplas ("" volta a ser ")
        return valor.replace("\"\"", "\"");
    }

    // =========================================================
    // AÇÕES DA TABELA
    // =========================================================


    private void visualizarProduto(Produtos produto) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/application/detalhesproduto.fxml"));

            Parent root = loader.load();

            ControleDetalhesProduto controle = loader.getController();

            controle.setProduto(produto);
            controle.setAcaoEditar(this::editarProduto);

            Stage janela = new Stage();

            janela.initModality(Modality.APPLICATION_MODAL);
            janela.initStyle(StageStyle.TRANSPARENT);

            Scene cena = new Scene(root);
            cena.setFill(Color.TRANSPARENT);

            janela.setTitle("Detalhes do Produto");
            janela.setScene(cena);
            janela.setResizable(false);
            janela.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();

            new Alert(Alert.AlertType.ERROR,"Não foi possível abrir os detalhes do produto.").showAndWait();
        }
    }


    private void editarProduto(Produtos produto) {

        produtoEmEdicao = produto;
        
        txtNomeProduto.setText(produto.getNome());
        
        txtCodBarras.setText(produto.getCodigoBarras() == null ? "" : produto.getCodigoBarras());
        
        txtPreco.setText(produto.getPreco() == null ? "" : produto.getPreco().toPlainString().replace(".",","));
        
        txtEstoqueInicial.setText(String.valueOf(produto.getQuantidade()));
        
        comboCategoria.setValue(produto.getCategoria());
        
        comboUnidade.setValue(produto.getUnidade());
        
        btnSalvarProduto.setText("Atualizar Produto");
        
        txtNomeProduto.requestFocus();
        
    }

    private void excluirProduto(Produtos produto) {
    	
    	Alert confirmacao = new Alert(AlertType.CONFIRMATION);
    	
    	confirmacao.setTitle("Excluir Produto");
    	confirmacao.setHeaderText("Deseja excluir " + produto.getNome() + "?");
    	confirmacao.setContentText("Esta ação removerá o produto do banco de dados.");
    	confirmacao.showAndWait().ifPresent(resposta -> {
    		
    		if(resposta == ButtonType.OK) {
    			boolean excluido = Conexao.excluirProduto(produto.getId());
    			
    			if(excluido) {
    				
    				Alert sucesso = new Alert(AlertType.INFORMATION);
    				
    				sucesso.setTitle("Produto excluído");
    				sucesso.setHeaderText("Produto excluído com sucesso!");
    				sucesso.setContentText(produto.getNome() + " foi removido do sistema.");
    				sucesso.showAndWait();
    				
    				definirProdutos(Conexao.buscarProdutos());
    			} else {
    				Alert erro = new Alert(AlertType.ERROR);
    				erro.setTitle("Não foi possíveç excluir");
    				erro.setHeaderText("O produto não pôde ser excluído.");
    				erro.setContentText("Se este produto já estover associado "
    									+ "a uma venda, ele não pode ser apagado "
    									+ "para preservar o histórico.");
    				erro.showAndWait();
    			}
    		}
    	});
    }

    // =========================================================
    // DATA E HORA
    // =========================================================

    private void iniciarRelogio() {

        relogio =
                new Timeline(
                        new KeyFrame(
                                Duration.seconds(1),
                                evento ->
                                        atualizarDataHora()
                        )
                );

        relogio.setCycleCount(
                Timeline.INDEFINITE
        );

        relogio.play();
    }

    private void atualizarDataHora() {

        Locale brasil =
                Locale.forLanguageTag(
                        "pt-BR"
                );

        LocalDateTime agora =
                LocalDateTime.now();

        DateTimeFormatter formatoData =
                DateTimeFormatter.ofPattern(
                        "'Hoje é' dd 'de' MMMM 'de' yyyy",
                        brasil
                );

        DateTimeFormatter formatoDiaHora =
                DateTimeFormatter.ofPattern(
                        "EEEE, HH:mm",
                        brasil
                );

        if (lblDataAtual != null) {

            lblDataAtual.setText(
                    agora.format(
                            formatoData
                    )
            );
        }

        if (lblDiaSemana != null) {

            String textoDiaHora =
                    agora.format(
                            formatoDiaHora
                    );

            if (!textoDiaHora.isEmpty()) {

                textoDiaHora =
                        Character.toUpperCase(
                                textoDiaHora.charAt(0)
                        )
                                + textoDiaHora.substring(1);
            }

            lblDiaSemana.setText(
                    textoDiaHora
            );
        }
    }

    // =========================================================
    // CONFIGURAÇÃO DAS UNIDADES
    // =========================================================

    private void configurarUnidades() {

        comboUnidade.getItems().setAll(
                "UN",
                "KG",
                "G",
                "L",
                "ML"
        );
    }

    // =========================================================
    // CARREGAR CATEGORIAS
    // =========================================================

    private void carregarCategorias() {

        comboCategoria.getItems().setAll(
                Conexao.buscarNomesCategorias()
        );
    }

    // =========================================================
    // LIMPAR CAMPOS
    // =========================================================

    private void limparCampos() {

        txtNomeProduto.clear();
        txtCodBarras.clear();
        txtPreco.clear();
        txtEstoqueInicial.clear();

        comboCategoria
                .getSelectionModel()
                .clearSelection();

        comboUnidade
                .getSelectionModel()
                .clearSelection();

        produtoEmEdicao = null;

        btnSalvarProduto.setText(
                "Salvar Produto"
        );
    }

    // =========================================================
    // AVISOS
    // =========================================================

    private void mostrarAviso(
            String titulo,
            String mensagem) {

        Alert alerta =
                new Alert(
                        AlertType.WARNING
                );

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    // =========================================================
    // SALVAR PRODUTO
    // =========================================================

    private void salvarProduto() {

        String nome =
                txtNomeProduto
                        .getText()
                        .trim();

        String codigoBarras =
                txtCodBarras
                        .getText()
                        .trim();

        String precoTexto =
                txtPreco
                        .getText()
                        .trim();

        String estoqueTexto =
                txtEstoqueInicial
                        .getText()
                        .trim();

        String categoriaSelecionada =
                comboCategoria.getValue();

        String unidadeSelecionada =
                comboUnidade.getValue();

        if (nome.isEmpty()
                || precoTexto.isEmpty()
                || estoqueTexto.isEmpty()
                || categoriaSelecionada == null
                || unidadeSelecionada == null) {

            mostrarAviso(
                    "Campos obrigatórios",
                    "Nome, Categoria, Preço, Estoque Inicial "
                            + "e Unidade devem ser preenchidos."
            );

            return;
        }

        try {

            BigDecimal preco =new BigDecimal(precoTexto.replace(",", "."));

            int estoque =Integer.parseInt( estoqueTexto);

            if (preco.compareTo(BigDecimal.ZERO) < 0) {

                mostrarAviso(
                        "Preço inválido",
                        "O preço não pode ser negativo."
                );
                return;
            }

            if (estoque < 0) {

                mostrarAviso(
                        "Estoque inválido",
                        "O estoque não pode ser negativo."
                );
                return;
            }

            Integer categoriaId =Conexao.buscarIdCategoria(categoriaSelecionada);

            if (categoriaId == null) {

                mostrarAviso(
                        "Categoria inválida",
                        "Não foi possível localizar a categoria no banco."
                );
                return;
            }

            boolean editando = produtoEmEdicao != null;

            int idProduto = editando
                    ? produtoEmEdicao.getId()
                    : 0;

            String marca = editando
                    ? produtoEmEdicao.getMarca()
                    : null;

            int estoqueMinimo = editando
                    ? produtoEmEdicao.getEstoqueMinimo()
                    : 5;

            boolean ativo = editando
                    ? produtoEmEdicao.isAtivo()
                    : true;

            Produtos produto = new Produtos(
                    idProduto,
                    nome,
                    codigoBarras.isEmpty()
                            ? null
                            : codigoBarras,
                    marca,
                    preco,
                    estoque,
                    estoqueMinimo,
                    unidadeSelecionada,
                    ativo,
                    categoriaId,
                    categoriaSelecionada
            );

            boolean sucessoBanco;

            if (editando) {

                sucessoBanco =
                        Conexao.atualizarProduto(produto);

            } else {

                sucessoBanco =
                        Conexao.cadastrarProduto(produto);
            }

            if (sucessoBanco) {

            	Alert sucesso =
            	        new Alert(
            	                AlertType.INFORMATION
            	        );

            	sucesso.setTitle(
            	        editando
            	                ? "Produto atualizado"
            	                : "Produto cadastrado"
            	);

            	sucesso.setHeaderText(
            	        editando
            	                ? "Produto atualizado com sucesso!"
            	                : "Produto cadastrado com sucesso!"
            	);

            	sucesso.setContentText(
            	        nome
            	                + (editando
            	                        ? " foi atualizado."
            	                        : " foi adicionado ao sistema.")
            	);

            	sucesso.showAndWait();

            	produtoEmEdicao = null;

            	btnSalvarProduto.setText(
            	        "Salvar Produto"
            	);

            	limparCampos();

            	definirProdutos(
            	        Conexao.buscarProdutos()
            	);

            } else {

                Alert erro =new Alert(AlertType.ERROR);

                erro.setTitle(
                        "Erro"
                );

                erro.setHeaderText(
                        "Não foi possível cadastrar o produto."
                );

                erro.setContentText(
                        "Verifique o console para mais detalhes."
                );

                erro.showAndWait();
            }

        } catch (NumberFormatException e) {

            mostrarAviso(
                    "Valor inválido",
                    "Digite um preço e estoque válidos.\n"
                            + "Exemplo de preço: 12,90\n"
                            + "Exemplo de estoque: 10"
            );
        }
    }
}