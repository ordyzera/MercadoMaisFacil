package application;

import java.text.NumberFormat;
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
    private TableColumn<Produto, Void> colAcoes;

    @FXML
    private TableColumn<Produto, String> colCategoria;

    @FXML
    private TableColumn<Produto, String> colCodigo;

    @FXML
    private TableColumn<Produto, String> colEstoque;

    @FXML
    private TableColumn<Produto, String> colMarca;

    @FXML
    private TableColumn<Produto, String> colPreco;

    @FXML
    private TableColumn<Produto, String> colProduto;

    @FXML
    private TableColumn<Produto, String> colStatus;

    // =========================================================
    // COMBOBOX
    // =========================================================

    @FXML
    private ComboBox<?> comboAllCategorias;

    @FXML
    private ComboBox<?> comboAllStatus;

    @FXML
    private ComboBox<?> comboUnidade;

    @FXML
    private ComboBox<?> txtCategoria;

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
    // TABELA E PAGINAÇÃO
    // =========================================================

    @FXML
    private TableView<Produto> tableProdutos;

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
    private TextField txtPreço;

    // =========================================================
    // DADOS
    // =========================================================

    private final ObservableList<Produto> todosProdutos =
            FXCollections.observableArrayList();

    private Timeline relogio;

    // =========================================================
    // INITIALIZE
    // =========================================================

    @FXML
    public void initialize() {

        configurarColunas();
        configurarColunaAcoes();
        configurarPaginacao();

        atualizarDataHora();
        iniciarRelogio();

        atualizarPaginacao();
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

        colEstoque.setCellValueFactory(dados ->
                new SimpleStringProperty(
                        String.valueOf(
                                dados.getValue().getQuantidade()
                        )
                )
        );

        colStatus.setCellValueFactory(dados ->
                new SimpleStringProperty(
                        dados.getValue().getStatus()
                )
        );
    }

    private String textoOuPadrao(String texto, String padrao) {

        if (texto == null || texto.isBlank()) {
            return padrao;
        }

        return texto;
    }

    private String formatarPreco(Produto produto) {

        if (produto.getPreco() == null) {
            return "R$ 0,00";
        }

        NumberFormat moeda =
                NumberFormat.getCurrencyInstance(
                        new Locale("pt", "BR")
                );

        return moeda.format(produto.getPreco());
    }

    // =========================================================
    // COLUNA AÇÕES
    // =========================================================

    private void configurarColunaAcoes() {

        colAcoes.setCellFactory(coluna ->
                new TableCell<Produto, Void>() {

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

                    Produto produto =
                            getTableRow().getItem();

                    if (produto != null) {
                        visualizarProduto(produto);
                    }
                });

                btnEditar.setOnAction(evento -> {

                    Produto produto =
                            getTableRow().getItem();

                    if (produto != null) {
                        editarProduto(produto);
                    }
                });

                btnExcluir.setOnAction(evento -> {

                    Produto produto =
                            getTableRow().getItem();

                    if (produto != null) {
                        excluirProduto(produto);
                    }
                });
            }

            @Override
            protected void updateItem(
                    Void item,
                    boolean vazio) {

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

        int totalProdutos =
                todosProdutos.size();

        int totalPaginas =
                Math.max(
                        1,
                        (int) Math.ceil(
                                (double) totalProdutos
                                        / PRODUTOS_POR_PAGINA
                        )
                );

        paginacaoProdutos.setPageCount(
                totalPaginas
        );

        int paginaAtual =
                paginacaoProdutos
                        .getCurrentPageIndex();

        if (paginaAtual >= totalPaginas) {

            paginaAtual =
                    totalPaginas - 1;

            paginacaoProdutos
                    .setCurrentPageIndex(
                            paginaAtual
                    );
        }

        atualizarPagina(paginaAtual);
        atualizarTotalProdutos();
    }

    private void atualizarPagina(
            int indicePagina) {

        int totalProdutos =
                todosProdutos.size();

        if (totalProdutos == 0) {

            tableProdutos.setItems(
                    FXCollections.observableArrayList()
            );

            lblMostrandoProdutos.setText(
                    "Mostrando 0 de 0 produtos"
            );

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

        ObservableList<Produto> produtosDaPagina =
                FXCollections.observableArrayList(
                        todosProdutos.subList(
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

        String total =
                String.valueOf(
                        todosProdutos.size()
                );

        if (lblTotalProdutos != null) {
            lblTotalProdutos.setText(total);
        }

        if (lblTotalProdutosLista != null) {
            lblTotalProdutosLista.setText(total);
        }
    }

    // =========================================================
    // RECEBE PRODUTOS DO BANCO
    // =========================================================

    public void definirProdutos(
            List<Produto> produtos) {

        todosProdutos.setAll(produtos);

        paginacaoProdutos
                .setCurrentPageIndex(0);

        atualizarPaginacao();
    }

    public void adicionarProduto(
            Produto produto) {

        if (produto == null) {
            return;
        }

        todosProdutos.add(produto);

        atualizarPaginacao();
    }

    // =========================================================
    // AÇÕES
    // =========================================================

    private void visualizarProduto(
            Produto produto) {

        Alert alerta =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alerta.setTitle(
                "Visualizar Produto"
        );

        alerta.setHeaderText(
                produto.getNome()
        );

        alerta.setContentText(
                "Código: "
                        + produto.getId()

                        + "\nCódigo de barras: "
                        + textoOuPadrao(
                                produto.getCodigoBarras(),
                                "-"
                        )

                        + "\nCategoria: "
                        + textoOuPadrao(
                                produto.getCategoria(),
                                "Sem categoria"
                        )

                        + "\nMarca: "
                        + textoOuPadrao(
                                produto.getMarca(),
                                "-"
                        )

                        + "\nPreço: "
                        + formatarPreco(produto)

                        + "\nEstoque: "
                        + produto.getQuantidade()

                        + "\nUnidade: "
                        + textoOuPadrao(
                                produto.getUnidade(),
                                "-"
                        )

                        + "\nStatus: "
                        + produto.getStatus()
        );

        alerta.showAndWait();
    }

    private void editarProduto(
            Produto produto) {

        Alert alerta =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alerta.setTitle(
                "Editar Produto"
        );

        alerta.setHeaderText(
                "Produto selecionado: "
                        + produto.getNome()
        );

        alerta.setContentText(
                "O botão Editar já está ligado "
                        + "ao produto desta linha.\n\n"
                        + "Depois vamos preencher o formulário "
                        + "com os dados dele."
        );

        alerta.showAndWait();
    }

    private void excluirProduto(
            Produto produto) {

        Alert confirmacao =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmacao.setTitle(
                "Excluir Produto"
        );

        confirmacao.setHeaderText(
                "Deseja excluir "
                        + produto.getNome()
                        + "?"
        );

        confirmacao.setContentText(
                "Por enquanto esta ação remove "
                        + "o produto somente da lista da tela.\n"
                        + "Depois vamos ligar a exclusão "
                        + "ao MySQL."
        );

        confirmacao
                .showAndWait()
                .ifPresent(resposta -> {

                    if (resposta
                            == ButtonType.OK) {

                        todosProdutos.remove(
                                produto
                        );

                        atualizarPaginacao();
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
}
