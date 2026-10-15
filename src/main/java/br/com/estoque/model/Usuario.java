package br.com.estoque.model;

public record Usuario(
        Long id, Long lojaId, String nome, String senha, String perfil, int versao, boolean ativo) {
    public boolean superadmin() {
        return perfil.equals("superadmin");
    }
}
