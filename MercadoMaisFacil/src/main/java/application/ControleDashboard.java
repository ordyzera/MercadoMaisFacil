package application;

import java.awt.Label;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class ControleDashboard {

    @FXML
    private Button btnCadastro;

    @FXML
    private Button btnCategoria;

    @FXML
    private Button btnClientes;

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnEstoque;

    @FXML
    private Button btnFinanceiro;

    @FXML
    private Button btnFornecedores;

    @FXML
    private Button btnProdutos;

    @FXML
    private Button btnRelatorio;

    @FXML
    private Button btnSair;

    @FXML
    private Button btnVendas;

    @FXML
    private ComboBox<?> comboFuncionario;

    @FXML
    private ImageView imgLogoMercadoMaisFacil;
    
    @FXML
    private Label lblVendasHoje;
    
    @FXML
    private AnchorPane painelDashboard;

    @FXML
    private TextField txtPesquisa;
    
    
    @FXML
    public void initialize() {
        lblVendasHoje.setText("R$ 1.254,80");
    }
}


