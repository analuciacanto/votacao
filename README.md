# 🗳️ Sistema de Votação

Este projeto é uma API REST desenvolvida em **Java com Spring Boot**, cujo objetivo é gerenciar **pautas**, **sessões de votação** e **votos de associados**, simulando o funcionamento de votações em assembleias no contexto do cooperativismo.

O sistema foi desenvolvido como parte de um **desafio técnico**, com foco em **simplicidade**, **organização**, **boas práticas** e **comunicação entre backend e aplicativo mobile**.

## 📌 Funcionalidades

- 📄 Cadastro de pautas  
- ⏱️ Abertura de sessão de votação  
  - Tempo configurável  
  - Padrão de 1 minuto caso não informado  
- 🗳️ Registro de votos  
  - Votos válidos: `SIM` ou `NAO`  
  - Cada associado pode votar apenas uma vez por pauta  
- 📊 Apuração e resultado da votação  
- 💾 Persistência de dados (não são perdidos após restart)
- - 📈 Observabilidade
- Logs estruturados (`INFO`, `WARN`, `ERROR`)
- Métricas de erros e tempo de execução de métodos via Actuator + Micrometer

## 🚀 Tecnologias Utilizadas

- Java 17  
- Spring Boot  
  - Spring Web  
  - Spring Data JPA  
  - Spring Validation  
- Hibernate  
- Lombok  
- Banco de dados H2 (em memória)  
- Maven  
- JUnit 5 e Mockito
- Spring Boot Actuator
- Micrometer
  
## 🧱 Arquitetura e Organização

O projeto segue uma arquitetura em **camadas**, priorizando organização e manutenibilidade:

```

br.com.softdesign.votacao
├── controller
├── domain
├── dto
├── repository
├── service
├── exception
└── integration / unit (testes)
```


### Conceitos aplicados

- Separação de responsabilidades  
- Uso de DTOs para comunicação externa  
- Tratamento centralizado de exceções  
- Testes unitários e de integração  
- Código limpo e legível  



## 🔗 Endpoints Principais (exemplos)

### Criar pauta

```shell
POST /pautas
```

**Body de exemplo:**

```json
{
  "titulo": "Implantação de novo sistema de votação",
  "descricao": "Discussão sobre a implantação do novo sistema eletrônico para as assembleias."
}
```

**Resposta esperada:**

```json

{
  "id": 1,
  "titulo": "Implantação de novo sistema de votação",
  "descricao": "Discussão sobre a implantação do novo sistema eletrônico para as assembleias.",
  "dataCriacao": "2026-01-23T12:00:00"
}
```

### Abrir sessão de votação

```shell
POST /sessao-votacao
```

**Body de exemplo:**

```json
{
  "pautaId": 150,
  "duracao": 5
}
```

**Resposta esperada:**

```json
{
	"id": 88,
	"pautaId": 150,
	"dataInicio": "2026-01-23T15:05:13.73530204",
	"dataFim": "2026-01-23T15:15:13.73530204",
	"duracao": 10,
	"sessaoAberta": true
}    
```


### Votar na sessão
```shell
POST /votos
```

**Body de exemplo:**

```json
{
  "sessaoVotacaoId": 88,
  "cpf": "02345688901",
  "voto": "NAO"
}

```

**Resposta esperada:**

```json
{
	"cpf": "02345688901",
	"voto": "NAO"
}
```

### Obter resultado da votação
```shell
GET /resultado/{ID}
```

**Resposta esperada:**

```json
{
	"pautaId": 150,
	"tituloPauta": "Implantação de novo sistema de votação",
	"totalSim": 1,
	"totalNao": 1,
	"resultado": "EMPATE"
} 
```

> 📌 O versionamento da API não foi implementado nesta versão.  
> Como evolução futura, está prevista a adoção de versionamento via URL (`/api/v1`), permitindo manutenção e evolução controlada da API.

---

## ⚠️ Regras de Negócio

- A sessão de votação aceita votos apenas enquanto estiver aberta  
- Um associado pode votar somente uma vez por pauta  
- Não é permitido votar em pautas inexistentes  
- Não é permitido votar em sessões encerradas  
- Erros de regra de negócio são tratados com exceções específicas  


## 🧪 Testes

O projeto conta com:

- Testes unitários  
- Testes de integração  
- Cobertura das principais regras de negócio  
- Validação de cenários de erro  

Os testes garantem maior confiabilidade e facilitam futuras evoluções.

### Executar todos os testes

```bash
mvn test
```

## 📈 Logs e Métricas

### 📝 Logs
A aplicação utiliza **SLF4J + Logback** para registro de logs.  
Os logs registram informações importantes sobre operações críticas, como:

