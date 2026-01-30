# 🚀 CRM API — Sistema de Gestão de Vendas & Relacionamento

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-brightgreen?style=for-the-badge&logo=spring)
![Spring Security](https://img.shields.io/badge/Spring_Security-RBAC_JWT-red?style=for-the-badge&logo=springsecurity)
![H2 Database](https://img.shields.io/badge/Database-H2_(In_Memory)-blue?style=for-the-badge&logo=h2)
![Swagger](https://img.shields.io/badge/Docs-Swagger_UI-85ea2d?style=for-the-badge&logo=swagger)

Uma API RESTful robusta projetada para simular um ambiente corporativo de **Gestão de Relacionamento com Clientes (CRM)**. O projeto foca em padrões de arquitetura limpa, segurança avançada e integridade de dados transacionais.

---

## ⚠️ Nota de Infraestrutura (Banco em Memória)

> **IMPORTANTE:** Esta aplicação está configurada para rodar com **H2 Database em memória (`mem`)**.
> 
> *   **Volatilidade:** Todos os dados criados (clientes, vendas, usuários) serão perdidos ao reiniciar a aplicação.
> *   **Bootstrapper:** Um usuário administrador padrão é recriado automaticamente a cada inicialização para garantir o acesso (veja [Credenciais](#-acesso-rápido-credenciais)).

---

## 🏛️ Arquitetura do Projeto

O projeto segue uma arquitetura em camadas bem definida para garantir a separação de responsabilidades e facilidade de manutenção:

```bash
src/main/java/com/helen/api_crm
├── 🔐 auth           # Autenticação, Login e Gestão de Usuários Base
├── 👥 clients        # Gestão de Carteira de Clientes
├── ⚙️ common         # Utilitários, Enums e Entidades Base (Audit)
├── 📊 dashboard      # Agregação de dados para relatórios gerenciais
├── ⚠️ exception      # Tratamento global de erros (ControllerAdvice)
├── 👔 manager        # Regras específicas para Gerentes
├── 📦 product        # Catálogo de Produtos e Estoque
├── 🔄 refreshToken   # Lógica de renovação de sessão JWT
├── 💰 sale           # Core do negócio: Fluxo de vendas e itens
├── 🛡️ security       # Configurações do Spring Security, Filtros JWT e CORS
└── 🧑‍💼 seller         # Regras específicas para Vendedores
```

### Destaques Técnicos
*   **Concorrência Segura:** Utilização de `PESSIMISTIC_WRITE` (Lock de banco de dados) ao finalizar vendas. Isso impede que dois vendedores vendam o último item do estoque simultaneamente ("Overselling").
*   **DTOs & Mappers:** Uso extensivo de **MapStruct** para conversão performática entre Entidades JPA e DTOs, evitando exposição do modelo de domínio.
*   **Audit:** Entidades base (`BaseEntity`) registram automaticamente datas de criação e atualização.

---

## 🛠️ Stack Tecnológica

| Componente | Tecnologia | Uso |
| :--- | :--- | :--- |
| **Linguagem** | Java 17 | Core da aplicação |
| **Framework** | Spring Boot 3.2 | Injeção de dependência e Web Server |
| **Segurança** | Spring Security 6 | Autenticação e Autorização |
| **Token** | JWT (JJWT) | Stateless Authentication |
| **Banco de Dados** | H2 Database | Persistência em memória (Modo MySQL) |
| **ORM** | Hibernate / JPA | Mapeamento Objeto-Relacional |
| **Mapper** | MapStruct | Conversão Entidade <-> DTO |
| **Docs** | SpringDoc OpenAPI | Documentação Swagger automática |
| **Testes** | JUnit 5 & Mockito | Testes Unitários e de Integração |

---

## ✨ Funcionalidades e Regras de Negócio

### 1. Hierarquia e Permissões (RBAC)
O sistema possui dois níveis de acesso rígidos:
*   **MANAGER (Gerente):**
    *   Pode criar Vendedores.
    *   Pode ver dashboard global (faturamento da empresa).
    *   Pode finalizar ou cancelar vendas.
    *   Pode reatribuir clientes.
*   **SELLER (Vendedor):**
    *   Pode cadastrar clientes (automaticamente vinculados a ele).
    *   Pode ver apenas seus próprios clientes e vendas.
    *   Pode criar pedidos de venda, mas não pode finalizá-los (apenas status `PENDING`).

### 2. Fluxo de Venda (Máquina de Estados)
A venda passa por validações estritas de estoque e estado:

1.  **Criação (`POST /sales`):**
    *   Status inicial: `PENDING`.
    *   Valida se há estoque suficiente *no momento do pedido*.
    *   Não baixa o estoque ainda (reserva lógica).
2.  **Finalização (`PUT /sales/{id}/complete`):**
    *   **Apenas Gerentes.**
    *   Aplica **Lock Pessimista** no banco.
    *   Revalida o estoque e efetua a baixa real.
    *   Muda status para `COMPLETED`.
3.  **Cancelamento (`PUT /sales/{id}/cancel`):**
    *   **Apenas Gerentes.**
    *   Exige motivo do cancelamento.
    *   Se a venda já estava `COMPLETED`, o sistema **estorna automaticamente** a quantidade dos itens para o estoque.

---

## 🚀 Guia de Instalação e Execução

### Pré-requisitos
*   Java JDK 17+
*   Git

### Passo a Passo

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/helen-cristina/api-crm.git
    cd api-crm
    ```

2.  **Compile e Execute (Via Wrapper):**
    *   *Linux/Mac:*
        ```bash
        ./mvnw clean spring-boot:run
        ```
    *   *Windows:*
        ```cmd
        mvnw.cmd clean spring-boot:run
        ```

3.  **Acesse a Aplicação:**
    *   API Base: `http://localhost:8080`
    *   Documentação Swagger: `http://localhost:8080/swagger-ui/index.html`
    *   Console H2: `http://localhost:8080/h2-console`

---

## 🔑 Acesso Rápido (Credenciais)

Ao iniciar, o sistema cria automaticamente um Super Usuário:

| Role | Email | Senha |
| :--- | :--- | :--- |
| **MANAGER** | `admin@crm.com` | `admin` |

> **Dica:** Utilize este usuário para criar vendedores e produtos iniciais via Swagger.

---

## 📡 Exemplos de Uso (JSON)

### 1. Autenticação (Login)
**POST** `/api/auth/login`
```json
{
  "email": "admin@crm.com",
  "password": "admin"
}
```
*Copie o `token` da resposta para usar nos headers: `Authorization: Bearer <token>`.*

### 2. Criar Produto (Gerente)
**POST** `/api/products`
```json
{
  "name": "Notebook Gamer",
  "description": "i7 16GB RAM RTX 3060",
  "price": 4500.00,
  "stockQuantity": 10
}
```

### 3. Criar Venda (Vendedor/Gerente)
**POST** `/api/sales`
```json
{
  "clientId": 1,
  "sellerId": 1,
  "description": "Venda de equipamento TI",
  "paymentMethod": "CREDIT_CARD",
  "discount": 100.00,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

---

## ⚙️ Configuração (Environment Variables)

O arquivo `application.properties` está configurado com defaults seguros para desenvolvimento. Para customizar em tempo de execução:

| Chave | Descrição | Default |
| :--- | :--- | :--- |
| `JWT_SECRET` | Chave de assinatura do Token (Base64) | *(Default interno)* |
| `CORS_ALLOWED_ORIGINS` | Domains permitidos (Frontend) | `http://localhost:3000,*` |
| `server.port` | Porta do servidor | `8080` |

**Exemplo de execução customizada:**
```bash
java -jar target/api-crm.jar --server.port=9090 --JWT_SECRET=MinhaChaveSuperSecreta
```

---

## 🧪 Testes

O projeto conta com cobertura de testes para os principais fluxos (Services e Controllers).

Para executar a bateria de testes:
```bash
./mvnw test
```

---

## 📄 Licença

Este projeto está sob a licença [MIT](LICENSE).

---

### 👩‍💻 Autora

**Helen Cristina Batista**  
*Desenvolvedora Back-end Java*

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/hcbatista/)
