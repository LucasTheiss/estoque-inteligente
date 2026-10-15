# Demonstração local — execução e roteiro

## Iniciar tudo

Abra o Docker Desktop. Na raiz do projeto, execute:

~~~powershell
powershell -ExecutionPolicy Bypass -File scripts/demo.ps1
~~~

Equivalente:

~~~powershell
docker compose --env-file .env.demo up -d --build --wait
~~~

Abra http://localhost:8080/login. O comando compila o projeto com Java 21 dentro do Docker, inicia PostgreSQL, MQTT, aplicação e NGINX. Flyway cria o esquema e a aplicação popula automaticamente o banco. Não é necessário instalar Java/Maven no computador nem cadastrar lojas, usuários ou produtos manualmente. Não é necessário token de API externa; o simulador IoT usa o broker local.

## Credenciais públicas padronizadas do MVP

Estas são as credenciais de demonstração definidas em .env.demo, publicadas aqui por autorização do responsável pelo projeto.

| Acesso | Usuário | Senha | Destino |
| --- | --- | --- | --- |
| Administração de cada loja | admin | Loja-demo-local-2026! | calcados, granel ou essencial |
| Gerência de cada loja | gerente | Loja-demo-local-2026! | calcados, granel ou essencial |
| Estoque de cada loja | estoquista | Loja-demo-local-2026! | calcados, granel ou essencial |
| Vendas de cada loja | vendedor | Loja-demo-local-2026! | calcados, granel ou essencial |
| Administração global | superadmin | Global-demo-local-2026! | Selecionar Administração global |
| PostgreSQL | estoque | Banco-demo-local-2026! | localhost:5433, banco estoque |
| MQTT | estoque-demo | Mqtt-demo-local-2026! | localhost:1883 |

Dentro do Docker, PostgreSQL usa db:5432 e MQTT usa mqtt:1883. As portas acima são para ferramentas no computador, como DBeaver/pgAdmin e cliente MQTT. Para padronizar a apresentação, use sempre --env-file .env.demo. A autenticação continua funcionando normalmente; ser uma senha pública de demonstração não exige remover login, perfis ou validações.

## Dados prontos na primeira inicialização

| Loja | Recursos | Produtos iniciais |
| --- | --- | --- |
| essencial — Almoxarifado Base | Estoque por unidade; sem IoT, relatórios/ocupação ou fracionados | Caixa organizadora, Papel A4, Fita adesiva, Envelope kraft |
| calcados — Calçados Aurora | Estoque por unidade, IoT e relatórios/ocupação | Tênis Horizon, Tênis Essential, Bota Urban, Sandália Serena |
| granel — Mercado Raiz | Estoque por kg, IoT, relatórios/ocupação e fracionados | Café especial, Castanha de caju, Arroz integral, Granola natural |

Cada loja recebe quatro usuários/perfis, quatro modelos e produtos, oito posições (A1–A4 e B1–B4), quatro vínculos produto/posição com saldos 6, 9, 12 e 15, quatro entradas no histórico, auditoria de provisionamento e permissões por perfil. Total inicial: três lojas, 13 usuários contando superadmin, 12 produtos, 24 posições, 12 vínculos e 12 entradas. Posições B1–B4 começam vazias. Os produtos de granel usam kg mesmo quando o saldo inicial é um número inteiro; aceitam movimentações fracionadas.

Sinalização IoT usa esp32-demo nas lojas habilitadas. A balança virtual balanca-demo publica 1,440 kg bruto e 0,015 kg de tara; resultado líquido 1,425 kg. O simulador confirma comandos de LED. Trata-se de simulação via MQTT real, sem necessidade de ESP32 ou balança física.

Reiniciar preserva registros e senhas. A seed não apaga nem repõe automaticamente lojas já existentes. Não use down -v para reiniciar: ele remove o banco. Se houver volume antigo com outra senha, trocar .env.demo não altera a senha daquele PostgreSQL; é necessário usar a credencial correspondente ou realizar uma troca deliberada. Nenhum volume existente foi apagado nesta entrega.

