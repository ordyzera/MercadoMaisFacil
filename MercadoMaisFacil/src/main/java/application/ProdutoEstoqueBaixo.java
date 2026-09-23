package application;

public class ProdutoEstoqueBaixo {

    private String nome;
    private int estoqueAtual;
    private int estoqueMinimo;

    public ProdutoEstoqueBaixo(
            String nome,
            int estoqueAtual,
            int estoqueMinimo) {

        this.nome = nome;
        this.estoqueAtual = estoqueAtual;
        this.estoqueMinimo = estoqueMinimo;
    }

    public String getNome() {
        return nome;
    }

    public int getEstoqueAtual() {
        return estoqueAtual;
    }

    public int getEstoqueMinimo() {
        return estoqueMinimo;
    }
}