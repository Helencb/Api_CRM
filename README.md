# 🚀 CRM API – Java Spring Boot

API RESTful de CRM (Customer Relationship Management) desenvolvida em Java com Spring Boot, utilizando autenticação JWT, Spring Security, JPA/Hibernate, MySQL e testes unitários.
O projeto segue boas práticas, arquitetura organizada e foco em código limpo e manutenível.

## 📌 Funcionalidades

CRUD completo de clientes: 

- Cadastro

- Listagem

- Busca por ID

- Atualização

- Exclusão

- Autenticação e autorização com JWT

- Validações de dados

- Tratamento global de exceções

- Paginação e ordenação de resultados

- Testes unitários dos serviços


## 🛠️ Tecnologias Utilizadas

 - Java

 - Spring Boot

 - Spring Web

 - Spring Data JPA (Hibernate)

 - Spring Security

 - JWT (JSON Web Token)

 - MySQL

 - Lombok

 - JUnit 5

 - Mockito

 - Maven


## 🔐 Segurança

* Autenticação baseada em JWT

* Endpoints protegidos com Spring Security

* Senhas criptografadas utilizando BCrypt

* Token enviado via Header HTTP:

* Authorization: Bearer <TOKEN>


## 🧪 Testes Unitários

O projeto conta com testes unitários utilizando JUnit 5 e Mockito, garantindo:

* Funcionamento correto da camada de serviços

* Validação das regras de negócio

* Tratamento adequado de exceções

* Testes de listagem, paginação e busca de dados


## ▶️ Como Executar o Projeto

1. Clonar o repositório
2. Configurar `application.yml` com seu MySQL
3. Rodar `mvn spring-boot:run`
4. Testar endpoints com Postman


## 📡 Endpoints Principais:

- `POST /login` → gerar token
- `POST /clientes` → criar cliente
- `GET /clientes` → listar clientes
- `GET /clientes/{id}` → buscar cliente
- `GET /clientes/paginado` → listar clientes paginados


## 🧭 Próximas Melhorias (Roadmap)

1. Documentação da API com Swagger / OpenAPI

2. Testes de integração

3. Dockerização da aplicação (API + MySQL)

4. Deploy em ambiente cloud



## 👩‍💻 Desenvolvido por

Helen Cristina Batista
Desenvolvedora Back-end Java | Spring Boot
Estudante de Análise e Desenvolvimento de Sistemas

## 🔗 LinkedIn:
https://www.linkedin.com/in/hcbatista/

⭐ Se este projeto te ajudou ou chamou atenção, considere deixar uma estrela no repositório!
