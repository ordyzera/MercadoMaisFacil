package application;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class TelaControleInicial {

    @FXML
    private AnchorPane painelConteudo;

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
    private TextField txtPesquisa;

    @FXML
    public void initialize() {

        carregarTela("dashboard.fxml");

        btnDashboard.setOnAction(
            evento -> carregarTela("dashboard.fxml")
        );
        
        btnProdutos.setOnAction(evento -> carregarTela("produtos.fxml"));
    }

    private void carregarTela(String arquivo) {

        try {

            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/application/" + arquivo)
            );

            Parent tela = loader.load();

            painelConteudo.getChildren().clear();
            painelConteudo.getChildren().add(tela);

            AnchorPane.setTopAnchor(tela, 0.0);
            AnchorPane.setBottomAnchor(tela, 0.0);
            AnchorPane.setLeftAnchor(tela, 0.0);
            AnchorPane.setRightAnchor(tela, 0.0);

        } catch (IOException e) {

            System.out.println(
                "Erro ao carregar a tela: " + arquivo
            );

            e.printStackTrace();
        }
    }
}
