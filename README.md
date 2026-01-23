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

### Abrir sessão de votação

```shell
POST /sessaoVotacao
```

### Obter resultado da votação
```shell
GET /pautas/{id}/resultado
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

## 📝 Observações

- A segurança da API foi abstraída, conforme solicitado no desafio  
- A aplicação cliente (mobile) não faz parte deste projeto  
- Dependências externas foram minimizadas para facilitar a execução  

## 👩‍💻 Autora

Desenvolvido por **Ana Canto**  
Desafio Técnico – Sicredi
