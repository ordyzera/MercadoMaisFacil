package application;

import java.math.BigDecimal;

public class Produtos {

    private int id;
    private String nome;
    private String codigoBarras;
    private String marca;
    private BigDecimal preco;
    private int quantidade;
    private int estoqueMinimo;
    private String unidade;
    private boolean ativo;
    private Integer categoriaId;
    private String categoria;

    public Produtos() {
    }

    public Produtos(
            int id,
            String nome,
            String codigoBarras,
            String marca,
            BigDecimal preco,
            int quantidade,
            int estoqueMinimo,
            String unidade,
            boolean ativo,
            Integer categoriaId,
            String categoria) {

        this.id = id;
        this.nome = nome;
        this.codigoBarras = codigoBarras;
        this.marca = marca;
        this.preco = preco;
        this.quantidade = quantidade;
        this.estoqueMinimo = estoqueMinimo;
        this.unidade = unidade;
        this.ativo = ativo;
        this.categoriaId = categoriaId;
        this.categoria = categoria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public int getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(int estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Integer getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getStatus() {
        return ativo ? "Ativo" : "Inativo";
    }
}
