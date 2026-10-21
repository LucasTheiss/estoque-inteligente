# Cadastro externo de fornecedores (demonstração)

Aplicação independente que mantém um cadastro próprio. Os registros são fictícios.
O Estoque Inteligente consulta a API e não possui comandos para alterar este cadastro.
Na fronteira desta aplicação o cadastro é um ALI; na fronteira do estoque é um AIE.
Não são dois arquivos lógicos diferentes dentro da mesma contagem.

Com JDK 21, execute nesta pasta:

```powershell
java Provedor.java
```

Em outro terminal, a administração do provedor pode manter o arquivo:

```powershell
java Provedor.java salvar F003 'Fornecedor de teste' DEMO-003 contato@teste.example '41-0000-0003'
java Provedor.java excluir F003
```

O servidor lê o arquivo a cada GET. A alteração aparece na próxima consulta do estoque.
Não há cópia persistente no banco do estoque. Realize operações de manutenção uma de cada vez.
A API HTTP é somente leitura; PUT/POST/DELETE recebem 405. A manutenção é um CLI local
para o administrador do provedor, fora da aplicação medida.

No Compose, o serviço `fornecedores` inicia automaticamente. Para manutenção no host,
use os comandos acima nesta pasta. O container recebe o arquivo por volume somente leitura.
Em desenvolvimento local, o endereço padrão é `http://127.0.0.1:8092/fornecedores`.
Para outro provedor compatível, configure `FORNECEDORES_URL` no processo do estoque.

Contrato: array JSON de objetos com cinco strings obrigatórias: `codigo`, `nome`,
`documento`, `email`, `telefone`. Limite do cliente: 256 KiB. Falhas são apresentadas
ao usuário; não se substituem erros por registros simulados silenciosamente.
