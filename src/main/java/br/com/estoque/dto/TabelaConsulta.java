package br.com.estoque.dto;

import java.util.List;

/** Resultado tabular com ordem explícita das colunas. */
public record TabelaConsulta(String titulo, List<String> colunas, List<List<String>> linhas) {}