## Roteiro sem cadastro preparatório

1. Entrar em essencial como admin e mostrar produtos, posições, vínculos e saldos já presentes.
2. Entrar em calcados como admin. Mostrar tamanhos, mapa, posições, histórico e exportação CSV/PDF.
3. Usar CALCADOS-1 em A1 para demonstrar entrada, saída e ajuste. O saldo inicial é 6 em um banco novo; informar quantidades válidas considerando o saldo visível. Demonstrar estorno de uma entrada/saída recente.
4. Entrar em granel como admin. Usar GRANEL-1 em A1 para saída de 0.125 kg, mostrando precisão fracionada. Na tela Movimentar, demonstrar a leitura de peso da balanca-demo; na busca assistida/mapa, demonstrar sinalização.
5. Comparar menus e cadastro de unidade entre essencial e granel, com o mesmo perfil. Explicar núcleo comum e opcionais iot, enterprise e fractional persistidos em tenants.
6. Entrar como vendedor para mostrar consulta/saída e restrições de cadastro. Voltar a admin para usuários e permissões.
7. Administração global: mostrar as três lojas e suas configurações.
8. Para demonstrar criação/edição/exclusão, cadastrar durante o vídeo registros descartáveis de código inédito; isso é a operação CRUD demonstrada, não preparação obrigatória. Produtos/posições com saldo não podem ser excluídos. Não desativar o próprio usuário nem o último admin.
9. Para provar persistência, manter um registro criado no vídeo, reiniciar somente app, aguardar /health e entrar novamente. O registro deve continuar presente.

~~~powershell
docker compose --env-file .env.demo restart app
~~~

O banco populado permite iniciar a demonstração sem cadastro preparatório. Em 14/10/2026, foram concluídos os CRUDs de Modelos, Vínculos e Lojas e os exemplos da Opção 01. Consulte [OPCAO_01.md](OPCAO_01.md) para arquivos, permissões e roteiro dos padrões. Demonstre exclusão/desativação com registros descartáveis; o provisionamento não reativa lojas existentes.

## Conferir banco e testes

~~~powershell
docker compose --env-file .env.demo exec db psql -U estoque -d estoque
~~~

No psql:

~~~sql
SELECT slug, iot, enterprise, fractional FROM tenants ORDER BY slug;
SELECT t.slug,
  (SELECT count(*) FROM users u WHERE u.tenant_id=t.id AND u.active) AS usuarios,
  (SELECT count(*) FROM products p WHERE p.tenant_id=t.id AND p.active) AS produtos,
  (SELECT count(*) FROM positions s WHERE s.tenant_id=t.id AND s.active) AS posicoes,
  (SELECT count(*) FROM occupancy o WHERE o.tenant_id=t.id) AS vinculos
FROM tenants t ORDER BY t.slug;
SELECT tablename FROM pg_tables WHERE schemaname='public' ORDER BY tablename;
~~~

Para testes completos com PostgreSQL/MQTT reais:

~~~powershell
powershell -ExecutionPolicy Bypass -File scripts/test.ps1
~~~

## Por que a compilação falhou anteriormente

A execução restrita no Windows recebeu java.nio.file.AccessDeniedException ao resolver o caminho de snakeyaml-2.4.jar durante o fechamento do arquivo pelo compilador Java. O processo não chegou à fase de testes. Não era evidência de erro de sintaxe no projeto ou de dependência corrompida.

O mesmo projeto compilou e passou nos testes fora desse sandbox. No Docker, o build usa Java 21 e um cache Maven próprio, sem depender do caminho de cache Windows que estava bloqueado. A solução operacional é executar scripts/demo.ps1 e scripts/test.ps1 no terminal normal com Docker Desktop disponível. Não foi necessário trocar a versão do SnakeYAML ou remover validações do aplicativo.
