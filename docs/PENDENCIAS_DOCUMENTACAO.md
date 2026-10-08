# Correções e esclarecimentos necessários na documentação

## Fonte e escopo

A inspeção de 07/10/2026 encontrou somente `README.md` como documentação
versionada. O commit inicial não contém especificação adicional. O README limita
o projeto a exemplos acadêmicos de padrões e exclui API e persistência concreta.
A solicitação atual pede o sistema completo; falta fornecer e versionar a
especificação que define esse sistema. **Precisa de correção:** reconciliar os
escopos e incluir requisitos e critérios de aceite rastreáveis.

Não é possível declarar conformidade com todos os requisitos enquanto essa fonte
estiver ausente. Os itens abaixo são constatações do código, não requisitos
de negócio aprovados.

| Item | Evidência atual | Correção ou decisão necessária |
| --- | --- | --- |
| Funcionalidades e interface | A aplicação apenas inicia o Spring; não há interface de usuário nem endpoints. | Especificar casos de uso, interface esperada, entradas, saídas e erros. |
| Persistência e atomicidade | Existem três interfaces de repositório e um provedor de conexões SQLite, sem implementações. Saldo e movimentação são gravados em chamadas separadas. | Definir esquema, transações para saldo/histórico, concorrência, recuperação de falhas e operações de consulta. O protótipo não garante atomicidade. |
| Ajuste de estoque | `Produto` admite saldo zero, mas a validação comum de operações rejeita zero. O parâmetro de ajuste representa o saldo final. | Documentar saldo final não negativo, permitir zerar o estoque e manter rejeição de ajuste sem alteração. Corrigir a validação e cobrir com teste. |
| Histórico de ajuste | `Movimentacao` armazena somente magnitude positiva e tipo `AJUSTE`. | Definir como preservar sentido do ajuste e/ou saldos anterior e final; o formato atual não permite reconstruir o saldo pelo histórico. |
| Gatilho de reposição | As três estratégias disparam apenas quando `quantidade < estoqueMinimo`. | Confirmar limite estrito ou inclusivo e documentar comportamento no mínimo e quando o mínimo é zero. |
| Cobertura | `ReposicaoCobertura` usa consumo diário × dias, condicionado ao mínimo, e força pedido de pelo menos uma unidade mesmo com cobertura já atendida. | Definir prioridade entre mínimo e cobertura e se deve haver pedido quando o alvo já está atendido. Não alterar a regra sem essa definição. |
| Ciclo das ordens | Estratégias calculam ordens; o repositório de ordens não possui chamadores. | Definir criação, consulta, recebimento/cancelamento e prevenção de pedidos duplicados, se exigidos. |
| Configuração de alertas | `estoque.alerta-percentual=20` não possui consumidor. | Definir significado, base de cálculo, canais e momento do alerta, se exigido. |
| Acesso e validação | Não há usuários, papéis nem contrato de normalização de SKU. | Especificar necessidade de autenticação/autorização e regras de identidade dos produtos. |

## Verificação do objetivo completo

- Padrões explicitados no README: dois Singletons, três operações Template Method
  e três estratégias estão presentes. Isso não prova implementação de um sistema
  completo.
- Reuso: há repetição de busca, construção de produto e registro nas operações;
  centralizar no template preservando as regras de entrada, saída e ajuste.
- Aceite final: pendente da especificação completa e da implementação/verificação
  dos seus requisitos. Não substituir esse aceite por testes do protótipo.
