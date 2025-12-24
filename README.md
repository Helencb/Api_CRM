# CRM API - Java Spring Boot

API RESTful para gestão de clientes, com autenticação JWT.

## Funcionalidades
- Cadastro, listagem, atualização e exclusão de clientes
- Login com JWT
- Validações e tratamento de erros
- Paginação e ordenação

## Tecnologias 
- Java 25
- Spring Boot
- Spring Data JPA
- Spring Security + JWT
- MySQL
- Lombok

## Como rodar
1. Clonar o repositório
2. Configurar `application.yml` com seu MySQL
3. Rodar `mvn spring-boot:run`
4. Testar endpoints com Postman

## Endpoints principais
- `POST /login` → gerar token
- `POST /clientes` → criar cliente
- `GET /clientes` → listar clientes
- `GET /clientes/{id}` → buscar cliente
- `GET /clientes/paginado` → listar clientes paginados
