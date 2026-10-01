package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ControleProdutos {

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

    @FXML
    private TableColumn<?, ?> colAcoes;

    @FXML
    private TableColumn<?, ?> colCategoria;

    @FXML
    private TableColumn<?, ?> colCodigo;

    @FXML
    private TableColumn<?, ?> colEstoque;

    @FXML
    private TableColumn<?, ?> colMarca;

    @FXML
    private TableColumn<?, ?> colPreco;

    @FXML
    private TableColumn<?, ?> colProduto;

    @FXML
    private TableColumn<?, ?> colStatus;

    @FXML
    private ComboBox<?> comboAllCategorias;

    @FXML
    private ComboBox<?> comboAllStatus;

    @FXML
    private ComboBox<?> comboUnidade;

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
    private TableView<?> tableProdutos;

    @FXML
    private ComboBox<?> txtCategoria;

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

}
