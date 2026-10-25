# Provedor externo de ofertas (X02)

Aplicação demonstrativa independente, com dados fictícios. Mantém ofertas comerciais; o cadastro de fornecedores (X01) mantém identidade e contato. São grupos de negócio distintos. O estoque só lê as ofertas via HTTP; não as grava no seu banco.

Com JDK 21, nesta pasta: `java Provedor.java`. GET `http://127.0.0.1:8093/ofertas`; outros métodos recebem 405. O Compose inicia o provedor automaticamente. `OFERTAS_URL` configura o cliente do estoque.

Manutenção feita pelo administrador do sistema externo, uma operação por vez:

```powershell
java Provedor.java salvar 789000000001 F001 EXT-001 'Produto demonstrativo 1' Aurora un 89.90 20 2026-10-24
java Provedor.java excluir F001 EXT-001
```

Chave de manutenção: fornecedor + SKU externo. Nove itens: GTIN, fornecedor, SKU externo, descrição, marca, unidade, preço em reais, disponibilidade na unidade informada e data ISO de atualização. Um registro lógico: oferta. O cliente limita respostas a 256 KiB, valida o contrato e não mascara falhas com dados fictícios locais. A data exibida é a da cotação; não se promete disponibilidade em tempo real.

No estoque: `/ofertas` consulta os nove campos; `/comparacao-precos` cruza GTIN e unidade com produtos ativos da loja. Diferença = cotação − preço cadastrado; percentual = diferença / preço cadastrado × 100. Preço interno zero produz percentual não aplicável. Sem correspondência ou com unidade diferente, a oferta não entra na comparação. Nenhuma dessas consultas altera preço, saldo ou auditoria.

Os GTINs de exemplo correspondem aos primeiros produtos da demonstração. Também é possível cadastrar um produto com esses códigos para demonstrar a comparação.
