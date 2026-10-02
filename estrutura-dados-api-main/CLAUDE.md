# CLAUDE.md — Sistema de Gestão para Concessionárias

> Este arquivo é a fonte de verdade do contexto do projeto. Toda decisão relevante
> tomada em conversa deve ser registrada aqui (seção "Decisões"). Claude deve atuar
> como **gerente de projetos / desenvolvedor sênior**: questionar propostas quando
> houver alternativa melhor, apontar riscos e **não concordar automaticamente** com
> o que for proposto.

## 1. Visão do produto

Aplicação de gestão para **concessionárias de veículos**, cobrindo três frentes de negócio:

1. **Venda de veículos** (novos e seminovos);
2. **Assistência técnica autorizada** — agendamento, ordens de serviço (OS), apontamento de mão de obra;
3. **Venda de peças** — balcão e consumo interno pelas OS, com controle de estoque.

Os três módulos compartilham um núcleo comum: clientes, veículos, catálogo de peças, usuários/perfis.

## 2. Stack técnica

| Camada    | Tecnologia                              | Status |
|-----------|------------------------------------------|--------|
| Backend   | Java + Spring Boot (Maven)               | Esqueleto criado (Initializr) |
| Frontend  | Angular (projeto separado, a criar)      | Não iniciado |
| Banco     | PostgreSQL                               | Driver no pom; sem datasource configurado |
| ORM       | Spring Data JPA + Hibernate              | Dependência presente |
| Migrações | Flyway (a adicionar)                     | **Pendente — obrigatório antes da 1ª entidade** |
| Auth      | **Fora do escopo** (decisão 2026-08-11)  | Entidade `Usuario` existe só para autoria das operações |

### Estado atual do repositório (2026-08-11)
- `pom.xml`: Spring Boot **2.7.18**, Java **8**, starters web/data-jpa, PostgreSQL, Lombok 1.18.24, DevTools.
- Código: apenas `com.api.spring.Application` (classe main) e um teste de contexto.
- `application.properties`: só `spring.application.name`. Sem conexão de banco.

## 3. Pontos de discordância levantados pelo Claude

1. **Versões desatualizadas** — RESOLVIDO: José confirmou que Java 8 é exigência do
   ambiente estudantil. Mantemos **Java 8 / Spring Boot 2.7.18** (usar `javax.*`,
   conferir compatibilidade de toda dependência nova). A objeção fica registrada:
   fora do contexto acadêmico, essa stack não deveria ir para produção.
2. **Flyway antes de qualquer entidade**: nunca usar `hibernate.ddl-auto=update` como
   estratégia de schema em um sistema com estoque/financeiro. (Mantido no plano.)
3. **Ordem dos módulos**: núcleo de cadastros primeiro, **oficina antes de vendas** —
   a oficina exercita o fluxo mais complexo e valida o modelo. (Aceito, refletido no plano.)
4. **Escopo do MVP**: financeiro completo (contas a pagar/receber, NF-e) fica **fora**;
   OS e venda geram apenas registro de faturamento simples. (Aceito.)

## 4. Modelo de domínio inicial (rascunho)

Núcleo:
- **Cliente** (PF/PJ, contatos, endereço)
- **Veículo** (chassi/VIN, placa, modelo, ano; pode pertencer ao estoque da loja OU a um cliente)
- **Peça** (código do fabricante, descrição, preço de custo/venda)
- **Estoque** (saldo por peça, movimentações de entrada/saída — nunca editar saldo direto)
- **Usuário / Perfil** (ADMIN, VENDEDOR, MECANICO, ATENDENTE, ESTOQUISTA)

Oficina:
- **Agendamento** (cliente + veículo + data + tipo de serviço)
- **OrdemDeServico** (status: ABERTA → EM_EXECUCAO → AGUARDANDO_PECA → FINALIZADA → FATURADA / CANCELADA)
- **ItemServico** (mão de obra: serviço, horas, valor/hora, mecânico)
- **ItemPeca** (peça consumida na OS → gera saída de estoque)

