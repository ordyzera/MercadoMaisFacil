package application;

import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class TelaControleInicial {

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
    private Label lblClientesCadastrados;

    @FXML
    private Label lblFaturamentoMes;

    @FXML
    private Label lblProdutosEstoque;

    @FXML
    private Label lblVendasHoje;

    @FXML
    private TextField txtPesquisa;
    
    @FXML
    public void initialize() {
        lblVendasHoje.setText("R$ 1.254,80");
        lblFaturamentoMes.setText("R$ 28.430,50");
        lblClientesCadastrados.setText("427");
        
        int totalEstoque = Conexao.buscarTotalProdutosEstoque();

        lblProdutosEstoque.setText(
            String.valueOf(totalEstoque)
        );
    }

}