- Criação de pautas
- Abertura de sessões de votação
- Registro de votos
- Regras de negócio violadas (ex.: voto duplicado, sessão fechada)

Níveis utilizados:

- `INFO` → operações bem-sucedidas
- `WARN` → violações de regras de negócio
- `ERROR` → falhas inesperadas do sistema
- 
### 📊 Métricas
A aplicação possui métricas implementadas usando **Spring Boot Actuator + Micrometer**.  
O monitoramento é feito principalmente via um **Aspect** que intercepta todos os métodos dos serviços, permitindo:

1. **Contadores de erros por tipo de exceção**  
   Cada vez que um método de serviço lança uma exceção, o contador correspondente é incrementado.  
   Exemplos de métricas geradas:

  - `service.erros.SessaoVotacaoInvalidaException` → contagem de sessões inválidas
  - `service.erros.VotoInvalidoException` → contagem de votos inválidos

2. **Métricas por método**  
   O Aspect permite facilmente medir o tempo de execução de qualquer método do serviço.  
   Exemplo de métrica de tempo exposta via Actuator:

```bash
GET http://localhost:8080/actuator/metrics/pautas.criar.tempo
```

Outras métricas podem ser adicionadas de forma similar, como contagem de chamadas ou timers por método, bastando registrar com `MeterRegistry`.

Endpoints disponíveis:

- `/actuator/metrics` → métricas detalhadas de toda a aplicação
- `/actuator/health` → status da aplicação e do banco de dados

Essas métricas permitem monitorar tanto **a saúde da aplicação** quanto **a performance e confiabilidade das operações críticas**, oferecendo visão completa para ambientes de produção.


### 🔍 Consultando métricas

Você pode consultar as métricas diretamente via `curl` ou no navegador:

```bash
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/metrics/pautas.criar.tempo
curl http://localhost:8080/actuator/health
```

## 🧠 Decisões de Projeto

- Solução simples, evitando overengineering  
- DTOs para evitar acoplamento com o domínio  
- Exceções específicas para regras de negócio  
- Persistência local visando facilitar a execução do avaliador  
- Foco na comunicação clara via JSON com o cliente mobile


## ▶️ Como Executar o Projeto

### Pré-requisitos

- Java 17 ou superior  
- Maven  

### Executando a aplicação

```bash
mvn spring-boot:run
```

A aplicação estará disponível em:

```bash
http://localhost:8080
```

# Tarefas Futuras / Bônus

## Melhorias futuras / Outros cenários

Além das tarefas bônus, eu também faria melhorias no código para cobrir **cenários que ainda não estão totalmente tratados**, por exemplo:  

- Funcionamento com **várias sessões de votação abertas ao mesmo tempo**  
- Evitar problemas com **associados duplicados** ou votos repetidos  
- Tratar **tempos de sessão diferentes** e possíveis inconsistências de contagem  
- Garantir que a API continue funcionando mesmo com **cenários inesperados**  

Essas melhorias ajudariam a tornar a aplicação mais **robusta e confiável**, preparada para situações reais que podem acontecer em uma assembleia com muitos associados.

## Tarefa Bônus 1 - Integração com sistemas externos

Para essa tarefa, eu chamaria o serviço externo passando o CPF do associado e veria se ele pode votar ou não.  
Se o CPF for inválido, a API retorna 404, e se for válido, retorna `ABLE_TO_VOTE` ou `UNABLE_TO_VOTE`.  
Antes de registrar o voto, eu checaria isso. Para testar, daria pra usar CPFs gerados e até fazer mock do serviço.

## Tarefa Bônus 2 - Performance

Eu tentaria garantir que a API aguentasse muitos votos sem travar.  
Algumas ideias que eu teria:
- Usar gravação em lote ou otimizar o banco
- Indexar colunas importantes para a contagem de votos
- Fazer testes de carga com ferramentas tipo JMeter ou Gatling
Assim consigo ver se a aplicação continua rápida mesmo com muito voto.

## Tarefa Bônus 3 - Versionamento da API

Eu faria versionamento da API usando a URL, tipo `/api/v1/...` e `/api/v2/...` quando precisar mudar algo que quebre compatibilidade.  
Também daria para fazer por header, mas o mais simples é pela URL mesmo.  
A ideia é conseguir evoluir a API sem quebrar quem já está usando.

## 📝 Observações

- A segurança da API foi abstraída, conforme solicitado no desafio  
- A aplicação cliente (mobile) não faz parte deste projeto  
- Dependências externas foram minimizadas para facilitar a execução  

## 👩‍💻 Autora

Desenvolvido por **Ana Canto**  
Desafio Técnico – Sicredi