Vendas:
- **VendaVeiculo** (veículo do estoque, cliente, vendedor, valor, forma de pagamento)
- **VendaPeca** (venda de balcão → gera saída de estoque)

Regras de negócio importantes já identificadas:
- Baixa de estoque acontece por **movimentação**, com validação de saldo, nunca por update direto.
- OS só pode ser finalizada se todas as peças reservadas tiverem sido baixadas.
- Veículo vendido sai do estoque da loja e passa a ser vinculado ao cliente (histórico de OS segue o veículo).

## 5. Etapas de desenvolvimento

- **Etapa 0 — Fundação técnica**: decidir versões (item 3.1), configurar datasource,
  Flyway, estrutura de pacotes, Docker Compose para o PostgreSQL, tratamento global
  de erros, padrão de DTOs.
- **Etapa 1 — Núcleo de cadastros**: Cliente, Veículo, Peça, Usuário (CRUD + validação + migrações).
- **Etapa 2 — Autenticação/autorização**: Spring Security + JWT, perfis de acesso.
- **Etapa 3 — Estoque**: movimentações, saldo, entrada de peças (compra simplificada).
- **Etapa 4 — Oficina**: agendamento, OS completa com itens de serviço e peças, máquina de estados.
- **Etapa 5 — Vendas**: venda de veículos e venda de peças no balcão.
- **Etapa 6 — Frontend Angular**: projeto separado, iniciado após a Etapa 2 (com API real para consumir).
- **Etapa 7 — Qualidade e entrega**: testes de integração (Testcontainers se possível), CI, deploy.

Frontend pode andar em paralelo a partir da Etapa 3, consumindo as APIs já prontas.

## 6. Convenções

- Pacote raiz: `com.api.spring` (avaliar renomear para algo do domínio, ex.: `com.unipar.concessionaria` — decisão pendente).
- Idioma: código e nomes de entidade em **português sem acentos** (confirmado em 2026-08-11).
- Commits pequenos, mensagem em português.
- Toda alteração de schema via migração Flyway (`V<n>__descricao.sql`).

## 7. Decisões registradas

| Data | Decisão | Motivo |
|------|---------|--------|
| 2026-08-11 | Criado este arquivo; plano em etapas aprovado como rascunho | Início do projeto |
| 2026-08-11 | **Java 8 / Spring Boot 2.7.18 mantidos** | Restrição do ambiente estudantil (exigência externa confirmada) |
| 2026-08-11 | Projeto **de disciplina**; desenvolvimento **humano** (Claude atua em planejamento/modelagem/revisão) | Definição do José |
| 2026-08-11 | **Loja única** — sem multi-tenant | Escopo da disciplina |
| 2026-08-11 | **Sem autenticação/login**; `Usuario` só para autoria | Foco no domínio; economiza tempo da disciplina |
| 2026-08-11 | Nomenclatura em **português sem acentos** | Escolha do José |
| 2026-08-11 | Prazo: **um semestre (~16 semanas)**; equipe de **3+ pessoas** | Cronograma do plano baseia-se nisso |
| 2026-08-11 | Criados `docs/PLANO_DESENVOLVIMENTO.md` e `docs/MODELAGEM_CLASSES.md` | Entregáveis do planejamento |

## 8. Documentos do projeto

- [docs/PLANO_DESENVOLVIMENTO.md](docs/PLANO_DESENVOLVIMENTO.md) — etapas, cronograma (16 semanas), divisão da equipe, riscos.
- [docs/MODELAGEM_CLASSES.md](docs/MODELAGEM_CLASSES.md) — todas as classes com justificativa de cada atributo.

## 9. Pendências / perguntas abertas

- [ ] Confirmar data real de início do semestre (o Gantt do plano assume 17/08/2026).
- [ ] Definir se o pacote raiz será renomeado (`com.api.spring` → algo do domínio) antes da Etapa 0.
- [ ] Validar a modelagem com o grupo/professor antes de criar as primeiras migrações.
