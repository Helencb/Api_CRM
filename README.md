# 🚀 CRM API — Sistema de Gestão de Relacionamento com Clientes

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

API RESTful robusta desenvolvida para **gestão de relacionamento com clientes (CRM)**, com foco em **segurança, organização, escalabilidade e boas práticas de engenharia de software**.

O sistema gerencia **hierarquias entre Gerentes e Vendedores**, controla o **fluxo completo de Vendas** (criação, aprovação e cancelamento) e disponibiliza **métricas consolidadas em dashboards**, seguindo princípios como **SOLID, DRY e Clean Code**.

---

## 🧠 Visão Geral

- Arquitetura em camadas
- Autenticação e autorização com **JWT**
- Controle de acesso baseado em papéis (**RBAC**)
- Regras de negócio bem definidas
- Código testado e versionamento de banco automatizado

---

## 🛠️ Stack Tecnológica

- **Linguagem:** Java 17
- **Framework:** Spring Boot 3.2
- **Segurança:** Spring Security 6 + JWT
- **Banco de Dados:** MariaDB / MySQL
  > Compatível com PostgreSQL
- **Migração de Banco:** Flyway
- **ORM:** JPA / Hibernate
- **Mapeamento:** MapStruct
- **Testes:** JUnit 5 & Mockito
- **Build:** Maven
- **Documentação:** Swagger / OpenAPI *(previsto)*

---

## ✨ Funcionalidades Principais

### 🔐 Autenticação & Segurança (RBAC)

- Login com geração de **Token JWT**
- Controle de acesso baseado em papéis:
    - **MANAGER**
        - Acesso total ao sistema
        - Criação de vendedores
        - Visualização de dashboards globais
    - **SELLER**
        - Acesso restrito aos seus próprios clientes e vendas

---

### 👥 Gestão de Usuários

- Cadastro de vendedores vinculados a um gerente
- Gestão completa de clientes
- Validação de permissões por perfil

---

### 💰 Gestão de Vendas

- Criação de vendas (**PENDING**)
- Finalização de vendas (**COMPLETED**)
- Cancelamento com motivo obrigatório (**CANCELED**)
- Aplicação rigorosa de regras de negócio
  > Ex: vendedor só pode visualizar suas próprias vendas

---

### 📊 Dashboard & Analytics

- Visão consolidada para gerentes:
    - Total de vendas
    - Receita
    - Ticket médio
- Desempenho individual por vendedor

---

## 🏗️ Arquitetura & Padrões

O projeto segue uma **arquitetura em camadas**, garantindo alta manutenibilidade e testabilidade:

- **Controller Layer**
    - Endpoints REST
    - Validação de entrada com `@Valid`
- **Service Layer**
    - Regras de negócio
    - Controle transacional com `@Transactional`
- **Repository Layer**
    - Persistência com Spring Data JPA
- **Mapper Layer**
    - Conversão entre DTOs e Entidades com MapStruct
- **Exception Handling**
    - Tratamento global de erros
    - Respostas padronizadas em JSON (`ApiError`)

---

## 🚀 Como Executar o Projeto

### 📌 Pré-requisitos

- Java JDK 17+
- Maven
- MariaDB ou Docker

---

### 1️⃣ Configuração do Banco de Dados

Crie o banco (ex: `crm_db`) e configure:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/crm_db
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

---

### 2️⃣ Configuração do JWT

- **Defina a chave secreta e o tempo de expiração do token no arquivo
application.properties:**

```properties 
api.security.token.secret=SUA_CHAVE_SECRETA_BASE64_MUITO_LONGA
api.security.token.expiration=36000000
```

### 3️⃣ Compilar e Executar

- **Execute os comandos abaixo no terminal:**

```properties
mvn clean install
mvn spring-boot:run
```

### 📍 A API estará disponível em:

```properties
http://localhost:8080
```


## 📡 Endpoints Principais

| Método | Endpoint                   | Descrição                         | Role |
|--------|----------------------------|-----------------------------------|------|
| POST   | `/api/auth/login`          | Autenticação e geração do JWT     | Público |
| GET    | `/api/clients`             | Lista clientes                    | Auth |
| POST   | `/api/sales`               | Cria nova venda                   | MANAGER, SELLER |
| PUT    | `/api/sales/{id}/complete` | Finaliza venda                    | MANAGER |
| POST   | `/api/sellers`             | Cria vendedor                     | MANAGER |
| GET    | `/dashboard/summary`       | Dashboard consolidado             | MANAGER |

---

## 🧪 Testes

- **O projeto possui testes unitários para Controllers e Services, garantindo a confiabilidade do código.**

---

- **Para executar os testes:**

```properties
mvn test
```
---

## 🤝 Contribuição

### Contribuições são bem-vindas! 

- **Faça um fork do projeto**


- **Crie sua branch:**

```properties
git checkout -b feature/minha-feature
```

- **Commit suas mudanças:**

```properties
git commit -m "Minha nova feature"
```

- **Push para a branch:**
```properties
git push origin feature/minha-feature
```

- **Abra um Pull Request**

---

### 👩‍💻 Autora

**Helen Cristina Batista
Desenvolvedora Back-end Java**