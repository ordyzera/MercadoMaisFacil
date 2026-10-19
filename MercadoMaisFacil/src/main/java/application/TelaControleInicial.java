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
        // Carrega a tela e marca o botão inicial ao abrir o sistema
        carregarTela("dashboard.fxml");
        marcarBotaoAtivo(btnDashboard);

        // Ações dos cliques nos botões
        btnDashboard.setOnAction(evento -> {
            carregarTela("dashboard.fxml");
            marcarBotaoAtivo(btnDashboard);
        });
        
        btnProdutos.setOnAction(evento -> {
            carregarTela("produtos.fxml");
            marcarBotaoAtivo(btnProdutos);
        });

        // Exemplo para a futura tela de categorias:
        btnCategoria.setOnAction(evento -> {
            // carregarTela("categorias.fxml"); // Descomente quando a tela existir
            marcarBotaoAtivo(btnCategoria);
        });
        
        // Repita o padrão para os restantes botões à medida que for criando as telas...
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
    
     private void marcarBotaoAtivo(Button botaoClicado) {
        
        Button[] botoesMenu = {
            btnDashboard, btnProdutos, btnCategoria, btnEstoque,
            btnFornecedores, btnClientes, btnVendas, btnCadastro,
            btnFinanceiro, btnRelatorio
        };
        
        // 2. Removemos a classe "botao-menu-ativo" de todos eles
        for (Button btn : botoesMenu) {
            btn.getStyleClass().remove("botao-menu-ativo");
        }
        
        // 3. Adicionamos a classe apenas ao botão que foi clicado
        if (!botaoClicado.getStyleClass().contains("botao-menu-ativo")) {
            botaoClicado.getStyleClass().add("botao-menu-ativo");
        }
    }
}
