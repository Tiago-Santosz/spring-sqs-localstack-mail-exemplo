# Spring SQS LocalStack Mail Example

Este projeto é uma demonstração de uma aplicação Spring Boot que integra SQS (usando LocalStack), PostgreSQL e envio de emails usando Mailpit.

## Pré-requisitos

- Java 21
- Docker e Docker Compose
- Maven

## Como executar

1. **Clone o repositório** (ou navegue até o diretório do projeto).

2. **Inicie os serviços com Docker Compose:**

   ```bash
   docker-compose up -d
   ```

   Isso iniciará:
   - LocalStack (SQS em localhost:4566)
   - PostgreSQL (localhost:5433)
   - Mailpit (localhost:8025 para web UI, localhost:1025 para SMTP)

3. **Execute a aplicação Spring Boot:**

   ```bash
   mvn spring-boot:run
   ```

   A aplicação estará rodando em http://localhost:8090.

## Como testar

1. **Crie um usuário via API:**

   Use curl ou uma ferramenta como Postman para enviar uma requisição POST para `http://localhost:8080/users` com o seguinte JSON:

   ```json
   {
     "name": "João Silva",
     "email": "joao@example.com"
   }
   ```

   Exemplo com curl:

   ```bash
   curl -X POST http://localhost:8080/users \
        -H "Content-Type: application/json" \
        -d '{"name": "João Silva", "email": "joao@example.com"}'
   ```

2. **Verifique o processamento:**

   - O usuário será salvo no PostgreSQL.
   - Uma mensagem será enviada para a fila SQS.
   - O listener (que roda a cada 5 segundos) processará a mensagem e enviará um email de boas-vindas.

3. **Verifique os emails:**

   Acesse http://localhost:8025 no navegador para ver os emails enviados via Mailpit.

## Arquitetura

- **Controller:** Recebe requisições para criar usuários.
- **Service:** Salva o usuário no banco e envia mensagem para SQS.
- **Listener:** Polla mensagens da SQS e envia emails.
- **Config:** Configuração do mail sender para Mailpit.

## Tecnologias

- Spring Boot 3.2.5
- Java 21
- PostgreSQL
- AWS SQS (via LocalStack)
- Mailpit para simulação de emails</content>
<parameter name="filePath">D:\Arquivo De Programas\programar-exemplos\spring-sqs-localstack-mail-exemplo\README.md
