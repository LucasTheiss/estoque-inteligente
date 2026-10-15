package br.com.estoque.service;

import java.nio.charset.StandardCharsets;
import java.util.Set;

public final class Campos {
    private Campos() {}

    public static String texto(String valor, String nome, boolean obrigatorio) {
        String result = valor == null ? "" : valor.strip();
        if ((obrigatorio && result.isEmpty()) || result.length() > 200)
            throw new IllegalArgumentException("Preencha " + nome + " com até 200 caracteres.");
        return result;
    }

    public static String unidade(String valor) {
        if (valor == null || !Set.of("un", "kg").contains(valor))
            throw new IllegalArgumentException("Unidade inválida.");
        return valor;
    }

    public static void senha(String valor) {
        if (valor == null
                || valor.length() < 12
                || valor.getBytes(StandardCharsets.UTF_8).length > 72)
            throw new IllegalArgumentException(
                    "A senha deve ter ao menos 12 caracteres e até 72 bytes UTF-8.");
    }

    public static int coordenada(Integer valor) {
        if (valor == null || valor < 0 || valor > 10000)
            throw new IllegalArgumentException("Linha e coluna devem estar entre 0 e 10.000.");
        return valor;
    }
}
