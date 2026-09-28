package application;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class ControleDashboard {

	 @FXML
	    private AnchorPane painelDashboard;

	    @FXML
	    private Label lblClientesCadastrados;

	    @FXML
	    private Label lblFaturamentoMes;

	    @FXML
	    private Label lblProdutosEstoque;

	    @FXML
	    private Label lblVendasHoje;

	    @FXML
	    private AreaChart<String, Number> graficoVendas7Dias;

	    @FXML
	    private PieChart graficoVendasCategoria;

	    @FXML
	    private Label lblTotalVendasCategoria;

	    @FXML
	    private ComboBox<?> comboPeriodoCategoria;

	    @FXML
	    private Label lblPctAlimentos;

	    @FXML
	    private Label lblPctBebidas;

	    @FXML
	    private Label lblPctHigiene;

	    @FXML
	    private Label lblPctFrios;

	    @FXML
	    private Label lblPctPadaria;

	    @FXML
	    private Label lblPctOutros;

	    @FXML
	    private Label lblDataAtual;

	    @FXML
	    private Label lblDiaSemana;

	    @FXML
	    private TableView<ProdutoEstoqueBaixo> tabelaEstoqueBaixo;

	    @FXML
	    private TableColumn<ProdutoEstoqueBaixo, String> colProdutoEstoque;

	    @FXML
	    private TableColumn<ProdutoEstoqueBaixo, Integer> colEstoqueAtual;

	    @FXML
	    private TableColumn<ProdutoEstoqueBaixo, Integer> colEstoqueMinimo;

	    @FXML
	    private TableView<UltimaVenda> tabelaUltimasVendas;

	    @FXML
	    private TableColumn<UltimaVenda, String> colProdutoVenda;

	    @FXML
	    private TableColumn<UltimaVenda, String> colClienteVenda;

	    @FXML
	    private TableColumn<UltimaVenda, String> colDataVenda;

	    @FXML
	    private TableColumn<UltimaVenda, String> colValorVenda;

	    private Timeline relogio;

	    @FXML
	    public void initialize() {

	        graficoVendas7Dias.setLegendVisible(false);
	        graficoVendas7Dias.setAnimated(false);
	        graficoVendas7Dias.setCreateSymbols(true);

	        NumberFormat moeda =
	                NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

	        BigDecimal vendasHoje = Conexao.buscarVendasHoje();
	        lblVendasHoje.setText(moeda.format(vendasHoje));

	        BigDecimal faturamentoMes = Conexao.buscarFaturamentoMes();
	        lblFaturamentoMes.setText(moeda.format(faturamentoMes));

	        int totalEstoque = Conexao.buscarTotalProdutosEstoque();
	        lblProdutosEstoque.setText(String.valueOf(totalEstoque));

	        int totalClientes = Conexao.buscarTotalClientes();
	        lblClientesCadastrados.setText(String.valueOf(totalClientes));

	        carregarGraficoUltimos7Dias();
	        carregarGraficoCategorias(moeda);
	        carregarEstoqueBaixo();
	        carregarUltimasVendas();
	        iniciarRelogio();
	    }

	    private void carregarGraficoUltimos7Dias() {

	        XYChart.Series<String, Number> serie = new XYChart.Series<>();

	        Map<String, BigDecimal> vendas7Dias =
	                Conexao.buscarVendasUltimos7Dias();

	        for (Map.Entry<String, BigDecimal> venda : vendas7Dias.entrySet()) {
	            serie.getData().add(
	                new XYChart.Data<>(
	                    venda.getKey(),
	                    venda.getValue()
	                )
	            );
	        }

	        graficoVendas7Dias.getData().clear();
	        graficoVendas7Dias.getData().add(serie);
	    }

	    private void carregarGraficoCategorias(NumberFormat moeda) {

	        Map<String, BigDecimal> vendasCategoria =
	                Conexao.buscarVendasPorCategoriaMes();

	        ObservableList<PieChart.Data> dadosCategoria =
	                FXCollections.observableArrayList();

	        BigDecimal totalCategorias = BigDecimal.ZERO;

	        for (Map.Entry<String, BigDecimal> categoria : vendasCategoria.entrySet()) {

	            dadosCategoria.add(
	                new PieChart.Data(
	                    categoria.getKey(),
	                    categoria.getValue().doubleValue()
	                )
	            );

	            totalCategorias = totalCategorias.add(categoria.getValue());
	        }

	        graficoVendasCategoria.setData(dadosCategoria);
	        lblTotalVendasCategoria.setText(moeda.format(totalCategorias));

	        double total = totalCategorias.doubleValue();

	        if (total > 0) {

	            double alimentos = vendasCategoria
	                    .getOrDefault("Alimentos", BigDecimal.ZERO)
	                    .doubleValue();

	            double bebidas = vendasCategoria
	                    .getOrDefault("Bebidas", BigDecimal.ZERO)
	                    .doubleValue();

	            double higiene = vendasCategoria
	                    .getOrDefault("Higiene e Limpeza", BigDecimal.ZERO)
	                    .doubleValue();

	            double frios = vendasCategoria
	                    .getOrDefault("Frios e Laticínios", BigDecimal.ZERO)
	                    .doubleValue();

	            double padaria = vendasCategoria
	                    .getOrDefault("Padaria", BigDecimal.ZERO)
	                    .doubleValue();

	            double outros = vendasCategoria
	                    .getOrDefault("Outros", BigDecimal.ZERO)
	                    .doubleValue();

	            lblPctAlimentos.setText(
	                    String.format("%.0f%%", alimentos / total * 100)
	            );

	            lblPctBebidas.setText(
	                    String.format("%.0f%%", bebidas / total * 100)
	            );

	            lblPctHigiene.setText(
	                    String.format("%.0f%%", higiene / total * 100)
	            );

	            lblPctFrios.setText(
	                    String.format("%.0f%%", frios / total * 100)
	            );

	            lblPctPadaria.setText(
	                    String.format("%.0f%%", padaria / total * 100)
	            );

	            lblPctOutros.setText(
	                    String.format("%.0f%%", outros / total * 100)
	            );

	        } else {

	            lblPctAlimentos.setText("0%");
	            lblPctBebidas.setText("0%");
	            lblPctHigiene.setText("0%");
	            lblPctFrios.setText("0%");
	            lblPctPadaria.setText("0%");
	            lblPctOutros.setText("0%");
	        }
	    }

	    private void carregarEstoqueBaixo() {

	        colProdutoEstoque.setCellValueFactory(
	            dado -> new ReadOnlyStringWrapper(
	                dado.getValue().getNome()
	            )
	        );

	        colEstoqueAtual.setCellValueFactory(
	            dado -> new ReadOnlyIntegerWrapper(
	                dado.getValue().getEstoqueAtual()
	            ).asObject()
	        );

	        colEstoqueMinimo.setCellValueFactory(
	            dado -> new ReadOnlyIntegerWrapper(
	                dado.getValue().getEstoqueMinimo()
	            ).asObject()
	        );

	        tabelaEstoqueBaixo.getItems().setAll(
	            Conexao.buscarProdutosEstoqueBaixo()
	        );
	    }

	    private void carregarUltimasVendas() {

	        colProdutoVenda.setCellValueFactory(
	            dados -> new ReadOnlyStringWrapper(
	                dados.getValue().getProduto()
	            )
	        );

	        colClienteVenda.setCellValueFactory(
	            dados -> new ReadOnlyStringWrapper(
	                dados.getValue().getCliente()
	            )
	        );

	        colDataVenda.setCellValueFactory(
	            dados -> new ReadOnlyStringWrapper(
	                dados.getValue().getData()
	            )
	        );

	        colValorVenda.setCellValueFactory(
	            dados -> new ReadOnlyStringWrapper(
	                dados.getValue().getValor()
	            )
	        );

	        tabelaUltimasVendas.getItems().setAll(
	            Conexao.buscarUltimasVendas()
	        );
	    }

	    private void iniciarRelogio() {

	        atualizarDataHora();

	        relogio = new Timeline(
	            new KeyFrame(
	                Duration.seconds(1),
	                evento -> atualizarDataHora()
	            )
	        );

	        relogio.setCycleCount(Timeline.INDEFINITE);
	        relogio.play();

	        painelDashboard.sceneProperty().addListener(
	            (observavel, cenaAntiga, cenaNova) -> {
	                if (cenaNova == null && relogio != null) {
	                    relogio.stop();
	                }
	            }
	        );
	    }

	    private void atualizarDataHora() {

	        LocalDateTime agora = LocalDateTime.now();

	        DateTimeFormatter formatoData =
	                DateTimeFormatter.ofPattern(
	                    "'Hoje é' dd 'de' MMMM 'de' yyyy",
	                    new Locale("pt", "BR")
	                );

	        DateTimeFormatter formatoDiaHora =
	                DateTimeFormatter.ofPattern(
	                    "EEEE, HH:mm",
	                    new Locale("pt", "BR")
	                );

	        lblDataAtual.setText(
	            agora.format(formatoData)
	        );

	        String diaHora = agora.format(formatoDiaHora);

	        diaHora =
	            Character.toUpperCase(diaHora.charAt(0))
	            + diaHora.substring(1);

	        lblDiaSemana.setText(diaHora);
	    }
	}